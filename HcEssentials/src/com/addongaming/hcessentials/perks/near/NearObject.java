package com.addongaming.hcessentials.perks.near;

import java.io.Serializable;

import org.bukkit.Location;

public class NearObject implements Serializable {
	private static final long serialVersionUID = -587440319980124056L;
	private final String playerName;
	private final String location;

	public NearObject(String playerName, Location location) {
		super();
		this.playerName = playerName;
		this.location = location.getWorld().getName() + " X: "
				+ location.getBlockX() + " Y: " + location.getBlockY() + " Z: "
				+ location.getBlockZ();
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getLocation() {
		return location;
	}

}
