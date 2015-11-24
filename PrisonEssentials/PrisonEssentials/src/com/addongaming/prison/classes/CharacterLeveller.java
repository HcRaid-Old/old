package com.addongaming.prison.classes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class CharacterLeveller implements Listener {
	@EventHandler
	public void entityDeathEvent(EntityDeathEvent event) {
		if (event.getEntity().getKiller() == null)
			return;
		Prisoner prisoner = PrisonerManager.getInstance()
				.getPrisonerInfo(event.getEntity().getKiller().getName());
		prisoner.giveCharacterExp(3);
	}
}
