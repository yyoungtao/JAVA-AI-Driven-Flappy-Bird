package main;
import interfaces.ParentBird;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Bird extends ParentBird{

	private double lastX, lastY;
	private double vx, vy;
	//private TranslateTransition translateTransition;
	private Timeline flipline;

	
	public Bird(Image img){
		super();
		this.setImage(img);
		this.setSmooth(true);
		//translateTransition = new TranslateTransition(Duration.seconds(4), this);
		
		flipline = new Timeline();
		flipline.setCycleCount(Timeline.INDEFINITE);
		
		setOnMousePressed((MouseEvent event) -> {
            lastX = event.getX();
            lastY = event.getY();
            toFront();
            //  postView.toFront();
        });
        setOnMouseDragged((MouseEvent event) -> {
            double layoutX = getLayoutX() + (event.getX() - lastX);
            double layoutY = getLayoutY() + (event.getY() - lastY);
 
            if ((layoutX >= 0) && (layoutX <= (getParent().getLayoutBounds().getWidth()))) {
                setLayoutX(layoutX);
            }
 
            if ((layoutY >= 0) && (layoutY <= (getParent().getLayoutBounds().getHeight()))) {
                setLayoutY(layoutY);
            }
 
            if ((getLayoutX() + (event.getX() - lastX) <= 0)) {
                setLayoutX(0);
            }
        });
        
	}
	
    public void play() {
       //translateTransition.play();
    	flipline.play();
    }
    
    public void reset(){
    	flipline.jumpTo(Duration.millis(2000));
    }
    
    public void stop(){
    	flipline.stop();
    }
    
    public void pause(){
    	flipline.pause();
    }
	
	public void gravity(){
		//this.get
		//setLayoutX(getLayoutX() + vx);
		//setLayoutY(getLayoutY() + vy);
		//vy += 0.5;

        //translateTransition.setFromY(getLayoutY());
        //translateTransition.setToY(300);
        //translateTransition.setRate(0.5);
        //translateTransition.setCycleCount(Timeline.INDEFINITE);
        //System.out.println(translateTransition.getCuePoints());
		flipline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,new KeyValue(this.translateYProperty(), 0)),
        		new KeyFrame(new Duration(2000),new KeyValue(this.translateYProperty(), 200)),
        		new KeyFrame(new Duration(4000),new KeyValue(this.translateYProperty(), 400)));
		
        vy += 0.5;
	}
	
	public void jump(){
		vy = -8;
		//translateTransition.setRate(-8);
		//translateTransition.getCurrentTime()-0.5
		//translateTransition.jumpTo(translateTransition.getCurrentTime().subtract(Duration.seconds(0.5)));
		
		flipline.jumpTo(flipline.getCurrentTime().subtract(Duration.seconds(0.6)));
	}
	
}
