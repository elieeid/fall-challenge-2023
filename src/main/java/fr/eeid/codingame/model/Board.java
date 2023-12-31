package fr.eeid.codingame.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.eeid.codingame.io.InputTracer;

public class Board {
	
	public static final int WIDTH = 10000;
    public static final int HEIGHT = 10000;
	public static final Vector CENTER = new Vector((WIDTH - 1) / 2.0, (HEIGHT - 1) / 2.0);
	
	// Given at startup
	private final int creatureCount;
	private final Map<Integer, Creature> creatures = new HashMap<>();
	private final Map<Integer, List<Creature>> creatureTypes = new HashMap<>();
	private List<Creature> monsters = new ArrayList<>();
	private final Map<Integer, Creature> symetricCreatures = new HashMap<>();

	// Updated each turn
	private int turn = 0;

	private int myScore;
	private int foeScore;
	
	private Map<Integer, Creature> myScannedcreatures = new HashMap<>();
	private Map<Integer, Creature> myUnscannedcreatures = new HashMap<>();
	private Set<Integer> myScanUnsavedCreatureIds = new HashSet<>();
	
	private Map<Integer, Creature> foeScannedcreatures = new HashMap<>();
	private Map<Integer, Creature> foeUnscannedcreatures = new HashMap<>();
	
	private Map<Integer, Drone> myDroneIds = new HashMap<>();
	private List<Drone> myDrones = new ArrayList<>();
	
	private Map<Integer, Drone> foeDrones = new HashMap<>();

	public Board(InputTracer input) {
		this.creatureCount = input.nextLineAsSingleInt();
		List<Creature> creatureType0s = new ArrayList<>();
		List<Creature> creatureType1s = new ArrayList<>();
		List<Creature> creatureType2s = new ArrayList<>();
		for (int i = 0; i < creatureCount; i++) {
			int[] line = input.nextLineAsInts();
			int creatureId = line[0];
			int type = line[2];
			Creature creature = new Creature(
					creatureId,
					line[1],
					type);
			this.creatures.put(creatureId, creature);
			this.myUnscannedcreatures.put(creatureId, creature);
			this.foeUnscannedcreatures.put(creatureId, creature);
			if (type == 0) {
				creatureType0s.add(creature);
			} else if (type == 1) {
				creatureType1s.add(creature);
			} else if (type == 2) {
				creatureType2s.add(creature);
			} else {
				monsters.add(creature);
			}
		}
		creatureType0s.sort(Comparator.comparing(Creature::getColor));
		creatureTypes.put(0, creatureType0s);
		symetricCreatures.put(creatureType0s.get(0).getId(), creatureType0s.get(1));
		symetricCreatures.put(creatureType0s.get(1).getId(), creatureType0s.get(0));
		symetricCreatures.put(creatureType0s.get(2).getId(), creatureType0s.get(3));
		symetricCreatures.put(creatureType0s.get(3).getId(), creatureType0s.get(2));
		
		creatureType1s.sort(Comparator.comparing(Creature::getColor));
		creatureTypes.put(1, creatureType1s);
		symetricCreatures.put(creatureType1s.get(0).getId(), creatureType1s.get(1));
		symetricCreatures.put(creatureType1s.get(1).getId(), creatureType1s.get(0));
		symetricCreatures.put(creatureType1s.get(2).getId(), creatureType1s.get(3));
		symetricCreatures.put(creatureType1s.get(3).getId(), creatureType1s.get(2));
		
		creatureType2s.sort(Comparator.comparing(Creature::getColor));
		creatureTypes.put(2, creatureType2s);
		symetricCreatures.put(creatureType2s.get(0).getId(), creatureType2s.get(1));
		symetricCreatures.put(creatureType2s.get(1).getId(), creatureType2s.get(0));
		symetricCreatures.put(creatureType2s.get(2).getId(), creatureType2s.get(3));
		symetricCreatures.put(creatureType2s.get(3).getId(), creatureType2s.get(2));
		
		creatureTypes.put(-1, monsters);
	}

	public void update(InputTracer input) {
		turn++;
		this.myScore = input.nextLineAsSingleInt();
		this.foeScore = input.nextLineAsSingleInt();
		
		int myScanCount = input.nextLineAsSingleInt();
		for (int i = 0; i < myScanCount; i++) {
			int creatureId = input.nextLineAsSingleInt();
			if (!myScannedcreatures.containsKey(creatureId)) {
				Creature creature = myUnscannedcreatures.remove(creatureId);
				myScannedcreatures.put(creatureId, creature);
			}
		}
		
		int foeScanCount = input.nextLineAsSingleInt();
		
		for (int i = 0; i < foeScanCount; i++) {
			int creatureId = input.nextLineAsSingleInt();
			if (!foeScannedcreatures.containsKey(creatureId)) {
				Creature creature = foeUnscannedcreatures.remove(creatureId);
				foeScannedcreatures.put(creatureId, creature);
			}
		}
		
		int myDroneCount = input.nextLineAsSingleInt();
		for (int i = 0; i < myDroneCount; i++) {
			int[] line = input.nextLineAsInts();
			int droneId = line[0];
			Drone myDrone = myDroneIds.get(droneId);
			if (myDrone == null) {
				myDrone = new Drone(
						droneId,
						line[1],
						line[2],
						line[3],
						line[4]);
				myDroneIds.put(droneId, myDrone);
				myDrones.add(myDrone);
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
		myScanUnsavedCreatureIds = new HashSet<>();
        for (int i = 0; i < droneScanCount; i++) {
        	int[] line = input.nextLineAsInts();
            int droneId = line[0];
            Drone drone = myDroneIds.get(droneId);
            int creatureId = line[1];
            if (drone == null) {
            	drone = foeDrones.get(droneId);
            } else {
            	myScanUnsavedCreatureIds.add(creatureId);
            }
            drone.addScanUnsaved(creatureId);
        }
		
		int visibleCreatureCount = input.nextLineAsSingleInt();
		monsters.forEach(monster -> monster.setInvisible());
        for (int i = 0; i < visibleCreatureCount; i++) {
        	int[] line = input.nextLineAsInts();
        	
            int creatureId = line[0];
            Creature creature = creatures.get(creatureId);
            
            Vector position = new Vector(line[1], line[2]);
            Vector speed = new Vector(line[3], line[4]);
            creature.updatePosition(position, speed);
            if (creature.getColor() >= 0  && turn <= (creature.getType()+1)*3) {
            	Creature symetricCreature = symetricCreatures.get(creature.getId());
            	Vector symmetricPosition = position.hsymmetric(Board.CENTER.getX());
            	symetricCreature.updatePosition(symmetricPosition, speed.hsymmetric());
            }
        }
        
        int radarBlipCount = input.nextLineAsSingleInt();
        Set<Integer> creaturesStillPresent = new HashSet<>();
        for (int i = 0; i < radarBlipCount; i++) {
        	String[] line = input.nextLine();
            int droneId = Integer.parseInt(line[0]);
            Drone drone = myDroneIds.get(droneId);
            if (drone == null) {
            	drone = foeDrones.get(droneId);
            }
            int creatureId = Integer.parseInt(line[1]);
            String radar = line[2];
            creaturesStillPresent.add(creatureId);
            Creature creature = creatures.get(creatureId);
            Creature symetricCreature = symetricCreatures.get(creatureId);
            creature.updatePosMinMax(turn, drone, radar, symetricCreature);
        }
        List<Creature> type0Creatures = creatureTypes.get(0).stream()
        		.filter(creature -> creaturesStillPresent.contains(creature.getId()))
        		.toList();
        creatureTypes.put(0, type0Creatures);
        List<Creature> type1Creatures = creatureTypes.get(1).stream()
        		.filter(creature -> creaturesStillPresent.contains(creature.getId()))
        		.toList();
        creatureTypes.put(1, type1Creatures);
        List<Creature> type2Creatures = creatureTypes.get(2).stream()
        		.filter(creature -> creaturesStillPresent.contains(creature.getId()))
        		.toList();
        creatureTypes.put(2, type2Creatures);
	}

	public List<String> getActions() {
		List<String> actions = new ArrayList<>();
		for (Creature creature : creatures.values()) {
			creature.assignMyClosestDrone(myDrones);
		}
		for (Drone myDrone : myDroneIds.values()) {
			myDrone.updateStrategy(creatureTypes, myScannedcreatures, myScanUnsavedCreatureIds);
			actions.add(myDrone.getAction(creatureTypes, myScanUnsavedCreatureIds, myScannedcreatures));
		}
		return actions;
	}

}
