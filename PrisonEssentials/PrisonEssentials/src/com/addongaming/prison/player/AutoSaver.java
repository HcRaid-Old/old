package com.addongaming.prison.player;

import org.bukkit.plugin.java.JavaPlugin;

public class AutoSaver {
	public AutoSaver(JavaPlugin jp) {
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						PrisonerManager.getInstance().saveAll();
					}
				}, 100L, 20 * 60 * 5L);
	}
}
