package core.essentials.gen;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ChunkChecker implements Runnable {

	public void run() {
		if (NetherGen.checking)
			return;
		NetherGen.checking = true;
		for (int i = 0; i <= 30; i++) {
			if (NetherGen.al.isEmpty()) {
				NetherGen.checking = false;
				return;
			}
			for (Iterator<Chunk> ite = NetherGen.al.iterator(); ite.hasNext();) {
				try {
					Chunk temp = ite.next();
					if (!temp.isLoaded())
						continue;
					for (int x = 0; x < 16; x++) {
						for (int y = 1; y < 128; y++) {
							for (int z = 0; z < 16; z++) {
								Block bl = temp.getBlock(x, y, z);
								for (Material m : NetherGen.disallowed) {
									if (bl.getType() == m)
										NetherGen.toChange.add(bl);
								}
							}
						}
					}
				} catch (ConcurrentModificationException ex) {
					NetherGen.checking = false;
					return;
				}

			}
		}
		NetherGen.checking = false;
	}
}
