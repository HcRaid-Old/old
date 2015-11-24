package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.management.ScoreManagement;

public class ScoreTicker implements Runnable {

	private ScoreManagement scoreManagement;

	public ScoreTicker(ScoreManagement scoreManagement) {
		this.scoreManagement = scoreManagement;
	}

	@Override
	public void run() {
		scoreManagement.runSchedulers();
	}

}
