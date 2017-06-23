package Entities;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import Main.Engine;
import Toolbox.Circle;
import Toolbox.ControlScheme;
import Toolbox.Globals;
import Toolbox.Utility;
import Toolbox.Vector2d;

public class Player extends Entity {
	public boolean AI;
	
	private final float MOVEMENT_SPEED = 240f;
	public float movementSpeedLevel;
	
	public ControlScheme controls;
	public boolean alive;
	public int health;
	public int wins;
	
	public float time;
	public float fireRate;
	public int fireRateLevel;
	
	public int particleSize;
	public int particleEffectLevel;
	
	private Color translucentWhite;
	
	public Player(Vector2d position, Circle cCircle, ControlScheme controls, int ID) {
		super(position, cCircle, ID);
		
		AI = false;
		
		this.controls = controls;
		alive = true;
		health = 1;
		wins = 0;
		
		time = 0.0f;
		fireRate = 1.0f;
		particleSize = 3;
		
		movementSpeedLevel = fireRateLevel = particleEffectLevel = 1;
		
		translucentWhite = new Color(255, 255, 255, 100);
	}
	
	public void damage() {
		health--;
		alive = health > 0;
	}
	
	public void AI(List<Particle> particles, List<Power_Up> powerups) {		
		if(particles.size() == 0) {
			if(powerups.size() == 0) {
				left = right = up = down = false;
				return;
			} else {
				if(Globals.CPU_LEVEL == Globals.STUPID) return; //Stupid CPUs won't go for power ups.
				
				int closestPowerUpIndex = 0; float distance = Float.MAX_VALUE;
				for(int i = 1; i < powerups.size(); i++) {
					Vector2d toPowerUp = Utility.subtractVectors(powerups.get(i).position , this.position);
					float currentDistance = Utility.getVectorLength(toPowerUp);
					if(currentDistance < distance) {
						distance = currentDistance;
						closestPowerUpIndex = i;
					}
				}
				
				Vector2d targetPosition = powerups.get(closestPowerUpIndex).position;
				right = targetPosition.x - this.position.x > MOVEMENT_SPEED * movementSpeedLevel / Engine.FPS;
				left = targetPosition.x - this.position.x < -MOVEMENT_SPEED * movementSpeedLevel / Engine.FPS;
				down = targetPosition.y - this.position.y > MOVEMENT_SPEED * movementSpeedLevel / Engine.FPS;
				up = targetPosition.y - this.position.y < -MOVEMENT_SPEED * movementSpeedLevel / Engine.FPS;
			}
		} else if(particles.size() == 1) {
			Vector2d particlePosition = particles.get(0).position;
			Vector2d particleVelocity = particles.get(0).velocity;
			
			float direction = Utility.dotProduct(
					Utility.subtractVectors(this.position, particlePosition), particleVelocity);
			
			if(direction >= 0.85f) {
				if(Math.abs(particleVelocity.x) > Math.abs(particleVelocity.y)) {
					if(!up && !down) {//We only do this if there is no vertical motion so it won't suddenly reverse direction
						if(position.y < (Engine.HEIGHT - 10) - position.y) {up = false; down = true;}
						else {up = true; down = false;}
					}
				} else if(Math.abs(particleVelocity.x) < Math.abs(particleVelocity.y)) {
					if(!left && !right) {//We only do this if there is no horizontal motion so it won't suddenly reverse direction
						if(position.x < (Engine.WIDTH - 10) - position.x) {right = true; left = false;}
						else {left = true; right = false;}
					}
				} else { //Components equal
					left = right = up = down = false;
					if(position.x < particlePosition.x && position.y < particlePosition.y) {
						//Go up or left
						right = false; down = false;
						up = position.y > position.x; left = !up;
					} else if(position.x > particlePosition.x && position.y < particlePosition.y) {
						//Go up or right
						left = false; down = false;
						up = position.y > Engine.WIDTH - 10 - position.x; right = !up;
					} else if(position.x < particlePosition.x && position.y > particlePosition.y) {
						//Go down or left
						right = false; up = false;
						up = position.y > position.x; left = !up;
					} else if(position.x > particlePosition.x && position.y > particlePosition.y) {
						//Go down or right
						left = false; down = false;
						right = Engine.WIDTH - 10 - position.x > position.y; up = !right;
					}
				}
			}
		} else {
			if(Globals.CPU_LEVEL == Globals.STUPID) {
				up = down = left = down = false; //Stupid CPUs get flustered when there are multiple particles nearby.
				return;
			}
			
			float[] distances = new float[particles.size()];
			int closestParticleIndex = 0;
			
			List<Particle> approaching = new ArrayList<Particle>();
			List<Particle> limiting = new ArrayList<Particle>();
						
			for(int i = 0; i < particles.size(); i++) {
				Particle p = particles.get(i);
				
				distances[i] = Utility.getVectorLength(Utility.subtractVectors(p.position, this.position));
				if(distances[closestParticleIndex] > distances[i]) closestParticleIndex = i;
				
				float direction = Utility.dotProduct(
						Utility.subtractVectors(this.position, p.position), p.velocity);
				
				if(direction >= 0.85f) approaching.add(p);
				else if(direction >= 0) limiting.add(p);
			}
			
			boolean leftOption, rightOption, upOption, downOption;
			leftOption = rightOption = upOption = downOption = true;
			
			boolean leftLimited, rightLimited, upLimited, downLimited;
			leftLimited = rightLimited = upLimited = downLimited = false;
			
			for(int i = 0; i < approaching.size(); i++) {
				Particle p = approaching.get(i);
				Vector2d relativePosition = Utility.subtractVectors(p.position, position);
				
				if(Math.abs(relativePosition.x) > Math.abs(relativePosition.y)) {
					if(relativePosition.x > 0) rightOption = false;
					else leftOption = false;
				} else if(Math.abs(relativePosition.y) > Math.abs(relativePosition.x)) {
					if(relativePosition.y > 0) downOption = false;
					else upOption = false;
				} else {
					if(relativePosition.x > 0) rightOption = false;
					else leftOption = false;
					
					if(relativePosition.y > 0) downOption = false;
					else upOption = false;
				}
			}
			
			for(int i = 0; i < limiting.size(); i++) {
				Particle p = limiting.get(i);
				Vector2d relativePosition = Utility.subtractVectors(p.position, position);
				
				if(relativePosition.y > 0) downLimited = true;
				else if(relativePosition.y < 0) upLimited = true;
				
				if(relativePosition.x > 0) rightLimited = true;
				else if(relativePosition.x < 0) leftLimited = true;
			}
			
			if(upOption && !upLimited) up = true; else up = false;
			if(downOption && !downLimited) up = true; else up = false;
			if(leftOption && !leftLimited) up = true; else up = false;
			if(rightOption && !rightLimited) up = true; else up = false;
		}
	}
	
	public void update() {
		float moveSpeed = MOVEMENT_SPEED * movementSpeedLevel;
		
		Vector2d v = new Vector2d(0, 0);
		
		if(left) v.x = -1;
		else if(right) v.x = 1;
		
		if(up) v.y = -1;
		else if(down) v.y = 1;
		
		v = Utility.setVectorLength(v, moveSpeed);
		
		float timeAdjustment = (float) Engine.frameTime / 1_000_000_000;
		
		translate(v.x * timeAdjustment, v.y * timeAdjustment);
		
		if(position.x + cCircle.radius > Engine.WIDTH - 10) position.x = Engine.WIDTH - 10 - cCircle.radius;
		else if(position.x - cCircle.radius < 10) position.x = 10 + cCircle.radius;
		
		if(position.y + cCircle.radius > Engine.HEIGHT - 10) position.y = Engine.HEIGHT - 10 - cCircle.radius;
		else if(position.y - cCircle.radius < 10) position.y = 10 + cCircle.radius;
	}
	
	public void setTime(float time) {
		this.time = time;
	}
	
	public boolean shoot(float time) {
		float elapsedTime = time - this.time;
		boolean shoot = elapsedTime >= (fireRate / fireRateLevel);
		if(shoot) this.time = time;
		return shoot;
	}
	
	public void powerUp(Power_Up p) {
		if(p.effect == Globals.FIRE_RATE_INCREASE) {
			if(fireRateLevel < 3) fireRateLevel++;
			else {
				p.effect = Globals.HEALTH_UP;
				health++;
			}
		} else if(p.effect == Globals.HEALTH_UP) {
			health++;
		} else if(p.effect == Globals.SPEED_UP) {
			if(movementSpeedLevel < 1.1f /*Tolerance for lack of precision in floats*/) movementSpeedLevel += 0.2f;
			else {
				p.effect = Globals.HEALTH_UP;
				health++;
			}
		} else if(p.effect == Globals.PARTICLE_SIZE_INCREASE) {
			if(particleEffectLevel < 2) particleEffectLevel++;
			else {
				p.effect = Globals.HEALTH_UP;
				health++;
			}
		}
	}
	
	public void resetStats(int startingHealth) {
		health = startingHealth;
		alive = true;
		fireRateLevel = 1;
		movementSpeedLevel = 1;
		particleEffectLevel = 1;
	}
	
	public void draw(Graphics2D g) {
		super.draw(g);
		
		Color startColor = g.getColor();
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int numEffects = 0;
		
		if(fireRateLevel > 1) {
			g.setColor(translucentWhite);
			if(fireRateLevel == 2) g.drawString("Fire Rate Up!", position.x - fm.stringWidth("Fire Rate Up!") / 2, position.y - 15);
			else g.drawString("Fire Rate Way Up!", position.x - fm.stringWidth("Fire Rate Way Up!") / 2, position.y - 15);
			numEffects++;
		}
		
		if(movementSpeedLevel > 1) {
			g.setColor(translucentWhite);
			g.drawString("Speed Up!", position.x - fm.stringWidth("Speed Up!") / 2, position.y - 15 - (numEffects * 12));
			numEffects++;
		}
		
		if(particleEffectLevel > 1) {
			g.setColor(translucentWhite);
			g.drawString("Particle Size Up!", position.x - fm.stringWidth("Particle Size Up!") / 2, position.y - 15 - (numEffects * 12));
		}
		
		g.setColor(startColor);
	}
}
