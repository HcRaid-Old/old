package com.addongaming.prison.data.skills;

import org.bukkit.Material;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public enum MiningData {
	COBBLESTONE(Material.COBBLESTONE, 1, 0, "prison.mine.cobble"), STONE(
			Material.STONE, 2, 0, "prison.mine.stone"), NETHERRACK(
			Material.NETHERRACK, 3, 0, "prison.mine.netherrack"), COAL(
			Material.COAL_ORE, 5, 0, "prison.mine.coal"), IRON(
			Material.IRON_ORE, 7, 8, "prison.mine.iron"), LAPIS(
			Material.LAPIS_ORE, 9, 10, "prison.mine.lapis"), GOLD(
			Material.GOLD_ORE, 12, 18, "prison.mine.gold"), NETHERBRICK(
			Material.NETHER_BRICK, 10, 22, "prison.mine.netherbrick"), NETHERQUARTZ(
			Material.QUARTZ_ORE, 12, 25, "prison.mine.quartz"), EMERALD(
			Material.EMERALD_ORE, 13, 30, "prison.mine.emerald"), GLOWSTONE(
			Material.GLOWSTONE, 14, 35, "prison.mine.glowstone"), REDSTONE(
			Material.REDSTONE_ORE, 15, 40, "prison.mine.redstone"), DIAMOND(
			Material.DIAMOND_ORE, 20, 50, "prison.mine.diamond");
	private Material material;
	private int value;
	private String permission;
	private int levelReq;

	MiningData(Material m, int value, int levelReq, String permission) {
		this.material = m;
		this.value = value;
		this.permission = permission;
		this.levelReq = levelReq;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public Material getMaterial() {
		return material;
	}

	public int getValue() {
		return value;
	}

	public String getPermission() {
		return permission;
	}

	/**
	 * Checks to see if the material is classified under MiningData
	 * 
	 * @param m
	 *            Material to check if it's covered under MiningData
	 * @return true if the block is classified under MiningData false if the
	 *         block isn't classified under MiningData
	 */
	public static boolean isMiningRelated(Material m) {
		for (MiningData md : values())
			if (md.getMaterial() == m)
				return true;
		return false;
	}

	/**
	 * Checks to see if a player can mine X block. If not returns why not in the
	 * form of a DataReturn
	 * 
	 * @param playerName
	 *            Players name to check it against
	 * @param mat
	 *            Material of the block being broken
	 * @return DataReturn NOLEVEL if their level is too low, NOPERM if they
	 *         haven't purchased the permission and if succeeded, SUCCESS
	 */
	public static DataReturn playerHasPermission(String playerName, Material mat) {
		Prisoner pri = PrisonerManager.getInstance()
				.getPrisonerInfo(playerName);
		for (MiningData md : values())
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
	 * Gets the relevant MiningData information for the material given
	 * 
	 * @param material
	 *            Material to be used for looking up the relevant MiningData
	 * @return Returns the MiningData for the material given.
	 */
	public static MiningData getMiningDataForMaterial(Material material) {
		for (MiningData md : values())
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
