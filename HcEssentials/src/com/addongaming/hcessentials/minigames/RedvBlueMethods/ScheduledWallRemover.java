package com.addongaming.hcessentials.minigames.RedvBlueMethods;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.data.LocationZone;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ScheduledWallRemover implements Runnable {

	public LocationZone redWall;
	public LocationZone blueWall;
	public int timer;
	public boolean ready = false;
	JavaPlugin jp;

	public ScheduledWallRemover(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void run() {
		if (ready == true) {
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(jp, new Runnable() {
				@Override
				public void run() {
					for (Block b : redWall.getAllBlocks()) {
						b.setType(Material.AIR);
					}
					for (Block b : blueWall.getAllBlocks()) {
						b.setType(Material.AIR);
					}
					System.out.println("DONE TASK");
				}
			}, 20L * 60 * timer);
		}
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public LocationZone getRedWall() {
		return redWall;
	}

	public void setRedWall(LocationZone redWall) {
		this.redWall = redWall;
	}

	public LocationZone getBlueWall() {
		return blueWall;
	}

	public void setBlueWall(LocationZone blueWall) {
		this.blueWall = blueWall;
	}

	public boolean getIfReady() {
		return ready;
	}

	public void setReady(boolean newready) {
		ready = newready;
	}
}
