package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Player;

import com.addongaming.minigames.management.WeaponManagement;
import com.addongaming.minigames.management.weapon.Gun;

public class ReloadScheduler implements HcRepeat {

	private Gun gun;
	private Player player;
	private WeaponManagement weaponManagement;
	private int id;
	private long reloadStart = System.currentTimeMillis();
	private boolean finished = false;

	public ReloadScheduler(Gun gun, Player player,
			WeaponManagement weaponManagement) {
		this.gun = gun;
		this.player = player;
		this.weaponManagement = weaponManagement;
	}

	@Override
	public void run() {
		if (finished)
			return;
		if (player.getItemInHand() == null
				|| !gun.isWeapon(player.getItemInHand())) {
			finished = true;
			return;
		}
		if (reloadStart + gun.getReload() < System.currentTimeMillis()) {
			gun.reloaded(player);
			finished = true;
			return;
		}
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
