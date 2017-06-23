package Main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import GameStateManager.GameStateManager;
import GameStates.*;

public class Engine extends JPanel implements Runnable, KeyListener{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 480;
	public static final int HEIGHT = 360;
	public static final int SCALE = 2;
	
	private Thread thread;
	private boolean running;
	
	public static final int FPS = 60;
	public static long frameTime;
	private final long targetTime = (long) (1_000_000_000 / FPS);
	
	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager gsm;
	
	List<Integer> pressedKeys;
	
	public Engine() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
		
		frameTime = targetTime;
		
		gsm = new GameStateManager();
		pressedKeys = new ArrayList<>();
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	public void run() {
		initialize();
		
		while(running) {
			long startTime = System.nanoTime();
			
		
			if(gsm.stateChangeRequested) {
				gsm.setState(gsm.requestedState);
				continue;
			}
			
			update();
			draw();
			drawToScreen();
			
			frameTime = System.nanoTime() - startTime;
			
			while(frameTime < targetTime) {
				frameTime = System.nanoTime() - startTime;
			}
		}
	}
	
	private void initialize() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = (Graphics2D) image.createGraphics();
		
		running = true;
		
		gsm.setState(new MenuState());
	}
	
	private void update() {
		gsm.getGameState().update();
	}
	
	private void draw() {
		gsm.getGameState().draw(g);
	}
	
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
	}

	public void keyPressed(KeyEvent key) {

		gsm.getGameState().keyPressed(key.getKeyCode());
	}

	public void keyReleased(KeyEvent key) {
		gsm.getGameState().keyReleased(key.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {}
	
}
