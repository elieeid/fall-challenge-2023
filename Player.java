import java.util.List;
import java.util.Map;
import java.util.Collection;
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
	private static enum StartLeftRight {
		LEFT, RIGHT;
	}
	private static enum DroneStrategy {
		TYPE0, TYPE1, TYPE2, SURFACE;
		public int getY(int y) {
			if (this == TYPE0) {
				return 4500;
			} else if (this == TYPE1) {
				return 7000;
			} else if (this == TYPE2) {
				return 8500;
			}
			return y;
		}
	}
	private static class Drone {
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
				strategy = DroneStrategy.TYPE0;
				for (Integer creatureId : creatureTypeIds.get(DroneStrategy.TYPE0)) {
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
				strategy = DroneStrategy.TYPE2;
			}
		}
		public int getId() {
			return id;
		}
		public String getAction(Map<DroneStrategy, List<Integer>> creatureTypeIds, Map<Integer, Creature> myScannedcreatures) {
			if (strategy == DroneStrategy.SURFACE) {
				return "MOVE " + droneX + " 500 0";
			}
			List<Integer> unscannedCreatureTypeIds = creatureTypeIds.get(strategy).stream()
					.filter(creatureType0Id -> !scanUnsavedCreatureIds.contains(creatureType0Id) && !myScannedcreatures.containsKey(creatureType0Id))
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
	private static class Coord {
		private final int x;
		private final int y;
		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
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
		private static final Coord COORD_LEFT_TYPE0 = new Coord(0, 4500);
		private static final Coord COORD_LEFT_TYPE1 = new Coord(0, 7000);
		private final int creatureCount;
		private final Map<Integer, Creature> creatures = new HashMap<>();
		private final Map<DroneStrategy, List<Integer>> creatureTypeIds = new HashMap<>();
		private List<Integer> creatureType0Ids = new ArrayList<>();
		private List<Integer> creatureType1Ids = new ArrayList<>();
		private List<Integer> creatureType2Ids = new ArrayList<>();
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
				int type = line[2];
				Creature creature = new Creature(
						creatureId,
						line[1],
						type);
				this.creatures.put(creatureId, creature);
				this.myUnscannedcreatures.put(creatureId, creature);
				this.foeUnscannedcreatures.put(creatureId, creature);
				if (type == 0) {
					creatureType0Ids.add(creatureId);
				} else if (type == 1) {
					creatureType1Ids.add(creatureId);
				} else {
					creatureType2Ids.add(creatureId);
				}
			}
			creatureTypeIds.put(DroneStrategy.TYPE0, creatureType0Ids);
			creatureTypeIds.put(DroneStrategy.TYPE1, creatureType1Ids);
			creatureTypeIds.put(DroneStrategy.TYPE2, creatureType2Ids);
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
	        for (int i = 0; i < droneScanCount; i++) {
	        	int[] line = input.nextLineAsInts();
	            int droneId = line[0];
	            Drone drone = myDrones.get(droneId);
	            if (drone == null) {
	            	drone = foeDrones.get(droneId);
	            }
	            int creatureId = line[1];
	            drone.addScanUnsaved(creatureId);
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
				myDrone.updateStrategy(creatureTypeIds, myScannedcreatures);
				actions.add(myDrone.getAction(creatureTypeIds, myScannedcreatures));
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
