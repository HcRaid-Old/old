package com.addongaming.prison.prison.warps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.prison.Prison;

public class WarpConfiguration {

	private JavaPlugin jp;
	List<Warp> warpList = new ArrayList<Warp>();

	public WarpConfiguration(JavaPlugin jp) {
		this.jp = jp;
		File file = new File(jp.getDataFolder() + File.separator + "Configs"
				+ File.separator + "warps.yml");
		loadConfig(file);
	}

	public void addWarp(String name, Location location, String prison) {
		Warp warp = new Warp(name, location, prison);
		warpList.add(warp);
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(
				jp.getDataFolder() + File.separator + "Configs"
						+ File.separator + "warps.yml"));
		yaml.set("warps." + prison + "." + name + ".location",
				Utils.locationToSaveString(location));
		try {
			yaml.save(new File(jp.getDataFolder() + File.separator + "Configs"
					+ File.separator + "warps.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		yaml.addDefault("warps.prison1.Spawn.location",
				Utils.locationToSaveString(loc));
		yaml.options().copyDefaults(true);
		try {
			yaml.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delWarp(String name, String prison) {
		for (Iterator<Warp> iter = warpList.iterator(); iter.hasNext();) {
			Warp warp = iter.next();
			if (warp.getName().equalsIgnoreCase(name)
					&& warp.getPrison().equalsIgnoreCase(prison)) {
				iter.remove();
				break;
			}
		}
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new File(
				jp.getDataFolder() + File.separator + "Configs"
						+ File.separator + "warps.yml"));
		yaml.set("warps." + prison + "." + name + ".location", null);
		try {
			yaml.save(new File(jp.getDataFolder() + File.separator + "Configs"
					+ File.separator + "warps.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Warp[] getWarpsByName(String warpName) {
		List<Warp> toReturn = new ArrayList<Warp>();
		for (Warp warp : warpList)
			if (warp.getPrison().equalsIgnoreCase(warpName))
				toReturn.add(warp);
		return toReturn.toArray(new Warp[toReturn.size()]);
	}

	public Warp[] getWarpsByPrison(Prison prison) {
		return getWarpsByPrison(prison.getName());
	}

	public Warp[] getWarpsByPrison(String prison) {
		List<Warp> toReturn = new ArrayList<Warp>();
		for (Warp warp : warpList)
			if (warp.getPrison().equalsIgnoreCase(prison)) {
				toReturn.add(warp);
			}
		return toReturn.toArray(new Warp[toReturn.size()]);
	}

	private void loadConfig(File configFile) {
		checkDefault(configFile);
		YamlConfiguration yaml = YamlConfiguration
				.loadConfiguration(configFile);
		for (String prisonName : yaml.getConfigurationSection("warps").getKeys(
				false)) {
			for (String warpName : yaml.getConfigurationSection(
					"warps." + prisonName).getKeys(false)) {
				Location location = Utils.loadLoc(yaml.getString("warps."
						+ prisonName + "." + warpName + ".location"));
				System.out.println("Loading warp:"
						+ prisonName
						+ "|"
						+ warpName
						+ "|"
						+ yaml.getString("warps." + prisonName + "." + warpName
								+ ".location"));
				warpList.add(new Warp(warpName, location, prisonName));
			}
		}
	}
}
