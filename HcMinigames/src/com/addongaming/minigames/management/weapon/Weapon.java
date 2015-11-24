package com.addongaming.minigames.management.weapon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface Weapon {
	public WeaponType getWeaponType();

	public Weapons getWeapons();

	public ItemStack getWeapon();

	public boolean isWeapon(ItemStack is);

	public int getDamage();

	public void setType(Material material);

	public void save();
}
