package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Player;

import com.addongaming.minigames.management.WeaponManagement;
import com.addongaming.minigames.management.weapon.Gun;
import com.addongaming.minigames.minigames.ArenaGame;

public class SemiAuto implements HcRepeat {

	private Gun gun;
	private Player player;
	private WeaponManagement weaponManagement;
	private int id;
	private int shotsLeft = 3;
	private long lastShot = 0;
	private boolean finished = false;
	private ArenaGame ag;

	public SemiAuto(Gun gun, ArenaGame ag, Player player,
			WeaponManagement weaponManagement) {
		this.gun = gun;
		this.ag = ag;
		this.player = player;
		this.weaponManagement = weaponManagement;
	}

	@Override
	public void run() {
		if (finished)
			return;
		if (lastShot + gun.getRof() < System.currentTimeMillis()) {
			if (shotsLeft == 0) {
				weaponManagement.removeWeaponFire(player.getName());
				finished = true;
				return;
			} else if (!gun.isWeapon(player.getItemInHand())) {
				finished = true;
				return;
			} else if (gun.getClipAmmo(player.getItemInHand()) == 0) {
				if (gun.getReservedAmmo(player.getItemInHand()) == 0) {
					weaponManagement.removeWeaponFire(player.getName());
				} else {
					gun.reload(player);
					weaponManagement
							.getMinigames()
							.getManagement()
							.getSchedulerManagement()
							.runScheduler(
									ag,
									new ReloadScheduler(gun, player,
											weaponManagement), 2l, 2l);
					weaponManagement.removeWeaponFire(player.getName());
				}
				finished = true;
				return;
			} else {
				gun.fire(player);
				shotsLeft--;
			}
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
