package fr.eeid.codingame.model;

public class Creature {

	private final int id;
	private final int color;
	private final int type;
	
	private Vector pos;
	private Vector speed;
	private boolean visible = false;
	
	public Creature(int id, int color, int type) {
		this.id = id;
		this.color = color; // (0 à 3)
		this.type = type; // (0 à 2)
	}

	public void updatePosition(int x, int y, int vx, int vy) {
		this.pos = new Vector(x, y);
		this.speed = new Vector(vx, vy);
		visible = true;
	}

	public int getId() {
		return id;
	}

	public Vector getPos() {
		return pos;
	}

	public Vector getSpeed() {
		return speed;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setInvisible() {
		visible = false;
	}

}
