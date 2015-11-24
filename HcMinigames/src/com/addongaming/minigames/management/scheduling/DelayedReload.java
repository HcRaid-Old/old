package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.minigames.management.WeaponManagement;
import com.addongaming.minigames.management.weapon.Gun;
import com.addongaming.minigames.management.weapon.Weapon;

public class DelayedReload implements Runnable {

	private Player player;
	private ItemStack itemStack;
	private WeaponManagement weaponManagement;

	public DelayedReload(Player player, ItemStack itemStack,
			WeaponManagement weaponManagement) {
		this.player = player;
		this.itemStack = itemStack;
		this.weaponManagement = weaponManagement;
	}

	@Override
	public void run() {
		Weapon weapon = weaponManagement.getWeapon(itemStack);
		if (weapon instanceof Gun) {
			Gun g = (Gun) weapon;
			g.reload(player);
		}
	}

}
