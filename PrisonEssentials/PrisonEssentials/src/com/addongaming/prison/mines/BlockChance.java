package com.addongaming.prison.mines;

import org.bukkit.Material;

public class BlockChance {

	private int chance;
	private Material m;

	@SuppressWarnings("deprecation")
	public BlockChance(String id, String chance) {
		this.m = Material.getMaterial(Integer.parseInt(id));
		this.chance = Integer.parseInt(chance);
	}

	public int getChance() {
		return chance;
	}

	public Material getM() {
		return m;
	}
}
