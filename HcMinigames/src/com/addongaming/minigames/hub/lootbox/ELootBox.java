package com.addongaming.minigames.hub.lootbox;

import java.util.Random;

public enum ELootBox {
	small(500, 800), medium(800, 1100), large(1100, 1400);
	private int min;
	private int max;

	ELootBox(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public int getRandomScore() {
		return new Random().nextInt(max - min) + min;
	}

	public int getBankIngots(int score) {
		return (int) score / 100;
	}
}
