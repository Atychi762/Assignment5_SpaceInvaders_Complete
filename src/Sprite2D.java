import java.awt.*;

public class Sprite2D {

	// member data
	protected double x,y;
	protected Image myImage;
	protected Image myImage2;
	int frameCount = 0;
	boolean isAlive = true;
	
	// static member data
	protected static int winWidth;
	
	// constructor
	public Sprite2D(Image i, Image j) {
		myImage = i;
		myImage2 = j;

	}
		
	public void setPosition(double xx, double yy) {
		x=xx;
		y=yy;
	}
	
	public void paint(Graphics g) {
		frameCount++;
		// changing the image every 50 frames
		if(isAlive) {
			if (frameCount % 100 < 50)
				g.drawImage(myImage, (int) x, (int) y, null);
			else
				g.drawImage(myImage2, (int) x, (int) y, null);
		}
	}
	
	public static void setWinWidth(int w) {
		winWidth = w;
	}
}

