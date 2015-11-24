package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BountyIssueEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String issuer;

	public BountyIssueEvent(String issuer) {
		this.issuer = issuer;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
