package fr.eeid.codingame.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DroneTest {

	@Test
	public void test() {
		Drone drone = new Drone(1, 8433, 2660, 0, 30);
		// 8562 6221
		Vector speed = new Vector(59, 597);
		List<Creature> monsters = new ArrayList<>();
		Creature monster1 = new Creature(17, -1, -1);
		Creature monster2 = new Creature(19, -1, -1);
		monsters.add(monster1);
		monsters.add(monster2);
		monster1.updatePosition(new Vector(7990, 2500), new Vector(443, 160));
		monster2.updatePosition(new Vector(7990, 2500), new Vector(443, 160));
		Vector move = drone.getMoveWithoutCollision(9999, 4500, monsters);
	}
}
