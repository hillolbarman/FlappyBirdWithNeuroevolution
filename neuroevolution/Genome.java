package neuroevolution;


public class Genome {
	public Network network;
	public double score;
	Genome() {
		this.score=0;
		this.network=new Network();
		this.network.perceptronGeneration();
	}
	Genome(double score, Network network) {
		this.score=score;
		this.network=network;
	}
}