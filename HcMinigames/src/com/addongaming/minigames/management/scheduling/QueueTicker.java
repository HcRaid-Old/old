package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.core.HcMinigames;

public class QueueTicker implements Runnable {
	private HcMinigames minigames;

	public QueueTicker(HcMinigames minigames) {
		this.minigames = minigames;
	}

	@Override
	public void run() {
		minigames.getManagement().getQueueManagement().tick();
	}

}
