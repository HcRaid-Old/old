package com.addongaming.hcessentials.bounty;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class BountyObj {
	private int currentBounty;
	private String playerName;
	private HashSet<String> playersGivenBounty = new HashSet<String>();

	public BountyObj(String playerName, int currentBounty) {
		this.playerName = playerName;
		this.currentBounty = currentBounty;
	}

	public void addBountyGive(String name) {
		playersGivenBounty.add(name);
	}

	public int getCurrentBounty() {
		return this.currentBounty;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public HashSet<String> getPlayersGivenBounty() {
		return playersGivenBounty;
	}

	public ItemStack getPlayersHead() {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(playerName);
		is.setItemMeta(sm);
		return is;
	}

	public void incrementBounty(int amount) {
		currentBounty += amount;
	}
}
