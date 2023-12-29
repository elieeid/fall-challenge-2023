package fr.eeid.codingame.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DroneTest {

	@Test
	public void test() {
		Drone drone = new Drone(1, 8503, 5624, 0, 30);
		// 8562 6221
		Vector speed = new Vector(59, 597);
		List<Creature> monsters = new ArrayList<>();
		Creature monster = new Creature(17, -1, -1);
		monsters.add(monster);
		monster.updatePosition(7913, 5523, 532, 91);
		Vector move = drone.getMoveWithoutCollision(8503, 8500, monsters);
	}
}
