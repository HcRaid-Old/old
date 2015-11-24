package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemRecycledEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String recycler;

	public ItemRecycledEvent(String recycler) {
		this.recycler = recycler;
	}

	public String getRecycler() {
		return this.recycler;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
