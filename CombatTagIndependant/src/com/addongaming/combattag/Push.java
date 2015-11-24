package com.addongaming.combattag;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.combat.antilogging.CombatLogging;
import com.addongaming.hcessentials.combat.antilogging.CombatThread;

public class Push extends JavaPlugin {
	private CombatLogging combatLog = null;

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelAllTasks();
	}

	@Override
	public void onEnable() {
		setupConfig();
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						new PlayerCache(getInstance());
					}
				}, 20L);
		PlayerCache.region = getConfig().getString("pushregion");
		if (getConfig().getBoolean("combatlog.enabled")) {
			combatLog = new CombatLogging(getConfig().getInt(
					"combatlog.timeout"));
			getCommand("combatlog").setExecutor(combatLog);
			getServer().getPluginManager().registerEvents(combatLog, this);
			Config.Combat.enderPearlsInCombat = getConfig().getBoolean(
					"combatlog.enderpearlteleport");
			Config.Combat.teleportInCombat = getConfig().getBoolean(
					"combatlog.anyteleport");
			getServer().getScheduler().scheduleSyncRepeatingTask(this,
					new CombatThread(), 50, 20);
		}
	}

	private void setupConfig() {
		FileConfiguration fc = getConfig();
		fc.addDefault("pushregion", "innerspawn");
		fc.addDefault("combatlog.enabled", Boolean.TRUE);
		fc.addDefault("combatlog.enderpearlteleport", Boolean.FALSE);
		fc.addDefault("combatlog.timeout", 30);
		fc.addDefault("combatlog.anyteleport", Boolean.FALSE);
		fc.options().copyDefaults(true);
		saveConfig();
	}

	protected JavaPlugin getInstance() {
		return this;
	}
}
