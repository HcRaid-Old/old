package com.addongaming.hcessentials.perks.headtrack;

import org.bukkit.Material;

public enum ItemFuel {
	iron_ingot(Material.IRON_INGOT, 75), gold_ingot(Material.GOLD_INGOT, 115), emerald(
			Material.EMERALD, 150), diamond(Material.DIAMOND, 250);
	private Material material;
	private int fuel;

	ItemFuel(Material material, int fuel) {
		this.material = material;
		this.fuel = fuel;
	}

	public Material getMaterial() {
		return material;
	}

	public int getFuel() {
		return fuel;
	}

	public static ItemFuel getByMaterial(Material type) {
		for (ItemFuel iff : values())
			if (iff.getMaterial() == type)
				return iff;
		return null;
	}
}
