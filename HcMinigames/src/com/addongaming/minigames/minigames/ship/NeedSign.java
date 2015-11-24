package com.addongaming.minigames.minigames.ship;

public class NeedSign {
	private int cost;
	private Needs need;
	private int cureAmount;

	public NeedSign(int cost, Needs need, int cureAmount) {
		super();
		this.cost = cost;
		this.need = need;
		this.cureAmount = cureAmount;
	}

	public int getCost() {
		return cost;
	}

	public int getCureAmount() {
		return cureAmount;
	}

	public Needs getNeed() {
		return need;
	}
}
