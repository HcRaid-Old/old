package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.minigames.ArenaGame;
import com.addongaming.minigames.minigames.ArenaGame.Status;

public class GameLimit implements HcRepeat {
	private ArenaGame ag;
	private int id;
	private long secondsTillFinish, secondsDelay, initSecondsTillFinish;
	private boolean finished = false, swapTeam = false;

	public GameLimit(ArenaGame ag, long secondsTillFinish, long secondsDelay) {
		this.ag = ag;
		this.secondsTillFinish = secondsTillFinish;
		this.initSecondsTillFinish = secondsTillFinish;
		this.secondsDelay = secondsDelay;
	}

	public GameLimit(ArenaGame ag, long secondsTillFinish, long secondsDelay,
			boolean swapTeam) {
		this.ag = ag;
		this.secondsTillFinish = secondsTillFinish;
		this.initSecondsTillFinish = secondsTillFinish;
		this.secondsDelay = secondsDelay;
		this.swapTeam = swapTeam;
	}

	public void reset() {
		this.secondsTillFinish = initSecondsTillFinish;
		finished = false;
	}

	@Override
	public void run() {
		if (finished)
			return;
		if (ag.getStatus() == Status.FINISHED) {
			finished = true;
			return;
		} else {
			secondsTillFinish = secondsTillFinish - secondsDelay;
			if (secondsTillFinish == 0) {
				if (swapTeam) {
					if (ag.getLobby().getCurrentGame() != null)
						ag.getLobby().getCurrentGame().swapSide();
				} else {
					finished = true;
					ag.getLobby().exitGame();
				}
				return;
			}
			if (secondsTillFinish % 60 == 0)
				ag.messageAll(secondsTillFinish / 60 + " minute"
						+ (secondsTillFinish / 60 == 1 ? "" : "s") + " left.");
			else if (secondsTillFinish == 30 || 10 >= secondsTillFinish)
				ag.messageAll(secondsTillFinish + " seconds left.");
		}
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
