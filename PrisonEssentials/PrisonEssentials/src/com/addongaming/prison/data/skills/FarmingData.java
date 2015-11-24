package com.addongaming.prison.data.skills;

import org.bukkit.Material;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public enum FarmingData {
	CACTUS(Material.CACTUS, Material.CACTUS, 3, 0, "prison.farm.cactus"), SUGAR_CANE(
			Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, 2, 10,
			"prison.farm.sugarcane"), PUMPKIN(Material.PUMPKIN,
			Material.PUMPKIN, 5, 15, "prison.farm.pumpkin"), WHEAT(
			Material.CROPS, Material.WHEAT, 6, 20, "prison.farm.wheat"), MELON(
			Material.MELON_BLOCK, Material.MELON, 10, 50, "prison.farm.melon");

	/**
	 * Gets the relevant MiningData information for the material given
	 * 
	 * @param material
	 *            Material to be used for looking up the relevant MiningData
	 * @return Returns the MiningData for the material given.
	 */
	public static FarmingData getFarmingDataForMaterial(Material material) {
		for (FarmingData md : values())
			if (md.getShop() == material || md.getBlock() == material)
				return md;
		return null;
	}

	public static boolean isFarmingRelated(Material m) {
		for (FarmingData md : values())
			if (md.getBlock() == m || md.getShop() == m)
				return true;
		return false;
	}

	public static DataReturn playerHasPermission(String playerName, Material mat) {
		Prisoner pri = PrisonerManager.getInstance()
				.getPrisonerInfo(playerName);
		for (FarmingData md : values())
			if (md.getBlock() == mat || md.getShop() == mat) {
				if (Stats.getLevel(pri.getStat(Stats.FARMING)) < md
						.getLevelReq())
					return DataReturn.NOLEVEL;
				else if (!pri.hasPermission(md.getPermission()))
					return DataReturn.NOPERM;
				else
					return DataReturn.SUCCESS;
			}
		return null;
	}

	private Material block;
	private int levelReq;

	private String permission;

	private Material shop;

	private int value;

	FarmingData(Material block, Material shop, int value, int levelReq,
			String permission) {
		this.block = block;
		this.shop = shop;
		this.value = value;
		this.permission = permission;
		this.levelReq = levelReq;
	}

	public Material getBlock() {
		return block;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public String getPermission() {
		return permission;
	}

	public Material getShop() {
		return shop;
	}

	public int getValue() {
		return value;
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
