package com.addongaming.prison.prison.warps;

import org.bukkit.Location;

import com.addongaming.prison.prison.Prison;
import com.addongaming.prison.prison.PrisonManager;

public class Warp {
	private Location location;
	private String name;
	private String prison;

	public Warp(String name, Location location, String prison) {
		this.name = name;
		this.location = location;
		this.prison = prison;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getPrison() {
		return prison;
	}

	public Prison getPrisonObject() {
		return PrisonManager.getInstance().getPrisonByName(prison);
	}

	public String getToText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getName().length(); i++)
			if (i > 2) {
				if (Character.isUpperCase(getName().charAt(i))) {
					sb.append(" " + getName().charAt(i));
				} else
					sb.append(getName().charAt(i));
			} else
				sb.append(getName().charAt(i));
		return sb.toString();
	}
}
