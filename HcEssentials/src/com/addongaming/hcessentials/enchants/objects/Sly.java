package com.addongaming.hcessentials.enchants.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.enchants.EnchantItem;
import com.addongaming.hcessentials.enchants.HcEnchantment;

public class Sly implements HcEnchantment {
	public static List<String> hiddenPlayers = new ArrayList<String>();
	private static HashMap<String, Date> playerTimes = new HashMap<String, Date>();
	private int enchantLevel1;
	private int randomNum;
	private static int timeBetweenSly;

	public static boolean canSly(String playerName) {
		if (!playerTimes.containsKey(playerName))
			return true;
		return new Date(playerTimes.get(playerName).getTime() + timeBetweenSly)
				.before(new Date());
	}

	public static String getTimeTillCanSly(String playerName) {
		return new SimpleDateFormat("ss").format(new Date(new Date(playerTimes
				.get(playerName).getTime() + timeBetweenSly).getTime()
				- new Date().getTime()));
	}

	public static void addToSlyTime(String playerName) {
		playerTimes.put(playerName, new Date());
	}

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
		return "Lets you turn invisible, but your boots will pay a toll.";
	}

	@Override
	public EnchantItem getHoldingType() {
		return EnchantItem.BOOTS;
	}

	@Override
	public int getLevelToEnchant(int level, ItemStack is) {
		if (!EnchantItem.isValid(is, getHoldingType()))
			return 0;
		int chance = new Random().nextInt(randomNum);
		if (chance == 1) {
			if (level > enchantLevel1)
				return 1;
		}
		return 0;
	}

	@Override
	public String getName() {
		return "Sly";
	}

	@Override
	public boolean loadConfig(JavaPlugin jp) {
		jp.getConfig().addDefault("enchants.sly.enabled", false);
		jp.getConfig().addDefault("enchants.sly.enchantingRandom", 50);
		jp.getConfig().addDefault("enchants.sly.enchantLevel1", 5);
		jp.getConfig().addDefault("enchants.sly.timebetweensly", 20);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		this.randomNum = jp.getConfig().getInt("enchants.sly.enchantingRandom");
		this.enchantLevel1 = jp.getConfig()
				.getInt("enchants.sly.enchantLevel1");
		Sly.timeBetweenSly = jp.getConfig().getInt(
				"enchants.sly.timeBetweenSly");
		if (jp.getConfig().getBoolean("enchants.sly.enabled")) {
			jp.getServer().getScheduler()
					.scheduleSyncRepeatingTask(jp, new SlyTimer(), 0, 7l);
		}
		return jp.getConfig().getBoolean("enchants.sly.enabled");
	}

}
