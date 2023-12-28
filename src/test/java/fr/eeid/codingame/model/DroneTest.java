package fr.eeid.codingame.model;

import java.awt.geom.Point2D;

import org.junit.jupiter.api.Test;

public class DroneTest {

	@Test
	public void test() {
		double distance = Point2D.distance(7999, 5300, 8247, 5783);
		double distance2 = Point2D.distance(7393, 3799, 1081, 6038);
		boolean p = distance < distance2;
	}
}
