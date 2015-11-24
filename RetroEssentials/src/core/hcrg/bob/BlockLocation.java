package core.hcrg.bob;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockLocation {
	private final int maxCount;
	private int currentCount;
	private final Location fallingLocation;
	private final Material material;

	public BlockLocation(int maxCount, int x, int y, int z, Material material) {
		super();
		this.maxCount = maxCount;
		this.currentCount = maxCount;
		this.fallingLocation = BoB.world.getBlockAt(x, y, z).getLocation();
		this.material = material;
	}

	public void onTick() {
		currentCount--;
		if (currentCount < 1) {
			currentCount = maxCount;
			fallingLocation.getWorld().spawnFallingBlock(fallingLocation,
					material, (byte) 0);
		}
	}
}
