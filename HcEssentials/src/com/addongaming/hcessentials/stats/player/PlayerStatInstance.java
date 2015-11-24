package com.addongaming.hcessentials.stats.player;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PlayerStatInstance {
	private Date loggedIn = new Date();
	private long timeLoggedIn = 0;
	private String playerName;
	private HashMap<EPlayerStat, Double> statMap = new HashMap<EPlayerStat, Double>();

	public long getTimeLoggedIn() {
		pauseTimer();
		loggedIn = new Date();
		return timeLoggedIn;
	}

	public String getLoggedInString() {
		long seconds = timeLoggedIn / 1000;
		int diffDays = (int) TimeUnit.SECONDS.toDays(seconds);
		long diffHours = TimeUnit.SECONDS.toHours(seconds) - (diffDays * 24);
		long diffMinutes = TimeUnit.SECONDS.toMinutes(seconds)
				- (TimeUnit.SECONDS.toHours(seconds) * 60);
		long diffSeconds = TimeUnit.SECONDS.toSeconds(seconds)
				- (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		StringBuilder sb = new StringBuilder();
		if (diffDays > 0)
			sb.append(diffDays + " days, ");
		if (diffHours > 0)
			sb.append(diffHours + " hours, ");
		if (diffMinutes > 0)
			sb.append(diffMinutes + " minutes, ");
		if (diffSeconds > 0)
			sb.append(diffSeconds + " seconds, ");
		sb.deleteCharAt(sb.length() - 2);
		return sb.toString();
	}

	public void setStat(EPlayerStat eps, Double value) {
		statMap.put(eps, value);
	}

	public void setTimeLoggedIn(long time) {
		this.timeLoggedIn = time;
	}

	public void incrementStat(EPlayerStat eps) {
		if (!statMap.containsKey(eps))
			statMap.put(eps, 0.0);
		else
			statMap.put(eps, statMap.get(eps) + 1);
	}

	public int getStat(EPlayerStat eps) {
		if (statMap.containsKey(eps))
			return (int) Math.round(statMap.get(eps));
		else
			return 0;
	}

	public void pauseTimer() {
		timeLoggedIn += new Date().getTime() - loggedIn.getTime();
	}

	public PlayerStatInstance(String playerName) {
		this.playerName = playerName;
	}

	public void restoreTimer() {
		loggedIn = new Date();
	}

	public String getPlayerName() {
		return playerName;
	}

	public void incrementStat(EPlayerStat stat, Double amount) {
		if (!statMap.containsKey(stat))
			statMap.put(stat, amount);
		else
			statMap.put(stat, statMap.get(stat) + amount);
	}

	public void merge(PlayerStatInstance psi) {
		this.timeLoggedIn += psi.getTimeLoggedIn();
		for (EPlayerStat eps : EPlayerStat.values())
			incrementStat(eps, (double) psi.getStat(eps));
	}
}
