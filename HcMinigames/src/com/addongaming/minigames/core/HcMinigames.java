package com.addongaming.minigames.core;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.TagAPI;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.minigames.hub.Hub;
import com.addongaming.minigames.listeners.BlockListener;
import com.addongaming.minigames.listeners.PlayerListener;
import com.addongaming.minigames.management.Management;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.SpawnZone;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class HcMinigames extends JavaPlugin {
	private DataLog dl;
	private Management management;
	private WorldEditPlugin worldEditHook;
	private Hub hub;
	private static HcMinigames instance;

	public HcMinigames() {
		instance = this;
		ArenaProperty.setMinigames(instance);
	}

	public static HcMinigames getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		hub.stop();
		for (Arena arena : management.getArenaManagement().getAllArenas())
			if (arena.hasCurrentGame())
				arena.getCurrentGame().getLobby().shutdown();
	}

	@Override
	public void onEnable() {
		setupSerialisation();
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						hook();
						dl = HcEssentials.getDataLogger().addLogger(
								"HcMinigames - Core");
						management = new Management(instance);
						hub = new Hub(instance);
						new BlockListener(instance);
						new PlayerListener(instance);
						for (Player player : Bukkit.getOnlinePlayers())
							TagAPI.refreshPlayer(player);
					}
				}, Config.Ticks.POSTWORLD);

	}

	private void setupSerialisation() {
		ConfigurationSerialization.registerClass(SpawnZone.class);
	}

	private void hook() {
		for (Plugin plugin : getServer().getPluginManager().getPlugins())
			if (plugin instanceof WorldEditPlugin)
				worldEditHook = (WorldEditPlugin) plugin;
	}

	public DataLog getDataLogger() {
		return dl;
	}

	public HcMinigames getPlugin() {
		return this;
	}

	public Management getManagement() {
		return management;
	}

	public WorldEditPlugin getWorldEditHook() {
		return worldEditHook;
	}

	public Hub getHub() {
		return hub;
	}
}
