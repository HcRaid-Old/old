package com.addongaming.prison.prison;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;

public class PrisonManager {

	private static PrisonManager prisonManager;
	public static PrisonManager getInstance() {
		return prisonManager;
	}

	private JavaPlugin jp;

	private List<Prison> prisonList = new ArrayList<Prison>();

	public PrisonManager(JavaPlugin jp) {
		prisonManager = this;
		this.jp = jp;
		load(new File(jp.getDataFolder() + File.separator + "Configs"
				+ File.separator + "prisons.yml"));
	}

	private void checkDefault(File configFile) {
		if (!configFile.getParentFile().exists())
			configFile.getParentFile().mkdirs();
		if (!configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		Location loc = Bukkit.getWorld("world").getBlockAt(0, 0, 0)
				.getLocation();
		YamlConfiguration yaml = YamlConfiguration
				.loadConfiguration(configFile);
		yaml.addDefault("prisons.Prison1.name", "Prison 1");
		yaml.addDefault("prisons.Prison1.world", "world");
		yaml.addDefault("prisons.Prison1.topcorner",
				Utils.locationToSaveString(loc));
		yaml.addDefault("prisons.Prison1.bottomcorner",
				Utils.locationToSaveString(loc));
		yaml.options().copyDefaults(true);
		try {
			yaml.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Prison getPrison(Entity entity) {
		for (Prison prison : prisonList)
			if (prison.getZone().isInZone(entity))
				return prison;
		return null;
	}

	public Prison getPrison(Location loc) {
		for (Prison prison : prisonList)
			if (prison.getZone().isInZone(loc))
				return prison;
		return null;
	}

	public Prison getPrisonByName(String name) {
		for (Prison prison : prisonList)
			if (prison.getName().equalsIgnoreCase(name))
				return prison;
		return null;
	}

	private void load(File configFile) {
		checkDefault(configFile);
		YamlConfiguration yaml = YamlConfiguration
				.loadConfiguration(configFile);
		for (String part1 : yaml.getConfigurationSection("prisons").getKeys(
				false)) {
			// prisons.Prison1
			String name = yaml.getString("prisons." + part1 + ".name");
			World world = Bukkit.getWorld(yaml.getString("prisons." + part1
					+ ".world"));
			Location locMax = Utils.loadLoc(yaml.getString("prisons." + part1
					+ ".topcorner"));
			Location locMin = Utils.loadLoc(yaml.getString("prisons." + part1
					+ ".bottomcorner"));
			prisonList.add(new Prison(name, world, locMin, locMax));
		}
	}
}
