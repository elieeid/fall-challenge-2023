package fr.eeid.codingame.model;

public enum DroneStrategy {
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
