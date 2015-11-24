package com.addongaming.prison.stats;

import org.bukkit.plugin.java.JavaPlugin;

public class StatManager {
	FarmingStats fStats;
	MiningStats mStats;
	WoodcuttingStats wStats;

	public StatManager(JavaPlugin jp) {
		mStats = new MiningStats();
		jp.getServer().getPluginManager().registerEvents(mStats, jp);
		wStats = new WoodcuttingStats();
		jp.getServer().getPluginManager().registerEvents(wStats, jp);
		fStats = new FarmingStats();
		jp.getServer().getPluginManager().registerEvents(fStats, jp);
	}

}
