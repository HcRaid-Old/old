package com.addongaming.eventman;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GmSwap extends JavaPlugin implements Listener {
	private List<String> worlds;
	private File saveFolder;

	@EventHandler
	public void playerWorldChange(PlayerChangedWorldEvent event) {
		if (event.getFrom() != null && event.getPlayer().getWorld() != null)
			if (event.getPlayer() != null && event.getPlayer().isOnline()) {
				if (!event.getPlayer().isOp()
						&& event.getPlayer().getGameMode() == GameMode.CREATIVE) {
					if (worlds.contains(event.getFrom().getName())) {
						event.getPlayer().setGameMode(GameMode.SURVIVAL);
					}
				}
			}
	}

	@Override
	public void onEnable() {
		loadConfig();
		getServer().getPluginManager().registerEvents(this, this);
		saveFolder = new File(getDataFolder(), "Inventories");
		if (!saveFolder.exists())
			saveFolder.mkdirs();
	}

	private boolean saveInventory(Player player) {
		YamlConfiguration config = new YamlConfiguration();
		config.set("inventory",
				Arrays.asList(player.getInventory().getContents()));
		config.set("armour",
				Arrays.asList(player.getInventory().getArmorContents()));
		try {
			config.save(new File(saveFolder, player.getUniqueId() + ".yml"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadInventory(Player player) {
		if (new File(saveFolder, player.getUniqueId() + ".yml").exists()) {
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(new File(saveFolder, player
							.getUniqueId() + ".yml"));
			player.getInventory().setContents(
					config.getList("inventory").toArray(
							new ItemStack[config.getList("inventory").size()]));
			player.getInventory().setArmorContents(
					config.getList("armour").toArray(
							new ItemStack[config.getList("armour").size()]));
		}
	}

	@SuppressWarnings("serial")
	private void loadConfig() {
		FileConfiguration fc = getConfig();
		fc.addDefault("worlds", new ArrayList<String>() {
			{
				add("dtc");
				add("events");
			}
		});
		fc.options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		worlds = getConfig().getStringList("worlds");
	}
}
