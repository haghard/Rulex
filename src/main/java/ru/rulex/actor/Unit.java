package ru.rulex.actor;

public class Unit {
	
	private static final Unit unit = new Unit();
	
	public static Unit unit() {
		return unit;
	}

}
