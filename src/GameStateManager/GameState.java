package GameStateManager;

import java.awt.Graphics2D;

public abstract class GameState {
	
	public abstract void initialize(GameStateManager gsm);
	
	public abstract void update();
	
	public abstract void draw(Graphics2D g);
	
	public abstract void keyPressed(int k);
	
	public abstract void keyReleased(int k);
	
}
