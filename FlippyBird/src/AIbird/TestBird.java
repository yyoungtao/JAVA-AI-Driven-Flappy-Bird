package AIbird;

import java.util.ArrayList;
import java.util.List;

import interfaces.ParentBird;
import javafx.scene.image.Image;
import main.PipeBox;

public class TestBird {

    //-----------------------------------------------------------------------
    //neat stuff
    public double fitness = 0;
    double []vision; //the input array fed into the neuralNet
    double []decision; //the out put of the NN
    double unadjustedFitness;
    double lifespan = 0.0; //how long the player lived for this.fitness
    int bestScore = 0; //stores the this.score achieved used for replay
    public boolean dead = false;
    int score = 0;
    int gen = 0;

    int genomeInputs = 4;
    int genomeOutputs = 1;
    public Genome brain;
    PipeBox closest_pipe;
    int index = 0;
    
	public TestBird(){
		super();
		//Image img = new Image(AIBird.class.getResource("/assets/bird.png").toExternalForm());

		//flipline = new Timeline();
		//flipline.setCycleCount(Timeline.INDEFINITE);
	    //-----------------------------------------------------------------------
	    //neat stuff
	    brain = new Genome(genomeInputs, genomeOutputs, false);
	    vision = new double[genomeInputs];
	}
 	
	 //display the bird

	 //move the bird in Y direction

	 //compute the score when birds pass the pipes

	 //check collision

	 //jump


	  
	  public void updateScore(){
		  score++;
		  //System.out.println(score);
	  }
	  
	  public void updateLifeSpan(){
		  //lifespan += 0.02;
		  lifespan++;
	  }



	  //---------------------------------------------------------------------------------------------------------------------------------------------------------
	  //gets the output of the this.brain then converts them to actions
	  public void think() {
		      //get the outputs of the neural network
		      decision = brain.feedForward(vision);
		      System.out.println(decision[0]);
	    }
	  //---------------------------------------------------------------------------------------------------------------------------------------------------------
	  //for Genetic algorithm
	  public double calculateFitness() {
		  fitness = 1.0 + (double)score * (double)score + lifespan / 20.0;
	    return fitness;
	  }

	  //---------------------------------------------------------------------------------------------------------------------------------------------------------
	  public TestBird crossover(TestBird parent2) {
		  TestBird child = new TestBird();
	      child.brain = this.brain.crossover(parent2.brain);
	      child.brain.generateNetwork();
	      return child;
	  }
	    //---------------------------------------------------------------------------------------------------------------------------------------------------------
	    //returns a clone of this player with the same brian
	  public TestBird clone() {
	    	TestBird clone = new TestBird();
	    clone.brain = this.brain.clone();//come problems
	    clone.fitness = this.fitness;
	    clone.brain.generateNetwork();
	    clone.gen = this.gen;
	    clone.bestScore = this.score;
	    return clone;
	  }

	  //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	  //since there is some randomness in games sometimes when we want to replay the game we need to remove that randomness
	  //this fuction does that

//	  cloneForReplay() {
//	    var clone = new Player();
//	    clone.brain = this.brain.clone();
//	    clone.fitness = this.fitness;
//	    clone.brain.generateNetwork();
//	    clone.gen = this.gen;
//	    clone.bestScore = this.score;
//
//	    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<replace
//	    return clone;
//	  }
	  
	  public static void main(String[] args){
		  //System.out.println(new Random().nextInt());
		  
		  List<HistoricalMarking> innovationHistory = new ArrayList<HistoricalMarking>();
		  //test brain
		  TestBird b = new TestBird();
		  b.brain.mutate(innovationHistory); // fullyConnect(this.innovationHistory);
		  b.brain.generateNetwork();
		  
		    System.out.println("inputs:"+b.brain.inputs);
		    System.out.println("outputs:"+b.brain.outputs);
		    System.out.println("layers:"+b.brain.layers);
		    System.out.println("nextnode:"+b.brain.nextNode);
		    System.out.println("biasNode:"+b.brain.biasNode);
		    
		    System.out.println(b.brain.connections.size());
		    
		    for(int i=0;i<b.brain.connections.size();i++){
		    	System.out.println(i+": "+b.brain.connections.get(i).innovationNo);
		    	System.out.println(i+": "+b.brain.connections.get(i).fromNode.number);
		    	System.out.println(i+": "+b.brain.connections.get(i).toNode.number);
		    	System.out.println(i+": "+b.brain.connections.get(i).weight);
		    	System.out.println(i+": "+b.brain.connections.get(i).enabled);
		    }
		  
		  b.vision[0] = 0.35;
		  b.vision[1] = 0.15;
		  b.vision[2] = 0.23;
		  b.vision[3] = 0.15;
		  b.think();
		  
		  
		  TestBird b1 = new TestBird();
		  b1.brain.mutate(innovationHistory); // fullyConnect(this.innovationHistory);
		  b1.brain.generateNetwork();
		  
		  TestBird a = b1.crossover(b);
		  
	  }
}
