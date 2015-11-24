package com.addongaming.hcessentials.enchants.objects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.enchants.EnchantItem;
import com.addongaming.hcessentials.enchants.HcEnchantment;
import com.addongaming.hcessentials.utils.Utils;

public class TnTArrow implements HcEnchantment {
	private static List<String> toggled = new ArrayList<String>();
	private static HashMap<String, Date> playerTimes = new HashMap<String, Date>();
	private int enchantLevel1;
	private int enchantLevel2;
	private int enchantLevel3;
	private int randomNum;
	private static int timeBetweenTnT;

	private static boolean hasTnT(Player player, int level) {
		return Utils.count(player, Material.TNT) >= level;
	}

	public static void toggle(Player player) {
		if (!toggled.contains(player.getName())) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
					+ "TnTBow" + ChatColor.GOLD + "] " + ChatColor.AQUA
					+ "You have toggled TnT arrows on!");
			toggled.add(player.getName());
		} else {
			toggled.remove(player.getName());
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
					+ "TnTBow" + ChatColor.GOLD + "] " + ChatColor.AQUA
					+ " You have toggled TnT arrows off!");
		}
	}

	public static boolean fire(Player player, int bowLevel) {
		if (toggled.contains(player.getName())) {
			if (!hasTnT(player, bowLevel)) {
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
						+ "TnTBow" + ChatColor.GOLD + "] " + ChatColor.AQUA
						+ "You need " + bowLevel + " tnt and 1 arrow!");
				return false;
			} else if (!canTnT(player.getName())) {
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
						+ "TnTBow" + ChatColor.GOLD + "] " + ChatColor.AQUA
						+ "You cannot fire another TnT arrow for "
						+ getTimeTillTnT(player.getName()) + "!");
				return false;
			}
		} else
			return false;
		playerTimes.put(player.getName(), new Date());
		return true;
	}

	public static boolean canTnT(String playerName) {
		if (!playerTimes.containsKey(playerName))
			return true;
		return new Date(playerTimes.get(playerName).getTime() + timeBetweenTnT)
				.before(new Date());
	}

	public static String getTimeTillTnT(String playerName) {
		return new SimpleDateFormat("mm:ss").format(new Date(new Date(
				playerTimes.get(playerName).getTime() + timeBetweenTnT)
				.getTime()
				- new Date().getTime()));
	}

	public static void addToTnTTime(String playerName) {
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
		return "Blow your way into a base with style! Left click to toggle TnT.";
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
		return "TnT Arrow";
	}

	@Override
	public boolean loadConfig(JavaPlugin jp) {
		jp.getConfig().addDefault("enchants.tntbow.enabled", false);
		jp.getConfig().addDefault("enchants.tntbow.enchantingRandom", 50);
		jp.getConfig().addDefault("enchants.tntbow.enchantLevel1", 5);
		jp.getConfig().addDefault("enchants.tntbow.enchantLevel2", 20);
		jp.getConfig().addDefault("enchants.tntbow.enchantLevel3", 30);
		jp.getConfig().addDefault("enchants.tntbow.timebetweentnt", 20);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		TnTArrow.timeBetweenTnT = jp.getConfig().getInt(
				"enchants.tntbow.timebetweentnt");
		this.randomNum = jp.getConfig().getInt(
				"enchants.tntbow.enchantingRandom");
		this.enchantLevel1 = jp.getConfig().getInt(
				"enchants.tntbow.enchantLevel1");
		this.enchantLevel2 = jp.getConfig().getInt(
				"enchants.tntbow.enchantLevel2");
		this.enchantLevel3 = jp.getConfig().getInt(
				"enchants.tntbow.enchantLevel3");
		return jp.getConfig().getBoolean("enchants.tntbow.enabled");
	}

}
