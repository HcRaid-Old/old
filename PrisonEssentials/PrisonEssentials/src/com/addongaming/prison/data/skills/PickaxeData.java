package com.addongaming.prison.data.skills;

import org.bukkit.Material;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public enum PickaxeData {
	WOOD(Material.WOOD_PICKAXE, 0, "prison.pickaxe.wood"), STONE(
			Material.STONE_PICKAXE, 0, "prison.pickaxe.stone"), IRON(
			Material.IRON_PICKAXE, 8, "prison.pickaxe.coal"), GOLD(
			Material.GOLD_PICKAXE, 18, "prison.pickaxe.gold"), DIAMOND(
			Material.DIAMOND_PICKAXE, 50, "prison.pickaxe.diamond");
	private Material material;
	private String permission;
	private int levelReq;

	PickaxeData(Material m, int levelReq, String permission) {
		this.material = m;
		this.permission = permission;
		this.levelReq = levelReq;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public Material getMaterial() {
		return material;
	}

	public String getPermission() {
		return permission;
	}

	/**
	 * Checks to see if the item in hand is classified as PickaxeData
	 * 
	 * @param m
	 *            Material to check if it's covered under PickaxeData
	 * @return true if the item is classified under PickaxeData false if the
	 *         item isn't classified under PickaxeData
	 */
	public static boolean isPickaxeRelated(Material m) {
		for (PickaxeData md : values())
			if (md.getMaterial() == m)
				return true;
		return false;
	}

	/**
	 * Checks to see if a player can mine with X pickaxe. If not returns why not
	 * in the form of a DataReturn
	 * 
	 * @param playerName
	 *            Players name to check it against
	 * @param mat
	 *            Material of the item in hand
	 * @return DataReturn NOLEVEL if their level is too low, NOPERM if they
	 *         haven't purchased the permission and if succeeded, SUCCESS
	 */
	public static DataReturn playerHasPermission(String playerName, Material mat) {
		Prisoner pri = PrisonerManager.getInstance()
				.getPrisonerInfo(playerName);
		for (PickaxeData md : values())
			if (md.getMaterial() == mat) {
				if (Stats.getLevel(pri.getStat(Stats.MINING)) < md
						.getLevelReq())
					return DataReturn.NOLEVEL;
				else if (!pri.hasPermission(md.getPermission()))
					return DataReturn.NOPERM;
				else
					return DataReturn.SUCCESS;
			}
		return null;
	}

	/**
	 * Gets the relevant PickaxeData information for the item material given
	 * 
	 * @param material
	 *            Material to be used for looking up the relevant PickaxeData
	 * @return Returns the PickaxeData for the material given.
	 */
	public static PickaxeData getPickaxeDataForMaterial(Material material) {
		for (PickaxeData md : values())
			if (md.getMaterial() == material)
				return md;
		return null;
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
}
