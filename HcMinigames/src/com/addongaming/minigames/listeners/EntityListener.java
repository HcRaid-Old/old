package com.addongaming.minigames.listeners;

import org.bukkit.event.Listener;

import com.addongaming.minigames.core.HcMinigames;

public class EntityListener implements Listener {
	private HcMinigames minigames;

	public EntityListener(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getServer().getPluginManager()
				.registerEvents(this, minigames);
	}
}
