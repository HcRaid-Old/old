package com.addongaming.hcessentials.enchants.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.enchants.EnchantItem;
import com.addongaming.hcessentials.enchants.HcEnchantment;

public class LegCrusher implements HcEnchantment {

	@Override
	public ItemStack addToItem(ItemStack toEnchant, int level)
			throws IllegalArgumentException {
		if (!EnchantItem.isValid(toEnchant, getHoldingType())) {
			throw new IllegalArgumentException("Expected: "
					+ getHoldingType().name() + " found "
					+ toEnchant.getType().name());
		}
		ItemMeta im = toEnchant.getItemMeta();
		List<String> lore;
		if (im.hasLore())
			lore = new ArrayList<String>(im.getLore());
		else
			lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + "" + ChatColor.GRAY + getName() + " "
				+ EnchantItem.numToNumeral(level));
		im.setLore(lore);
		toEnchant.setItemMeta(im);
		return toEnchant;
	}

	@Override
	public String getDescription() {
		return "Crush their legs and slow them down.";
	}

	@Override
	public EnchantItem getHoldingType() {
		return EnchantItem.BOW;
	}

	@Override
	public int getLevelToEnchant(int level, ItemStack is) {
		return 0;
	}

	@Override
	public String getName() {
		return "Leg Crusher";
	}

	@Override
	public boolean loadConfig(JavaPlugin jp) {
		jp.getConfig().addDefault("enchants.legcrush.enabled", true);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		return jp.getConfig().getBoolean("enchants.legcrush.enabled");
	}

}
