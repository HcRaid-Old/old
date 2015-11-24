package com.hamgooof.bedrockbase.core;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.hamgooof.bedrockbase.BBHandler;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class BBPlugin extends JavaPlugin {
	public static String title;
	public static String world;
	private BBHandler bbHandler;
	public static WorldGuardPlugin worldGuard;
	public static int xHomeOffset, yHomeOffset, zHomeOffset;

	@Override
	public void onEnable() {
		setupConfig();
		loadConfig();
		worldGuard = getPlugin(WorldGuardPlugin.class);
	}

	private void loadConfig() {
		FileConfiguration fc = getConfig();
		title = ChatColor.translateAlternateColorCodes('&',
				fc.getString("title"))
				+ ChatColor.RESET + " ";
		xHomeOffset = fc.getInt("homeoffset.x");
		yHomeOffset = fc.getInt("homeoffset.y");
		zHomeOffset = fc.getInt("homeoffset.z");
		int x = fc.getInt("currentx"), z = fc.getInt("currentz"), initialheight = fc
				.getInt("initialheight");
		world = fc.getString("world");
		bbHandler = new BBHandler(this, x, z, initialheight,
				fc.getStringList("permissionschematic"));
		getCommand("bb").setExecutor(bbHandler);
	}

	private void setupConfig() {
		FileConfiguration fc = getConfig();
		fc.addDefault("title", "&6[&bBB&6]");
		fc.addDefault("currentx", 0);
		fc.addDefault("currentz", 0);
		fc.addDefault("world", "bedrockworld");
		fc.addDefault("initialheight", 10);
		fc.addDefault("permissionschematic", new ArrayList<String>() {
			{
				add("hcraid.bb.first|firstbase.schematic");
			}
		});
		fc.addDefault("homeoffset.x", 0);
		fc.addDefault("homeoffset.y", 0);
		fc.addDefault("homeoffset.z", 0);
		fc.options().copyDefaults(true);
		saveConfig();
		reloadConfig();
	}

	@Override
	public void onDisable() {
	}
}
