import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
	private static class Drone {
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
	private static class Creature {
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
	private static enum Color {
	}
	private static class Board {
		private final int creatureCount;
		private Map<Integer, Creature> creatures = new HashMap<>();
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
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		InputTracer input = new InputTracer(in);
		Board board = new Board(input);
		while (true) {
			board.update(input);
			System.out.println(board.getAction());
		}
	}
}
