package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.management.flag.Flag;
import com.addongaming.minigames.minigames.ArenaGame.Status;

public class FlagTicker implements HcRepeat {
	private Flag flag;
	private int id;
	private boolean finished = false;

	public FlagTicker(Flag flag) {
		this.flag = flag;
	}

	@Override
	public void run() {
		if (finished || flag == null || flag.getAg() == null
				|| flag.getAg().getStatus() == Status.FINISHED) {
			finished = true;
			return;
		}
		flag.tick();
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
