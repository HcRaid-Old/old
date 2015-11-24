package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EXPKeepEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String claimer;
	private boolean cancelled = false;
	private int newLevel;
	private int oldLevel;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public EXPKeepEvent(String claimer, int oldLevel, int newLevel) {
		this.claimer = claimer;
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public void setNewLevel(int newLevel) {
		this.newLevel = newLevel;
	}

	public void setOldLevel(int oldLevel) {
		this.oldLevel = oldLevel;
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
