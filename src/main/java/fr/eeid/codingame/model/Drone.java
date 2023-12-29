package fr.eeid.codingame.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drone {

	private final int id;
	private Vector pos;
	private int emergency;
	private int battery;
	
	private DroneStrategy strategy = DroneStrategy.SURFACE;
	private StartLeftRight leftRight = StartLeftRight.LEFT;
	private Set<Integer> scanUnsavedCreatureIds = new HashSet<>();
	private final Map<Integer, String> creaturesRadarPositions = new HashMap<>();
	
	public Drone(int id, int droneX, int droneY, int emergency, int battery) {
		this.id = id;
		this.pos = new Vector(droneX, droneY);
		this.emergency = emergency;
		this.battery = battery;
	}

	public void update(int droneX, int droneY, int emergency, int battery) {
		this.pos = new Vector(droneX, droneY);
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
	
	public void updateStrategy(Map<Integer, List<Creature>> creatureTypes, Map<Integer, Creature> myScannedcreatures, Set<Integer> myScanUnsavedCreatureIds) {
		if (strategy == DroneStrategy.SURFACE && emergency == 0 && scanUnsavedCreatureIds.isEmpty()) {
			leftRight = StartLeftRight.LEFT;
			if (pos.getX() > 5000) {
				leftRight = StartLeftRight.RIGHT;
			}
			strategy = DroneStrategy.TYPE2;
			for (Creature creature : creatureTypes.get(2)) {
				if (myScannedcreatures.get(creature.getId()) == null && !myScanUnsavedCreatureIds.contains(creature.getId())) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE1;
			for (Creature creature : creatureTypes.get(1)) {
				if (myScannedcreatures.get(creature.getId()) == null && !myScanUnsavedCreatureIds.contains(creature.getId())) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE0;
		}
	}

	public int getId() {
		return id;
	}

	public String getAction(Map<Integer, List<Creature>> creatureTypes, Set<Integer> myScanUnsavedCreatureIds, Map<Integer, Creature> myScannedcreatures) {
		if (emergency == 1) {
			strategy = DroneStrategy.SURFACE;
			return "WAIT 0";
		}
		List<Creature> visibleMonsters = creatureTypes.get(-1).stream()
				.filter(monster -> monster.isVisible())
				.toList();
		if (strategy == DroneStrategy.SURFACE) {
			Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, visibleMonsters);
			return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
		}
		List<Creature> unscannedCreatureTypes = creatureTypes.get(strategy.getInteger()).stream()
				.filter(creatureType -> 
					!myScanUnsavedCreatureIds.contains(creatureType.getId())
					&& !myScannedcreatures.containsKey(creatureType.getId())
					)
				.toList();
		if (unscannedCreatureTypes.isEmpty()) {
			strategy = DroneStrategy.SURFACE;
			Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, visibleMonsters);
			return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
		}
		
		double moveY = strategy.getY(pos.getY());
		double moveX = moveX(moveY, unscannedCreatureTypes);
		
		int light = pos.getY() >= (moveY - 2000) ? 1 : 0;
		
		Vector moveWithoutCollision = getMoveWithoutCollision(moveX, moveY, visibleMonsters);
		
		String action = "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " " + light;
		return action;
	}

	public Vector getMoveWithoutCollision(double moveX, double moveY, List<Creature> monsters) {
		Vector speed = getSpeed(moveX, moveY);
		
		List<Creature> monsterCollisions = monsters.stream()
				.filter(monster -> isCollision(speed, monster))
				.toList();
		
		Vector newSpeed1 = new Vector(speed.getX(), speed.getY());
		while (!monsterCollisions.isEmpty()) {
			double moveX1 = (int) (newSpeed1.getX() * Math.cos(0.1) - newSpeed1.getY() * Math.sin(0.1));
			double moveY1 = (int) (newSpeed1.getX() * Math.sin(0.1) + newSpeed1.getY() * Math.cos(0.1));
			newSpeed1 = new Vector(moveX1, moveY1);
			Vector speedFinal = new Vector(moveX1, moveY1);
			monsterCollisions = monsters.stream()
					.filter(monster -> isCollision(speedFinal, monster))
					.toList();
		}
		Vector newPosition1 = new Vector(pos.getX() + newSpeed1.getX(), pos.getY() + newSpeed1.getY());
		
		Vector newSpeed2 = new Vector(speed.getX(), speed.getY());
		monsterCollisions = monsters.stream()
				.filter(monster -> isCollision(speed, monster))
				.toList();
		while (!monsterCollisions.isEmpty()) {
			double moveX2 = (int) (newSpeed2.getX() * Math.cos(-0.1) - newSpeed2.getY() * Math.sin(-0.1));
			double moveY2 = (int) (newSpeed2.getX() * Math.sin(-0.1) + newSpeed2.getY() * Math.cos(-0.1));
			newSpeed2 = new Vector(moveX2, moveY2);
			Vector speedFinal = new Vector(moveX2, moveY2);
			monsterCollisions = monsters.stream()
					.filter(monster -> isCollision(speedFinal, monster))
					.toList();
		}
		
		Vector newPosition2 = new Vector(pos.getX() + newSpeed2.getX(), pos.getY() + newSpeed2.getY());
		return newPosition2;
	}

	private Vector getSpeed(double moveX, double moveY) {
		Vector move = new Vector(moveX, moveY);
		Vector moveVec = new Vector(pos, move);
        if (moveVec.length() > 600) {
            moveVec = moveVec.normalize().mult(600);
        }
        Vector speed = moveVec.round();
		return speed;
	}

	private double moveX(double moveY, List<Creature> unscannedCreatureTypes) {
		if (pos.getY() <= (moveY - 2000)) {
			return pos.getX();
		}
		if (leftRight == StartLeftRight.LEFT) {
			for (Creature unscannedCreatureType : unscannedCreatureTypes) {
				String radarPosition = creaturesRadarPositions.get(unscannedCreatureType.getId());
				if ("TL".equals(radarPosition) || "BL".equals(radarPosition)) {
					return 0;
				}
			}
			return 9999;
		}
		for (Creature unscannedCreatureType : unscannedCreatureTypes) {
			String radarPosition = creaturesRadarPositions.get(unscannedCreatureType.getId());
			if ("TR".equals(radarPosition) || "BR".equals(radarPosition)) {
				return 9999;
			}
		}
		return 0;
	}
	
	public boolean isCollision(Vector speed, Creature ugly) {
        // Check instant collision
        if (ugly.getPos().inRange(pos, 500)) {
            return true;
        }
        
        // Both units are motionless
        if (speed.isZero() && ugly.getSpeed().isZero()) {
            return false;
        }

        // Change referencial
        double x = ugly.getPos().getX();
        double y = ugly.getPos().getY();
        double ux = pos.getX();
        double uy = pos.getY();

        double x2 = x - ux;
        double y2 = y - uy;
        double r2 = 500;
        double vx2 = ugly.getSpeed().getX() - speed.getX();
        double vy2 = ugly.getSpeed().getY() - speed.getY();

        // Resolving: sqrt((x2 + t*vx2)^2 + (y2 + t*vy2)^2) = r2 <=> t^2*(vx2^2 + vy2^2) + t*2*(x2*vx2 + y2*vy2) + x2^2 + y2^2 - r2^2 = 0
        // at^2 + bt + c = 0;
        // a = vx2^2 + vy2^2
        // b = 2*(x2*vx2 + y2*vy2)
        // c = x2^2 + y2^2 - radius^2 

        double a = vx2 * vx2 + vy2 * vy2;

        if (a <= 0.0) {
            return false;
        }

        double b = 2.0 * (x2 * vx2 + y2 * vy2);
        double c = x2 * x2 + y2 * y2 - r2 * r2;
        double delta = b * b - 4.0 * a * c;

        if (delta < 0.0) {
            return false;
        }

        double t = (-b - Math.sqrt(delta)) / (2.0 * a);

        if (t <= 0.0) {
            return false;
        }

        if (t > 1.0) {
            return false;
        }
        return true;
    }
	
}
