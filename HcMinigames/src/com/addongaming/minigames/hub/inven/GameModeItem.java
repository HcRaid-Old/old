package com.addongaming.minigames.hub.inven;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.addongaming.minigames.management.arena.GameMode;

public class GameModeItem {
	private Material material;
	private String displayName;
	private List<String> lore = new ArrayList<String>();
	private GameMode gameMode;

	public GameModeItem(Material material, String displayName,
			List<String> lore, GameMode gameMode) {
		this.material = material;
		this.displayName = ChatColor.translateAlternateColorCodes('&',
				displayName);
		for (String str : lore)
			this.lore.add(ChatColor.translateAlternateColorCodes('&', str));
		this.gameMode = gameMode;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<String> getLore() {
		return lore;
	}

	public Material getMaterial() {
		return material;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof ItemStack))
			return super.equals(obj);
		ItemStack is = (ItemStack) obj;
		if (is.getType() == material
				&& is.hasItemMeta()
				&& is.getItemMeta().hasDisplayName()
				&& is.getItemMeta().getDisplayName()
						.equalsIgnoreCase(displayName))
			return true;
		return super.equals(obj);
	}

	public ItemStack getItemStack() {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
