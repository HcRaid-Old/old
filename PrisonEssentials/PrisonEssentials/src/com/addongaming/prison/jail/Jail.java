package com.addongaming.prison.jail;

import org.bukkit.Location;

public class Jail {

	private int gravel;
	private Location jailLoc;
	private String name;
	private String region;
	private Location safeLoc;
	private int sponge;
	private String world;
	private String prison;

	public Jail(String name, String world, String region, int sponge,
			int gravel, Location jailLoc, Location safeLoc, String prison) {
		this.name = name;
		this.world = world;
		this.region = region;
		this.sponge = sponge;
		this.gravel = gravel;
		this.jailLoc = jailLoc;
		this.safeLoc = safeLoc;
		this.prison = prison;
	}

	public Location getJailLoc() {
		return jailLoc;
	}

	public String getName() {
		return name;
	}

	public int getNeededGravel() {
		return gravel;
	}

	public int getNeededSponge() {
		return sponge;
	}

	public String getRegion() {
		return region;
	}

	public Location getSafeLoc() {
		return safeLoc;
	}

	public String getWorld() {
		return world;
	}

	public String getPrison() {
		return prison;
	}
}
