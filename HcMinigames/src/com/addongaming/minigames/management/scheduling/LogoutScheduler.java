package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.core.HcMinigames;

public class LogoutScheduler implements Runnable {
	private String playerName;
	private HcMinigames minigames;

	public LogoutScheduler(String playerName, HcMinigames minigames) {
		this.playerName = playerName;
		this.minigames = minigames;
	}

	@Override
	public void run() {
		if (minigames.getManagement().getQueueManagement()
				.hasPlayer(playerName)) {
			minigames.getManagement().getQueueManagement()
					.removePlayerFromAll(playerName);
		}
	}
}
