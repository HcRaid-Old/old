package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.minigames.management.flag.Flag;
import com.addongaming.minigames.minigames.ArenaGame;

public class Arena {
	HashMap<ArenaProperty, Object> arenaProperties = new HashMap<ArenaProperty, Object>();
	protected boolean enabled = false;
	protected int id;
	protected GameMode gamemode;
	protected String arenaType;
	protected ArenaGame currentGame = null;
	// Hashmap to have multiple spawn locations as well as assign them to a
	// single team
	// K=Team id, V = TeamSpawns, object containing team spawn map.
	protected HashMap<Integer, TeamSpawns> teamSpawnMap = new HashMap<Integer, TeamSpawns>();
	protected List<Flag> flags = new ArrayList<Flag>();
	protected SpawnZone lobbyLocation;
	protected YamlConfiguration yamlConfig;

	public HashMap<ArenaProperty, Object> getArenaProperties() {
		return arenaProperties;
	}

	public final void setConfig(YamlConfiguration config) {
		this.yamlConfig = config;
	}

	public Arena(int id, GameMode gamemode, String arenaType) {
		this.id = id;
		this.gamemode = gamemode;
		this.arenaType = arenaType;
	}

	public Arena(int id, GameMode gamemode, String arenaType,
			YamlConfiguration config) {
		this.yamlConfig = config;
		this.id = id;
		this.gamemode = gamemode;
		this.arenaType = arenaType;
		reload();
	}

	/**
	 * Loads the important settings from the Arena's yml file. Child classes
	 * should call this first then set up their values
	 */
	public void reload() {
		if (yamlConfig == null)
			return;
		teamSpawnMap.clear();
		arenaProperties.clear();
		flags.clear();
		setEnabled(yamlConfig.getBoolean("enabled"));
		if (yamlConfig.contains("spawn")) {
			for (String piece : yamlConfig.getConfigurationSection("spawn")
					.getKeys(false)) {
				if (!piece.contains("team"))
					continue;
				List<SpawnZone> spawnLocations = (List<SpawnZone>) yamlConfig
						.getList("spawn." + piece);
				int team = Integer.parseInt(piece.substring(4, piece.length()));
				for (SpawnZone zone : spawnLocations) {
					addSpawnZone(team, zone);
				}
			}
			if (yamlConfig.contains("spawn.lobby")) {
				SpawnZone lobby = (SpawnZone) yamlConfig.get("spawn.lobby");
				setLobbyLocation(lobby);
			}
		}
		if (yamlConfig.contains("property")) {
			for (ArenaProperty ap : ArenaProperty.values())
				if (yamlConfig.contains("property." + ap.name())) {
					arenaProperties.put(ap,
							yamlConfig.get("property." + ap.name()));
				}

		}
		int flagCounter = 0;
		while (yamlConfig.contains("flag" + flagCounter + ".location")) {
			flags.add(new Flag(this, Utils.loadLoc(yamlConfig.getString("flag"
					+ flagCounter + ".location"))));
			flagCounter++;
		}
	}

	public void connectGame(ArenaGame ag) {
		this.currentGame = ag;
	}

	/**
	 * Override this if you need to add extras like roll-backs
	 */
	public void cancelGame() {
		currentGame = null;
	}

	public void setLobbyLocation(SpawnZone location) {
		this.lobbyLocation = location;
	}

	public HashMap<Integer, TeamSpawns> getTeamSpawnMap() {
		return teamSpawnMap;
	}

	public void addSpawnZone(int team, SpawnZone zone) {
		TeamSpawns ts = teamSpawnMap.get(team);
		if (ts == null)
			ts = new TeamSpawns();
		ts.addZone(zone);
		teamSpawnMap.put(team, ts);
	}

	public boolean removeSpawnZone(int team, int index) {
		TeamSpawns ts = teamSpawnMap.get(team);
		if (ts == null)
			return false;
		if (!ts.removeZone(index))
			return false;
		if (ts.getZones().isEmpty()) {
			teamSpawnMap.remove(team);
			return true;
		}
		teamSpawnMap.put(team, ts);
		return true;
	}

	public ArenaGame getCurrentGame() {
		return currentGame;
	}

	public boolean hasCurrentGame() {
		return currentGame != null;
	}

	public String getArenaType() {
		return arenaType;
	}

	public GameMode getGameMode() {
		return gamemode;
	}

	public void setInt(ArenaProperty ap, int i) {
		arenaProperties.put(ap, i);
	}

	public void setString(ArenaProperty ap, String i) {
		arenaProperties.put(ap, i);
	}

	public void setBoolean(ArenaProperty ap, boolean i) {
		arenaProperties.put(ap, i);
	}

	public int getInt(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return (int) arenaProperties.get(ap);
		return (int) ap.getDefaultValue();
	}

	public String getString(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return (String) arenaProperties.get(ap);
		return (String) ap.getDefaultValue();
	}

	public void removeProperty(ArenaProperty ap) {
		arenaProperties.remove(ap);
	}

	public boolean getBoolean(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return (boolean) arenaProperties.get(ap);
		return (boolean) ap.getDefaultValue();
	}

	public SpawnZone getSpawnZone(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return (SpawnZone) arenaProperties.get(ap);
		return null;
	}

	public LocationZone getLocationZone(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return (LocationZone) arenaProperties.get(ap);
		return null;
	}

	public boolean hasLocationZone(ArenaProperty ap) {
		if (arenaProperties.containsKey(ap))
			return true;
		return false;
	}

	public int getId() {
		return id;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public SpawnZone getLobbyLocation() {
		return lobbyLocation;
	}

	/**
	 * * Used to save the arena to a file, child classes should call the super
	 * method at the beginning of their processing and the config should be
	 * altered from there and returned
	 * 
	 * @return YamlConfiguration to save, child classes should edit this and
	 *         return it
	 */
	public YamlConfiguration saveToFile() {
		// TODO Auto-generated method stub
		System.out.println("Super");
		YamlConfiguration config = new YamlConfiguration();
		// Enabled
		config.set("enabled", isEnabled());
		// Gamemode
		config.set("gamemode", getGameMode().name());
		// Spawn
		HashMap<Integer, TeamSpawns> spawnMap = getTeamSpawnMap();
		for (Integer i : spawnMap.keySet()) {
			List<SpawnZone> spawnList = new ArrayList<SpawnZone>();
			for (SpawnZone lz : spawnMap.get(i).getZones())
				spawnList.add(lz);
			config.set("spawn.team" + i, spawnList);
		}
		if (getLobbyLocation() != null)
			config.set("spawn.lobby", getLobbyLocation());
		for (ArenaProperty ap : arenaProperties.keySet())
			config.set("property." + ap.name(), arenaProperties.get(ap));
		int flagCounter = 0;
		for (Flag flag : flags) {
			config.set("flag" + flagCounter + ".location",
					Utils.locationToSaveString(flag.getOrigLoc()));
			flagCounter++;
		}
		return config;
	}

	public void putProperty(ArenaProperty ap, Object property) {
		arenaProperties.put(ap, property);
	}

	public void addFlag(Location location) {
		flags.add(new Flag(this, location));
	}

	public Flag[] getFlags() {
		return flags.toArray(new Flag[flags.size()]);
	}

	public boolean removeFlag(int index) {
		if (index >= flags.size()) {
			return false;
		}
		flags.remove(index);
		return true;
	}
}
