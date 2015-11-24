package com.addongaming.minigames.management.arena;

public enum Team {
	ERROR(-2), NONE(-1), BLUE(0), RED(1), NORMAL(0), INFECTED(1);
	private final int teamId;

	Team(int teamId) {
		this.teamId = teamId;
	}

	public int getTeamId() {
		return teamId;
	}

	public static Team getById(Integer i) {
		for (Team team : values())
			if (team.getTeamId() == i)
				return team;
		return ERROR;
	}
}
