package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;

public class TeamSpawns {
	private List<SpawnZone> spawnZones = new ArrayList<SpawnZone>();

	public void addZone(SpawnZone newZone) {
		spawnZones.add(newZone);
	}

	public List<SpawnZone> getZones() {
		return spawnZones;
	}

	public boolean removeZone(int index) {
		if (spawnZones.size() <= index)
			return false;
		spawnZones.remove(index);
		return true;
	}

	public Location getRandomLocation() {
		Random r = new Random();
		SpawnZone sz = spawnZones.get(r.nextInt(spawnZones.size()));
		return sz.getRandomFreeLocation();
	}
}
