package fr.eeid.codingame.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drone {

	private final int id;
	private int droneX;
	private int droneY;
	private int emergency;
	private int battery;
	
	private DroneStrategy strategy = DroneStrategy.SURFACE;
	private StartLeftRight leftRight = StartLeftRight.LEFT;
	private Set<Integer> scanUnsavedCreatureIds = new HashSet<>();
	private final Map<Integer, String> creaturesRadarPositions = new HashMap<>();
	
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
		scanUnsavedCreatureIds = new HashSet<>();
	}
	
	public void addScanUnsaved(int creatureId) {
		scanUnsavedCreatureIds.add(creatureId);
	}
	
	public void updateRadar(int creatureId, String radar) {
		creaturesRadarPositions.put(creatureId, radar);
	}
	
	public void updateStrategy(Map<DroneStrategy, List<Integer>> creatureTypeIds, Map<Integer, Creature> myScannedcreaturescreatureTypeIds) {
		if (strategy == DroneStrategy.SURFACE && scanUnsavedCreatureIds.isEmpty()) {
			leftRight = StartLeftRight.LEFT;
			if (droneX > 5000) {
				leftRight = StartLeftRight.RIGHT;
			}
			strategy = DroneStrategy.TYPE2;
			for (Integer creatureId : creatureTypeIds.get(DroneStrategy.TYPE2)) {
				if (myScannedcreaturescreatureTypeIds.get(creatureId) == null) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE1;
			for (Integer creatureId : creatureTypeIds.get(DroneStrategy.TYPE1)) {
				if (myScannedcreaturescreatureTypeIds.get(creatureId) == null) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE0;
		}
	}

	public int getId() {
		return id;
	}

	public String getAction(Map<DroneStrategy, List<Integer>> creatureTypeIds, Set<Integer> myScanUnsavedCreatureIds, Map<Integer, Creature> myScannedcreatures) {
		if (strategy == DroneStrategy.SURFACE) {
			return "MOVE " + droneX + " 500 0";
		}
		List<Integer> unscannedCreatureTypeIds = creatureTypeIds.get(strategy).stream()
				.filter(creatureType0Id -> 
					!myScanUnsavedCreatureIds.contains(creatureType0Id)
					&& !myScannedcreatures.containsKey(creatureType0Id)
					)
				.toList();
		if (unscannedCreatureTypeIds.isEmpty()) {
			strategy = DroneStrategy.SURFACE;
			return "MOVE " + droneX + " 500 0";
		}
		
		int moveY = strategy.getY(droneY);
		int moveX = moveX(moveY, unscannedCreatureTypeIds);
		int light = droneY >= (moveY - 2000) ? 1 : 0;
		return "MOVE " + moveX + " " + moveY + " " + light;
	}

	private int moveX(int moveY, List<Integer> unscannedCreatureType0Ids) {
		if (droneY <= (moveY - 2000)) {
			return droneX;
		}
		if (leftRight == StartLeftRight.LEFT) {
			for (Integer unscannedCreatureType0Id : unscannedCreatureType0Ids) {
				String radarPosition = creaturesRadarPositions.get(unscannedCreatureType0Id);
				if ("TL".equals(radarPosition) || "BL".equals(radarPosition)) {
					return 0;
				}
			}
			return 9999;
		}
		for (Integer unscannedCreatureType0Id : unscannedCreatureType0Ids) {
			String radarPosition = creaturesRadarPositions.get(unscannedCreatureType0Id);
			if ("TR".equals(radarPosition) || "BR".equals(radarPosition)) {
				return 9999;
			}
		}
		return 0;
	}
	
}
