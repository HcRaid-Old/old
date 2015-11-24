package com.addongaming.hcessentials.commands.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.events.PlayerTeleportCommandEvent;

public class TeleportRunnable implements Runnable {
	private final Location to;
	private final Location from;
	private final Player player;
	private final String teleportSuceeded;
	private final String teleportFailed;
	private final Class<Teleport> calling;

	public TeleportRunnable(Location to, Player player,
			String teleportSuceeded, String teleportFailed,
			Class<Teleport> calling) {
		this.to = to;
		this.from = player.getLocation();
		this.player = player;
		this.teleportSuceeded = teleportSuceeded;
		this.teleportFailed = teleportFailed;
		this.calling = calling;
	}

	public TeleportRunnable(Player to, Player player, String teleportSuceeded,
			String teleportFailed, Class<Teleport> calling) {
		this.to = to.getLocation();
		this.from = player.getLocation();
		this.player = player;
		this.teleportSuceeded = teleportSuceeded;
		this.teleportFailed = teleportFailed;
		this.calling = calling;
	}

	@Override
	public void run() {
		if (player.getLocation().getWorld() != from.getWorld()) {
			player.sendMessage(teleportFailed
					+ "Pending teleportation cancelled.");
			return;
		}
		if (player.getLocation().distance(from) > 3) {
			player.sendMessage(teleportFailed
					+ "Pending teleportation cancelled.");
			return;
		}
		if (player.teleport(to)) {
			player.sendMessage(teleportSuceeded + "Teleporting...");
			Bukkit.getPluginManager().callEvent(
					new PlayerTeleportCommandEvent(from, to, player, calling));
		} else {
			player.sendMessage(teleportFailed
					+ "Something went wrong teleporting...");
		}
	}

}
