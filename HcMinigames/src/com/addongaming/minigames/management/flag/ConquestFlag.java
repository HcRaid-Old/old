package com.addongaming.minigames.management.flag;

import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.Team;

public class ConquestFlag extends Flag {

	public ConquestFlag(Flag flag) {
		super(flag);
		setOwner(Team.BLUE.getTeamId());
	}

	@Override
	public void tick() {
		if (getOwner() == Team.RED.getTeamId())
			return;
		int blueCounter = 0, redCounter = 0;
		for (ArenaPlayer ap : super.getNearbyPlayers(3))
			if (ap.getTeam() == Team.RED.getTeamId())
				redCounter++;
			else if (ap.getTeam() == Team.BLUE.getTeamId())
				blueCounter++;
		if (blueCounter > 0 && redCounter > 0)
			return;
		else if (redCounter > 0) {
			double redTeamSize = getAg().getByTeam(Team.RED.getTeamId()).size();
			double redTeamPowerIncrease = (redTeamSize / redCounter) * 100;
			redTeamPowerIncrease = neutraliseScore(redTeamPowerIncrease);
			if (getPower() + redTeamPowerIncrease >= 100) {
				setPower((short) 100);
				for (ArenaPlayer ap : super.getNearbyPlayers(3))
					if (ap.getTeam() == Team.RED.getTeamId()) {
						ap.incrementFlagCaps();
						ap.incrementScore(300);
					}
				flagChangeHands();
			} else {
				setPower((short) (getPower() + redTeamPowerIncrease));
				for (ArenaPlayer ap : super.getNearbyPlayers(3))
					if (ap.getTeam() == Team.RED.getTeamId())
						getAg().message(ap.getBase(),
								"Flag power: " + getPower() + " / 100");
			}

		} else if (blueCounter > 0) {
			if (getPower() == 0)
				return;
			double blueTeamSize = getAg().getByTeam(Team.RED.getTeamId())
					.size();
			double blueTeamPowerIncrease = (blueTeamSize / blueCounter) * 100;
			blueTeamPowerIncrease = neutraliseScore(blueTeamPowerIncrease);
			if (getPower() - blueTeamPowerIncrease <= 0) {
				setPower((short) 0);
			} else {
				setPower((short) (getPower() - blueTeamPowerIncrease));
			}
			for (ArenaPlayer ap : super.getNearbyPlayers(3))
				if (ap.getTeam() == Team.BLUE.getTeamId())
					getAg().message(ap.getBase(),
							"Flag power: " + getPower() + " / 100");
		}
	}

	private double neutraliseScore(double d) {
		if (d > 30)
			d = 30;
		else if (d < 5)
			d = 5;
		return d;
	}

	private void flagChangeHands() {
		setOwner(Team.RED.getTeamId());
		spawnFlag();
		getAg().messageAll("The red team have captured a flag.");
		getAg().onFlagCapture(this);
	}
}
