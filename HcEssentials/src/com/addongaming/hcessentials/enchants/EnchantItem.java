package com.addongaming.hcessentials.enchants;

import org.bukkit.inventory.ItemStack;

public enum EnchantItem {
	ALL, BOOTS, BOW, CHESTPLATE, HELMET, LEGGINGS, SWORD, SWORDBOW, AXE, PICKAXE;

	public static boolean isValid(ItemStack is, EnchantItem ei) {
		if (ei.equals(ALL))
			return true;
		if (ei.equals(SWORDBOW))
			if (is.getType().name().endsWith("BOW")
					|| is.getType().name().endsWith("SWORD"))
				return true;
		if (is.getType().name().endsWith(ei.name()))
			return true;
		return false;
	}

	public static int numberalToNum(String str) {
		switch (str.toUpperCase()) {
		case "I":
			return 1;
		case "II":
			return 2;
		case "III":
			return 3;
		case "IV":
			return 4;
		case "V":
			return 5;
		default:
			return 0;
		}
	}

	public static String numToNumeral(int i) {
		switch (i) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		default:
			return "0";
		}
	}

	public static String toString(EnchantItem im) {
		switch (im) {
		case HELMET:
			return "a helmet";
		case CHESTPLATE:
			return "a chestplate";
		case LEGGINGS:
			return "a pair of leggings";
		case BOOTS:
			return "pair of boots";
		case SWORD:
			return "any sword";
		case ALL:
			return "any armour or sword/bow";
		case BOW:
			return "a bow";
		case SWORDBOW:
			return "either sword or bow";
		case AXE:
			return "any axe.";
		case PICKAXE:
			return "any pickaxe.";
		default:
			return "ERROR please create a ticket";
		}

	}
}
