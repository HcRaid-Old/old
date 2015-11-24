package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.management.WeaponManagement;

public class WeaponManagementCleanser implements Runnable {
	private WeaponManagement wm;

	public WeaponManagementCleanser(WeaponManagement wm) {
		this.wm = wm;
	}

	@Override
	public void run() {
		wm.onTick();
	}
}
