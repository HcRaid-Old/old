package com.addongaming.hcessentials.world.gen;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockChanger implements Runnable {
	private int maxBlocks;

	public BlockChanger(int maxBlocks) {
		this.maxBlocks = maxBlocks;
	}

	@Override
	public void run() {
		if (WorldRegen.altering)
			return;
		WorldRegen.altering = true;
		int count = 0;
		// System.out.println("Block to change size: " + Main.toChange.size());
		synchronized (WorldRegen.toChange) {
			for (Iterator<Block> iter = WorldRegen.toChange.iterator(); iter
					.hasNext(); count++) {

				if (count > maxBlocks) {
					WorldRegen.altering = false;
					System.out.println("Done " + maxBlocks + " blocks "
							+ WorldRegen.toChange.size() + " left");
					// System.out.println(NetherGen.toChange.size() + " left");
					return;
				}
				try {
					Block b = iter.next();
					WorldRegen.toChange.remove(b);
					b.setType(Material.AIR);
				} catch (ConcurrentModificationException ex) {
					System.err.println("CONCURRENT");
					break;
				}
			}
		}
		// if (count > 0)
		// System.out.println("Finished all blocks given. " + count);
		WorldRegen.altering = false;
	}

}
