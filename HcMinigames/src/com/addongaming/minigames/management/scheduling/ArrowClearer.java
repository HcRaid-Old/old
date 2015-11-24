package com.addongaming.minigames.management.scheduling;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ArrowClearer implements Runnable {

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers())
			((CraftPlayer) player).getHandle().getDataWatcher()
					.watch(9, (byte) 0);

	}

}
