package AIbird;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import interfaces.ParentBird;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.PipeBox;

public class AIBird extends ImageView {

	private Timeline flipline;
	
	public Image bird_png = new Image(ParentBird.class.getResource("/assets/bird.png").toExternalForm());
    //-----------------------------------------------------------------------
    //neat stuff
    public double fitness = 0;
    double []vision; //the input array fed into the neuralNet
    double []decision; //the out put of the NN
    double unadjustedFitness;
    double lifespan = 0.0; //how long the player lived for this.fitness
    int bestScore = 0; //stores the this.score achieved used for replay
    public boolean dead = false;
    public int score = 0;
    int gen = 0;

    int genomeInputs = 4;
    int genomeOutputs = 1;
    public Genome brain;
    PipeBox closest_pipe;
    int index = 0;
    
	public AIBird(){
		super();
		//Image img = new Image(AIBird.class.getResource("/assets/bird.png").toExternalForm());
		this.setImage(bird_png);
		this.setSmooth(true);
		flipline = new Timeline();
		flipline.setCycleCount(Timeline.INDEFINITE);
	    //-----------------------------------------------------------------------
	    //neat stuff
	    brain = new Genome(genomeInputs, genomeOutputs, false);
	    
	    vision = new double[genomeInputs];
	    
	    gravity();
	}
	
    public void play() {
    	//System.out.println(index++);
     	flipline.play();
     	reset();
     }
     
     public void reset(){
     	//flipline.jumpTo(Duration.millis(2000));
     	flipline.jumpTo(Duration.millis(1500));
     }
     
     public void stop(){
     	flipline.stop();
     	dead = true;
     	//System.out.println("dead: "+lifespan+" "+bestScore);
     }
     
     public void pause(){
     	flipline.pause();
     }
     
     public void show(Group rootGroup){
    	 rootGroup.getChildren().add(this);
     }
 	
 	public void gravity(){
 		flipline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,new KeyValue(this.translateYProperty(), 0)),
         		//new KeyFrame(new Duration(2000),new KeyValue(this.translateYProperty(), 200)),
         		//new KeyFrame(new Duration(4000),new KeyValue(this.translateYProperty(), 400))
         		new KeyFrame(new Duration(1500),new KeyValue(this.translateYProperty(), 200)),
         		new KeyFrame(new Duration(3000),new KeyValue(this.translateYProperty(), 400))
         		);
 	}
 	
 	public void jump(){
 		if(!dead){
 	 		flipline.jumpTo(flipline.getCurrentTime().subtract(Duration.seconds(0.3)));
 		}
 	}

	 //display the bird

	 //move the bird in Y direction

	 //compute the score when birds pass the pipes

	 //check collision

	 //jump



	  //-------------------------------------------------------------------neat functions
 	  //get the visions of the current states as the inputs to the neural network brains
	  public void look(List<PipeBox> pipeList) {
		  if(pipeList.size()>0){
			    vision[0] = flipline.getCurrentTime().toSeconds()/4.0; //bird can tell its current y velocity	    
			    //closest pipes top pipe or bottom pipe
			    //PipeBox pipe1 = pipeList.get(pipeList.size()-2);
			    //PipeBox pipe2 = pipeList.get(pipeList.size()-1);
			    PipeBox pipe1 = pipeList.get(0);
			    PipeBox pipe2 = pipeList.get(1);
			    //pipe1 up pipe2 bottom
			    double distance = pipe1.getBoundsInParent().getMinX()-this.getBoundsInParent().getMaxX();
			    vision[1] = (distance/249.0);
                //height above bottomY
    			vision[2] = Math.max(0.0,pipe2.getBoundsInParent().getMinY()-this.getBoundsInParent().getMaxY())/350.0;
    			//distance below topThing
    			vision[3] = Math.max(0.0,this.getBoundsInParent().getMinY()-pipe1.getBoundsInParent().getMaxY())/350.0; 
			    //System.out.println(vision[0]+" "+vision[1]+" "+vision[2]+" "+vision[3]);
		  }
	  }
	  
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
		      //System.out.println(decision[0]);
		      if (decision[0] > 0.6) {
		          jump();
		      }
	    }
	  //---------------------------------------------------------------------------------------------------------------------------------------------------------
	  //for Genetic algorithm
	  public double calculateFitness() {
		  fitness = 1.0 + (double)score * (double)score + lifespan / 20.0;
	    return fitness;
	  }

	  //---------------------------------------------------------------------------------------------------------------------------------------------------------
	  public AIBird crossover(AIBird parent2) {
		  AIBird child = new AIBird();
	      //child.brain = this.brain.crossover(parent2.brain);
	      child.brain = this.brain.crossover_improve(parent2.brain);
	      child.brain.generateNetwork();
	      return child;
	  }
	    //---------------------------------------------------------------------------------------------------------------------------------------------------------
	    //returns a clone of this player with the same brian
	  public AIBird clone() {
	    	AIBird clone = new AIBird();
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
	  }
}
