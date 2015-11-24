package com.addongaming.hcessentials.world.gen;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ChunkChecker implements Runnable {
	private int amount;

	public ChunkChecker(int amount) {
		this.amount = amount;
	}

	public void run() {
		if (WorldRegen.checking) {
			return;
		}
		WorldRegen.checking = true;
		for (int i = 0; i <= amount; i++) {
			if (WorldRegen.al.isEmpty()) {
				WorldRegen.checking = false;
				return;
			}
			for (Iterator<Chunk> ite = WorldRegen.al.iterator(); ite.hasNext();) {
				try {
					Chunk temp = ite.next();
					if (!temp.isLoaded())
						continue;
					for (int x = 0; x < 16; x++) {
						for (int y = 1; y < 128; y++) {
							for (int z = 0; z < 16; z++) {
								Block bl = temp.getBlock(x, y, z);
								for (Material m : WorldRegen.disallowed) {
									if (bl.getType() == m)
										WorldRegen.toChange.add(bl);
								}
							}
						}
					}
				} catch (ConcurrentModificationException ex) {
					WorldRegen.checking = false;
					return;
				}

			}
		}
		WorldRegen.checking = false;
	}
}
