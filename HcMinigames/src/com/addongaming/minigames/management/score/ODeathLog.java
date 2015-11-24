package com.addongaming.minigames.management.score;

import com.addongaming.minigames.management.scheduling.MGAsyncDatabaseRunnable;

public class ODeathLog {
	private String userName, killer, weapon, arena, gamemode;
	private int points;
	private long time;

	public ODeathLog(String userName, String killer, String weapon,
			String arena, String gamemode, int points, long time) {
		this.userName = userName;
		this.killer = killer;
		this.weapon = weapon;
		this.arena = arena;
		this.gamemode = gamemode;
		this.points = points;
		this.time = time;
	}

	public String getQuery() {
		String query = "INSERT INTO MGDeathLog(username, killer, weapon, points, arena, gamemode, time) VALUES (?,?,?,?,?,?,?)";
		return query;
	}

	public MGAsyncDatabaseRunnable format(MGAsyncDatabaseRunnable runnable) {
		runnable.addString("Username", userName);
		runnable.addString("Killer", killer);
		runnable.addString("Weapon", weapon);
		runnable.addInt("points", points);
		runnable.addString("Arena", arena);
		runnable.addString("Gamemode", gamemode);
		runnable.addInt("time", (int) (time / 1000));
		return runnable;
	}

}
