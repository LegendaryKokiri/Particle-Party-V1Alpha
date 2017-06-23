package GameStates;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import Entities.Particle;
import Entities.Player;
import Entities.Power_Up;
import GameStateManager.GameState;
import GameStateManager.GameStateManager;
import Main.Engine;
import Toolbox.Circle;
import Toolbox.ControlScheme;
import Toolbox.Globals;
import Toolbox.Utility;
import Toolbox.Vector2d;

public class PlayState extends GameState {

	private GameStateManager gsm;
	
	private List<Player> players;
	private Player player1;
	private Player player2;
	
	private List<Particle> particles1, particles2;
	private Vector2d[] velocities; //The possible velocities for particles
	
	private List<Power_Up> powerups;
	private Color gold;
	
	private Color[] playerColors;
	
	private boolean beginGame;
	private boolean paused;
	
	private long startTime;
	private float time;
	private int powerUpTime;
	
	public void initialize(GameStateManager gsm) {
		System.out.println("Beginning initialization.");
		this.gsm = gsm;
		
		players = new ArrayList<Player>();
		particles1 = new ArrayList<Particle>();
		
		particles2 = new ArrayList<Particle>();
		powerups = new ArrayList<Power_Up>();
		
		player2 = new Player(new Vector2d(Engine.WIDTH * 3 / 4, Engine.HEIGHT / 2), new Circle(10), 
				new ControlScheme(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT), 1);
		
		player2.AI = (Globals.PLAYER_TWO == Globals.CPU);

		if(!player2.AI) player1 = new Player(new Vector2d(Engine.WIDTH / 4, Engine.HEIGHT / 2), new Circle(10), 
				new ControlScheme(KeyEvent.VK_E, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_F), 0);
		else player1 = new Player(new Vector2d(Engine.WIDTH / 4, Engine.HEIGHT / 2), new Circle(10), 
				new ControlScheme(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT), 0);
		
		playerColors = new Color[]{Color.GREEN, Color.RED};
		
		players.add(player1);
		players.add(player2);
		
		velocities = new Vector2d[]{new Vector2d(1, 0), new Vector2d(0.7f, 0.7f), new Vector2d(0, 1), new Vector2d(-0.7f, 0.7f),
				new Vector2d(-1, 0), new Vector2d(-0.707f, -0.707f), new Vector2d(0, -1), new Vector2d(0.707f, -0.707f)};
		
		resetField();
		System.out.println("Reset Field.");
		
		gold = new Color(219, 203, 28);
		
		beginGame = false;
		paused = false;
		System.out.println("Completed initialization.");
	}

	public void update() {
		if(!beginGame) return;
		if(paused) return;
		
		Player p1 = players.get(0);
		Player p2 = players.get(1);
		
		List<Particle> aiParticles = new ArrayList<Particle>();
		
		time = (float) ((System.nanoTime() - startTime) / 1e9);
		if((int) time / 10 > powerUpTime) {
			powerUpTime = (int) (time / 10);
			Power_Up power = new Power_Up(Utility.midPoint(players.get(0).getPosition(), players.get(1).getPosition()),
					new Circle(8));
			powerups.add(power);
		}
		
		for(int i = 0; i < particles1.size(); i++) {
			particles1.get(i).update();
		}
		
		for(int i = 0; i < particles2.size(); i++) {
			particles2.get(i).update();
		}
		
		Vector2d p1v = p1.getPosition();
		float radius1 = p1.getCCircle().radius;
		
		for(int i = 0; i < particles2.size(); i++) {
			Vector2d part = particles2.get(i).getPosition();
			float prad = particles2.get(i).getCCircle().radius;
			
			if(Math.abs(p1v.x - part.x) > radius1) continue;
			if(Math.abs(p1v.y - part.y) > radius1) continue;
			
			if(Utility.getVectorLength(Utility.subtractVectors(p1v, part)) < radius1 + prad) {
				p1.damage();
				particles2.remove(i);
				i--;
			}
		}
		
		Vector2d p2v = p2.getPosition();
		float radius2 = p2.getCCircle().radius;
		
		for(int i = 0; i < particles1.size(); i++) {
			Vector2d part = particles1.get(i).getPosition();
			float prad = particles1.get(i).getCCircle().radius;
			
			if(p2.AI) {
				if(Math.abs(p2v.x - part.x) <= 20 && Math.abs(p2v.y - part.y) <= 20) aiParticles.add(particles1.get(i));
			}
			
			if(Math.abs(p2v.x - part.x) > radius2) continue;
			if(Math.abs(p2v.y - part.y) > radius2) continue;
			
			if(Utility.getVectorLength(Utility.subtractVectors(p2v, part)) < radius2 + prad) {
				p2.damage();
				particles1.remove(i);
				i--;
			}
		}
		
		for(int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			
			if(p.AI) p.AI(aiParticles, powerups);
			p.update();
			if(p.shoot(time)) {
				int x = (int) ((p.time / p.fireRate) % 8);
				
				Particle part = new Particle(Utility.addVectors(p.getPosition(),
						Utility.setVectorLength(velocities[x], 5)),
						Utility.clone(velocities[x]),
						new Circle(p.particleSize * p.particleEffectLevel),
						p.ID);
				
				if(p.ID == 0) particles1.add(part);
				else if(p.ID == 1) particles2.add(part);
			}
		}
		
		for(int i = 0; i < powerups.size(); i++) {
			Power_Up power = powerups.get(i);
			Vector2d p = power.getPosition();
			float pRad = power.getCCircle().radius;
			boolean p1Collided = Utility.getVectorLength(Utility.subtractVectors(p1.getPosition(), p)) < radius1 + pRad;
			boolean p2Collided = Utility.getVectorLength(Utility.subtractVectors(p2.getPosition(), p)) < radius1 + pRad;
			
			if(p1Collided && !p2Collided) {
				p1.powerUp(power);
				powerups.remove(i);
				i--;
			} else if(!p1Collided && p2Collided) {
				p2.powerUp(power);
				powerups.remove(i);
				i--;
			} else if(p1Collided && p2Collided) {
				//No one gets it if both hit it at the same time
				powerups.remove(i);
				i--;
			}
		}
		
		if((!players.get(0).alive && players.get(1).alive) || (players.get(0).alive && !players.get(1).alive)) {
			resetField();
		}
	}
	
	private void resetField() {
		beginGame = false;
		particles1.clear();
		particles2.clear();
		powerups.clear();
		
		if(players.get(0).health > players.get(1).health) players.get(0).wins++;
		else if(players.get(0).health < players.get(1).health) players.get(1).wins++;
		
		if(Globals.GAME_MODE == Globals.FRENZY) {
			player1.resetStats(100);
			player2.resetStats(100);
		} else if(Globals.GAME_MODE == Globals.SURVIVAL) {
			player1.resetStats(1);
			player2.resetStats(1);
		}
		
		time = 0f;
		powerUpTime = 0;
		
		player1.setPosition(new Vector2d(Engine.WIDTH / 4, Engine.HEIGHT / 2));
		
		player2.setPosition(new Vector2d(Engine.WIDTH * 3 / 4, Engine.HEIGHT / 2));
	}

	public void draw(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics(g.getFont());
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT); //Background
		
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, Engine.WIDTH, 10); //Top Border
		g.fillRect(0, 0, 10, Engine.HEIGHT); //Left Border
		g.fillRect(0, Engine.HEIGHT - 10, Engine.WIDTH, 10); //Bottom Border
		g.fillRect(Engine.WIDTH - 10, 0, 10, Engine.HEIGHT); //Right Border
		
		//Score
		if(!beginGame) {
			g.setColor(Color.GREEN);			
			g.drawString("Green Health: " + Integer.toString(players.get(0).health), 11, 21);
			g.drawString("Green Wins: " + Integer.toString(players.get(0).wins), 11, 36);
			
			g.setColor(Color.RED);
			g.drawString("Red Health: " + Integer.toString(players.get(1).health), 11, 51);
			g.drawString("Red Wins: " + Integer.toString(players.get(1).wins), 11, 66);
		} else {
			g.setColor(Color.GREEN);
			g.drawString(Integer.toString(players.get(0).health), 11, 21);
			
			int redHealthWidth = fm.stringWidth(Integer.toString(players.get(1).health));
			g.setColor(Color.RED);
			g.drawString(Integer.toString(players.get(1).health), Engine.WIDTH - 10 - redHealthWidth, 21);
		}
		
		if(paused) {
			g.setColor(Color.WHITE);
			g.drawString("PAUSED", Engine.WIDTH / 2 - (fm.stringWidth("PAUSED") / 2), Engine.HEIGHT / 2);
			return;
		}
		
		//Entities
		for(int i = 0; i < players.size(); i++) {
			g.setColor(playerColors[players.get(i).ID]);
			players.get(i).draw(g);
		}
		
		g.setColor(playerColors[0]);
		for(int i = 0; i < particles1.size(); i++) {
			particles1.get(i).draw(g);
		}
		
		g.setColor(playerColors[1]);
		for(int i = 0; i < particles2.size(); i++) {
			particles2.get(i).draw(g);
		}
		
		g.setColor(gold);
		for(int i = 0; i < powerups.size(); i++) {
			powerups.get(i).draw(g);
		}
	}

	public void keyPressed(int k) {
		if(!beginGame) {
			beginGame = k == KeyEvent.VK_ENTER;
			if(beginGame) {
				startTime = System.nanoTime();
				player1.setTime(0);
				player2.setTime(0);
				return;
			}
			
			if(k == KeyEvent.VK_ESCAPE) gsm.setState(new MenuState());
		}
		
		if(beginGame && k == KeyEvent.VK_SPACE) {
			paused = !paused;
		}
		
		for(int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			ControlScheme c = p.controls;
			
			if(k == c.upKey) p.setUp(true);
			if(k == c.downKey) p.setDown(true);
			
			if(k == c.leftKey) p.setLeft(true);
			if(k == c.rightKey) p.setRight(true);
		}
	}

	public void keyReleased(int k) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).AI) continue;
			Player p = players.get(i);
			ControlScheme c = p.controls;
			
			if(k == c.upKey) p.setUp(false);
			if(k == c.downKey) p.setDown(false);
			
			if(k == c.leftKey) p.setLeft(false);
			if(k == c.rightKey) p.setRight(false);
		}
	}
	
}
