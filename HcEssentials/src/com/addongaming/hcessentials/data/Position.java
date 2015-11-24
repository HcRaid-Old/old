package com.addongaming.hcessentials.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.addongaming.hcessentials.utils.Utils;

public class Position implements ConfigurationSerializable {
	private Location loc;

	public Position(Location location) {
		this.loc = location;
	}

	public Location getLoc() {
		return this.loc;
	}

	public static Position valueOf(Map<String, Object> map) {
		return new Position(Utils.loadLoc(String.valueOf(map.get("location"))));
	}

	public Map<String, Object> serialize() {
		Map tempMap = new HashMap();
		tempMap.put("location", Utils.locationToSaveString(this.loc));
		return tempMap;
	}
}