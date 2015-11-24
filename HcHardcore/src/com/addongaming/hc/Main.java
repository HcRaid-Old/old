package com.addongaming.hc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		createFile(event.getEntity().getName());
		int lives = getLives(event.getEntity().getName());
		if (lives == 1 && !event.getEntity().isOp()) {
			event.getEntity().kickPlayer(
					ChatColor.RED + "You have died 3 times.");
		} else {
			event.getEntity().sendMessage(
					"You have " + (lives - 1) + " lives left.");
		}
		writeFile(event.getEntity().getName(), lives - 1);
	}

	@EventHandler
	public void playerLogin(final PlayerLoginEvent event) {
		createFile(event.getPlayer().getName());
		if (getLives(event.getPlayer().getName()) == 0
				&& !event.getPlayer().isOp()) {
			event.setKickMessage(ChatColor.RED + "You have died 3 times.");
			event.setResult(Result.KICK_OTHER);
		} else {
			if (event.getResult() == Result.ALLOWED
					&& !event.getPlayer().hasPlayedBefore()) {
				getServer().getScheduler().scheduleSyncDelayedTask(this,
						new Runnable() {

							@Override
							public void run() {
								event.getPlayer().teleport(
										new Location(Bukkit.getWorld("event1"),
												259, 142, 227));
							}
						}, 20l);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(new Location(Bukkit.getWorld("event1"), 259,
				142, 227));
	}

	private void writeFile(String name, int lives) {
		File file = new File(getDataFolder() + File.separator + name + ".txt");
		try {
			file.createNewFile();
			PrintWriter pw = new PrintWriter(file);
			pw.println(lives);
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private int getLives(String name) {
		try {
			Scanner scan = new Scanner(new File(getDataFolder()
					+ File.separator + name + ".txt"));
			int tr = Integer.parseInt(scan.nextLine());
			scan.close();
			System.out.println("Lives: " + tr);
			return tr;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	private void createFile(String name) {
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		File file = new File(getDataFolder() + File.separator + name + ".txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
				PrintWriter pw = new PrintWriter(file);
				pw.println("3");
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
