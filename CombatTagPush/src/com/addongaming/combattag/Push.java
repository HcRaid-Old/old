package com.addongaming.combattag;

import org.bukkit.plugin.java.JavaPlugin;

public class Push extends JavaPlugin {

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelAllTasks();
	}

	@Override
	public void onEnable() {
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						new PlayerCache(getInstance());
					}
				}, 20L);
	}

	protected JavaPlugin getInstance() {
		return this;
	}
}
