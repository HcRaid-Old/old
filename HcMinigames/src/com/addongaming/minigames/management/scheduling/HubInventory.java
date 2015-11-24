package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.hub.Hub;

public class HubInventory implements Runnable {
	private MinigameUser mg;
	private Hub hub;

	public HubInventory(MinigameUser mg, Hub hub) {
		this.mg = mg;
		this.hub = hub;
	}

	@Override
	public void run() {
		if (mg.isValid()) {
			hub.populateInve(mg);
			if (mg.isTextureChanges()) {
				mg.getBase().setResourcePack(
						"http://textures.hcraid.com:8070/normal.zip");
				mg.setTextureChanges(false);
			}
		}
	}

}
