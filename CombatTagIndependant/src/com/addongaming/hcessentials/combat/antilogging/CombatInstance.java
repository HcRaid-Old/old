package com.addongaming.hcessentials.combat.antilogging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CombatInstance {

	private boolean combat = false;
	private List<String> inCombat = new ArrayList<String>();
	private Date lastHit = new Date(0);

	public void attack() {

		lastHit = new Date();
	}

	public boolean canExitCombat() {
		return !inCombat.isEmpty();
	}

	public void enteredCombat(String playerName) {
		combat = true;
		lastHit = new Date();
		inCombat.add(playerName);
	}

	public void exitCombat() {
		combat = false;
		inCombat.clear();
	}

	public boolean exitCombat(String playerName) {
		inCombat.remove(playerName);
		return reCalcTimer();
	}

	public String[] getAllCombats() {
		return inCombat.toArray(new String[inCombat.size()]);
	}

	public String getTimeLeft() {
		Date d = new Date((lastHit.getTime() + CombatLogging.timeOutOfCombat)
				- new Date().getTime());
		return new SimpleDateFormat("ss").format(d);
	}

	public boolean isInCombat() {
		return combat;
	}

	public boolean isInCombatWith(String str) {
		return inCombat.contains(str);
	}

	/**
	 * Recalculates the time to see if the person is out of combat.
	 * 
	 * @return false if out of combat, true if not.
	 */
	public boolean reCalcTimer() {
		if (inCombat.isEmpty()) {
			combat = false;
			return false;
		}
		if (new Date(lastHit.getTime() + CombatLogging.timeOutOfCombat)
				.before(new Date())) {
			combat = false;
			inCombat.clear();
			return false;
		} else
			return true;
	}

}
