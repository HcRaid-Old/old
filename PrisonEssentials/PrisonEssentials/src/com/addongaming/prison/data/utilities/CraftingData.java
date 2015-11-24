package com.addongaming.prison.data.utilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public enum CraftingData {
	WORKBENCH("prison.make.workbench", 0, 0, Material.WORKBENCH), CHEST(
			"prison.make.chest", 100, 2, Material.CHEST), FURNACE(
			"prison.make.furnace", 200, 5, Material.FURNACE), ENDER_CHEST(
			"prison.make.enderchest", 1000, 20, Material.ENDER_CHEST), ENCHANTMENT_TABLE(
			"prison.make.enchantment", 2000, 20, Material.ENCHANTMENT_TABLE), NULL1(
			"", 0, 999, null), NULL2("", 0, 999, null), NULL3("", 0, 999, null), NULL4(
			"", 0, 999, null), WOODEN_PICKAXE("prison.make.woodpick", 0, 0,
			Material.WOOD_PICKAXE), STONE_PICKAXE("prison.make.stonepick", 20,
			2, Material.STONE_PICKAXE), IRON_PICKAXE("prison.make.ironpick",
			200, 5, Material.IRON_PICKAXE), GOLD_PICKAXE(
			"prison.make.goldpick", 700, 10, Material.GOLD_PICKAXE), DIAMOND_PICKAXE(
			"prison.make.diapick", 5000, 30, Material.DIAMOND_PICKAXE), NULL5(), NULL6(), NULL7(), NULL8(), WOODEN_AXE(
			"prison.make.woodaxe", 0, 0, Material.WOOD_AXE), STONE_AXE(
			"prison.make.stoneaxe", 20, 2, Material.STONE_AXE), IRON_AXE(
			"prison.make.ironaxe", 200, 5, Material.IRON_AXE), GOLD_AXE(
			"prison.make.goldaxe", 700, 10, Material.GOLD_AXE), DIAMOND_AXE(
			"prison.make.diaaxe", 5000, 30, Material.DIAMOND_AXE), NULL9(), NULL10(), NULL11(), NULL12(), WOODEN_HOE(
			"prison.make.woodhoe", 0, 0, Material.WOOD_HOE), STONE_HOE(
			"prison.make.stonehoe", 20, 2, Material.STONE_HOE), IRON_HOE(
			"prison.make.ironhoe", 200, 5, Material.IRON_HOE), GOLD_HOE(
			"prison.make.goldhoe", 700, 10, Material.GOLD_HOE), DIAMOND_HOE(
			"prison.make.diahoe", 5000, 30, Material.DIAMOND_HOE), NULL13(), NULL14(), NULL15(), NULL16(), WOODEN_SPADE(
			"prison.make.woodspade", 0, 0, Material.WOOD_SPADE), STONE_SPADE(
			"prison.make.stonespade", 20, 2, Material.STONE_SPADE), IRON_SPADE(
			"prison.make.ironspade", 200, 5, Material.IRON_SPADE), GOLD_SPADE(
			"prison.make.goldspade", 700, 10, Material.GOLD_SPADE), DIAMOND_SPADE(
			"prison.make.diaspade", 5000, 30, Material.DIAMOND_SPADE);

	private String permission;
	private int cost;
	private int charLevel;
	private Material mat;

	CraftingData() {
		mat = null;
	}

	CraftingData(String permission, int cost, int charLevel, Material mat) {
		this.permission = permission;
		this.cost = cost;
		this.charLevel = charLevel;
		this.mat = mat;
	}

	public Material getMat() {
		return mat;
	}

	public int getCharLevel() {
		return charLevel;
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

	public static CraftingData getCraftByMaterial(Material type) {
		for (CraftingData cd : values())
			if (cd.getMat() == type)
				return cd;
		return null;
	}

	public static DataReturn hasCraftingPermission(Player p, Material type) {
		CraftingData cd = getCraftByMaterial(type);
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (PlayerClasses.getLevel(prisoner.getCharacterExp()) < cd
				.getCharLevel())
			return DataReturn.NOLEVEL;
		if (!prisoner.hasPermission(cd.getPermission()))
			return DataReturn.NOPERM;
		else
			return DataReturn.SUCCESS;
	}
}
