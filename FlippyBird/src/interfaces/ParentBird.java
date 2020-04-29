package interfaces;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ParentBird extends ImageView {

	public Image bird_png = new Image(ParentBird.class.getResource("/assets/bird.png").toExternalForm());
	
	  public void updateScore(){
	  }
}
