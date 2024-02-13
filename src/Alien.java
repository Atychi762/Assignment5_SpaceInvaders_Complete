import java.awt.*;

public class Alien extends Sprite2D {
	private static double xSpeed=0;
	
	public Alien(Image i) {
		super(i); // invoke constructor on superclass Sprite2D
	}
	
	// public interface
	public boolean move() {
		x+=xSpeed;
		
		// direction reversal needed?
		if (x<=0 || x>=winWidth-myImage.getWidth(null))
			return true;
		else
			return false;
	}
	
	public static void setFleetXSpeed(double dx) {
		xSpeed=dx;
	}
	
	public static void reverseDirection() {
		xSpeed=-xSpeed;
	}	
	
	public void jumpDownwards() {
		y+=20;
	}
}

