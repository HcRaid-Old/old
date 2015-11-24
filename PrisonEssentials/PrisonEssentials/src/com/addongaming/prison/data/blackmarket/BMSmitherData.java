package com.addongaming.prison.data.blackmarket;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public enum BMSmitherData {
	LEATHER_HELM(Material.LEATHER_HELMET, 0, 500, "prison.armour.leather.helm"), LEATHER_CHEST(
			Material.LEATHER_CHESTPLATE, 0, 1000, "prison.armour.leather.chest"), LEATHER_LEGS(
			Material.LEATHER_LEGGINGS, 0, 1000, "prison.armour.leather.legs"), LEATHER_BOOTS(
			Material.LEATHER_BOOTS, 0, 500, "prison.armour.leather.boots"), null1, null2, null3, null4, null5,

	GOLD_HELM(Material.GOLD_HELMET, 14, 1000, "prison.armour.gold.helm"), GOLD_CHEST(
			Material.GOLD_CHESTPLATE, 18, 2500, "prison.armour.gold.chest"), GOLD_LEGS(
			Material.GOLD_LEGGINGS, 18, 2000, "prison.armour.gold.legs"), GOLD_BOOTS(
			Material.GOLD_BOOTS, 14, 1000, "prison.armour.gold.boots"), null6, null7, null8, null9, null10,

	IRON_HELM(Material.IRON_HELMET, 20, 2000, "prison.armour.iron.helm"), IRON_CHEST(
			Material.IRON_CHESTPLATE, 25, 4000, "prison.armour.iron.chest"), IRON_LEGS(
			Material.IRON_LEGGINGS, 20, 3500, "prison.armour.iron.legs"), IRON_BOOTS(
			Material.IRON_BOOTS, 20, 2000, "prison.armour.iron.boots"), null11, null12, null13, null14, null15,

	DIAMOND_HELM(Material.DIAMOND_HELMET, 30, 5000,
			"prison.armour.diamond.helm"), DIAMOND_CHEST(
			Material.DIAMOND_CHESTPLATE, 35, 10000,
			"prison.armour.diamond.chest"), DIAMOND_LEGS(
			Material.DIAMOND_LEGGINGS, 33, 8000, "prison.armour.diamond.legs"), DIAMOND_BOOTS(
			Material.DIAMOND_BOOTS, 30, 5000, "prison.armour.diamond.boots"), null16, null17, null18, null19, null20, WOOD_SWORD(
			Material.WOOD_SWORD, 0, 100, "prison.weapon.wood"), STONE_SWORD(
			Material.STONE_SWORD, 5, 500, "prison.weapon.stone"), GOLD_SWORD(
			Material.GOLD_SWORD, 10, 1000, "prison.weapon.gold"), IRON_SWORD(
			Material.IRON_SWORD, 20, 5000, "prison.weapon.iron"), DIAMOND_SWORD(
			Material.DIAMOND_SWORD, 30, 10000, "prison.weapon.diamond"),

	;

	private Material material;
	private String permission;
	private int levelReq;
	private int cost;

	BMSmitherData(Material m, int levelReq, int cost, String permission) {
		this.material = m;
		this.permission = permission;
		this.cost = cost;
		this.levelReq = levelReq;
	}

	// placeholder
	BMSmitherData() {
		material = null;
	}

	public int getCost() {
		return cost;
	}

	public int getCharLevel() {
		return levelReq;
	}

	public Material getMaterial() {
		return material;
	}

	public String getPermission() {
		return permission;
	}

	public boolean isPlaceHolder() {
		return material == null;
	}

	public static DataReturn canCraft(Player player, Material mat) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				player.getUniqueId());
		BMSmitherData bmsd = getData(mat);
		if (prisoner == null)
			return DataReturn.FAILURE;
		else if (PlayerClasses.getLevel(prisoner.getCharacterExp()) < bmsd
				.getCharLevel())
			return DataReturn.NOLEVEL;
		else if (!prisoner.hasPermission(bmsd.getPermission()))
			return DataReturn.NOPERM;
		else
			return DataReturn.SUCCESS;
	}

	public static BMSmitherData getData(Material type) {
		for (BMSmitherData bmsd : values())
			if (!bmsd.isPlaceHolder() && bmsd.getMaterial() == type)
				return bmsd;
		return null;
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase().replace('_', ' '));
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		sb.setCharAt(sb.lastIndexOf(" ") + 1,
				Character.toUpperCase(sb.charAt(sb.lastIndexOf(" ") + 1)));
		return sb.toString();
	}
}
