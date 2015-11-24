package com.addongaming.hcessentials.teams;

public enum Ranks {
	leader(2), member(0), mod(1);
	private final int rank;

	Ranks(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
}
