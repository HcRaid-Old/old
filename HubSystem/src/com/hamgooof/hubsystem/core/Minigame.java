package com.hamgooof.hubsystem.core;

import org.bukkit.Location;

public class Minigame {

	private String command;
	private Location miniGameHub;

	public Minigame(String command, Location miniGameHub) {
		this.command = command;
		this.miniGameHub = miniGameHub;
	}

	public String getCommand() {
		return command;
	}

	public Location getMiniGameHub() {
		return miniGameHub;
	}

}
