package GameStates;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import GameStateManager.GameState;
import GameStateManager.GameStateManager;
import Main.Engine;
import Toolbox.Globals;

public class MenuState extends GameState {

	private GameStateManager gsm;
	
	private String[] options;
	private String[] playerTwo;
	private String[] playerTwoLevel;
	private int selection;
	private int playerTwoSelection;
	private int playerTwoLevelSelection;
	
	public void initialize(GameStateManager gsm) {
		this.gsm = gsm;
		
		options = new String[]{"Survival", "Frenzy", "Quit"};
		playerTwo = new String[]{"Human Player -->", "<-- Computer Player"};
		playerTwoLevel = new String[]{"Stupid -->", "<-- Stupidly Hard"};
		selection = 0;
		playerTwoSelection = 0;
		playerTwoLevelSelection = 0;
	}

	public void update() {
		
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Engine.WIDTH, Engine.HEIGHT); //Background
		
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, Engine.WIDTH, 10); //Top Border
		g.fillRect(0, 0, 10, Engine.HEIGHT); //Left Border
		g.fillRect(0, Engine.HEIGHT - 10, Engine.WIDTH, 10); //Bottom Border
		g.fillRect(Engine.WIDTH - 10, 0, 10, Engine.HEIGHT); //Right Border
		
		//Menu Options
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int stringWidth = fm.stringWidth("Select a game mode.");
		
		g.setColor(Color.WHITE);
		g.drawString("Select a game mode.", Engine.WIDTH / 2 - stringWidth / 2, 20);
		for(int i = 0; i < options.length; i++) {
			if(selection == i) g.setColor(Color.YELLOW);
			else g.setColor(Color.WHITE);
			
			stringWidth = fm.stringWidth(options[i]);
			g.drawString(options[i], Engine.WIDTH / 2 - stringWidth / 2, 40 + i * 20);
		}
		g.setColor(Color.WHITE);
		
		if(selection == 3) g.setColor(Color.YELLOW);
		stringWidth = fm.stringWidth("Do you want to play against another person or against the computer?");
		g.drawString("Do you want to play against another person or against the computer?",
				Engine.WIDTH / 2 - stringWidth / 2, 60 + options.length * 20);
		stringWidth = fm.stringWidth(playerTwo[playerTwoSelection]);
		g.drawString(playerTwo[playerTwoSelection], Engine.WIDTH / 2 - stringWidth / 2, 80 + options.length * 20);
		
		g.setColor(Color.WHITE);
		if(selection == 4) g.setColor(Color.YELLOW);
		stringWidth = fm.stringWidth("Set the computer player's difficulty.");
		g.drawString("Set the computer player's difficulty.",
				Engine.WIDTH / 2 - stringWidth / 2, 100 + options.length * 20);
		stringWidth = fm.stringWidth(playerTwoLevel[playerTwoLevelSelection]);
		g.drawString(playerTwoLevel[playerTwoLevelSelection], Engine.WIDTH / 2 - stringWidth / 2, 120 + options.length * 20);
	}

	public void keyPressed(int k) {
		if(k == KeyEvent.VK_UP) {
			selection--;
			if(selection < 0) selection = 0;
		} else if(k == KeyEvent.VK_DOWN) {
			selection++;
			if(selection > options.length - 1 + 2 /*Plus two for CPU settings*/) selection = options.length - 1;
		}
		
		if(k == KeyEvent.VK_LEFT) {
			if(selection == 3) playerTwoSelection = 0;
			else if(selection == 4) {
				playerTwoLevelSelection--;
				if(playerTwoLevelSelection < 0) playerTwoLevelSelection = 0;
			}
		} else if(k == KeyEvent.VK_RIGHT) {
			if(selection == 3)  playerTwoSelection = 1;
			else if(selection == 4) {
				playerTwoLevelSelection++;
				if(playerTwoLevelSelection > playerTwoLevel.length - 1) playerTwoLevelSelection = playerTwoLevel.length - 1;
			}
		}
		
		
		if(k == KeyEvent.VK_ENTER) {
			if(selection == 0) {
				Globals.GAME_MODE = Globals.SURVIVAL;
				Globals.PLAYER_TWO = playerTwoSelection;
				Globals.CPU_LEVEL = playerTwoLevelSelection;
				gsm.requestStateChange(new PlayState());
			} else if(selection == 1) {
				System.out.println("State Change Requested (Frezny)");
				Globals.GAME_MODE = Globals.FRENZY;
				Globals.PLAYER_TWO = playerTwoSelection;
				Globals.CPU_LEVEL = playerTwoLevelSelection;
				gsm.requestStateChange(new PlayState());
			} else if(selection == 2) {
				System.exit(0);
			}
		}
	}

	public void keyReleased(int k) {
		
	}
	
}
