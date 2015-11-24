package com.addongaming.minigames.management.weapon;

public class WeaponFire {
	private String name;
	private Gun gun;
	private long lastShot;

	public WeaponFire(String name, Gun Gun, long lastShot) {
		this.name = name;
		this.gun = Gun;
		this.lastShot = System.currentTimeMillis();
	}

	public void shoot() {
		if (gun.getGft() == GunFireType.FULLY_AUTOMATIC)
			this.lastShot = System.currentTimeMillis();
	}

	public boolean shouldRemove() {
		if (gun.getGft() != GunFireType.SEMI_AUTOMATIC)
			return lastShot + (250 > gun.getRof() ? 250 : gun.getRof()) < System
					.currentTimeMillis();
		else
			return lastShot
					+ (250 > (gun.getRof() * 3) ? 250 : (gun.getRof() * 3)) < System
						.currentTimeMillis();
	}

	public void setTimer(int i) {
		this.lastShot = i;
	}

	public Gun getWeapon() {
		return gun;
	}

}
