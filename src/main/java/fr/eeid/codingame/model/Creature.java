package fr.eeid.codingame.model;

import java.util.ArrayList;
import java.util.List;

public class Creature {

	private final int id;
	private final int color;
	private final int type;
	private final List<ApproximativePosition> approximativePositions = new ArrayList<>();
	
	private Vector pos;
	private Vector speed;
	private boolean visible = false;
	private Drone myClosestDrone;
	
	
	public Creature(int id, int color, int type) {
		this.id = id;
		this.color = color; // (0 à 3)
		this.type = type; // (0 à 2)
		this.approximativePositions.add(new ApproximativePosition(type));
	}

	public void updatePosition(int x, int y, int vx, int vy) {
		this.pos = new Vector(x, y);
		this.speed = new Vector(vx, vy);
		visible = true;
	}

	public int getId() {
		return id;
	}

	public int getColor() {
		return color;
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

	public void updatePosMinMax(int turn, Drone drone, String radar, Creature symetricCreature) {
		ApproximativePosition approximativePosition = last(turn);
		Vector dronePos = drone.getPos();
		approximativePosition.updateApproximativePosition(dronePos, radar);
		approximativePosition.putRadarDrone(drone, radar);
		
		if (symetricCreature != null && turn < (type+1)*3) {
			ApproximativePosition symetricApproximativePosition = symetricCreature.last(turn);
			Vector symmetricDronePos = dronePos.hsymmetric(Board.CENTER.getX());
			String symmetricRadar = symmetric(radar);
			symetricApproximativePosition.updateApproximativePosition(symmetricDronePos, symmetricRadar);
			
		}
	}

	public ApproximativePosition last(int turn) {
		ApproximativePosition approximativePosition = null;
		if (approximativePositions.size() < turn ) {
			approximativePosition = new ApproximativePosition(type);
			approximativePositions.add(approximativePosition);
		} else {
			approximativePosition = approximativePositions.get(turn-1);
		}
		return approximativePosition;
	}

	private String symmetric(String radar) {
		if ("TL".equals(radar)) {
			return "TR";
		} else if ("TR".equals(radar)) {
			return "TL";
		} else if ("BR".equals(radar)) {
			return "BL";
		} else { // BL
			return "BR";
		}
	}

	public void assignMyClosestDrone(List<Drone> myDrones) {
		Vector pos = getApproximativePosition();
		Vector posDrone1 = myDrones.get(0).getPos();
		Vector posDrone2 = myDrones.get(1).getPos();
		if (pos.distance(posDrone1) < pos.distance(posDrone2)) {
			myClosestDrone = myDrones.get(0);
			return;
		}
		myClosestDrone = myDrones.get(1);
	}

	public Vector getApproximativePosition() {
		return approximativePositions.get(approximativePositions.size()-1).getPos();
	}

	public Drone getMyClosestDrone() {
		return myClosestDrone;
	}

}
