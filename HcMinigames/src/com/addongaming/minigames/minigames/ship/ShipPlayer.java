package com.addongaming.minigames.minigames.ship;

import java.util.ArrayList;
import java.util.HashMap;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.weapon.Weapon;
import com.addongaming.minigames.management.weapon.Weapons;

public class ShipPlayer extends ArenaPlayer {
	private HashMap<Needs, Integer> needMap = new HashMap<Needs, Integer>();
	private ShipPlayer quarry = null;
	private int moneyOnSelf = 0, bank = 0;
	private ArrayList<String> playersFound = new ArrayList<String>();
	private String charName = "";
	private ShipPlayer attackedBy = null;
	private Weapons lastAttackedBy = null;
	private int coldbloodedKills = 0;

	public ShipPlayer getAttackedBy() {
		return attackedBy;
	}

	public Weapons getLastAttackedBy() {
		return lastAttackedBy;
	}

	public void setLastAttackedBy(ShipPlayer attackedBy, Weapons lastAttackedBy) {
		this.attackedBy = attackedBy;
		this.lastAttackedBy = lastAttackedBy;
	}

	public ShipPlayer(MinigameUser user) {
		super(user);
		for (Needs need : Needs.values())
			needMap.put(need, 0);
	}

	public boolean hasFoundPlayer(String name) {
		return playersFound.contains(name);
	}

	public void foundPlayer(String name) {
		playersFound.add(name);
	}

	public void clearFoundPlayers() {
		playersFound.clear();
	}

	public void removeFoundPlayer(String name) {
		playersFound.remove(name);
	}

	public String getCharName() {
		return charName;
	}

	public void setCharName(String charName) {
		this.charName = charName;
	}

	public void incrementNeed(Needs need, int amount) {
		int i = needMap.get(need);
		if (i + amount >= 100)
			needMap.put(need, 100);
		else
			needMap.put(need, i + amount);
	}

	public int getNeedLevel(Needs need) {
		return needMap.get(need);
	}

	public ShipPlayer getQuarry() {
		return quarry;
	}

	public int getMoneyOnSelf() {
		return moneyOnSelf;
	}

	public int getBank() {
		return bank;
	}

	public void incrementMoneyOnSelf(int amount) {
		moneyOnSelf += amount;
	}

	public void incrementBank(int amount) {
		bank += amount;
	}

	public void decrementMoneyOnSelf(int amount) {
		moneyOnSelf -= amount;
	}

	public void decrementBank(int amount) {
		bank -= amount;
	}

	public void setBank(int bank) {
		this.bank = bank;
	}

	public void setMoneyOnSelf(int moneyOnSelf) {
		this.moneyOnSelf = moneyOnSelf;
	}

	public void setQuarry(ShipPlayer quarry) {
		this.quarry = quarry;
	}

	public void setWeaponUse(ShipPlayer attacker, Weapon weapon) {

	}

	public void incrementColdbloodedKills() {
		this.coldbloodedKills++;
	}

	public int getColdbloodedKills() {
		return coldbloodedKills;
	}
}
