package com.addongaming.prison.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PickPocketEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player pickpocket;
	private Player pickpocketed;
	private boolean succeeded;

	public PickPocketEvent(Player pickpocket, Player pickpocketed,
			boolean succeeded) {
		this.pickpocket = pickpocket;
		this.pickpocketed = pickpocketed;
		this.succeeded = succeeded;
	}

	public Player getPickpocket() {
		return pickpocket;
	}

	public Player getPickpocketed() {
		return pickpocketed;
	}

	public boolean isSucceeded() {
		return succeeded;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
