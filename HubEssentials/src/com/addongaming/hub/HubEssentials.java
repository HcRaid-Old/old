package com.addongaming.hub;

import org.bukkit.plugin.java.JavaPlugin;

public class HubEssentials extends JavaPlugin {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new PotionEffects(this),
				this);
		getServer().getPluginManager().registerEvents(new PlayerEvents(this),
				this);
	}
}
