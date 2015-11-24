package com.addongaming.minigames.management;

import com.addongaming.minigames.core.HcMinigames;

public class RollbackManagement {
	private HcMinigames minigames;

	public Rollback createRollback() {
		return new Rollback();
	}

	public RollbackManagement(HcMinigames minigames) {
		this.minigames = minigames;
	}

	public HcMinigames getMinigames() {
		return minigames;
	}
}
