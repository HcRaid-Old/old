package com.addongaming.minigames.management.weapon;

public enum Weapons {
	TOMAHAWK(WeaponType.THROWING), DEAGLE(WeaponType.GUN), AK74(WeaponType.GUN), GLOCK(
			WeaponType.GUN), USP(WeaponType.GUN), P228(WeaponType.GUN), MAC10(
			WeaponType.GUN), TMP(WeaponType.GUN), MP5(WeaponType.GUN), UMP(
			WeaponType.GUN), P90(WeaponType.GUN), M3(WeaponType.GUN), XM1014(
			WeaponType.GUN), GALIL(WeaponType.GUN), FAMAS(WeaponType.GUN), M4A1(
			WeaponType.GUN), AUG(WeaponType.GUN), SAW(WeaponType.GUN), SCOUT(
			WeaponType.GUN), AWP(WeaponType.GUN), CLAYMORE(WeaponType.PHYSICAL), WRENCH(
			WeaponType.PHYSICAL), KATANA(WeaponType.PHYSICAL), FRYING_PAN(
			WeaponType.PHYSICAL), PIPE(WeaponType.PHYSICAL), CROUQET_MALLET(
			WeaponType.PHYSICAL), KITCHEN_KNIFE(WeaponType.PHYSICAL), POTS(
			WeaponType.PHYSICAL), SPADE(WeaponType.PHYSICAL), SHANK(
			WeaponType.PHYSICAL), BASEBALL_BAT(WeaponType.PHYSICAL), TENNIS_RACKET(
			WeaponType.PHYSICAL), REVOLVER(WeaponType.GUN), WINCHESTER(
			WeaponType.GUN), BLUNDERBUSS(WeaponType.GUN), TOMMY_GUN(
			WeaponType.GUN);
	private WeaponType weaponType;

	Weapons(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase().replace('_', ' '));
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		sb.setCharAt(sb.lastIndexOf(" ") + 1,
				Character.toUpperCase(sb.charAt(sb.lastIndexOf(" ") + 1)));
		return sb.toString();
	}

	/**
	 * From gun lore
	 * 
	 * @param string
	 * @return
	 */
	public static Weapons getFromReadable(String string) {
		for (Weapons gft : values())
			if (gft.toReadableText().equalsIgnoreCase(string))
				return gft;
		return null;
	}

	public static Weapons getType(String string) {
		for (Weapons gft : values())
			if (gft.name().equalsIgnoreCase(string))
				return gft;
		return null;
	}
}
