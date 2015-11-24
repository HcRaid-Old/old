package com.addongaming.hcessentials.perks.headtrack;

import java.awt.Point;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.utils.Utils;

public class TrackInstance {
	private Random random;
	private Location trackeeLocation;
	private Rectangle rect;
	private Player trackee;
	private Player tracker;
	public static int randomRadius = 50;
	private boolean spinny = false;

	public TrackInstance(Player trackee, Player tracker) {
		this.random = new Random((trackee.getName() + new SimpleDateFormat(
				"yyyy.MM.dd").format(new Date())).hashCode());
		this.trackeeLocation = trackee.getLocation();
		this.rect = new Rectangle(
				(int) (trackee.getLocation().getX() + (random
						.nextInt(randomRadius * 2) - randomRadius)),
				(int) (trackee.getLocation().getY() + (random
						.nextInt(randomRadius * 2) - randomRadius)),
				randomRadius * 2, randomRadius * 2);
		this.trackee = trackee;
		this.tracker = tracker;
	}

	public Location getNextTrackingLocation(int distance) {
		if (trackee.getWorld() != tracker.getWorld())
			return null;
		double fullDistance = trackee.getLocation().distance(
				tracker.getLocation());
		if (fullDistance <= distance) {
			return trackee
					.getWorld()
					.getBlockAt(new Random().nextInt(rect.width) + rect.x, 0,
							new Random().nextInt(rect.height) + rect.y)
					.getLocation();
		} else {
			float scalarDifference = (float) (distance / fullDistance);
			return Utils.getLocationBetweenTwoLocations(tracker.getLocation(),
					trackee.getLocation(), scalarDifference);
		}
	}

	public boolean isInRectangle() {
		if (rect.contains(new Point(tracker.getLocation().getBlockX(), tracker
				.getLocation().getBlockZ())))
			return true;
		return false;
	}
}
