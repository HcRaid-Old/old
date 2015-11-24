package com.addongaming.minigames.core;

import java.util.ArrayList;
import java.util.HashMap;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.scheduling.HubInventory;

public class MinigameUser {
	private Player base;
	private String name;
	private int overallScore = 0, bankPoints = 0, smallLoot = 0, medLoot = 0,
			largeLoot = 0;
	private boolean fetchingInfo = true;
	private final HashMap<GameMode, ArrayList<String>> mapsVotedFor = new HashMap<GameMode, ArrayList<String>>();
	private NPC pet = null;
	private boolean textureChanges = false;

	public MinigameUser(Player base) {
		this.base = base;
		this.name = base.getName();
		update();
	}

	public boolean isTextureChanges() {
		return textureChanges;
	}

	public void setTextureChanges(boolean textureChanges) {
		this.textureChanges = textureChanges;
	}

	public void clean() {
		overallScore = 0;
		bankPoints = 0;
		smallLoot = 0;
		medLoot = 0;
		largeLoot = 0;
	}

	public void votedFor(String arenaType, GameMode gameMode) {
		ArrayList<String> list;
		if (mapsVotedFor.containsKey(gameMode))
			list = mapsVotedFor.get(gameMode);
		else
			list = new ArrayList<String>();
		list.add(arenaType);
		mapsVotedFor.put(gameMode, list);
	}

	public NPC getPet() {
		return pet;
	}

	public void refresh() {
		HcMinigames
				.getInstance()
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						HcMinigames.getInstance(),
						new HubInventory(HcMinigames.getInstance().getHub()
								.getMinigameUser(getName()), HcMinigames
								.getInstance().getHub()), 2l);
	}

	public boolean hasVotedFor(String arenaType, GameMode gameMode) {
		if (!mapsVotedFor.containsKey(gameMode))
			return false;
		else if (mapsVotedFor.get(gameMode).contains(arenaType)) {
			return true;
		}
		return false;
	}

	public boolean isFetchingInfo() {
		return fetchingInfo;
	}

	public synchronized void setFetchingInfo(boolean fetchingInfo) {
		this.fetchingInfo = fetchingInfo;
	}

	public synchronized void update() {
		textureChanges = false;
		// TODO Call score to set-up crap;
		fetchingInfo = true;
		HcMinigames.getInstance().getManagement().getScoreManagement()
				.updatePlayer(this);
	}

	public synchronized void setBankPoints(int bankPoints) {
		this.bankPoints = bankPoints;
	}

	public synchronized void setLargeLoot(int largeLoot) {
		this.largeLoot = largeLoot;
	}

	public synchronized void setMedLoot(int medLoot) {
		this.medLoot = medLoot;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized void setOverallScore(int scoreLeft) {
		this.overallScore = scoreLeft;
	}

	public synchronized void setSmallLoot(int smallLoot) {
		this.smallLoot = smallLoot;
	}

	public synchronized int getBankPoints() {
		return bankPoints;
	}

	public synchronized int getLargeLoot() {
		return largeLoot;
	}

	public synchronized int getMedLoot() {
		return medLoot;
	}

	public synchronized int getScoreLeft() {
		return overallScore;
	}

	public synchronized int getSmallLoot() {
		return smallLoot;
	}

	public synchronized String getName() {
		return name;
	}

	public final Player getBase() {
		return base;
	}

	public synchronized final void setBase(Player base) {
		this.base = base;
	}

	public synchronized Server getServer() {
		return base.getServer();
	}

	public World getWorld() {
		return base.getWorld();
	}

	public Location getLocation() {
		return base.getLocation();
	}

	public synchronized boolean isValid() {
		return base != null && base.isOnline();
	}

	public void setPet(NPC npc) {
		if (pet != null)
			pet.destroy();
		this.pet = npc;
	}

	public void destroyPet() {
		pet.destroy();
		pet = null;
	}
}
