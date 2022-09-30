import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class CardView extends Rectangle {
	
	Image cardImg;
	ImagePattern imgView;
	
	CardView(String fName)
	{
		this.setWidth(167);
		this.setHeight(250);
		
		cardImg = new Image(fName);
		imgView = new ImagePattern(cardImg);
	
		this.setFill(imgView);
	}
}
