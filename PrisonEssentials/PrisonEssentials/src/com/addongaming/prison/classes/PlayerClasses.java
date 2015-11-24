package com.addongaming.prison.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of the regular generic stats a player can level up.
 * 
 * @author Jake
 * 
 */
public enum PlayerClasses {
	ASSASSIN, EXOTICDEALER, GUARD, LIMBO, MURDERER, SNITCH, THIEF;
	public static int expTillLevel(int currentClassExp) {
		short counter = 1;
		for (Integer i : getLevels()) {
			if (i > currentClassExp)
				return i - currentClassExp;

		}
		return 0;
	}

	/**
	 * Gets the current class level for the EXP given
	 * 
	 * @param exp
	 *            Current EXP for the class
	 * @return The level that the EXP equivalates to
	 */
	public static short getLevel(int exp) {
		short counter = 1;
		for (Integer i : getLevels()) {
			if (i > exp)
				return counter;
			else
				counter++;
		}
		return counter;
	}

	/**
	 * Returns an array of the EXP needed for each level.
	 * 
	 * @return Integer[] (array) of exp levels
	 */
	public static Integer[] getLevels() {
		List<Integer> li = new ArrayList<Integer>();
		double xp = 0.0d;
		for (int i = 1; i < 99; i++) {
			xp += Math.floor(i + 30 * (Math.pow(2, i / 8) / 5));
			li.add((int) xp);
		}
		return li.toArray(new Integer[li.size()]);
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
}
