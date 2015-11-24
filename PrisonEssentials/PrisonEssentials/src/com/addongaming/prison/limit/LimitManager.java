package com.addongaming.prison.limit;

import org.bukkit.plugin.java.JavaPlugin;

public class LimitManager {
	private JavaPlugin jp;

	public LimitManager(JavaPlugin jp) {
		this.jp = jp;
		enable();
	}

	private void enable() {
		jp.getServer().getPluginManager()
				.registerEvents(new MiningLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new WoodcuttingLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new CraftingLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new InventoryLimiter(), jp);
		jp.getServer().getPluginManager().registerEvents(new AreaLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new FarmingLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new DyingLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new BMCraftingLimiter(), jp);
		jp.getServer().getPluginManager()
				.registerEvents(new BMWieldingLimiter(), jp);
	}
}
