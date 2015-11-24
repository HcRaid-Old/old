package com.addongaming.prison.prison.warps;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.prison.Prison;

public class WarpSystem {

	private static WarpSystem warpSystem;
	public static WarpSystem getInstance() {
		return warpSystem;
	}

	private final WarpConfiguration warpConfiguration;

	public WarpSystem(JavaPlugin jp) {
		warpSystem = this;
		warpConfiguration = new WarpConfiguration(jp);
	}

	public void addWarp(String name, Location location, String prison) {
		warpConfiguration.addWarp(name, location, prison);
	}

	public void delWarp(String name, String prison) {
		warpConfiguration.delWarp(name, prison);
	}

	public Warp getWarpByNameAndPrison(String name, String prisonName) {
		for (Warp warp : getWarpByPrisonName(prisonName))
			if (warp.getName().equalsIgnoreCase(name))
				return warp;
		return null;
	}

	public Warp[] getWarpByPrison(Prison prison) {
		return warpConfiguration.getWarpsByPrison(prison);
	}

	public Warp[] getWarpByPrisonName(String prisonName) {
		return warpConfiguration.getWarpsByPrison(prisonName);
	}
}
