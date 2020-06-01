package neuroevolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Network{
	ArrayList<Layer> layers;
	public Network() {
		this.layers=new ArrayList<Layer>();
	}
	void perceptronGeneration() {
		int input=Options.network[0][0],hiddens[]=Options.network[1],output=Options.network[2][0];
		int index=0;
		int noOfPrevNeurons=0;
		Layer layer=new Layer(index);
		layer.populate(input, noOfPrevNeurons);
		this.layers.add(layer);
		noOfPrevNeurons=input;
		index++;
		for(int i=0;i<hiddens.length;i++) {
			layer=new Layer(index);
			layer.populate(hiddens[i], noOfPrevNeurons);
			noOfPrevNeurons=hiddens[i];
			this.layers.add(layer);
			index++;
		}
		layer=new Layer(index);
		layer.populate(output, noOfPrevNeurons);
		this.layers.add(layer);
	}
	
	public double[] compute(double[] inputs) {
		for(int i=0;i<inputs.length;i++) {
			if(this.layers.get(0)!=null && this.layers.get(0).neurons.get(i)!=null) {
				this.layers.get(0).neurons.get(i).value=inputs[i];
			}
		}
		Layer prevLayer=this.layers.get(0);
		for(int i=1;i<this.layers.size();i++) {
			for(int j=0;j<this.layers.get(i).neurons.size();j++ ) {
				double sum=0;
				for(int k=0;k<prevLayer.neurons.size();k++) {
					sum+=prevLayer.neurons.get(k).value * this.layers.get(i).neurons.get(j).weights[k];
				}
				this.layers.get(i).neurons.get(j).value=Options.activation(sum);
			}	
			prevLayer=this.layers.get(i);
		}
		Layer lastLayer=this.layers.get(this.layers.size()-1);
		
		double out[]=new double[lastLayer.neurons.size()];
		for(int i=0;i<lastLayer.neurons.size();i++) {
			out[i]=lastLayer.neurons.get(i).value;
		}
		return out;
	}
	
//	void backPropagation() {}

	
	Data getSave() {
		Data data=new Data();
		data.set(this.layers.size());
		for(int i=0;i<this.layers.size();i++) {
			data.neurons[i]=this.layers.get(i).neurons.size();
			data.weights[i]=new double[data.neurons[i]][];
			for(int j=0;j<this.layers.get(i).neurons.size();j++) {
				data.weights[i][j]=new double[this.layers.get(i).neurons.get(j).weights.length];
				for(int k=0;k<this.layers.get(i).neurons.get(j).weights.length;k++) {
					data.weights[i][j][k]=this.layers.get(i).neurons.get(j).weights[k];
				}
			}
		}
		return data;
	}
	
	void setSave(Data data) {
		int previousNeurons=0;
		this.layers=new ArrayList<Layer>();
		for(int i=0;i<data.neurons.length;i++) {
			Layer layer=new Layer(i);
			layer.populate(data.neurons[i], previousNeurons);
			for(int j=0;j<data.neurons[i];j++) {
				for(int k=0;k<data.weights[i][j].length;k++) {
					layer.neurons.get(j).weights[k]=data.weights[i][j][k];
				}
			}
			previousNeurons=data.neurons[i];
			this.layers.add(layer);
		}
	}
	
	public void writeToFile(File file) {
		Data data=this.getSave();
		try  {
			FileWriter fw=new FileWriter(file);
			for(int i=0;i<data.neurons.length;i++) {
				fw.write(data.neurons[i]+" ");
			}
			fw.write("\n");
			for(int i=0;i<data.weights.length;i++) {
				for(int j=0;j<data.weights[i].length;j++) {
					for(int k=0;k<data.weights[i][j].length;k++) {
							fw.write(data.weights[i][j][k]+" ");
					}
					fw.write("\n");
				}
			}
			fw.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
