package com.addongaming.prison.stats;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Enum of the regular generic stats a player can level up.
 * 
 * @author Jake
 * 
 */
public enum Stats {
	FARMING, MINING, NONE, WOODCUTTING;
	public static int expTillLevel(int currentClassExp) {
		short counter = 1;
		for (Integer i : getLevels()) {
			if (i > currentClassExp)
				return i - currentClassExp;
			else
				counter++;
		}
		return 0;
	}

	/**
	 * Gets the current stat level for the EXP given
	 * 
	 * @param exp
	 *            Current EXP for the stat
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
			xp += Math.floor(i + 50 * (Math.pow(2, i / 4.9) / 2));
			li.add((int) xp);
		}
		return li.toArray(new Integer[li.size()]);
	}

	public static void levelledUp(Player play, Stats stat, short level) {
		play.sendMessage(ChatColor.GOLD + "[" + ChatColor.AQUA + stat.toText()
				+ ChatColor.GOLD + "] " + ChatColor.GREEN
				+ "You have levelled up " + stat.toText() + " to level "
				+ level + "!");
		Location eyes = play.getEyeLocation();
		play.getWorld().playEffect(eyes, Effect.MOBSPAWNER_FLAMES, 0, 30);
	}

	/**
	 * Converts the Stats name to a user-friendly name
	 * 
	 * @return User-friendly name for the stat
	 */
	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

}
