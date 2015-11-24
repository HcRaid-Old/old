package com.addongaming.hcessentials.perks.near;

import com.addongaming.hcessentials.data.Ranks;

public enum ENear {
	grunt(Ranks.grunt, 0), creeper(Ranks.creeper, 100), blaze(Ranks.blaze, 250), ghast(
			Ranks.ghast, 500), ender(Ranks.ender, 1000), hero(Ranks.hero, 2000);

	private Ranks rank;
	private int radius;

	ENear(Ranks rank, int radius) {
		this.rank = rank;
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public Ranks getRank() {
		return rank;
	}

	public static ENear getByRank(Ranks r) {
		for (ENear en : ENear.values())
			if (en.getRank() == r)
				return en;
		return null;
	}
}
