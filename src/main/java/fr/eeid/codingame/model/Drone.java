package fr.eeid.codingame.model;

import java.awt.geom.Point2D;
import java.util.Collection;

public class Drone {

	private final int id;
	private int droneX;
	private int droneY;
	private int emergency;
	private int battery;
	
	public Drone(int id, int droneX, int droneY, int emergency, int battery) {
		this.id = id;
		this.droneX = droneX;
		this.droneY = droneY;
		this.emergency = emergency;
		this.battery = battery;
	}

	public void update(int droneX, int droneY, int emergency, int battery) {
		this.droneX = droneX;
		this.droneY = droneY;
		this.emergency = emergency;
		this.battery = battery;
	}

	public int getId() {
		return id;
	}

	public Creature getClosest(Collection<Creature> creatures) {
		Creature closestOne = null;
		double closestDistance = 0d;
		for (Creature creature : creatures) {
			if (closestOne == null) {
				closestOne = creature;
				closestDistance = Point2D.distance(droneX, droneY, creature.getX(), creature.getY());
				continue;
			}
			double distance = Point2D.distance(droneX, droneY, creature.getX(), creature.getY());
			if (distance < closestDistance) {
				closestOne = creature;
				closestDistance = distance;
			}
			
		}
		return closestOne;
	}
	
}
