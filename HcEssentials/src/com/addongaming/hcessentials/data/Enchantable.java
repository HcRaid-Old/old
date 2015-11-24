package com.addongaming.hcessentials.data;

import org.bukkit.Material;

public enum Enchantable {
	// TODO Add Chainmail
	diaAxe(Material.DIAMOND_AXE, ItemType.AXE), diaBoots(
			Material.DIAMOND_BOOTS, ItemType.BOOTS), diaChest(
			Material.DIAMOND_CHESTPLATE, ItemType.CHESTPLATE), diaHelm(
			Material.DIAMOND_HELMET, ItemType.HELMET), diaHoe(
			Material.DIAMOND_HOE, ItemType.HOE), diaLegs(
			Material.DIAMOND_LEGGINGS, ItemType.LEGGINGS), diaPick(
			Material.DIAMOND_PICKAXE, ItemType.SPADE), diaSpade(
			Material.DIAMOND_SPADE, ItemType.SPADE), diaSword(
			Material.DIAMOND_SWORD, ItemType.SWORD), goldAxe(Material.GOLD_AXE,
			ItemType.AXE), goldBoots(Material.GOLD_BOOTS, ItemType.BOOTS), goldChest(
			Material.GOLD_CHESTPLATE, ItemType.CHESTPLATE), goldHelm(
			Material.GOLD_HELMET, ItemType.HELMET), goldHoe(Material.GOLD_HOE,
			ItemType.HOE), goldLegs(Material.GOLD_LEGGINGS, ItemType.LEGGINGS), goldPick(
			Material.GOLD_PICKAXE, ItemType.PICKAXE), goldSpade(
			Material.GOLD_SPADE, ItemType.PICKAXE), goldSword(
			Material.GOLD_SWORD, ItemType.SWORD), ironAxe(Material.IRON_AXE,
			ItemType.AXE), ironBoots(Material.IRON_BOOTS, ItemType.BOOTS), ironChest(
			Material.IRON_CHESTPLATE, ItemType.CHESTPLATE), ironHelm(
			Material.IRON_HELMET, ItemType.HELMET), ironHoe(Material.IRON_HOE,
			ItemType.HOE), ironLegs(Material.IRON_LEGGINGS, ItemType.LEGGINGS), ironPick(
			Material.IRON_PICKAXE, ItemType.PICKAXE), ironSpade(
			Material.IRON_SPADE, ItemType.SPADE), ironSword(
			Material.IRON_SWORD, ItemType.SWORD), lethBoots(
			Material.LEATHER_BOOTS, ItemType.BOOTS), lethChest(
			Material.LEATHER_CHESTPLATE, ItemType.CHESTPLATE), lethHelmet(
			Material.LEATHER_HELMET, ItemType.HELMET), lethLegs(
			Material.LEATHER_LEGGINGS, ItemType.LEGGINGS), woodenAxe(
			Material.WOOD_AXE, ItemType.AXE), woodenHoe(Material.WOOD_HOE,
			ItemType.HOE), woodenSword(Material.WOOD_SWORD, ItemType.SWORD), woodPick(
			Material.WOOD_PICKAXE, ItemType.PICKAXE), woodSpade(
			Material.WOOD_SPADE, ItemType.SPADE), stoneHoe(Material.STONE_HOE,
			ItemType.HOE), stoneSword(Material.STONE_SWORD, ItemType.SWORD), stonePick(
			Material.STONE_PICKAXE, ItemType.PICKAXE), stoneSpade(
			Material.STONE_SPADE, ItemType.SPADE), stoneAze(Material.STONE_AXE,
			ItemType.AXE), fishingRod(Material.FISHING_ROD, ItemType.FISHINGROD), bow(
			Material.BOW, ItemType.BOW);
	public static ItemType getItemType(Enchantable e) {
		return e.getItemType();
	}

	public static ItemType getItemType(Material material) {
		if (material == null)
			return ItemType.UNDEFINED;
		for (Enchantable e : values())
			if (e.getMaterial() == material)
				return getItemType(e);
		return ItemType.UNDEFINED;
	}

	public static boolean isEnchantable(Material material) {
		if (material == null)
			return false;
		for (Enchantable e : values())
			if (e.getMaterial() == material)
				return true;
		return false;
	}

	private final ItemType itemType;

	private final Material material;

	Enchantable(Material material, ItemType itemType) {
		this.material = material;
		this.itemType = itemType;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public Material getMaterial() {
		return material;
	}

}
