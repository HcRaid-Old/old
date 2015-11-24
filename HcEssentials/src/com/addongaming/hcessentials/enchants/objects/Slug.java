package com.addongaming.hcessentials.enchants.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.enchants.EnchantItem;
import com.addongaming.hcessentials.enchants.HcEnchantment;

public class Slug implements HcEnchantment {
	private int enchantLevel1;
	private int enchantLevel2;
	private int enchantLevel3;
	private int randomNum;

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
		return "Turn your foes into a slug to slow them down.";
	}

	@Override
	public EnchantItem getHoldingType() {
		return EnchantItem.BOW;
	}

	@Override
	public int getLevelToEnchant(int level, ItemStack is) {
		if (!EnchantItem.isValid(is, getHoldingType()))
			return 0;
		int chance = new Random().nextInt(randomNum);
		if (chance == 1) {
			if (level >= enchantLevel3)
				return 3;
			else if (level >= enchantLevel2)
				return 2;
			else if (level >= enchantLevel1)
				return 1;
		}
		return 0;
	}

	@Override
	public String getName() {
		return "Slug";
	}

	@Override
	public boolean loadConfig(JavaPlugin jp) {
		jp.getConfig().addDefault("enchants.slug.enabled", true);
		jp.getConfig().addDefault("enchants.slug.enchantingRandom", 50);
		jp.getConfig().addDefault("enchants.slug.enchantLevel1", 5);
		jp.getConfig().addDefault("enchants.slug.enchantLevel2", 17);
		jp.getConfig().addDefault("enchants.slug.enchantLevel3", 27);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		this.randomNum = jp.getConfig().getInt(
				"enchants.slug.enchantingRandom");
		this.enchantLevel1 = jp.getConfig().getInt(
				"enchants.slug.enchantLevel1");
		this.enchantLevel2 = jp.getConfig().getInt(
				"enchants.slug.enchantLevel2");
		this.enchantLevel3 = jp.getConfig().getInt(
				"enchants.slug.enchantLevel3");
		return jp.getConfig().getBoolean("enchants.slug.enabled");
	}

}
