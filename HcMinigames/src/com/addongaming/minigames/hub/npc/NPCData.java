package com.addongaming.minigames.hub.npc;

import org.bukkit.World;

public class NPCData {
	private int id;
	private int value;
	private World world;

	public NPCData(int id, int value, World world) {
		this.id = id;
		this.value = value;
		this.world = world;
	}

	public NPCData(int id, World world) {
		this.id = id;
		this.value = -1;
		this.world = world;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	public World getWorld() {
		return world;
	}
}
