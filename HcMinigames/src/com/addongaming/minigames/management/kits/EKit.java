package com.addongaming.minigames.management.kits;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.Team;

public enum EKit {
	WARRIOR(GameMode.KITS), BESERKER(GameMode.KITS), RANGER(GameMode.KITS), MAGE(
			GameMode.KITS), TEMPLAR(GameMode.KITS), HEALER(GameMode.KITS), PRIEST(
			GameMode.KITS), REDVSBLUE(GameMode.RVB), TDM(
			GameMode.TEAMDEATHMATCH), CONQUEST_ATTACKER(GameMode.CONQUEST), CONQUEST_DEFENDER(
			GameMode.CONQUEST), ASSAULT(new GameMode[] {
			GameMode.KILL_CONFIRMED, GameMode.MODERN_WARFARE }), SPEC_OPS(
			new GameMode[] { GameMode.KILL_CONFIRMED, GameMode.MODERN_WARFARE }), HEAVY_GUNNER(
			new GameMode[] { GameMode.KILL_CONFIRMED, GameMode.MODERN_WARFARE }), DEMOLITIONS(
			new GameMode[] { GameMode.KILL_CONFIRMED, GameMode.MODERN_WARFARE }), SNIPER(
			new GameMode[] { GameMode.KILL_CONFIRMED, GameMode.MODERN_WARFARE }), SURVIVAL_GAMES(
			new GameMode[] { GameMode.SURVIVAL_GAMES }), SUPPORT_GUNNER(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.BLUE
					.getTeamId()), PRIVATE_FIRST_CLASS(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.BLUE
					.getTeamId()), MARKSMAN(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.BLUE
					.getTeamId()), SHOCK_TROOPER(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.RED
					.getTeamId()), EXPLOSIVE_EXPERT(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.RED
					.getTeamId()), MILITIA(
			new GameMode[] { GameMode.TACTICAL_INTERVENTION }, Team.RED
					.getTeamId()), GUN_GAME(GameMode.GUN_GAME), THE_SHIP(
			GameMode.THE_SHIP);
	private GameMode[] gameMode;
	private boolean premium = false;
	private int team = -1;

	EKit(GameMode gameMode[]) {
		this.gameMode = gameMode;
	}

	EKit(GameMode gameMode) {
		this.gameMode = new GameMode[] { gameMode };
	}

	EKit(GameMode gameMode[], int team) {
		this.gameMode = gameMode;
		this.team = team;
	}

	EKit(GameMode gameMode, int team) {
		this.gameMode = new GameMode[] { gameMode };
		this.team = team;
	}

	EKit(GameMode gameMode[], boolean premium) {
		this.gameMode = gameMode;
		this.premium = premium;
	}

	EKit(GameMode gameMode, boolean premium) {
		this.gameMode = new GameMode[] { gameMode };
		this.premium = premium;
	}

	EKit(GameMode gameMode[], boolean premium, int team) {
		this.gameMode = gameMode;
		this.premium = premium;
		this.team = team;
	}

	EKit(GameMode gameMode, boolean premium, int team) {
		this.gameMode = new GameMode[] { gameMode };
		this.premium = premium;
		this.team = team;
	}

	public boolean isPremium() {
		return premium;
	}

	public GameMode[] getGameMode() {
		return gameMode;
	}

	public static String asString() {
		List<String> str = new ArrayList<String>();
		for (EKit gm : values())
			str.add(gm.name());
		return StringUtils.join(str, ", ");
	}

	public int getTeam() {
		return team;
	}

	public static EKit getByName(String kit) {
		for (EKit eKit : values())
			if (eKit.name().equalsIgnoreCase(kit))
				return eKit;
		return null;
	}

	public static EKit[] getByGamemode(GameMode gameMode) {
		List<EKit> kits = new ArrayList<EKit>();
		for (EKit kit : values())
			for (GameMode gm : kit.getGameMode())
				if (gm == gameMode)
					kits.add(kit);
		return kits.toArray(new EKit[kits.size()]);
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
	 * 
	 * @param string
	 * @return
	 */
	public static EKit getFromReadable(String string) {
		string = ChatColor.stripColor(string);
		for (EKit gft : values())
			if (gft.toReadableText().equalsIgnoreCase(string))
				return gft;
		return null;
	}
}
