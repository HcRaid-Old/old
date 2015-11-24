package com.addongaming.hcessentials.afk;

import org.bukkit.entity.Player;

public class AFKRunnable implements Runnable {
	private Player player;
	private int taskId;
	private int moveCounter = 0;
	private int yawPitchCounter = 0;
	private int maxCounter;
	private int x, y, z;
	private YawPitch yawPitch;

	public AFKRunnable(Player player, int minsTillKick) {
		this.player = player;
		this.maxCounter = minsTillKick;
		this.x = player.getLocation().getBlockX();
		this.y = player.getLocation().getBlockY();
		this.z = player.getLocation().getBlockZ();
		yawPitch = new YawPitch(player.getLocation().getYaw(), player
				.getLocation().getPitch());
	}

	public void resetCounter() {
		moveCounter = 0;
		yawPitchCounter = 0;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void run() {
		if (new YawPitch(player.getLocation().getYaw(), player.getLocation()
				.getPitch()).matches(yawPitch)) {
			yawPitchCounter++;
		} else {
			yawPitchCounter = 0;
			yawPitch = new YawPitch(player.getLocation().getYaw(), player
					.getLocation().getPitch());
		}
		if (player.getLocation().getBlockX() == x
				&& player.getLocation().getBlockY() == y
				&& player.getLocation().getBlockZ() == z) {
			moveCounter++;
		} else {
			this.x = player.getLocation().getBlockX();
			this.y = player.getLocation().getBlockY();
			this.z = player.getLocation().getBlockZ();
			moveCounter = 0;
		}
		if (yawPitchCounter >= maxCounter || moveCounter >= maxCounter) {
			player.kickPlayer("You have been AFK for " + maxCounter
					+ " minutes.");
		}
	}
}
