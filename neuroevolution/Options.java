package neuroevolution;

public class Options {
	public static int population,historic,scoreSort,nbChild;
	public static double elitism;
	public static double randomBehaviour;
	public static double mutationRate;
	public static double mutationRange;
	public static boolean lowHistoric;
	public static int network[][];
	public static void update() {
		population=500;
		elitism=0.2;
		randomBehaviour=0.2;
		mutationRate=0.1;
		mutationRange=0.5;
		network= new int[][]{{6},{6,4},{1}};
		historic=0;
		lowHistoric=false;
		scoreSort=-1;
		nbChild=1;
	}
	//Sigmoid
	public static double activation(double a) {
		double ap=(-a)/1;
		return (1/(1*Math.exp(ap)));
	}
	//Random no generator
	public static double randomClamped() {
		return Math.random()*2-1;
	}
	
	
}
