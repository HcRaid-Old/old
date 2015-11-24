package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Player;

import com.addongaming.minigames.management.WeaponManagement;

public class WeaponCheck implements Runnable {

	private Player player;
	private WeaponManagement weaponManagement;

	public WeaponCheck(Player player, WeaponManagement weaponManagement) {
		this.player = player;
		this.weaponManagement = weaponManagement;
	}

	@Override
	public void run() {
		if (player.getItemInHand() != null)
			if (weaponManagement.isWeapon(player.getItemInHand())) {
				weaponManagement.reset(player);
			}
	}

}
