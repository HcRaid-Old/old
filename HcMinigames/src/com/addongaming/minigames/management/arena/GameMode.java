package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum GameMode {
	TEAMDEATHMATCH(65, 2, 36), KITS(1, 2, 36), INFECTION(60, 2, 36), RVB(130,
			2, 64), CONQUEST(65, 2, 36), MODERN_WARFARE(65, 2, 36), KILL_CONFIRMED(
			65, 2, 36), SURVIVAL_GAMES(130, 2, 24, true), TACTICAL_INTERVENTION(
			20, 2, 36), GUN_GAME(65, 2, 24), THE_SHIP(1, 2, 16, true);
	private int lobbyTime, minSize, maxSize;
	private boolean chestPopulating = false;

	GameMode(int lobbyTime, int minSize, int maxSize) {
		this.lobbyTime = lobbyTime;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	GameMode(int lobbyTime, int minSize, int maxSize, boolean chestPopulating) {
		this.lobbyTime = lobbyTime;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.chestPopulating = chestPopulating;
	}

	public int getLobbyTime() {
		return lobbyTime;
	}

	public boolean isChestPopulating() {
		return chestPopulating;
	}

	public static String asString() {
		List<String> str = new ArrayList<String>();
		for (GameMode gm : values())
			str.add(gm.name());
		return StringUtils.join(str, ", ");
	}

	public static GameMode getByName(String string) {
		for (GameMode gm : values())
			if (gm.name().equalsIgnoreCase(string))
				return gm;
		return null;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getMinSize() {
		return minSize;
	}

	public String toReadableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(name().toLowerCase().replace('_', ' '));
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		sb.setCharAt(sb.lastIndexOf(" ") + 1,
				Character.toUpperCase(sb.charAt(sb.lastIndexOf(" ") + 1)));
		return sb.toString();
	}
}
