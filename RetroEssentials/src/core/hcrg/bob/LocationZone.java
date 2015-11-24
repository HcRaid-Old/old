package core.hcrg.bob;

import java.util.Random;

import org.bukkit.Location;

public class LocationZone {
	Location min;
	Location max;

	public LocationZone(Location min, Location max) {
		this.min = min;
		this.max = max;
	}

	public boolean isInZone(Location loc) {
		return loc.getBlockX() > max.getBlockX()
				&& loc.getBlockX() < min.getBlockX()
				&& loc.getBlockY() > max.getBlockY()
				&& loc.getBlockY() < min.getBlockY()
				&& loc.getBlockZ() > max.getBlockZ()
				&& loc.getBlockZ() < min.getBlockZ();
	}

	public Location getRandomLocation() {
		int xDiffer = max.getBlockX() - min.getBlockX();
		int zDiffer = max.getBlockZ() - min.getBlockZ();
		return new Location(min.getWorld(), new Random().nextInt(xDiffer),
				min.getY(), new Random().nextInt(zDiffer));
	}
}
