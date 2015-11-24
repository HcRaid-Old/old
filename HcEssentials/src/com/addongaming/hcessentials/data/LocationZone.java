package com.addongaming.hcessentials.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import com.addongaming.hcessentials.utils.Utils;

public class LocationZone implements ConfigurationSerializable {
	private Location min;
	private Location max;

	public LocationZone(Location loc1, Location loc2) {
		int minX, maxX;
		int minY, maxY;
		int minZ, maxZ;
		if (loc1.getBlockX() > loc2.getBlockX()) {
			minX = loc2.getBlockX();
			maxX = loc1.getBlockX();
		} else {
			minX = loc1.getBlockX();
			maxX = loc2.getBlockX();
		}
		if (loc1.getBlockY() > loc2.getBlockY()) {
			minY = loc2.getBlockY();
			maxY = loc1.getBlockY();
		} else {
			minY = loc1.getBlockY();
			maxY = loc2.getBlockY();
		}
		if (loc1.getBlockZ() > loc2.getBlockZ()) {
			minZ = loc2.getBlockZ();
			maxZ = loc1.getBlockZ();
		} else {
			minZ = loc1.getBlockZ();
			maxZ = loc2.getBlockZ();
		}
		min = loc1.getWorld().getBlockAt(minX, minY, minZ).getLocation();
		max = loc1.getWorld().getBlockAt(maxX, maxY, maxZ).getLocation();
	}

	public boolean isInZone(Location loc) {
		if (min.getWorld() != loc.getWorld())
			return false;
		if (loc.getX() >= min.getX() && loc.getX() <= max.getX())
			if (loc.getY() >= min.getY() && loc.getY() <= max.getY())
				if (loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
					return true;
				}
		return false;
	}

	public Location getMax() {
		return max;
	}

	public Location getMin() {
		return min;
	}

	public boolean isInZone(Entity entity) {
		return this.isInZone(entity.getLocation());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> myMap = new HashMap<String, Object>();
		myMap.put("min", Utils.locationToSaveString(min));
		myMap.put("max", Utils.locationToSaveString(max));
		return myMap;
	}

	public static LocationZone valueOf(Map<String, Object> map) {
		return new LocationZone(Utils.loadLoc(String.valueOf(map.get("min"))),
				Utils.loadLoc(String.valueOf(map.get("max"))));
	}

	public Block[] getAllBlocks() {
		List<Block> blockList = new ArrayList<Block>();
		for (int x = getMin().getBlockX(); x <= getMax().getBlockX(); x++) {
			for (int y = getMin().getBlockY(); y <= getMax().getBlockY(); y++) {
				for (int z = getMin().getBlockZ(); z <= getMax().getBlockZ(); z++) {
					blockList.add(getMin().getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blockList.toArray(new Block[blockList.size()]);
	}

	public String asString() {
		return "Min X:" + getMin().getBlockX() + " Y: " + getMin().getBlockY()
				+ " Z: " + getMin().getBlockZ() + " Max X:"
				+ getMax().getBlockX() + " Y: " + getMax().getBlockY() + " Z: "
				+ getMax().getBlockZ();
	}
}
