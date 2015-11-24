package com.addongaming.hcessentials.bounty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class BountyHeadObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<String> playerList = new ArrayList<String>();
	private String playerName;

	public BountyHeadObj(String playerName) {
		this.playerName = playerName;
	}

	public void addName(String str) {
		playerList.add(str);
	}

	public ItemStack getHead() {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(playerName);
		is.setItemMeta(sm);
		return is;
	}

	public List<String> getNames() {
		return playerList;
	}

	public void removeName(String str) {
		playerList.remove(str);
	}
}
