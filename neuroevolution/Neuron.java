package neuroevolution;

public class Neuron {
	double value;
	double weights[];
	Neuron() {
		this.value=0;
	}
	void populate(int noOfWeights) {
		this.weights=new double[noOfWeights];
		for(int i=0;i<noOfWeights;i++) {
			this.weights[i]=Options.randomClamped();
		}
	}
}

class Data {
	int neurons[];
	double weights[][][];
	void set(int totalLayers) {
		this.neurons=new int[totalLayers];
		this.weights=new double[totalLayers][][];
	}
}