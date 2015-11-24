package com.addongaming.minigames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.addongaming.minigames.core.MinigameUser;

public class PlayerEnteredGameEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private MinigameUser user;

	public PlayerEnteredGameEvent(MinigameUser user) {
		this.user = user;
	}

	public MinigameUser getUser() {
		return user;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
