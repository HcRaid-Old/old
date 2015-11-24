package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatLogStartEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String player1;
	private final String player2;
	private boolean cancelled = false;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public CombatLogStartEvent(String player1, String player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public boolean isPlayer(String name) {
		return player1.equalsIgnoreCase(name) || player2.equalsIgnoreCase(name);
	}

	public String getPlayer1() {
		return player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
