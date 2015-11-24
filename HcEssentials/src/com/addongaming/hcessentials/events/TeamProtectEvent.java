package com.addongaming.hcessentials.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.addongaming.hcessentials.teams.Team;

public class TeamProtectEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String player1;
	private final String player2;
	private boolean cancelled = false;
	private final Team team;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public TeamProtectEvent(String player1, String player2, Team team) {
		this.player1 = player1;
		this.player2 = player2;
		this.team = team;
	}

	public Team getTeam() {
		return this.team;
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
