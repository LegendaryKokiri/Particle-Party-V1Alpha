package Entities;

import Main.Engine;
import Toolbox.Circle;
import Toolbox.Utility;
import Toolbox.Vector2d;

public class Particle extends Entity {

	public Vector2d velocity;
	
	public Particle(Vector2d position, Vector2d velocity, Circle cCircle, int ID) {
		super(position, cCircle, ID);
		this.velocity = velocity;
	}
	
	public void update() {
		position.x += velocity.x;
		position.y += velocity.y;
		
		if(position.x + cCircle.radius >= Engine.WIDTH - 10) velocity.x = Utility.sign(velocity.x, Utility.NEGATIVE);
		else if(position.x - cCircle.radius <= 10) velocity.x = Utility.sign(velocity.x, Utility.POSITIVE);
		
		if(position.y + cCircle.radius >= Engine.HEIGHT - 10) velocity.y = Utility.sign(velocity.y, Utility.NEGATIVE);
		else if(position.y - cCircle.radius <= 10) velocity.y = Utility.sign(velocity.y, Utility.POSITIVE);
	}

}
