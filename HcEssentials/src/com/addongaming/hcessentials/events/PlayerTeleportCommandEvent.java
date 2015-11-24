package com.addongaming.hcessentials.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.addongaming.hcessentials.commands.teleport.Teleport;

public class PlayerTeleportCommandEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Location from, to;
	private final Player player;

	private boolean cancelled = false;
	private Class<Teleport> calling;

	public PlayerTeleportCommandEvent(Location from, Location to,
			Player player, Class<Teleport> calling) {
		this.from = from;
		this.to = to;
		this.player = player;
		this.calling = calling;
	}

	public Class<Teleport> getCalling() {
		return calling;
	}

	public Location getFrom() {
		return from;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getTo() {
		return to;
	}

	public boolean isCancelled() {
		return this.cancelled;
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
