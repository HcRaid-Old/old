package com.addongaming.hcessentials.combat.antilogging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CombatThread implements Runnable {

	@Override
	public void run() {
		for (String playerName : CombatLogging.combatMap.keySet()) {
			CombatInstance ci = CombatLogging.combatMap.get(playerName);
			if (ci.isInCombat() && !ci.reCalcTimer()) {
				ci.exitCombat();
				Player p = Bukkit.getPlayer(playerName);
				if (p != null && p.isOnline()) {
					CombatLogging.message(p, "You are now out of combat.");
				}
			}
		}
	}

}
