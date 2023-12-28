import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
class Player {
	private static class InputTracer {
	    private final Scanner input;
	    private final List<String> readLines;
	    public InputTracer(Scanner input) {
	        this.input = input;
	        readLines = new ArrayList<>();
	    }
	    public String[] nextLine(){
	        String line = input.nextLine();
	        readLines.add(line);
	        return line.split(" ");
	    }
	    public int[] nextLineAsInts(){
	        return Arrays.stream(this.nextLine())
	                .mapToInt(Integer::parseInt)
	                .toArray();
	    }
	    public String nextLineAsSingleString(){
	        String line = input.nextLine();
	        readLines.add(line);
	        return line;
	    }
	    public int nextLineAsSingleInt(){
	        String line = input.nextLine();
	        readLines.add(line);
	        return Integer.parseInt(line);
	    }
	    public String trace(){
	        return String.join("\\n", readLines);
	    }
	}
	private static class Vector {
	    public static final Vector ZERO = new Vector(0, 0);
	    private final double x, y;
	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        long temp;
	        temp = Double.doubleToLongBits(x);
	        result = prime * result + (int) (temp ^ (temp >>> 32));
	        temp = Double.doubleToLongBits(y);
	        result = prime * result + (int) (temp ^ (temp >>> 32));
	        return result;
	    }
	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) return true;
	        if (obj == null) return false;
	        if (getClass() != obj.getClass()) return false;
	        Vector other = (Vector) obj;
	        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
	        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
	        return true;
	    }
	    public Vector(double x, double y) {
	        this.x = x;
	        this.y = y;
	    }
	    public Vector(Vector a, Vector b) {
	        this.x = b.x - a.x;
	        this.y = b.y - a.y;
	    }
	    public Vector(double angle) {
	        this.x = Math.cos(angle);
	        this.y = Math.sin(angle);
	    }
	    public Vector rotate(double angle) {
	        double nx = (x * Math.cos(angle)) - (y * Math.sin(angle));
	        double ny = (x * Math.sin(angle)) + (y * Math.cos(angle));
	        return new Vector(nx, ny);
	    };
	    public boolean equals(Vector v) {
	        return v.getX() == x && v.getY() == y;
	    }
	    public Vector round() {
	        return new Vector((int) Math.round(this.x), (int) Math.round(this.y));
	    }
	    public Vector truncate() {
	        return new Vector((int) this.x, (int) this.y);
	    }
	    public double getX() {
	        return x;
	    }
	    public double getY() {
	        return y;
	    }
	    public double distance(Vector v) {
	        return Math.sqrt((v.x - x) * (v.x - x) + (v.y - y) * (v.y - y));
	    }
	    public boolean inRange(Vector v, double range) {
	        return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y) <= range * range;
	    }
	    public Vector add(Vector v) {
	        return new Vector(x + v.x, y + v.y);
	    }
	    public Vector mult(double factor) {
	        return new Vector(x * factor, y * factor);
	    }
	    public Vector sub(Vector v) {
	        return new Vector(this.x - v.x, this.y - v.y);
	    }
	    public double length() {
	        return Math.sqrt(x * x + y * y);
	    }
	    public double lengthSquared() {
	        return x * x + y * y;
	    }
	    public Vector normalize() {
	        double length = length();
	        if (length == 0)
	            return new Vector(0, 0);
	        return new Vector(x / length, y / length);
	    }
	    public double dot(Vector v) {
	        return x * v.x + y * v.y;
	    }
	    public double angle() {
	        return Math.atan2(y, x);
	    }
	    @Override
	    public String toString() {
	        return "[" + x + ", " + y + "]";
	    }
	    public String toIntString() {
	        return (int) x + " " + (int) y;
	    }
	    public Vector project(Vector force) {
	        Vector normalize = this.normalize();
	        return normalize.mult(normalize.dot(force));
	    }
	    public final Vector cross(double s) {
	        return new Vector(-s * y, s * x);
	    }
	    public Vector hsymmetric(double center) {
	        return new Vector(2 * center - this.x, this.y);
	    }
	    public Vector vsymmetric(double center) {
	        return new Vector(this.x, 2 * center - this.y);
	    }
	    public Vector vsymmetric() {
	        return new Vector(this.x, -this.y);
	    }
	    public Vector hsymmetric() {
	        return new Vector(-this.x, this.y);
	    }
	    public Vector symmetric() {
	        return symmetric(new Vector(0, 0));
	    }
	    public Vector symmetric(Vector center) {
	        return new Vector(center.x * 2 - this.x, center.y * 2 - this.y);
	    }
	    public boolean withinBounds(double minx, double miny, double maxx, double maxy) {
	        return x >= minx && x < maxx && y >= miny && y < maxy;
	    }
	    public boolean isZero() {
	        return x == 0 && y == 0;
	    }
	    public Vector symmetricTruncate(Vector origin) {
	        return sub(origin).truncate().add(origin);
	    }
	    public Vector symmetricTruncate() {
	        return new Vector(
	            (x < Board.CENTER.x) ? Math.floor(x) : Math.ceil(x),
	            (y < Board.CENTER.y) ? Math.floor(y) : Math.ceil(y)
	        );
	    }
	    public double euclideanTo(double x, double y) {
	        return Math.sqrt(sqrEuclideanTo(x, y));
	    }
	    public double sqrEuclideanTo(double x, double y) {
	        return Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2);
	    }
	    public double sqrEuclideanTo(Vector other) {
	        return sqrEuclideanTo(other.x, other.y);
	    }
	    public Vector add(double x, double y) {
	        return new Vector(this.x + x, this.y + y);
	    }
	    public double manhattanTo(Vector other) {
	        return manhattanTo(other.x, other.y);
	    }
	    public double chebyshevTo(double x, double y) {
	        return Math.max(Math.abs(x - this.x), Math.abs(y - this.y));
	    }
	    public double manhattanTo(double x, double y) {
	        return Math.abs(x - this.x) + Math.abs(y - this.y);
	    }
	    public double euclideanTo(Vector pos) {
	        return euclideanTo(pos.x, pos.y);
	    }
	    public Vector epsilonRound() {
	        return new Vector(
	            Math.round(x * 10000000.0) / 10000000.0,
	            Math.round(y * 10000000.0) / 10000000.0
	        );
	    }
	}
	private static enum StartLeftRight {
		LEFT, RIGHT;
	}
	private static enum DroneStrategy {
		TYPE0, TYPE1, TYPE2, SURFACE;
		public double getY(double y) {
			if (this == TYPE0) {
				return 4500;
			} else if (this == TYPE1) {
				return 7000;
			} else if (this == TYPE2) {
				return 8500;
			}
			return y;
		}
		public int getInteger() {
			if (this == TYPE2) {
				return 2;
			} else if (this == TYPE1) {
				return 1;
			}
			return 0;
		}
	}
	private static class Drone {
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
		public void updateStrategy(Map<Integer, List<Creature>> creatureTypes, Map<Integer, Creature> myScannedcreaturescreatureTypeIds) {
			if (strategy == DroneStrategy.SURFACE && scanUnsavedCreatureIds.isEmpty()) {
				leftRight = StartLeftRight.LEFT;
				if (pos.getX() > 5000) {
					leftRight = StartLeftRight.RIGHT;
				}
				strategy = DroneStrategy.TYPE2;
				for (Creature creature : creatureTypes.get(2)) {
					if (myScannedcreaturescreatureTypeIds.get(creature.getId()) == null) {
						return;
					}
				}
				strategy = DroneStrategy.TYPE1;
				for (Creature creature : creatureTypes.get(1)) {
					if (myScannedcreaturescreatureTypeIds.get(creature.getId()) == null) {
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
				return "WAIT 0";
			}
			List<Creature> monsters = creatureTypes.get(-1);
			if (strategy == DroneStrategy.SURFACE) {
				Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, monsters);
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
				Vector moveWithoutCollision = getMoveWithoutCollision(pos.getX(), 500, monsters);
				return "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " 0";
			}
			double moveY = strategy.getY(pos.getY());
			double moveX = moveX(moveY, unscannedCreatureTypes);
			int light = pos.getY() >= (moveY - 2000) ? 1 : 0;
			Vector moveWithoutCollision = getMoveWithoutCollision(moveX, moveY, monsters);
			String action = "MOVE " + (int) moveWithoutCollision.getX() + " " + (int) moveWithoutCollision.getY() + " " + light;
			return action;
		}
		private Vector getMoveWithoutCollision(double moveX, double moveY, List<Creature> monsters) {
			Vector speed = getSpeed(moveY, moveX);
			List<Creature> monsterCollisions = monsters.stream()
					.filter(monster -> monster.isVisible())
					.filter(monster -> isCollision(speed, monster))
					.toList();
			Vector newSpeed1 = new Vector(speed.getX(), speed.getY());
			while (!monsterCollisions.isEmpty()) {
				double moveX1 = newSpeed1.getX() * Math.cos(0.1) - newSpeed1.getY() * Math.sin(0.1);
				double moveY1 = newSpeed1.getX() * Math.sin(0.1) + newSpeed1.getY() * Math.cos(0.1);
				newSpeed1 = new Vector(moveX1, moveY1);
				Vector speedFinal = new Vector(moveX1, moveY1);
				monsterCollisions = monsters.stream()
						.filter(monster -> monster.isVisible())
						.filter(monster -> isCollision(speedFinal, monster))
						.toList();
			}
			Vector newPosition1 = new Vector(pos.getX() + newSpeed1.getX(), pos.getY() + newSpeed1.getY());
			Vector newSpeed2 = new Vector(speed.getX(), speed.getY());
			monsterCollisions = monsters.stream()
					.filter(monster -> monster.isVisible())
					.filter(monster -> isCollision(speed, monster))
					.toList();
			while (!monsterCollisions.isEmpty()) {
				double moveX2 = newSpeed2.getX() * Math.cos(-0.1) - newSpeed2.getY() * Math.sin(-0.1);
				double moveY2 = newSpeed2.getX() * Math.sin(-0.1) + newSpeed2.getY() * Math.cos(-0.1);
				newSpeed2 = new Vector(moveX2, moveY2);
				Vector speedFinal = new Vector(moveX2, moveY2);
				monsterCollisions = monsters.stream()
						.filter(monster -> monster.isVisible())
						.filter(monster -> isCollision(speedFinal, monster))
						.toList();
			}
			Vector newPosition2 = new Vector(pos.getX() + newSpeed2.getX(), pos.getY() + newSpeed2.getY());
			return newPosition2;
		}
		private Vector getSpeed(double moveY, double moveX) {
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
		boolean isCollision(Vector speed, Creature ugly) {
	        if (ugly.getPos().inRange(pos, 500)) {
	            return true;
	        }
	        if (speed.isZero() && ugly.getSpeed().isZero()) {
	            return false;
	        }
	        double x = ugly.getPos().getX();
	        double y = ugly.getPos().getY();
	        double ux = pos.getX();
	        double uy = pos.getY();
	        double x2 = x - ux;
	        double y2 = y - uy;
	        double r2 = 500;
	        double vx2 = ugly.getSpeed().getX() - speed.getX();
	        double vy2 = ugly.getSpeed().getY() - speed.getY();
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
	private static class Creature {
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
	private static class Board {
		public static final int WIDTH = 10000;
	    public static final int HEIGHT = 10000;
		public static final Vector CENTER = new Vector((WIDTH - 1) / 2.0, (HEIGHT - 1) / 2.0);
		private final int creatureCount;
		private final Map<Integer, Creature> creatures = new HashMap<>();
		private final Map<Integer, List<Creature>> creatureTypes = new HashMap<>();
		private List<Creature> creatureType0s = new ArrayList<>();
		private List<Creature> creatureType1s = new ArrayList<>();
		private List<Creature> creatureType2s = new ArrayList<>();
		private List<Creature> monsters = new ArrayList<>();
		private int myScore;
		private int foeScore;
		private Map<Integer, Creature> myScannedcreatures = new HashMap<>();
		private Map<Integer, Creature> myUnscannedcreatures = new HashMap<>();
		private Set<Integer> myScanUnsavedCreatureIds = new HashSet<>();
		private Map<Integer, Creature> foeScannedcreatures = new HashMap<>();
		private Map<Integer, Creature> foeUnscannedcreatures = new HashMap<>();
		private Map<Integer, Drone> myDrones = new HashMap<>();
		private Map<Integer, Drone> foeDrones = new HashMap<>();
		public Board(InputTracer input) {
			this.creatureCount = input.nextLineAsSingleInt();
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
			creatureTypes.put(0, creatureType0s);
			creatureTypes.put(1, creatureType1s);
			creatureTypes.put(2, creatureType2s);
			creatureTypes.put(-1, monsters);
		}
		public void update(InputTracer input) {
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
			myScanUnsavedCreatureIds = new HashSet<>();
	        for (int i = 0; i < droneScanCount; i++) {
	        	int[] line = input.nextLineAsInts();
	            int droneId = line[0];
	            Drone drone = myDrones.get(droneId);
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
	            Drone drone = myDrones.get(droneId);
	            if (drone == null) {
	            	drone = foeDrones.get(droneId);
	            }
	            int creatureId = Integer.parseInt(line[1]);
	            String radar = line[2];
	            drone.updateRadar(creatureId, radar);
	        }
		}
		public List<String> getActions() {
			List<String> actions = new ArrayList<>();
			for (Drone myDrone : myDrones.values()) {
				myDrone.updateStrategy(creatureTypes, myScannedcreatures);
				actions.add(myDrone.getAction(creatureTypes, myScanUnsavedCreatureIds, myScannedcreatures));
			}
			return actions;
		}
	}
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		InputTracer input = new InputTracer(in);
		Board board = new Board(input);
		while (true) {
			board.update(input);
			for (String action : board.getActions()) {
				System.out.println(action);
			}
		}
	}
}
