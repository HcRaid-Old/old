package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyClaimedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String claimer;
	private boolean cancelled = false;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public BountyClaimedEvent(String claimer) {
		this.claimer = claimer;
	}

	public String getClaimer() {
		return this.claimer;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
