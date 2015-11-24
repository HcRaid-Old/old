package com.addongaming.prison.data.skills;

import org.bukkit.Material;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public enum TreeData {
	OAK(Material.LOG, (byte) 0, 2, 0, "prison.cut.oak"), SPRUCE(Material.LOG,
			(byte) 1, 5, 0, "prison.cut.spruce"), BIRCH(Material.LOG, (byte) 2,
			3, 2, "prison.cut.birch"), JUNGLE(Material.LOG, (byte) 3, 5, 5,
			"prison.cut.jungle"), ACACIA(Material.LOG_2, (byte) 0, 7, 7,
			"prison.cut.acacia"), DARKOAK(Material.LOG_2, (byte) 1, 13, 18,
			"prison.darkoak.emerald");
	private Material material;
	private int value;
	private String permission;
	private int levelReq;
	private byte data;

	TreeData(Material m, byte data, int value, int levelReq, String permission) {
		this.material = m;
		this.data = data;
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
	 * Checks to see if the material is classified under TreeData
	 * 
	 * @param m
	 *            Material to check if it's covered under TreeData
	 * @return true if the block is classified under TreeData false if the block
	 *         isn't classified under TreeData
	 */
	public static boolean isTreeRelated(Material m) {
		for (TreeData md : values())
			if (md.getMaterial() == m)
				return true;
		return false;
	}

	/**
	 * Checks to see if a player can cut X block. If not returns why not in the
	 * form of a DataReturn
	 * 
	 * @param playerName
	 *            Players name to check it against
	 * @param mat
	 *            Material of the block being chopped
	 * @param data
	 *            Data of the chopped being broken
	 * @return DataReturn NOLEVEL if their level is too low, NOPERM if they *
	 *         haven't purchased the permission and if succeeded, SUCCESS
	 */
	public static DataReturn playerHasPermission(String playerName,
			Material mat, byte data) {
		Prisoner pri = PrisonerManager.getInstance()
				.getPrisonerInfo(playerName);
		data = (byte) convertData(mat, data);
		for (TreeData md : values())
			if (md.getMaterial() == mat && data == md.getData()) {
				if (Stats.getLevel(pri.getStat(Stats.WOODCUTTING)) < md
						.getLevelReq())
					return DataReturn.NOLEVEL;
				else if (!pri.hasPermission(md.getPermission()))
					return DataReturn.NOPERM;
				else
					return DataReturn.SUCCESS;
			}
		return null;
	}

	private static int convertData(Material mat, byte data2) {
		if (mat == Material.LOG)
			return data2 % 4;
		else if (mat == Material.LOG_2)
			return data2 % 2;
		return 0;
	}

	/**
	 * Gets the data that's associated with the log as some trees have the same
	 * material/id but a different data value
	 * 
	 * @return data associated with the log
	 */
	public byte getData() {
		return data;
	}

	/**
	 * Gets the relevant MiningData information for the material given
	 * 
	 * @param material
	 *            Material to be used for looking up the relevant MiningData
	 * @param data
	 *            Data of the block being looked up
	 * @return Returns the MiningData for the material given.
	 */
	public static TreeData getTreeDataForMaterial(Material material, byte data) {
		data = (byte) convertData(material, data);
		for (TreeData md : values())
			if (md.getMaterial() == material && md.getData() == data)
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
