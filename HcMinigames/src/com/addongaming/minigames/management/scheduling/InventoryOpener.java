package com.addongaming.minigames.management.scheduling;

import org.bukkit.inventory.Inventory;

import com.addongaming.minigames.management.arena.ArenaPlayer;

public class InventoryOpener implements Runnable {
	private ArenaPlayer player;
	private Inventory inventory;

	public InventoryOpener(ArenaPlayer player, Inventory inventory) {
		this.player = player;
		this.inventory = inventory;
	}

	@Override
	public void run() {
		if (player.getBase() != null && player.getBase().isOnline())
			player.getBase().openInventory(inventory);
	}

}
