package com.addongaming.hcessentials.raiding.objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Location;

public class Raid {
	private boolean cancelled = false;
	private final long cooldown;
	private long duration;
	private final Location loc;
	private Date startDate;

	/**
	 * 
	 * @param loc
	 *            Location of the raid
	 * @param startDate
	 *            Start date/time of the raid
	 * @param duration
	 *            Duration until it expires
	 * @param cooldown
	 *            Cooldown to wait after expirery before another can be created
	 */
	public Raid(Location loc, Date startDate, long duration, long cooldown) {
		this.loc = loc;
		this.startDate = startDate;
		this.duration = duration;
		this.cooldown = cooldown;
		System.out.println("DURATION: " + msToMins(duration) + " COOLDOWN: "
				+ msToMins(cooldown));
	}

	public boolean canCancel() {
		return !(this.cancelled || hasFinished());
	}

	public void cancel() {
		if (this.cancelled || hasFinished())
			return;
		this.cancelled = true;
		startDate = new Date();
		duration = 0;
	}

	/**
	 * Gets whether another raid home can be set for the player Is calculated
	 * by: starttime+duration+cooldown is before the current time
	 * 
	 * @return true if another can be created, false if not
	 */
	public boolean canCreateAnother() {
		return new Date(startDate.getTime() + duration + cooldown)
				.before(new Date());
	}

	/**
	 * Gets the raid location for teleporting
	 * 
	 * @return
	 */
	public Location getLocation() {
		return loc;
	}

	public String getTimeBeforeAnother() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		Date difference = new Date((startDate.getTime() + duration + cooldown)
				- (new Date().getTime()));
		return sdf.format(difference);
	}

	public String getTimeRemaining() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		Date difference = new Date((startDate.getTime() + duration)
				- (new Date().getTime()));
		return sdf.format(difference);
	}

	/**
	 * Gets whether the raid home has finished or not.
	 * 
	 * @return true if finished, false if not.
	 */
	public boolean hasFinished() {
		return new Date(startDate.getTime() + duration).before(new Date());
	}

	private String msToMins(long duration2) {
		return duration2 / 60000 + "";
	}
}
