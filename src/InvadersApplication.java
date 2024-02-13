import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

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
	private Image bulletImage;
	private ArrayList<PlayerBullet> bulletList = new ArrayList<>();
	private boolean gameState = false;
	private int score = 0;
	private int best_Score = 0;
	private int waveNum = 1;
	private Iterator<PlayerBullet> al = bulletList.iterator();
	
	// constructor
	public InvadersApplication() {
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
    	this.setTitle("Space Invaders!");

        // initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offscreenGraphics = strategy.getDrawGraphics();

        isInitialised = true;

		// create and start our animation thread
		Thread t = new Thread(this);
		t.start();

		// send keyboard events arriving into this JFrame back to its own event handlers
		addKeyListener(this);
	}
	
	// thread's entry point
	public void run() {
		while ( 1==1 ) {
			if(gameState) {
				// 1: sleep for 1/50 sec
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}

				// 2: animate game objects
				boolean alienDirectionReversalNeeded = false;
				for (int i = 0; i < NUMALIENS; i++) {
					if (AliensArray[i].move())
						alienDirectionReversalNeeded = true;
				}
				if (alienDirectionReversalNeeded) {
					Alien.reverseDirection();
					for (int i = 0; i < NUMALIENS; i++)
						AliensArray[i].jumpDownwards();
				}

				PlayerShip.move();

				for (int i = 0; i < bulletList.size(); i++) {
					bulletList.get(i).move();
					for (int j = 0; j < NUMALIENS; j++) {
						if (bulletList.get(i).isAlive && AliensArray[j].isAlive) {
							if (
									((AliensArray[j].x < bulletList.get(i).x) && (AliensArray[j].x + 50 > bulletList.get(i).x) ||
											(bulletList.get(i).x < AliensArray[j].x) && (bulletList.get(i).x + 6 > AliensArray[j].x))
											&&
											((AliensArray[j].y < bulletList.get(i).y) && (AliensArray[j].y + 32 > bulletList.get(i).y) ||
													(bulletList.get(i).y < AliensArray[j].y) && (bulletList.get(i).y + 16 > AliensArray[j].y))
							) {
								score += 10;
								AliensArray[j].isAlive = false;
								bulletList.get(i).isAlive = false;
							}
						}
					}
				}
				// check player collision
				for (int j = 0; j < NUMALIENS; j++) {
					if (AliensArray[j].isAlive) {
						if (
								((AliensArray[j].x < PlayerShip.x) && (AliensArray[j].x + 50 > PlayerShip.x) ||
										(PlayerShip.x < AliensArray[j].x) && (PlayerShip.x + 54 > AliensArray[j].x))
										&&
										((AliensArray[j].y < PlayerShip.y) && (AliensArray[j].y + 32 > PlayerShip.y) ||
												(PlayerShip.y < AliensArray[j].y) && PlayerShip.y + 32 > AliensArray[j].y)
						) {
							gameState = false;
							if(score > best_Score){
								best_Score = score;
							}
							score = 0;
							Alien.setFleetXSpeed(0);
						}
					}
				}

				int deathCheck = 0;
				for (int i = 0; i < NUMALIENS; i++) {
					if (!AliensArray[i].isAlive) {
						deathCheck++;
					}
				}
				if (deathCheck == NUMALIENS) {
					score += 50;
					startNewWave();
				}
			}

			// 3: force an application repaint
			this.repaint();
		}
	}
	
	// Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode()==KeyEvent.VK_LEFT)
			if(gameState)
    			PlayerShip.setXSpeed(-4);
			else{
				gameState = true;
				startNewGame();
			}
    	else if (e.getKeyCode()==KeyEvent.VK_RIGHT)
    		if(gameState)
				PlayerShip.setXSpeed(4);
			else{
				gameState = true;
				startNewGame();
			}
    }
    
    public void keyReleased(KeyEvent e) {	
    	if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT) 
    		if(gameState)
				PlayerShip.setXSpeed(0);
			else{
				gameState = true;
				startNewGame();
			}

		// if the space bar is released call shootBullet
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
			if(gameState)
				shootBullet();
			else{
				gameState = true;
				startNewGame();
			}
    }
    
    public void keyTyped(KeyEvent e) {
    }


	// method handles the shooting of a bullet
	public void shootBullet(){

		PlayerBullet temp = new PlayerBullet(bulletImage, WindowSize.width);
		temp.setPosition(PlayerShip.x + 25, PlayerShip.y - 10);
		bulletList.add(temp);

	}

	public void startNewWave(){
		waveNum += 2;
		for (int i=0; i<NUMALIENS; i++) {
			AliensArray[i].isAlive = true;
			double xx = (i%5)*80 + 70;
			double yy = (i/5)*40 + 50; // integer division!
			AliensArray[i].setPosition(xx, yy);
		}
		Alien.setFleetXSpeed(2 + waveNum);
	}

	public void startNewGame(){
		// load image from disk
		ImageIcon icon = new ImageIcon(workingDirectory + "//alien_ship_1.png");
		Image alienImage = icon.getImage();
		icon = new ImageIcon(workingDirectory + "//alien_ship_2.png");
		Image alienImage2 = icon.getImage();

		// create and initialise some aliens, passing them each the image we have loaded
		for (int i=0; i<NUMALIENS; i++) {
			AliensArray[i] = new Alien(alienImage, alienImage2);
			double xx = (i%5)*80 + 70;
			double yy = (i/5)*40 + 50; // integer division!
			AliensArray[i].setPosition(xx, yy);
		}
		Alien.setFleetXSpeed(2);

		// create and initialise the player's spaceship
		icon = new ImageIcon(workingDirectory + "//player_ship.png");
		Image shipImage = icon.getImage();
		PlayerShip = new Spaceship(shipImage);
		PlayerShip.setPosition(300,560);

		icon = new ImageIcon(workingDirectory + "//bullet.png");
		bulletImage = icon.getImage();

		// tell all sprites the window width
		Sprite2D.setWinWidth(WindowSize.width);

	}

	// application's paint method
	public void paint(Graphics g) {
		if(gameState) {
			if (!isInitialised)
				return;

			g = offscreenGraphics;

			// clear the canvas with a big black rectangle
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WindowSize.width, WindowSize.height);
			g.setColor(Color.WHITE);
			g.drawString("Score:" + score, 275, 50);
			g.drawString("Best:" + best_Score, 450, 50);

			// redraw all game objects
			for (int i = 0; i < NUMALIENS; i++)
				AliensArray[i].paint(g);

			PlayerShip.paint(g);
			for (int i = 0; i < bulletList.size(); i++) {
				bulletList.get(i).paint(g);
			}
		}
		else{
			g = offscreenGraphics;

			// clear the canvas with a big black rectangle
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WindowSize.width, WindowSize.height);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Ariel", Font.PLAIN, 50));
			g.drawString("Space Invaders!", 200, 150);
			g.setFont(new Font("Ariel", Font.PLAIN, 25));
			g.drawString("Press any key to play", 275, 250);
			g.drawString("[Arrow keys to move, Space to fire]", 200, 300);

		}

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

