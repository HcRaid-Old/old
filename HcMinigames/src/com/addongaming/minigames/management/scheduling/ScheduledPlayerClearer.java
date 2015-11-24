package com.addongaming.minigames.management.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.addongaming.minigames.core.HcMinigames;

public class ScheduledPlayerClearer implements Runnable {
	private String userName;
	private HcMinigames minigames;

	public ScheduledPlayerClearer(String userName, HcMinigames minigames) {
		this.userName = userName;
		this.minigames = minigames;
	}

	@Override
	public void run() {
		Player player = Bukkit.getPlayer(userName);
		if(player !=null && player.isOnline())
			return;
		minigames.getManagement().getArenaManagement().getGame(userName);
	}

}
