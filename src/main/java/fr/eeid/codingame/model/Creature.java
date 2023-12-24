package fr.eeid.codingame.model;

public class Creature {

	private final int id;
	private final int color;
	private final int type;
	
	private int x;
	private int y;
	private int vx;
	private int vy;
	
	public Creature(int id, int color, int type) {
		this.id = id;
		this.color = color; // (0 à 3)
		this.type = type; // (0 à 2)
	}

	public void updatePosition(int x, int y, int vx, int vy) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
