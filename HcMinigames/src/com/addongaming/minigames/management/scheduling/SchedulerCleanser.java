package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.core.HcMinigames;

public class SchedulerCleanser implements Runnable {
	private HcMinigames minigames;

	public SchedulerCleanser(HcMinigames minigames) {
		this.minigames = minigames;
	}

	int counter = 0;

	@Override
	public void run() {
		minigames.getManagement().getSchedulerManagement().cleanse();
		counter++;
		if (counter % 5 == 0)
			minigames.getManagement().getQueueManagement().cleanse();
		if (counter > 100)
			counter = 0;
	}
}
