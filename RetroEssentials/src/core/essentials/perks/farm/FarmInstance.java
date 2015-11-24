package core.essentials.perks.farm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class FarmInstance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2569700515203677535L;
	private final String name;
	final List<CropInstance> l = new ArrayList<CropInstance>();

	public FarmInstance(String name) {
		this.name = name;
	}

	public boolean isEmpty() {
		return l.isEmpty();
	}

	public String getName() {
		return name;
	}

	public void checkCrops() {
		Player p = Bukkit.getPlayer(name);
		if (p == null || !p.isOnline()) {
			for (Iterator<CropInstance> it = l.iterator(); it.hasNext();) {
				CropInstance ci = it.next();
				if (new Random().nextInt(4) == 1) {
					if (ci.canUpdate()) {
						ci.update();
					}
					if (ci.hasFinished()) {
						it.remove();
					}
				}
			}
		}
	}

	public void addCrop(Block b, Player p) {
		if (l.size() > CropAmount.getHighestAmount(p).getAmount())
			return;
		Block block = b.getRelative(BlockFace.UP);
		if (block.getType() != Material.AIR || block.getLightLevel() < 8)
			return;
		for (int x = block.getX() - 4; x < block.getX() + 4; x++) {
			for (int y = block.getZ() - 4; y < block.getZ() + 4; y++) {
				Material m = block
						.getWorld()
						.getBlockAt(
								new Location(b.getWorld(), x, b.getY() - 1, y))
						.getType();
				if (m == Material.WATER || m == Material.STATIONARY_WATER) {
					l.add(new CropInstance(b.getWorld().getName(), b.getX(), b
							.getY(), b.getZ(), new Date().getTime()));
					return;
				}
			}
		}
	}
}
