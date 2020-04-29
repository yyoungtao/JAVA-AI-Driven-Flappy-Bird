package main;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import AIbird.AIBird;
import AIbird.HistoricalMarking;
import AIbird.Population;
import interfaces.ParentBird;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlippyBirdPanel extends Application {


	private int screen_height=400;
	public static int screen_width=400;
	private int gap = 100;
	private int min_pipe_height = 50;
	private int max_pipe_height = 250;
	private Random r = new Random();
	
	public List<PipeBox> pipeList;
	
	private Timeline pipeline;
	//private Timeline check;
	private AnimationTimer  check_timer;
	//private AnimationTimer  AI_timer;
	
	private Stage stage;
	 
	public boolean failed = false;
	public boolean pause = false;
	
	//private int epoches = 0;
	
	//private long lastUpdate = 0;
	int index = 0;
	
	public static int nextConnectionNo = 1;
	
	public static void main(String[] args) {
		launch(args);
	}

   @Override
    public void start(Stage primaryStage) throws Exception {
       //set scene to stage
        primaryStage.setScene(createContent());

        //set title to stage
        primaryStage.setTitle("Flippy Bird");

        //center stage on screen
        primaryStage.centerOnScreen();

        //show the stage
        primaryStage.show();
        
        stage = primaryStage;
    }
   
   @Override
   public void stop() {
	   //check.stop();
	   check_timer.stop();
       pipeline.stop();
       //AI_timer.stop();
   }

	public Scene createContent() {
		pipeline = new Timeline();
		pipeline.setCycleCount(Timeline.INDEFINITE);
		
		//initialize population
		Population population = new Population(1000);
	    
	    pipeList = new ArrayList<PipeBox>();
	    
        //create root node of scene, i.e. group
        Group rootGroup = new Group();

        //create scene with set width, height and color
        Scene scene = new Scene(rootGroup, screen_width, screen_height, Color.WHITESMOKE);

        //add some node to scene
        Text text = new Text(20, 110, "Flappy Bird. Press Enter to start, \n press space to jump");
        text.setFill(Color.DODGERBLUE);
        text.setEffect(new Lighting());
        text.setFont(Font.font(Font.getDefault().getFamily(), 20));

        //add text to the main root group
        rootGroup.getChildren().add(text);
        //bird.gravity();


        //rootGroup.getChildren().add(bird);
        
        pipeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1.5),
                  event -> {
                  	PipeBox[] pipes = create_pipe();  
                 	//up pipe
                 	pipes[0].setRotate(180);
                 	pipes[0].runing();
                 	pipes[0].play();
                 	pipes[0].reset();
                 	//bottom pipe
                 	pipes[1].setLayoutY(screen_height-pipes[1].getContentHeight());
                 	pipes[1].runing();
                 	pipes[1].play();
                 	pipes[1].reset();
                 	
                 	pipeList.add(pipes[0]);
                 	pipeList.add(pipes[1]);

					rootGroup.getChildren().add(pipes[0]);
					rootGroup.getChildren().add(pipes[1]);

				}));

        check_timer = new AnimationTimer() {
            public void handle(long now) {
//            	if (lastUpdate > 0)
//                {
//                    long nanosElapsed = now - lastUpdate;
//                    double frameRate = 1000000000.0 / nanosElapsed;
//                    System.out.println(frameRate);
//                    //index %= frameRates.length;
//                    //frameRates[index++] = frameRate;
//                }
//                lastUpdate = now;
            	for(int i=0 ; i<population.players.size() ; i++){
            		if(!population.players.get(i).dead){
            			if(!rootGroup.getChildren().contains(population.players.get(i))){
            				rootGroup.getChildren().add(population.players.get(i));
            				population.players.get(i).play();
                			population.players.get(i).setLayoutX(FlippyBirdPanel.screen_width/2);
            			}
            			population.players.get(i).look(pipeList);
            			population.players.get(i).think();
            			population.players.get(i).updateLifeSpan();
            			
            			ArrayList<PipeBox> toRemove = new ArrayList<PipeBox>();
                    	
                    	//bird.look(pipeList);
                    	//bird.think();
        	        	for (PipeBox temp : pipeList) {
        	                if(check_collision(temp, population.players.get(i))){
        	                	//System.out.println("collision");
        	                	//failed = true;
        	                	population.players.get(i).stop();
        	                }
        	                if(temp.getBoundsInParent().getMaxX() < screen_width/2){
        	                	toRemove.add(temp);
        	                }
        	            }
                	    pipeList.removeAll(toRemove);
            		}else{
            			rootGroup.getChildren().remove(population.players.get(i));
            		}
            	}
            	
            	if(population.done()){
            		//System.out.println("all dead");
                	//check_timer.stop();
                	pipeline.stop();
                	//bird.pause();
                	//createAlert();
                	rootGroup.getChildren().removeAll(population.players);
                	population.naturalSelection();
                	
                	//output the previous bead bird
                	System.out.println(population.bestPlayer.score);
                	
                	//rootGroup.getChildren().add(population.players.get(0));
                	for (PipeBox temp : pipeList) {
                		temp.stop();
                		rootGroup.getChildren().remove(temp);
                	}
                	
                	//check_timer.start();
                	pipeList.clear();
                	pipeline.play();
            	}
            }
        };
        
//        AI_timer = new AnimationTimer() {
//            public void handle(long now) {
//            	bird.look();
//            }
//        };
        
        //bird.play();
        //bird.reset();
        //bird.pause();
        //pipeline.pause();
        //check_timer.stop();
        
        pipeline.play();
        check_timer.start();
        
        scene.setOnKeyPressed((KeyEvent ke) -> {
        	if(ke.getCode()==KeyCode.ENTER) {
        		if(pause){
        			//check.play();
        			check_timer.start();
        			//AI_timer.start();
                	pipeline.play();
                	//bird.play();
                	//bird.reset();
                	failed = false;
                	pause = false;
        		}
        		if(failed){
        			for (PipeBox temp : pipeList) {
                		rootGroup.getChildren().remove(temp);
                	}
                	pipeList.clear();
                	//check.play();
                	check_timer.start();
                	//AI_timer.start();
                	pipeline.play();
                	//bird.play();
                	//bird.reset();
                	failed = false;
                }
            }
        	if(ke.getCode()==KeyCode.SPACE) {
                //bird.jump();
            }
        });
        
    	return scene;
    }
	
	
	
	
	public PipeBox[] create_pipe(){
		PipeBox[] pipes = new PipeBox[2];
		Image pipe_head = new Image(FlippyBirdPanel.class.getResource("/assets/pipeHead.png").toExternalForm());
      	Image pipe_body = new Image(FlippyBirdPanel.class.getResource("/assets/pipeBody.png").toExternalForm());
      	
      	int height = r.nextInt((max_pipe_height - min_pipe_height) + 1) + min_pipe_height;
      	int residual_height = screen_height-gap-height;
      	
      	pipes[0] = new PipeBox(pipe_head, pipe_body, height);
      	pipes[1] = new PipeBox(pipe_head, pipe_body, residual_height);
      	
      	return pipes;
	}
	
	protected Alert createAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.getDialogPane().setContentText("You Lose!!!!!");
        alert.show();
        return alert;
    }
	
	public boolean check_collision(PipeBox temp, AIBird bird){
		//System.out.println(bird.getBoundsInParent().getMinX());
		if(temp.getBoundsInParent().getMaxX() <= bird.getBoundsInParent().getMinX()){
			//increase score
			bird.updateScore();
		}
		//temp.getBoundsInParent().intersects(bird.getBoundsInParent().getMinX(), bird.getBoundsInParent().getMinY(), bird.getBoundsInParent().getWidth(), bird.getBoundsInParent().getHeight())
		if(temp.getBoundsInParent().intersects(bird.getBoundsInParent())){
			return true;
		}
		
//		if(temp.getBoundsInParent().contains(new Point2D(bird.getBoundsInParent().getMaxX(),bird.getBoundsInParent().getMaxY()))
//				||temp.getBoundsInParent().contains(new Point2D(bird.getBoundsInParent().getMinX(),bird.getBoundsInParent().getMinY()))
//				||temp.getBoundsInParent().contains(new Point2D(bird.getBoundsInParent().getMinX()+bird.getBoundsInParent().getWidth(),bird.getBoundsInParent().getMinY()))
//				||temp.getBoundsInParent().contains(new Point2D(bird.getBoundsInParent().getMinX(),bird.getBoundsInParent().getMinY()+bird.getBoundsInParent().getHeight()))){
//			return true;
//		}
		return false;
	}
}
