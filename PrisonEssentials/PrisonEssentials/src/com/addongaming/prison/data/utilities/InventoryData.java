package com.addongaming.prison.data.utilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public enum InventoryData {
	WORKBENCH("prison.inve.workbench", 100, CraftingData.WORKBENCH
			.getCharLevel(), InventoryType.WORKBENCH, Material.WORKBENCH), CHEST(
			"prison.inve.chest", 150, 0, InventoryType.CHEST, Material.CHEST), FURNACE(
			"prison.inve.furnace", 200, CraftingData.FURNACE.getCharLevel(),
			InventoryType.FURNACE, Material.FURNACE), ENDER_CHEST(
			"prison.inve.enderchest", 500, CraftingData.ENDER_CHEST
					.getCharLevel(), InventoryType.ENDER_CHEST,
			Material.ENDER_CHEST), ENCHANTMENT_TABLE("prison.inve.enchantment",
			1000, CraftingData.ENCHANTMENT_TABLE.getCharLevel(),
			InventoryType.ENCHANTING, Material.ENCHANTMENT_TABLE);

	private String permission;
	private int cost;
	private int charLevel;
	private InventoryType it;
	private Material material;

	InventoryData(String permission, int cost, int charLevel, InventoryType it,
			Material material) {
		this.permission = permission;
		this.cost = cost;
		this.charLevel = charLevel;
		this.it = it;
		this.material = material;
	}

	public int getCharLevel() {
		return charLevel;
	}

	public InventoryType getInventoryType() {
		return this.it;
	}

	public int getCost() {
		return cost;
	}

	public String getPermission() {
		return permission;
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase().replace('_', ' '));
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		sb.setCharAt(sb.lastIndexOf(" ") + 1,
				Character.toUpperCase(sb.charAt(sb.lastIndexOf(" ") + 1)));
		return sb.toString();
	}

	public static boolean isInData(InventoryType it) {
		for (InventoryData id : values())
			if (it == id.getInventoryType())
				return true;
		return false;

	}

	public static InventoryData getInventoryByType(InventoryType it) {
		for (InventoryData cd : values())
			if (cd.getInventoryType() == it)
				return cd;
		return null;
	}

	public static DataReturn canOpenInventory(Player p, InventoryType type) {
		Prisoner pri = PrisonerManager.getInstance()
				.getPrisonerInfo(p.getName());
		for (InventoryData md : values())
			if (md.getInventoryType() == type) {
				if (PlayerClasses.getLevel(pri.getCharacterExp()) < md
						.getCharLevel())
					return DataReturn.NOLEVEL;
				else if (!pri.hasPermission(md.getPermission()))
					return DataReturn.NOPERM;
				else
					return DataReturn.SUCCESS;
			}
		return null;
	}

	public Material getMaterial() {
		return material;
	}

	public static InventoryData getInventoryByMaterial(Material type) {
		for (InventoryData id : values())
			if (id.getMaterial() == type)
				return id;
		return null;
	}
}
