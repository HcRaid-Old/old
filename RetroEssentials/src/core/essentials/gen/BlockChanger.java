package core.essentials.gen;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockChanger implements Runnable {

	@Override
	public void run() {
		if (NetherGen.altering)
			return;
		NetherGen.altering = true;
		int count = 0;
		// System.out.println("Block to change size: " + Main.toChange.size());
		synchronized (NetherGen.toChange) {
			for (Iterator<Block> iter = NetherGen.toChange.iterator(); iter
					.hasNext(); count++) {

				if (count > 600) {
					NetherGen.altering = false;
					System.out.println("Done 600 blocks "
							+ NetherGen.toChange.size() + " left");
					// System.out.println(NetherGen.toChange.size() + " left");
					return;
				}
				try {
					Block b = iter.next();
					NetherGen.toChange.remove(b); // System.out.println("CHANGING TO NETHERACK");
					b.setType(Material.NETHERRACK);
				} catch (ConcurrentModificationException ex) {
					System.err.println("CONCURRENT");
					break;
				}
			}
		}
		// if (count > 0)
		// System.out.println("Finished all blocks given. " + count);
		NetherGen.altering = false;
	}

}
