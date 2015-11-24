package com.addongaming.prison.prison;

import org.bukkit.Location;
import org.bukkit.World;

import com.addongaming.hcessentials.data.LocationZone;

public class Prison {

	private String name;
	private World world;
	private LocationZone zone;

	public Prison(String name, World world, Location min, Location max) {
		this.name = name;
		this.world = world;
		this.zone = new LocationZone(min, max);
	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		return world;
	}

	public LocationZone getZone() {
		return zone;
	}
}
