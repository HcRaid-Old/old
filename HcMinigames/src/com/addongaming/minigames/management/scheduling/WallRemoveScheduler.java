package com.addongaming.minigames.management.scheduling;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.minigames.ArenaGame;

public class WallRemoveScheduler implements Runnable {
	private LocationZone redWall;
	private LocationZone blueWall;
	private ArenaGame ag;
	private Arena arena;
	private boolean removed = false;

	public WallRemoveScheduler(LocationZone redWall, LocationZone blueWall,
			ArenaGame ag, Arena arena) {
		this.redWall = redWall;
		this.blueWall = blueWall;
		this.ag = ag;
		this.arena = arena;
	}

	@Override
	public void run() {
		if (!arena.hasCurrentGame() || arena.getCurrentGame() != ag)
			return;
		for (Block b : redWall.getAllBlocks()) {
			ag.blockBreak(b);
			b.setType(Material.AIR);
		}
		for (Block b : blueWall.getAllBlocks()) {
			ag.blockBreak(b);
			b.setType(Material.AIR);
		}
		removed = true;
		ag.messageAll("The wall has dissapeared!");
	}

	public boolean isRemoved() {
		return removed;
	}

}
