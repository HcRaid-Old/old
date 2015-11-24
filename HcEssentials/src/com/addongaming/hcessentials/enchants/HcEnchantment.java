package com.addongaming.hcessentials.enchants;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface HcEnchantment {
	public boolean loadConfig(JavaPlugin jp);

	public ItemStack addToItem(ItemStack toEnchant, int level)
			throws IllegalArgumentException;

	public String getDescription();

	public EnchantItem getHoldingType();

	public int getLevelToEnchant(int levelsUsed, ItemStack is);

	public String getName();
}
