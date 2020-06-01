import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import neuroevolution.*;

public class FlappyBird extends PApplet {

ArrayList<Bird> birds;
boolean jumping = false;
PVector gravity = new PVector(0, 0.5f);
ArrayList<Pipe> pipes = new ArrayList<Pipe>();

//***************************************************************
Generation currentGeneration;
ArrayList<Generation> gens=new ArrayList<Generation>();
int noOfGen=0;
int maxScore=0;
int lastMax=0;
Bird maxBird;
File file;
Genome ge;
public void assignBrains(Generation gen) {
	birds=new ArrayList<Bird>();
	  for(int i=0;i<Options.population;i++) {
		  Bird b=new Bird();
		  b.index=i;
		  b.brain=gen.genomes.get(i).network;
		  birds.add(b);
	  }
}

public void nextGeneration() {
	noOfGen++;
	if(currentGeneration==null) {
		currentGeneration=new Generation();
		currentGeneration.createGeneration();	
		assignBrains(currentGeneration);
	} else {
		currentGeneration=currentGeneration.generateNextGeneration();
		assignBrains(currentGeneration);
	}
	if(maxBird!=null) {
		maxBird.brain.writeToFile(file);
	}
}

public void updateScore() {
	for(int i=0;i<birds.size();i++) {
		Bird b=birds.get(i);
		currentGeneration.genomes.get(b.index).score=(double)b.max;
	}
}

public void keyPressed() {
	if(key==32) {
		noLoop();
		updateScore();
		nextGeneration();
		System.exit(0);
	}
}

//******************************************************************************
public void setup() {
  nextGeneration();
  pipes.add(new Pipe());
  file=new File("bestvalues.txt");

}

public void draw() {
  background(0);

  if (frameCount % 75 == 0) {
    pipes.add(new Pipe());
  }

//  if (keyPressed) {
//    PVector up = new PVector(0, -5);
//    b.applyForce(up);
//  }
  for(Bird b:birds) {
	  b.think(pipes);
	  b.update();
	  b.show();
  }
  for (int i = pipes.size() - 1; i >= 0; i--) {
	  Pipe p = pipes.get(i);
	  p.update();
	  p.show(false);
	  if (p.x < -p.w) {
	      pipes.remove(i);
	    }
	  }
  
  ArrayList<Bird> toRemove=new ArrayList<Bird>();
  for(Bird b:birds) {
	  boolean safe = true;
	  if(b.pos.y==height || b.pos.y==0) { safe=false; }
	  else {
		  for (int i = pipes.size() - 1; i >= 0; i--) {
		    Pipe p = pipes.get(i);
		    if (p.hits(b)) {
		      safe = false;
		      break;
		    }
		    else {
		    	if ((b.pos.x > p.x) && (b.pos.x < (p.x + p.w))) {
		  	      if ((b.pos.y > (p.top + b.r)) && (b.pos.y < (height - p.bottom - b.r))) {
		  	        b.score+=5;
		  	      }
		  	    }
		    }
		  }
	  }
	  
	  if (safe) {
	    b.score++;
	    if(b.score>b.max) b.max=b.score;
	  } else {
	    b.score -= 100;
	    toRemove.add(b);
	  }
		  
	  fill(255, 0, 255);
	  textSize(16);
	  text(b.score, 10, 50);
	  b.score = constrain(b.score, 0, b.score);
		  
	  //***********************************************************************
	  if(b.score>maxScore) {
		  maxScore=b.score;
		  maxBird=b;
		  b.col=true;
	  }
	  //***********************************************************************
	  
  }
  text(birds.size(), 10, 25);
  text(noOfGen, 10, 75);
  text(maxScore, 10, 100);
  
  //*******************************************************************

  updateScore();
  for(Bird b:toRemove)	birds.remove(b);
  if(birds.size()==0 || maxScore>lastMax*2 || (birds.size()<=(Options.elitism*Options.population*0.1)) && birds.get(0).score>=lastMax*2) {
	  lastMax=maxScore;
	  pipes.removeAll(pipes);
	  nextGeneration();
  }
  //********************************************************************
}



public class Bird{
	  PVector pos;
	  PVector vel;
	  PVector acc;
	  float r = 12;
	  int score;
	  int max;
	  int index=0;
	  boolean col=false;
	  
	  Network brain;

	  Bird() {
	    pos = new PVector(50, height/2);
	    vel = new PVector(0, 0);
	    acc = new PVector();
	    score=0;
	    max=0;
	  }
	  
	  //****************************************************************
	  void think(ArrayList<Pipe> pipes) {
		  
		  if(pipes.size()>0) {
		  Pipe closest=null;
		  double closestD=Double.MAX_VALUE;
		  for(int i=0;i<pipes.size();i++) {
			  double d=pipes.get(i).x-this.pos.x+pipes.get(i).w;
			  if(d<closestD && d>0) {
				  closest=pipes.get(i);
				  closestD=d;
			  }
		  }
		  
		  double inputs[]=new double[6];
		  inputs[0]=this.pos.y/height;
		  inputs[1]=closest.top/height;
		  inputs[2]=closest.bottom/height;
		  inputs[3]=closest.x/width;
		  inputs[4]=this.acc.y/height;
		  inputs[5]=((closest.bottom-closest.top)/2)/height;
		  double[] output=this.brain.compute(inputs);
		  if(output[0]>0.5) {
			  this.applyForce(new PVector(0, -5));
		  }
		  }
	  }
	  //*************************************************************
	  public void applyForce(PVector force) {
	    acc.add(force);
	  }

	  public void update() {
	    applyForce(gravity);
	    pos.add(vel);
	    vel.add(acc);
	    vel.limit(4);
	    acc.mult(0);

	    if (pos.y > height) {
	      pos.y = height;
	      vel.mult(0);
	    }
	    if (pos.y < 0) {
	        pos.y = 0;
	        vel.mult(0);
	      }
	  }
	  public void settings() {  size(400, 800); }
	  public void show() {
	    stroke(255);
	    if(col) {fill(0,255,0);}
	    else fill(255,100);
	    ellipse(pos.x, pos.y, r*2, r*2);
	  }
	}

class Pipe{
	  float x;
	  float top;
	  float bottom;
	  float w = 30;
	  
	  Pipe() {
	    x = width;

	    float b=random(50,175);
	    float a=random(50,height-b-50);
//	    top = random(100, height/2-40);
//	    bottom =random(100, height/2-40);
	    top = a;
	    bottom=height-a-b;
	  }

	  public boolean hits(Bird b) {
	    if ((b.pos.x > x) && (b.pos.x < (x + w))) {
	      if ((b.pos.y < (top + b.r)) || (b.pos.y > (height - bottom - b.r))) {
	        return true;
	      }
	    }
	    return false;
	  }

	  public void update() {
	    x -= 3;
	  }

	  public void show(boolean hit) {
	    stroke(255);
	    
	    if (hit) {      
	      fill(255, 0, 0,100);
	    } else {
	      fill(255);
	    }
	    
	    rect(x, 0, w, top); 
	    rect(x, height - bottom, w, bottom);
	  }
	}


  public void settings() {  size(800, 400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FlappyBird" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
