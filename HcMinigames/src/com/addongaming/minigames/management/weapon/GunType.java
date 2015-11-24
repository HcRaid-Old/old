package com.addongaming.minigames.management.weapon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum GunType {
	PISTOL, SMG, ASSAULT, SNIPER, SHOTGUN, LMG, LAUNCHER;
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
	public static GunType getFromReadable(String string) {
		for (GunType gft : values())
			if (gft.toReadableText().equalsIgnoreCase(string))
				return gft;
		return null;
	}

	public static GunType getType(String string) {
		for (GunType gft : values())
			if (gft.name().equalsIgnoreCase(string))
				return gft;
		return null;
	}

	public static String asString() {
		List<String> str = new ArrayList<String>();
		for (GunType gm : values())
			str.add(gm.name());
		return StringUtils.join(str, ", ");
	}
}
