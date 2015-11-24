package com.addongaming.minigames.management.killstreak;

import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.serialised.SerItemStack;

public class ItemKillStreak implements KillStreak {
	private final SerItemStack itemStack;
	private final int neededKills;

	public ItemKillStreak(ItemStack item, int neededKills) {
		itemStack = new SerItemStack(item);
		this.neededKills = neededKills;
	}

	@Override
	public Type getType() {
		return Type.ITEM;
	}

	public SerItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public int getNeededKills() {
		return neededKills;
	}

}
