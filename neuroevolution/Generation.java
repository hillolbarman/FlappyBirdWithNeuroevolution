package neuroevolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Generation{
	public ArrayList<Genome> genomes;
	public Generation() {
		Options.update();
		genomes=new ArrayList<Genome>();
	}
	void addGenome(Genome genome) {
		int i=0;
		for(;i<this.genomes.size();i++) {
			if(this.genomes.get(i).score<0) { //Sort descending
				if(genome.score > this.genomes.get(i).score) { break; }
			}
			else {	//Sort ascending
				if(genome.score<this.genomes.get(i).score) { break; }
			}
		}
		this.genomes.add(i, genome);
	}
	
	Genome[] breed(Genome g1, Genome g2, int noOfChildren) {
		Genome[] newNets=new Genome[noOfChildren];
		for(int nb=0;nb<noOfChildren;nb++) {
			Genome net=new Genome();
			//CrossOver
			for(int i=0;i<g1.network.layers.size();i++) {
				for(int j=0; j<g1.network.layers.get(i).neurons.size();j++) {
					for(int k=0;k<g1.network.layers.get(i).neurons.get(j).weights.length;k++) {
						double weight;
						if(Math.random()<=0.5) {
							weight=g1.network.layers.get(i).neurons.get(j).weights[k];
						} else {
							weight=g2.network.layers.get(i).neurons.get(j).weights[k];
						}
						
						//Mutation
						if(Math.random() <= Options.mutationRate) {
							weight+=Math.random() * Options.mutationRange*2 - Options.mutationRange;
						}
						net.network.layers.get(i).neurons.get(j).weights[k]=weight;
					}
				}
			}
			newNets[nb]=net;
		}
		return newNets;
	}
	
	public Generation generateNextGeneration() {
		sortScores();
		Generation gen=new Generation();
		int size=0;
		//Get the elite few
		for(int i=0;i<Math.round(Options.elitism*Options.population);i++) {
			if(size<Options.population) {
				gen.addGenome(this.genomes.get(i));;
			}
		}
		//Create random
		for(int i=0;i<Math.round(Options.randomBehaviour*Options.population);i++) {
			Genome net=new Genome();
			for(int j=0;j<net.network.layers.size();j++) {
				for(int k=0;k<net.network.layers.get(j).neurons.size();k++) {
					net.network.layers.get(j).neurons.get(k).populate(gen.genomes.get(0).network.layers.get(j).neurons.get(k).weights.length);
				}
			}
			if(size<Options.population) {
				gen.addGenome(net);
			}
		}
		//Crossover for the rest
		int max=0;
		boolean loop=true;
		while(loop) {
			for(int i=0;i<max;i++) {
				Genome[] children=this.breed(this.genomes.get(i), this.genomes.get(max), (Options.nbChild>0)?Options.nbChild:1);
				for(Genome c : children) {
					gen.addGenome(c);
					if(gen.genomes.size()>=Options.population) { loop=false; break; }
				}
			}
			max++;
			if(max>=this.genomes.size()-1) {
				max=0;
			}
		}
		return gen;
		
	}
	
	private void sortScores() {
		Collections.sort(this.genomes, new Comparator<Genome>() {
			public int compare(Genome g1, Genome g2) {
				if(g1.score>g2.score) return -1;
				if(g1.score<g2.score) return 1;
				return 0;
			}
		});
	}
	
	
	public void createGeneration() {
		for(int i=0;i<Options.population;i++) {
			Genome gene=new Genome();
			this.addGenome(gene);
		}
	}
	
}
