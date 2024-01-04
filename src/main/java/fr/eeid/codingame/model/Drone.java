package fr.eeid.codingame.model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drone {

	private final int id;
	private Vector pos;
	private int emergency;
	private int battery;
	private boolean hasUsedLight;

	private DroneStrategy strategy = DroneStrategy.DIVE;
	private StartLeftRight leftRight = StartLeftRight.LEFT;
	private Set<Integer> scanUnsavedCreatureIds = new HashSet<>();

	public Drone(int id, int droneX, int droneY, int emergency, int battery) {
		this.id = id;
		this.pos = new Vector(droneX, droneY);
		if (droneX < 5000) {
			leftRight = StartLeftRight.LEFT;
		} else {
			leftRight = StartLeftRight.RIGHT;
		}
		this.emergency = emergency;
		this.battery = battery;
	}

	public void update(int droneX, int droneY, int emergency, int battery) {
		this.pos = new Vector(droneX, droneY);
		this.emergency = emergency;
		if (this.battery > battery) {
			hasUsedLight = true;
		} else {
			hasUsedLight = false;
		}
		this.battery = battery;
		scanUnsavedCreatureIds = new HashSet<>();
	}

	public void addScanUnsaved(int creatureId) {
		scanUnsavedCreatureIds.add(creatureId);
	}

	public void updateStrategy(Map<Integer, List<Creature>> creatureTypes, Map<Integer, Creature> myScannedcreatures,
			Set<Integer> myScanUnsavedCreatureIds) {
		if (strategy == DroneStrategy.SURFACE && emergency == 0 && scanUnsavedCreatureIds.isEmpty()) {
			strategy = DroneStrategy.DIVE;
			for (Creature creature : creatureTypes.get(2)) {
				if (myScannedcreatures.get(creature.getId()) == null
						&& !myScanUnsavedCreatureIds.contains(creature.getId())) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE1;
			for (Creature creature : creatureTypes.get(1)) {
				if (myScannedcreatures.get(creature.getId()) == null
						&& !myScanUnsavedCreatureIds.contains(creature.getId())) {
					return;
				}
			}
			strategy = DroneStrategy.TYPE0;
		}
	}

	public int getId() {
		return id;
	}

	public String getAction(Map<Integer, List<Creature>> creatureTypes, Set<Integer> myScanUnsavedCreatureIds,
			Map<Integer, Creature> myScannedcreatures) {
		if (emergency == 1) {
			strategy = DroneStrategy.SURFACE;
			return "WAIT 0";
		}
		List<Creature> visibleMonsters = creatureTypes.get(-1).stream().filter(monster -> monster.isVisible()).toList();
		if (strategy == DroneStrategy.SURFACE) {
			Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, visibleMonsters);
			return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
		}
		
		long myStrategyScanUnsavedCreatureIds = creatureTypes.get(strategy.getInteger()).stream()
				.filter(creature -> scanUnsavedCreatureIds.contains(creature.getId())).count();
		if (myStrategyScanUnsavedCreatureIds >= 1) {
			strategy = DroneStrategy.SURFACE;
			Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, visibleMonsters);
			return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
		}

		List<Creature> unscannedCreatureTypes = creatureTypes.get(strategy.getInteger()).stream()
				.filter(creatureType -> !myScanUnsavedCreatureIds.contains(creatureType.getId())
						&& !myScannedcreatures.containsKey(creatureType.getId()))
				.sorted((creature1, creature2) -> (int) (pos.distance(creature1.getApproximativePosition()) - pos.distance(creature2.getApproximativePosition())))
				.toList();
		if (unscannedCreatureTypes.isEmpty()) {
			strategy = DroneStrategy.SURFACE;
			Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, visibleMonsters);
			return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
		}
		
		//unscannedCreatureTypes.sort(Comparator.comparing(creature -> pos.distance(creature.getApproximativePosition())));

		Creature closestCreature = unscannedCreatureTypes.get(0);
		Vector closestPosition = closestCreature.getApproximativePosition();
		
		double moveY = closestPosition.getY() - 250;
		double moveX = closestPosition.getX();
		if (pos.getY() <= (moveY - 2000)) {
			moveX = pos.getX();
		}
				
		int light = pos.getY() >= (moveY - 2000) || pos.getY() == 2300 || pos.getY() == 4100 || pos.getY() == 6500 ? 1 : 0;
				
		Vector moveWithoutCollision = getMoveWithoutCollision(moveX, moveY, visibleMonsters);
				
		String action = "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " " + light;
		for (Creature creature : creatureTypes.get(strategy.getInteger())) {
			Vector approximativePosition = creature.getApproximativePosition();
			action += " ID:" + creature.getId() + " X:" + approximativePosition.getX() + " Y:"
					+ approximativePosition.getY();
		}
		return action;
	}

	public Vector getMoveWithoutCollision(double moveX, double moveY, List<Creature> monsters) {
		Vector speed = getSpeed(moveX, moveY);

		List<Creature> monsterCollisions = monsters.stream().filter(monster -> isCollision(speed, monster)).toList();

		Vector newSpeed1 = new Vector(speed.getX(), speed.getY());
		double angleTotal = 0;
		double rotationComplete = 6.3; // Un cercle complet a 2 �2π radians (environ 6.283 radians)
		while (!monsterCollisions.isEmpty()) {
			long moveX1 = Math.round(newSpeed1.getX() * Math.cos(0.1) - newSpeed1.getY() * Math.sin(0.1));
			long moveY1 = Math.round(newSpeed1.getX() * Math.sin(0.1) + newSpeed1.getY() * Math.cos(0.1));
			angleTotal += 0.1;
			if (angleTotal >= rotationComplete) {
				return new Vector(pos.getX(), 500);
			}
			newSpeed1 = new Vector(moveX1, moveY1);
			Vector speedFinal = new Vector(moveX1, moveY1);
			monsterCollisions = monsters.stream().filter(monster -> isCollision(speedFinal, monster)).toList();
		}
		Vector newPosition1 = new Vector(pos.getX() + newSpeed1.getX(), pos.getY() + newSpeed1.getY());

		Vector newSpeed2 = new Vector(speed.getX(), speed.getY());
		monsterCollisions = monsters.stream().filter(monster -> isCollision(speed, monster)).toList();
		while (!monsterCollisions.isEmpty()) {
			long moveX2 = Math.round(newSpeed2.getX() * Math.cos(-0.1) - newSpeed2.getY() * Math.sin(-0.1));
			long moveY2 = Math.round(newSpeed2.getX() * Math.sin(-0.1) + newSpeed2.getY() * Math.cos(-0.1));
			newSpeed2 = new Vector(moveX2, moveY2);
			Vector speedFinal = new Vector(moveX2, moveY2);
			monsterCollisions = monsters.stream().filter(monster -> isCollision(speedFinal, monster)).toList();
		}

		Vector newPosition2 = new Vector(pos.getX() + newSpeed2.getX(), pos.getY() + newSpeed2.getY());

		Vector move = new Vector(moveX, moveY);
		if (move.distance(newPosition1) < move.distance(newPosition2)) {
			return newPosition1;
		}
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

		// Resolving: sqrt((x2 + t*vx2)^2 + (y2 + t*vy2)^2) = r2 <=> t^2*(vx2^2 + vy2^2)
		// + t*2*(x2*vx2 + y2*vy2) + x2^2 + y2^2 - r2^2 = 0
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

	public double getX() {
		return pos.getX();
	}

	public double getY() {
		return pos.getY();
	}

	public Vector getPos() {
		return pos;
	}

}
