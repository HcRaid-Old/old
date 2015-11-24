package com.addongaming.hcessentials.data;

import org.bukkit.Material;

public enum Ores {

	iron(Material.IRON_ORE, Material.IRON_INGOT), gold(Material.GOLD_ORE,
			Material.GOLD_INGOT), stone(Material.COBBLESTONE, Material.STONE);
	private Material original;
	private Material end;

	Ores(Material original, Material end) {
		this.original = original;
		this.end = end;
	}

	public Material getEnd() {
		return end;
	}

	public Material getOriginal() {
		return original;
	}

	public static Ores getByMaterial(Material mat) {
		for (Ores ore : Ores.values())
			if (ore.getEnd() == mat || ore.getOriginal() == mat)
				return ore;
		return null;
	}

}
