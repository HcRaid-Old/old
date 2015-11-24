package com.addongaming.hcessentials.data;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialItems {
	public static enum SpecialTypes {
		COMMON, SPECIAL, LEGENDARY, UBER
	};

	public static HashMap<Enchantment, Integer> getRandomWeaponEnchantments(
			SpecialTypes st) {
		HashMap<Enchantment, Integer> hm = new HashMap<Enchantment, Integer>();
		Random r = new Random();
		switch (st) {
		case COMMON:
			hm.put(Enchantment.DAMAGE_ALL, 3);
			if (r.nextInt(3) == 1)
				hm.put(Enchantment.KNOCKBACK, r.nextInt(10) > 7 ? 2 : 1);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.LOOT_BONUS_MOBS, r.nextInt(10) > 7 ? 2 : 1);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 2 : 1);
			break;
		case SPECIAL:
			hm.put(Enchantment.DAMAGE_ALL, 4);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.LOOT_BONUS_MOBS, r.nextInt(10) > 7 ? 3 : 2);
			if (r.nextInt(3) == 1)
				hm.put(Enchantment.FIRE_ASPECT, r.nextInt(10) > 7 ? 3 : 2);
			if (r.nextInt(5) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 3 : 2);
			break;
		case LEGENDARY:
			hm.put(Enchantment.DAMAGE_ALL, r.nextInt(10) > 5 ? 5 : 4);
			hm.put(Enchantment.LOOT_BONUS_MOBS, r.nextInt(10) > 7 ? 4 : 3);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.FIRE_ASPECT, r.nextInt(10) > 7 ? 4 : 3);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 4 : 3);
			break;
		case UBER:
			hm.put(Enchantment.DAMAGE_ALL, r.nextInt(10) > 5 ? 6 : 5);
			hm.put(Enchantment.LOOT_BONUS_MOBS, 4);
			hm.put(Enchantment.KNOCKBACK, 2);
			hm.put(Enchantment.FIRE_ASPECT, r.nextInt(10) > 7 ? 5 : 4);
			hm.put(Enchantment.DURABILITY, r.nextInt(2) + 5);
			break;
		}
		return hm;
	}

	public static String getRandomWeaponName(SpecialTypes st) {
		String weaponName = "";
		// TODO Text (first/secondwords) are placeholders.
		String[][] firstWords = {
				{ "Weak", "Flimsy", "Poorly crafted", "Warm", "Dull", "Common",
						"Cool", "Light" },
				{ "Strong", "Rare", "Sharpened", "Dual Wielded",
						"Custom crafted", "Heavy" },
				{ "Master crafted", "Bejewelled", "Dark", "Legendary",
						"Gleaming", "Burning", "Freezing" },
				{ "Mythological", "Glorious", "Furious", "Infinite",
						"Everlasting", "Resplendent", "Possessed", "Zealous",
						"Corrupted" } };
		String[][] secondWords = {
				{ "Machete", "Dagger", "Shiv", "Knife", "Gauntlet", "Sword",
						"Banana", "Butter Knife" },
				{ "Saber", "Pike", "Shortsword", "Broadsword", "Rapier" },
				{ "War-Hammer", "Great-Sword", "Claymore", "Katana", "Mace" },
				{ "Fury", "Righteousness", "Honour", "Justice", "Light",
						"Repentance", "Destroyer", "Defiler", "Scaptre" } };
		Random r = new Random();
		switch (st) {
		case COMMON:
			weaponName += firstWords[0][r.nextInt(firstWords[0].length)] + " ";
			weaponName += secondWords[0][r.nextInt(secondWords[0].length)];
			return ChatColor.BLUE + "[C] " + weaponName + ChatColor.RESET;
		case SPECIAL:
			weaponName += firstWords[1][r.nextInt(firstWords[1].length)] + " ";
			weaponName += secondWords[1][r.nextInt(secondWords[1].length)];
			return ChatColor.LIGHT_PURPLE + "[S] " + weaponName
					+ ChatColor.RESET;
		case LEGENDARY:
			weaponName += firstWords[2][r.nextInt(firstWords[2].length)] + " ";
			weaponName += secondWords[2][r.nextInt(secondWords[2].length)];
			return ChatColor.RED + "[L] " + weaponName + ChatColor.RESET;
		case UBER:
			weaponName += firstWords[3][r.nextInt(firstWords[3].length)] + " ";
			weaponName += secondWords[3][r.nextInt(secondWords[3].length)];
			return ChatColor.DARK_PURPLE + "[" + ChatColor.RED + "U"
					+ ChatColor.DARK_PURPLE + "] " + weaponName
					+ ChatColor.RESET;
		}
		return null;
	}

	public static ItemStack generateArmourPiece(Material mat, SpecialTypes st,
			String firstWord) {
		ItemStack is = new ItemStack(mat);
		HashMap<Enchantment, Integer> hmMap = getRandomArmourEnchantments(st);
		is.addUnsafeEnchantments(hmMap);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(getRandomArmourSecondName(st,
				Enchantable.getItemType(mat), firstWord));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack generateSword(Material mat, SpecialTypes st) {
		ItemStack is = new ItemStack(mat);
		HashMap<Enchantment, Integer> hmMap = getRandomWeaponEnchantments(st);
		is.addUnsafeEnchantments(hmMap);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(getRandomWeaponName(st));
		is.setItemMeta(im);
		return is;
	}

	public static HashMap<Enchantment, Integer> getRandomArmourEnchantments(
			SpecialTypes st) {
		HashMap<Enchantment, Integer> hm = new HashMap<Enchantment, Integer>();
		Random r = new Random();
		switch (st) {
		case COMMON:
			hm.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			if (r.nextInt(3) == 1)
				hm.put(Enchantment.THORNS, 1);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.PROTECTION_FIRE, r.nextInt(10) > 7 ? 2 : 1);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 2 : 1);
			break;
		case SPECIAL:
			hm.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.THORNS, r.nextInt(10) > 7 ? 2 : 1);
			if (r.nextInt(3) == 1)
				hm.put(Enchantment.PROTECTION_FIRE, r.nextInt(10) > 7 ? 3 : 2);
			if (r.nextInt(5) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 3 : 2);
			break;
		case LEGENDARY:
			hm.put(Enchantment.PROTECTION_ENVIRONMENTAL, r.nextInt(10) > 5 ? 6
					: 4);
			hm.put(Enchantment.THORNS, r.nextInt(10) > 7 ? 3 : 2);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.PROTECTION_FIRE, r.nextInt(10) > 7 ? 4 : 3);
			if (r.nextInt(2) == 1)
				hm.put(Enchantment.DURABILITY, r.nextInt(10) > 7 ? 4 : 3);
			break;
		case UBER:
			hm.put(Enchantment.PROTECTION_ENVIRONMENTAL, r.nextInt(10) > 8 ? 7
					: 6);
			hm.put(Enchantment.THORNS, 4);
			hm.put(Enchantment.PROTECTION_FIRE, r.nextInt(10) > 7 ? 5 : 4);
			hm.put(Enchantment.DURABILITY, r.nextInt(2) + 5);
			break;
		}
		return hm;
	}

	public static String getRandomArmourFirstName(SpecialTypes st) {
		String[][] firstWords = {
				{ "Weak", "Flimsy", "Poorly crafted", "Dull", "Common",
						"Peasants", "Poorly maintained" },
				{ "Strong", "Rare", "Custom crafted", "Hinged",
						"Impact Absorbing" },
				{ "Master crafted", "Legendary", "Gleaming", "Dark",
						"Impenetrable", "Kevlar", "Protecting" },
				{ "Divine", "God-Like", "Mythological", "Glorious",
						"Possessed", "Corrupted", "Bejewelled", "Bestowed" } };

		switch (st) {
		case COMMON:
			return firstWords[0][new Random().nextInt(firstWords[0].length)];
		case SPECIAL:
			return firstWords[1][new Random().nextInt(firstWords[1].length)];
		case LEGENDARY:
			return firstWords[2][new Random().nextInt(firstWords[2].length)];
		case UBER:
			return firstWords[3][new Random().nextInt(firstWords[3].length)];
		default:
			return null;
		}
	}

	public static String getRandomArmourSecondName(SpecialTypes st,
			ItemType it, String firstWord) {
		String weaponName = "";
		// TODO Text (first/secondwords) are placeholders.

		String[][] secondWords;
		switch (it) {
		case HELMET:
			secondWords = new String[][] {
					{ "Swag cap", "Feathered hat", "Fruit-bowl hat", "Scalp",
							"Mud hat" },
					{ "Rounded iron hat", "Chain helm", "Iron helm",
							"Nose guarded helm" },
					{ "Glass helm", "Elven helm", "Obsidian helm",
							"Dwarven helm", "Orcish helm", "Top hat",
							"Bowler hat" },
					{ "Deadric helm", "Dragon scale helm", "Angelic helm",
							"Demonic helm", "Crown", "Tricorn" } };
			break;
		case CHESTPLATE:
			secondWords = new String[][] {
					{ "Leather Thongs", "Fur Chest", "Robot Carcass", "Shirt",
							"Cardboard Chest", "Pots and pans" },
					{ "Chainmail chest", "Platemail chest", "Cuirass",
							"Steel chest" },
					{ "Glass Chest", "Elven Chest", "Obsidian Chest",
							"Mithril Chest", "Dwarven Chest", "Orcish Chest" },
					{ "Demonic Chest", "Robe", "Shadows", "Holy Light",
							"Dragon Scale Chest", "Deadric Chest",
							"Heavenly Chest" } };
			break;
		case LEGGINGS:
			secondWords = new String[][] {
					{ "Crotchless pant", "Leather Thong", "Panties",
							"Knee guards" },
					{ "Platemail leggings", "Dwarven leggings",
							"Chainmail leggings", "Iron leggings" },
					{ "Glass leggings", "Elven leggings", "Mithril leggings",
							"Orcish leggings" },
					{ "Deadric leggings", "Dragon scale leggings",
							"Angelic leggings", "Demonic leggings" } };
			break;
		case BOOTS:
			secondWords = new String[][] {
					{ "Skulls", "Custard", "Cardboard", "Sandals",
							"Mud and Dirt", "Forcefields" },
					{ "Platemail boots", "Chainmail boots", "Iron boots",
							"Pumps" },
					{ "Glass boots", "Elven boots", "Obsidian boots",
							"Mithril boots", "Dwarven boots", "Orcish boots" },
					{ "Holy Light", "Deadric boots", "Dragon scale boots",
							"Angel footwear", "Demon footwear" } };
			break;
		default:
			secondWords = new String[][] { { "error" }, { "error" },
					{ "error" }, { "error" } };
			break;
		}
		Random r = new Random();
		switch (st) {
		case COMMON:
			weaponName += firstWord;
			weaponName += secondWords[0][r.nextInt(secondWords[0].length)];
			return ChatColor.BLUE + "[C] " + weaponName + ChatColor.RESET;
		case SPECIAL:
			weaponName += firstWord;
			weaponName += secondWords[1][r.nextInt(secondWords[1].length)];
			return ChatColor.LIGHT_PURPLE + "[S] " + weaponName
					+ ChatColor.RESET;
		case LEGENDARY:
			weaponName += firstWord;
			weaponName += secondWords[2][r.nextInt(secondWords[2].length)];
			return ChatColor.RED + "[L] " + weaponName + ChatColor.RESET;
		case UBER:
			weaponName += firstWord;
			weaponName += secondWords[3][r.nextInt(secondWords[3].length)];
			return ChatColor.DARK_PURPLE + "[" + ChatColor.RED + "U"
					+ ChatColor.DARK_PURPLE + "] " + weaponName
					+ ChatColor.RESET;
		}
		return null;
	}
}
