package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.addongaming.hcessentials.data.Position;

public class SurvivalGameArena extends Arena {
	private List<Position> spawnPositions;
	private HashMap<Position, Integer> chestMap;

	public SurvivalGameArena(int id, GameMode gamemode, String arenaType) {
		super(id, gamemode, arenaType);
	}

	public SurvivalGameArena(int id, GameMode gamemode, String arenaType,
			YamlConfiguration config) {
		super(id, gamemode, arenaType, config);
	}

	public void addPosition(Location location) {
		spawnPositions.add(new Position(location));
	}

	public boolean removePosition(int id) {
		if (0 > id || id >= spawnPositions.size())
			return false;
		spawnPositions.remove(id);
		return true;
	}

	public Position[] getPositions() {
		return spawnPositions.toArray(new Position[spawnPositions.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reload() {
		chestMap = new HashMap<Position, Integer>();
		spawnPositions = new ArrayList<Position>();
		if (yamlConfig == null) {
			return;
		}
		spawnPositions.clear();
		chestMap.clear();
		super.reload();
		if (yamlConfig.contains("spawnpositions")) {
			List<Position> positions = (List<Position>) yamlConfig
					.getList("spawnpositions");
			if (!positions.isEmpty()) {
				System.out.println("Positions is null: " + (positions == null));
				System.out.println("SpawnPositions is null: "
						+ (spawnPositions == null));
				spawnPositions.addAll(positions);
			}
		}
		if (yamlConfig.contains("chestlocations"))
			chestMap = new HashMap<Position, Integer>(
					(Map<Position, Integer>) yamlConfig.getMapList(
							"chestlocations").get(0));
		// Load data from config here
	}

	@SuppressWarnings("serial")
	@Override
	public YamlConfiguration saveToFile() {
		YamlConfiguration yaml = super.saveToFile();
		// Set-up extras here
		yaml.set("spawnpositions", spawnPositions);
		yaml.set("chestlocations", new ArrayList<HashMap<Position, Integer>>() {
			{
				this.add(chestMap);
			}
		});
		return yaml;
	}

	public HashMap<Position, Integer> getChestMap() {
		return chestMap;
	}

	public void setChests(HashMap<Position, Integer> chestMap) {
		this.chestMap = chestMap;
	}
}
