package org.KitPvP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public enum Kits {
	ranger("Ranger", new ItemStack[] { new ItemStack(Material.LEATHER_HELMET),
			new ItemStack(Material.LEATHER_CHESTPLATE),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_BOOTS) }, new ItemStack[] {
			new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 64) }), fighter(
			"Fighter", new ItemStack[] { new ItemStack(Material.IRON_HELMET),
					new ItemStack(Material.AIR),
					new ItemStack(Material.IRON_LEGGINGS),
					new ItemStack(Material.IRON_BOOTS) },
			new ItemStack[] { new ItemStack(Material.IRON_SWORD) }), offense(
			"Offense", new ItemStack[] { new ItemStack(Material.AIR),
					new ItemStack(Material.LEATHER_CHESTPLATE),
					new ItemStack(Material.AIR),
					new ItemStack(Material.LEATHER_BOOTS) },
			new ItemStack[] { new ItemStack(Material.IRON_SWORD) {
				{
					this.addEnchantment(Enchantment.DAMAGE_ALL, 2);
				}
			} }), medic("Medic", new ItemStack[] { new ItemStack(Material.AIR),
			new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.AIR), new ItemStack(Material.AIR) },
			new ItemStack[] { new ItemStack(Material.STONE_SWORD) },
			PotionType.INSTANT_HEAL, 2), warrior("Warrior", new ItemStack[] {
			new ItemStack(Material.AIR),
			new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_BOOTS) },
			new ItemStack[] { new ItemStack(Material.DIAMOND_AXE) {
				{
					this.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				}
			} }), acrobat("Acrobat", new ItemStack[] {
			new ItemStack(Material.LEATHER_HELMET),
			new ItemStack(Material.LEATHER_CHESTPLATE),
			new ItemStack(Material.LEATHER_LEGGINGS),
			new ItemStack(Material.LEATHER_BOOTS) },
			new ItemStack[] { new ItemStack(Material.BOW) {
				{
					this.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
				}
			} }),

	;

	String name;
	ItemStack[] armour;
	ItemStack[] hotbar;
	Potion p;

	Kits(String name, ItemStack[] armour, ItemStack[] hotbar) {
		this.name = name;
		List<ItemStack> al = Arrays.asList(armour);
		Collections.reverse(al);
		this.armour = al.toArray(new ItemStack[al.size()]);
		this.hotbar = hotbar;
		p = null;
	}

	public ItemStack[] getArmour() {
		return armour.clone();
	}

	public ItemStack[] getHotbarItems() {
		ArrayList<ItemStack> al = new ArrayList<ItemStack>();
		for (ItemStack is : hotbar)
			al.add(is.clone());
		if (p != null)
			al.add(getPotion());
		return al.toArray(new ItemStack[al.size()]);
	}

	private ItemStack getPotion() {
		ItemStack is = new ItemStack(Material.POTION, 5);
		p.apply(is);
		return is;
	}

	public String getName() {
		return name;
	}

	Kits(String name, ItemStack[] armour, ItemStack[] hotbar,
			PotionType potion, int levelPot) {
		this.name = name;
		List<ItemStack> al = Arrays.asList(armour);
		Collections.reverse(al);
		this.armour = al.toArray(new ItemStack[al.size()]);
		this.hotbar = hotbar;
		Potion p = new Potion(potion, levelPot);
		p.setSplash(true);
		this.p = p;
	}

}
