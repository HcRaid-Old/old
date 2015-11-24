package com.addongaming.hcessentials.stats.server;

import org.bukkit.Bukkit;

public class ServerStatsRunnable implements Runnable {

	@SuppressWarnings("unused")
	@Override
	public void run() {
		int players = Bukkit.getOnlinePlayers().length;
		long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
		long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
		long usedMemory = totalMemory - freeMemory;
		long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

		// log("Maximum memory: " + maxMemory + " MB");
		// log("Allocated memory: " + totalMemory + " MB");
		// log("Used memory: " + usedMemory + " MB");
		// log("Free memory: " + freeMemory + " MB");
	}

}
