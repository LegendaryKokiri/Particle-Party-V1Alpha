package Entities;

import java.util.Random;

import Toolbox.Circle;
import Toolbox.Globals;
import Toolbox.Vector2d;

public class Power_Up extends Entity {

	public int effect;
	
	public Power_Up(Vector2d position, Circle cCircle) {
		super(position, cCircle, -1);
		Random random = new Random();
		effect = random.nextInt(Globals.NUM_EFFECTS);
	}
	
	public void update() {
		
	}

}
