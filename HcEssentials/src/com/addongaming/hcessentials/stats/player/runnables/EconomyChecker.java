package com.addongaming.hcessentials.stats.player.runnables;

import java.util.HashMap;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.stats.player.EPlayerStat;
import com.addongaming.hcessentials.stats.player.PlayerStatsHandler;

public class EconomyChecker implements Runnable {
	private final PlayerStatsHandler psh;
	private HashMap<String, Double> playerBal = new HashMap<String, Double>();

	public EconomyChecker(PlayerStatsHandler psh) {
		this.psh = psh;
	}

	@Override
	public void run() {
		for (String str : psh.getPlayerMap().keySet()) {
			if (!playerBal.containsKey(str)) {
				playerBal.put(str, HcEssentials.economy.getBalance(str));
				continue;
			}
			double currAmount = HcEssentials.economy.getBalance(str);
			double oldAmount = playerBal.get(str);
			if (oldAmount == currAmount)
				continue;
			double amount;
			EPlayerStat eps;
			if (currAmount > oldAmount) {
				eps = EPlayerStat.moneyEarnt;
				amount = currAmount - oldAmount;
			} else {
				eps = EPlayerStat.moneySpent;
				amount = oldAmount - currAmount;
			}
			playerBal.put(str, currAmount);
			if (amount < 2)
				continue;
			int i = (int) Math.round(amount);
			psh.addToStat(str, eps, i);
		}
	}

}
