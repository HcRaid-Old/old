package com.addongaming.prison.data.utilities;

import org.bukkit.Material;

public enum CommandData {
	BAL("/bal", "prison.cmd.bal", 0, 0, Material.EMERALD), MSG("/msg",
			"prison.cmd.msg", 0, 0, Material.SIGN), PAY("/pay",
			"prison.cmd.pay", 500, 5, Material.GOLD_INGOT), SPAWN("/spawn",
			"prison.cmd.spawn", 0, 0, Material.BED), WARP("/warp",
			"prison.cmd.warp", 1000, 10, Material.ENDER_PORTAL);

	public static CommandData getCommandByMaterial(Material type) {
		for (CommandData cd : values())
			if (cd.getMat() == type)
				return cd;
		return null;
	}

	private int charLevel;
	private String command;
	private int cost;
	private Material mat;

	private String permission;

	CommandData(String command, String permission, int cost, int charLevel,
			Material mat) {
		this.command = command;
		this.permission = permission;
		this.cost = cost;
		this.charLevel = charLevel;
		this.mat = mat;
	}

	public int getCharLevel() {
		return charLevel;
	}

	public String getCommand() {
		return command;
	}

	public int getCost() {
		return cost;
	}

	public Material getMat() {
		return mat;
	}

	public String getPermission() {
		return permission;
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
}
