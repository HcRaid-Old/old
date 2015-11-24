package com.addongaming.hcessentials.config;

import org.bukkit.ChatColor;

import com.addongaming.hcessentials.combat.global.deathmsgs.DMTypes;

public class Config {

	public static class Combat {
		public static DMTypes dmt;
		public static boolean enderPearlsInCombat = false;
		public static boolean teleportInCombat = false;
	}

	/**
	 * Tick types upon plugin loading. Holds information about the ticks that
	 * should be scheduled before a scheduler runs as not to cause conflictions
	 * between subplugins
	 * 
	 * @author Jake
	 */
	public static class Ticks {
		public static final int POSTWORLD = 3;
		public static final int PREWORLD = 1;
		public static final int WORLDLOAD = 2;
	}

	/**
	 * This allows us to have a static reference to Bank chests & Tracking
	 * chests so the two shouldn't become confused
	 * */
	public static class Chests {
		public static final int BANK = 10;
		public static final int TRACKING = 11;
	}

	public static boolean combatLogger = false;
	public final static String enchantSign = ChatColor.DARK_GRAY + "["
			+ ChatColor.DARK_RED + "Enchant" + ChatColor.DARK_GRAY + "]";
	public static final String itemSign =  ChatColor.DARK_GRAY + "["
			+ ChatColor.DARK_RED + "Items" + ChatColor.DARK_GRAY + "]";
	public static boolean enchantSignPurchasable = true;
	public static boolean enchantTable = true;
	public static boolean joinMessage = false;
	public static boolean notifications = false;
	public static boolean raidHomes = false;
	public static boolean silkSpawning = false;
	public static boolean teams = false;
	public static String serverName;
}
