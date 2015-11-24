package com.addongaming.hcessentials.antilag;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkUnloader implements Runnable {
	private JavaPlugin jp;
	private Player p = null;

	public ChunkUnloader(JavaPlugin jp) {
		this.jp = jp;
	}

	public ChunkUnloader(JavaPlugin jp, Player p) {
		this.jp = jp;
		this.p = p;
	}

	@Override
	public void run() {

		int counter = 0;
		for (World w : jp.getServer().getWorlds()) {
			Chunk[] chunkie = w.getLoadedChunks();

			int i = new Random().nextInt(2);
			int fin = chunkie.length / 2;
			int start = 0;
			if (i == 1) {
				start = fin;
				fin = chunkie.length;
			}
			if (p != null) {
				start = 0;
				fin = chunkie.length;
			}
			try {
				for (; start != fin; start++)
					if (chunkie[start].unload(true, true))
						counter++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (p != null)
			p.sendMessage("Chunks unloaded: " + counter);
		// System.out.println("Chunks unloaded: " + counter);

	}
}
