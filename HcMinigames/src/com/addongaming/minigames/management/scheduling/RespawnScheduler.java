package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.minigames.ArenaGame;

public class RespawnScheduler implements Runnable {
	private ArenaPlayer player;
	private ArenaGame game;

	public RespawnScheduler(ArenaPlayer player, ArenaGame game) {
		this.player = player;
		this.game = game;
	}

	@Override
	public void run() {
		if (player.getBase().isOnline())
			game.onSpawn(player);
	}

}
