package com.addongaming.hcessentials.antilag;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class AlwaysDay implements Runnable {
	private List<String> world;

	public AlwaysDay(List<String> world) {
		this.world = world;
	}

	@Override
	public void run() {
		for (World w : Bukkit.getWorlds()) {
			if (world.contains("*") || world.contains(w.getName()))
				w.setTime(4000);
		}
	}
}
