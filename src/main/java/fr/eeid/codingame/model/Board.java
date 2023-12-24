package fr.eeid.codingame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.eeid.codingame.io.InputTracer;

public class Board {
	// Given at startup
	private final int creatureCount;
	private Map<Integer, Creature> creatures = new HashMap<>();

	// Updated each turn
	private int myScore;
	private int foeScore;
	
	private Map<Integer, Creature> myScannedcreatures = new HashMap<>();
	private Map<Integer, Creature> myUnscannedcreatures = new HashMap<>();
	
	private Map<Integer, Creature> foeScannedcreatures = new HashMap<>();
	private Map<Integer, Creature> foeUnscannedcreatures = new HashMap<>();
	
	private Map<Integer, Drone> myDrones = new HashMap<>();
	
	private Map<Integer, Drone> foeDrones = new HashMap<>();

	public Board(InputTracer input) {
		this.creatureCount = input.nextLineAsSingleInt();
		for (int i = 0; i < creatureCount; i++) {
			int[] line = input.nextLineAsInts();
			int creatureId = line[0];
			Creature creature = new Creature(
					creatureId,
					line[1],
					line[2]);
			this.creatures.put(creatureId, creature);
			this.myUnscannedcreatures.put(creatureId, creature);
			this.foeUnscannedcreatures.put(creatureId, creature);
		}
	}

	public void update(InputTracer input) {
		this.myScore = input.nextLineAsSingleInt();
		this.foeScore = input.nextLineAsSingleInt();
		
		int myScanCount = input.nextLineAsSingleInt();
		for (int i = 0; i < myScanCount; i++) {
			int creatureId = input.nextLineAsSingleInt();
			Creature creature = myUnscannedcreatures.remove(creatureId);
			myScannedcreatures.put(creatureId, creature);
		}
		
		int foeScanCount = input.nextLineAsSingleInt();
		
		for (int i = 0; i < foeScanCount; i++) {
			int creatureId = input.nextLineAsSingleInt();
			Creature creature = foeUnscannedcreatures.remove(creatureId);
			foeScannedcreatures.put(creatureId, creature);
		}
		
		int myDroneCount = input.nextLineAsSingleInt();
		for (int i = 0; i < myDroneCount; i++) {
			int[] line = input.nextLineAsInts();
			int droneId = line[0];
			Drone myDrone = myDrones.get(droneId);
			if (myDrone == null) {
				myDrone = new Drone(
						droneId,
						line[1],
						line[2],
						line[3],
						line[4]);
				myDrones.put(droneId, myDrone);
			} else {
				myDrone.update(line[1],
						line[2],
						line[3],
						line[4]);
			}
		}

		int foeDroneCount = input.nextLineAsSingleInt();
		for (int i = 0; i < foeDroneCount; i++) {
			int[] line = input.nextLineAsInts();
			int droneId = line[0];
			Drone myDrone = foeDrones.get(droneId);
			if (myDrone == null) {
				myDrone = new Drone(
						droneId,
						line[1],
						line[2],
						line[3],
						line[4]);
				foeDrones.put(droneId, myDrone);
			} else {
				myDrone.update(line[1],
						line[2],
						line[3],
						line[4]);
			}
		}
		
		int droneScanCount = input.nextLineAsSingleInt();
        for (int i = 0; i < droneScanCount; i++) {
        	int[] line = input.nextLineAsInts();
            int droneId = line[0];
            int creatureId = line[1];
        }
		
		int visibleCreatureCount = input.nextLineAsSingleInt();
        for (int i = 0; i < visibleCreatureCount; i++) {
        	int[] line = input.nextLineAsInts();
        	
            int creatureId = line[0];
            Creature creature = creatures.get(creatureId);
            
            int creatureX = line[1];
            int creatureY = line[2];
            int creatureVx = line[3];
            int creatureVy = line[4];
            creature.updatePosition(creatureX, creatureY, creatureVx, creatureVy);
        }
        
        int radarBlipCount = input.nextLineAsSingleInt();
        for (int i = 0; i < radarBlipCount; i++) {
        	String[] line = input.nextLine();
            int droneId = Integer.parseInt(line[0]);
            int creatureId = Integer.parseInt(line[1]);
            String radar = line[2];
        }
		
	}

	public String getAction() {
		Drone myDrone = myDrones.values().iterator().next();
		Creature creature = myDrone.getClosest(myUnscannedcreatures.values());
		String action = "MOVE " + creature.getX() + " " + creature.getY() + " 1";
		return action;
	}
}
