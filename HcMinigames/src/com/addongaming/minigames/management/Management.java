package com.addongaming.minigames.management;

import com.addongaming.minigames.core.HcMinigames;

public class Management {
	private final ArenaManagement arenaManagement;
	private final QueueManagement queueManagement;
	private final SoundManagement soundManagement;
	private final WeaponManagement weaponManagement;
	private final SchedulerManagement schedulerManagement;
	private final KitManagement kitManagement;
	private final KillStreakManagement killStreakManagement;
	private final RollbackManagement rollbackManagement;
	private final ChestFillingManagement chestFillingManagement;
	private final ScoreManagement scoreManagement;

	public Management(HcMinigames minigames) {
		arenaManagement = new ArenaManagement(minigames);
		queueManagement = new QueueManagement(minigames);
		soundManagement = new SoundManagement(minigames);
		weaponManagement = new WeaponManagement(minigames);
		schedulerManagement = new SchedulerManagement(minigames);
		kitManagement = new KitManagement(minigames);
		killStreakManagement = new KillStreakManagement(minigames);
		rollbackManagement = new RollbackManagement(minigames);
		chestFillingManagement = new ChestFillingManagement(minigames);
		scoreManagement = new ScoreManagement(minigames);
	}

	public ScoreManagement getScoreManagement() {
		return scoreManagement;
	}

	public ChestFillingManagement getChestFillingManagement() {
		return chestFillingManagement;
	}

	public RollbackManagement getRollbackManagement() {
		return rollbackManagement;
	}

	public ArenaManagement getArenaManagement() {
		return arenaManagement;
	}

	public QueueManagement getQueueManagement() {
		return queueManagement;
	}

	public SoundManagement getSoundManagement() {
		return soundManagement;
	}

	public WeaponManagement getWeaponManagement() {
		return weaponManagement;
	}

	public SchedulerManagement getSchedulerManagement() {
		return schedulerManagement;
	}

	public KitManagement getKitManagement() {
		return kitManagement;
	}

	public KillStreakManagement getKillStreakManagement() {
		return killStreakManagement;
	}
}
