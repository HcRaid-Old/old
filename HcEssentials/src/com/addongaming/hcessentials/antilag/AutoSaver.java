package com.addongaming.hcessentials.antilag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoSaver implements Runnable {
	private JavaPlugin jp = null;
	private Player p = null;
	private World world = null;

	public AutoSaver(JavaPlugin jp) {
		this.jp = jp;
	}

	public AutoSaver(JavaPlugin jp, Player p) {
		this.jp = jp;
		this.p = p;
	}

	public AutoSaver(World world, Player p) {
		this.world = world;
		this.p = p;
	}

	@Override
	public void run() {
		if (jp != null) {
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE
					+ "[Server] Saving world data. Expect lag for a short while.");
			System.out.println("Saving worlds.");
			long count = 1;
			for (World w : jp.getServer().getWorlds()) {
				jp.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(jp, new AutoSaver(w, p),
								count++);
			}
			for (Player p : Bukkit.getOnlinePlayers())
				p.saveData();
			System.out.println("Finished saving player data.");
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE
									+ "[Server] Save Complete.");
						}
					}, count++);
		} else {
			if (p != null)
				p.sendMessage("Saving world " + world.getName());
			world.save();
			if (p != null)
				p.sendMessage("Finished saving world " + world.getName());
		}
	}

}
