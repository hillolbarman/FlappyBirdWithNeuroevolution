package neuroevolution;

import java.util.ArrayList;

public class Layer {
	int id;
	ArrayList<Neuron> neurons;
	Layer(int id) {
		this.id=id;
		neurons=new ArrayList<Neuron>();
	}
	void populate(int noOfNeurons, int noOfInputs) {
		for(int i=0;i<noOfNeurons;i++) {
			Neuron n=new Neuron();
			n.populate(noOfInputs);
			neurons.add(n);
		}
	}
}
