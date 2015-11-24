package com.addongaming.minigames.management.scheduling;

import org.kitteh.tag.TagAPI;

import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.minigames.ArenaGame;

public class ArenaRun implements HcRepeat {
	private int id, secondsTillStart = 0;
	private ArenaGame ag;

	public ArenaRun(ArenaGame ag, int secondsTillStart) {
		this.secondsTillStart = secondsTillStart;
		this.ag = ag;
	}

	@Override
	public void run() {
		if (finished)
			return;
		if (secondsTillStart > 0) {
			secondsTillStart = secondsTillStart - 1;
		} else {
			secondsTillStart = -1;
		}
		if (secondsTillStart == 0) {
			ag.onStart();
			for (ArenaPlayer ap : ag.getArenaList())
				if (ap.getBase() != null && ap.getBase().isOnline())
					TagAPI.refreshPlayer(ap.getBase());
			secondsTillStart = -1;
			finished = true;
			return;
		}
		if (secondsTillStart % 10 == 0 || secondsTillStart == 5) {
			ag.messageAll("Starting in " + secondsTillStart + " seconds.");
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

	private boolean finished = false;

	@Override
	public boolean isFinished() {
		return finished;
	}

}
