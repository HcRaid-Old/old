package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Player;

public class KillDelay implements Runnable {
	public KillDelay(Player player) {
		this.player = player;
	}

	private final Player player;

	@Override
	public void run() {
		if (player != null && player.isOnline())
			player.setHealth(0.0d);
	}

}
