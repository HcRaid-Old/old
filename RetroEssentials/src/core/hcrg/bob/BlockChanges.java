package core.hcrg.bob;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockChanges {
	private Location loc;
	private Material orig;

	public BlockChanges(Location loc, Material orig) {
		this.loc = loc;
		this.orig = orig;
	}

	public Location getLocation() {
		return loc;
	}

	public Material getMaterial() {
		return orig;
	}
}
