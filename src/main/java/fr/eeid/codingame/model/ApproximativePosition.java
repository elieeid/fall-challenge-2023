package fr.eeid.codingame.model;

import java.util.HashMap;
import java.util.Map;

public class ApproximativePosition {
	
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	
	private Map<Drone, String> radarDrone = new HashMap<>();
	
	public ApproximativePosition(int type) {
		this.xMin = 0;
		this.xMax = 9999;
		this.yMin = yMin(type);
		this.yMax = yMax(type);
	}

	private double yMin(int type) {
		if (type == 0) {
			return 2500;
		}
		if (type == 1) {
			return 5000;
		}
		return 7500;
	}

	private double yMax(int type) {
		if (type == 0) {
			return 5000;
		}
		if (type == 1) {
			return 7500;
		}
		return 10000;
	}
	
	public void putRadarDrone(Drone drone, String radar) {
		radarDrone.put(drone, radar);
	}

	public void updateApproximativePosition(Vector pos, String radar) {
		double x = pos.getX();
		double y = pos.getY();
		if ("TL".equals(radar)) {
			xMax = Math.min(xMax, x);
			yMax = Math.min(yMax, y);
		} else if ("TR".equals(radar)) {
			xMin = Math.max(xMin, x);
			yMax = Math.min(yMax, y);
		} else if ("BR".equals(radar)) {
			xMin = Math.max(xMin, x);
			yMin = Math.max(yMin, y);
		} else { // BL
			xMax = Math.min(xMax, x);
			yMin = Math.max(yMin, y);
		}
	}

	public Vector getPos() {
		return new Vector((xMin + xMax) / 2, (yMin + yMax) / 2);
	}
	
}
