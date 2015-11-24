package com.addongaming.minigames.management.flag;

import org.bukkit.Location;

import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.Team;

public class CTFFlag extends Flag {
	private long lastFirework = 0;
	private Location lastLocation;
	private ArenaPlayer carrying;

	public CTFFlag(Flag flag) {
		super(flag);
		setOwner(Team.BLUE.getTeamId());
	}

	@Override
	public void tick() {
		
	}

}
