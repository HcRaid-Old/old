package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.addongaming.hcessentials.data.Position;

public class TheShipArena extends Arena {
	private List<Position> signPositions;
	private List<Position> guardPosition;
	private Position prisonLoc, prisonReleaseLoc;
	private HashMap<Position, Integer> chestMap;

	public TheShipArena(int id, GameMode gamemode, String arenaType) {
		super(id, gamemode, arenaType);
	}

	public TheShipArena(int id, GameMode gamemode, String arenaType,
			YamlConfiguration config) {
		super(id, gamemode, arenaType, config);
	}

	public void addPosition(Location location) {
		guardPosition.add(new Position(location));
	}

	public boolean removePosition(int id) {
		if (0 > id || id >= guardPosition.size())
			return false;
		guardPosition.remove(id);
		return true;
	}

	public Position[] getPositions() {
		return guardPosition.toArray(new Position[guardPosition.size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reload() {
		signPositions = new ArrayList<Position>();
		guardPosition = new ArrayList<Position>();
		prisonLoc = null;
		prisonReleaseLoc = null;
		if (yamlConfig == null) {
			return;
		}
		signPositions.clear();
		guardPosition.clear();
		super.reload();
		if (yamlConfig.contains("guardlocations")) {
			List<Position> positions = (List<Position>) yamlConfig
					.getList("guardlocations");
			if (!positions.isEmpty()) {
				guardPosition.addAll(positions);
			}
		}
		if (yamlConfig.contains("chestlocations"))
			chestMap = new HashMap<Position, Integer>(
					(Map<Position, Integer>) yamlConfig.getMapList(
							"chestlocations").get(0));
		if (yamlConfig.contains("signlocations")) {
			List<Position> positions = (List<Position>) yamlConfig
					.getList("signlocations");
			if (!positions.isEmpty()) {
				signPositions.addAll(positions);
			}
		}
		if (yamlConfig.contains("prisonloc"))
			prisonLoc = (Position) yamlConfig.get("prisonloc");
		if (yamlConfig.contains("prisonreleaseloc"))
			prisonReleaseLoc = (Position) yamlConfig.get("prisonreleaseloc");
		// Load data from config here
	}

	@SuppressWarnings("serial")
	@Override
	public YamlConfiguration saveToFile() {
		YamlConfiguration yaml = super.saveToFile();
		// Set-up extras here
		yaml.set("guardlocations", guardPosition);
		yaml.set("signlocations", signPositions);
		if (prisonLoc != null)
			yaml.set("prisonloc", prisonLoc);
		if (prisonReleaseLoc != null)
			yaml.set("prisonreleaseloc", prisonReleaseLoc);
		if (chestMap != null && !chestMap.isEmpty())
			yaml.set("chestlocations",
					new ArrayList<HashMap<Position, Integer>>() {
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
