package com.addongaming.hcessentials.minigames.dtcmethods;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.addongaming.hcessentials.minigames.games.DTC;

public class DTCMethods implements Runnable {

	JavaPlugin jp;
	private DTC instance;
	org.bukkit.block.Chest dtchest;

	public DTCMethods(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void run() {
		if (instance.hasStarted()) {
			BukkitScheduler sch = Bukkit.getServer().getScheduler();
			sch.scheduleSyncRepeatingTask(jp, new Runnable() {
				@Override
				public void run() {
					if (instance.getLocations() != null) {
						for (Block block : instance.getLocationZone()
								.getAllBlocks()) {
							if (block.getState() != null
									&& block.getState() instanceof InventoryHolder) {
								((InventoryHolder) block.getState())
										.getInventory().setContents(
												instance.getChest()
														.getContents());
							}
						}
					}
				}
			}

			, 0L, 20L * 60 * instance.getTimer());

		}
	}
}
