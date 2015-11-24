package com.addongaming.minigames.management.weapon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum GunFireType {
	SINGLE_SHOT, SEMI_AUTOMATIC, SCATTER_SHOT, FULLY_AUTOMATIC;
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
	public static GunFireType getFromReadable(String string) {
		for (GunFireType gft : values())
			if (gft.toReadableText().equalsIgnoreCase(string))
				return gft;
		return null;
	}

	public static GunFireType getType(String string) {
		for (GunFireType gft : values())
			if (gft.name().equalsIgnoreCase(string))
				return gft;
		return null;
	}

	public static String asString() {
		List<String> str = new ArrayList<String>();
		for (GunFireType gm : values())
			str.add(gm.name());
		return StringUtils.join(str, ", ");
	}
}
