package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.minigames.TheShip;
import com.addongaming.minigames.minigames.ship.ShipPlayer;

public class QuarryUpdater implements HcRepeat {
	private int id;
	private boolean finished = false;
	private TheShip theShip;

	public QuarryUpdater(TheShip theShip) {
		this.theShip = theShip;
	}

	@Override
	public void run() {
		if (finished)
			return;
		for (ArenaPlayer ap : theShip.getArenaList())
			if (ap instanceof ShipPlayer) {
				ShipPlayer sp = (ShipPlayer) ap;
				if (sp.isValid() && sp.getQuarry() != null
						&& sp.getQuarry().isValid()) {
					sp.getBase().setCompassTarget(
							sp.getQuarry().getBase().getLocation());
				}
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

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

}
