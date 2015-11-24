package com.addongaming.overkill;

import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class Overkill extends JavaPlugin {
	private Essentials essentials;

	@Override
	public void onEnable() {
		// new StackSizeChanger().alterArmourWeaponsStackSize(3);
		essentials = (Essentials) getServer().getPluginManager().getPlugin(
				"Essentials");
		getServer().getPluginManager().registerEvents(
				new NetherHomeLimiter(essentials), this);

	}
}
