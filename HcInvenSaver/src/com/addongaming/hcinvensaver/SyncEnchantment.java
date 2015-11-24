package com.addongaming.hcinvensaver;

import java.io.Serializable;

import org.bukkit.enchantments.Enchantment;

public class SyncEnchantment implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7920394569158123748L;
	private final int id, level;

	/**
	 * A serializable implementation of Bukkit's Enchantment class.
	 * 
	 * @param e
	 *            (Enchantment)
	 */
	public SyncEnchantment(final Enchantment e) {
		id = e.getId();
		level = e.getMaxLevel();
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

}