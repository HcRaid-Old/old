package core.hcrg.bob;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class GeneratedBlock {

	private Location locationToFall;
	private int maxTicks;
	private Material material;
	private int currentTicks;
	private byte byt;

	public GeneratedBlock(Material material, int x, int y, int z, int maxTicks) {
		this.material = material;
		this.locationToFall = new Location(Bukkit.getWorld("minigames"), x, y,
				z);
		this.maxTicks = maxTicks;
		currentTicks = maxTicks;
		this.byt = (byte) 0;
	}

	public GeneratedBlock(Material material, int x, int y, int z, int maxTicks,
			int byt) {
		this.material = material;
		this.locationToFall = new Location(Bukkit.getWorld("minigames"), x, y,
				z);
		this.maxTicks = maxTicks;
		currentTicks = maxTicks;
		this.byt = (byte) byt;
	}

	public void onTick() {
		currentTicks -= 5;
		if (currentTicks == 0) {
			currentTicks = maxTicks;
			locationToFall.getWorld().spawnFallingBlock(locationToFall,
					material, byt);
		}
	}
}
