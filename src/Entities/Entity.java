package Entities;

import java.awt.Graphics2D;

import Toolbox.Circle;
import Toolbox.Vector2d;

public class Entity {
	protected Vector2d position;
	protected Circle cCircle;
	
	protected boolean left, right, up, down;
	
	public int ID;
	
	public Entity(Vector2d position, Circle cCircle, int ID) {
		this.position = position;
		this.cCircle = cCircle;
		this.ID = ID;
	}
	
	public void setPosition(Vector2d position) {this.position = position;}
	public Vector2d getPosition() {return position;}
	public Circle getCCircle() {return cCircle;}
	
	public void update() {
		
	}
	
	public void translate(float x, float y) {
		position.x += x;
		position.y += y;
	}
	
	public void setLeft(boolean b) {left = b;}
	public void setRight(boolean b) {right = b;}
	public void setUp(boolean b) {up = b;}
	public void setDown(boolean b) {down = b;}
	
	public void draw(Graphics2D g) {
		g.fillOval((int) (position.x - cCircle.radius), (int) (position.y - cCircle.radius), 
				(int) cCircle.radius, (int) cCircle.radius);
	}
}
