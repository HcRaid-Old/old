package com.addongaming.hcessentials.commands.teleport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.addongaming.hcessentials.events.PlayerTeleportCommandEvent;

public interface Teleport extends Listener {
	@EventHandler
	public void onTeleport(PlayerTeleportCommandEvent event);
}
