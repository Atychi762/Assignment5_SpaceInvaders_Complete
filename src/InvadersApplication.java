import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;
import java.awt.image.*;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {
	
	// member data
	private static String workingDirectory;
	private static boolean isInitialised = false;
private static final Dimension WindowSize = new Dimension(800,600);
	private BufferStrategy strategy;
	private Graphics offscreenGraphics;
	private static final int NUMALIENS = 30;
	private Alien[] AliensArray = new Alien[NUMALIENS];
	private Spaceship PlayerShip;
	
	// constructor
	public InvadersApplication() {
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
    	this.setTitle("Space Invaders!");
        
        // load image from disk
        ImageIcon icon = new ImageIcon(workingDirectory + "//alien_ship_1.png");
        Image alienImage = icon.getImage();
        
        // create and initialise some aliens, passing them each the image we have loaded
        for (int i=0; i<NUMALIENS; i++) {
        	AliensArray[i] = new Alien(alienImage);
        	double xx = (i%5)*80 + 70;
        	double yy = (i/5)*40 + 50; // integer division!
        	AliensArray[i].setPosition(xx, yy);
        }
        Alien.setFleetXSpeed(2);
        
        // create and initialise the player's spaceship
        icon = new ImageIcon(workingDirectory + "/player_ship.png");
        Image shipImage = icon.getImage();
        PlayerShip = new Spaceship(shipImage);
        PlayerShip.setPosition(300,560);
        
        // tell all sprites the window width
        Sprite2D.setWinWidth(WindowSize.width);
        
        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();
        
        // send keyboard events arriving into this JFrame back to its own event handlers
        addKeyListener(this);
        
        // initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offscreenGraphics = strategy.getDrawGraphics();

        isInitialised = true;
	}
	
	// thread's entry point
	public void run() {
		while ( 1==1 ) {
			
			// 1: sleep for 1/50 sec
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) { }
			
			// 2: animate game objects
			boolean alienDirectionReversalNeeded = false;
			for (int i=0;i<NUMALIENS; i++) {
				if (AliensArray[i].move())
					alienDirectionReversalNeeded=true;
			}
			if (alienDirectionReversalNeeded) {
				Alien.reverseDirection();
				for (int i=0;i<NUMALIENS; i++)
					AliensArray[i].jumpDownwards();
			}
			
			PlayerShip.move();
			
			// 3: force an application repaint
			this.repaint();
		}
	}
	
	// Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode()==KeyEvent.VK_LEFT)
    		PlayerShip.setXSpeed(-4);
    	else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
    		PlayerShip.setXSpeed(4);
    }
    
    public void keyReleased(KeyEvent e) {	
    	if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT) 
    		PlayerShip.setXSpeed(0);
    }
    
    public void keyTyped(KeyEvent e) {
    }
    //

	// application's paint method
	public void paint(Graphics g) {		
		if (!isInitialised)
			return;
		
		g = offscreenGraphics;
		
		// clear the canvas with a big black rectangle
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WindowSize.width, WindowSize.height);
		
		// redraw all game objects
		for (int i=0;i<NUMALIENS; i++)
			AliensArray[i].paint(g);
		
		PlayerShip.paint(g);
		
		// flip the buffers offscreen<-->onscreen
		strategy.show();
	}
	
	// application entry point
	public static void main(String[] args) {
		workingDirectory = System.getProperty("user.dir");
		System.out.println("Working Directory = " + workingDirectory);
		InvadersApplication w = new InvadersApplication();
	}

}

