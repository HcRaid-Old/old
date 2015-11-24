package com.addongaming.minigames.management.scheduling;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.MinigameUser;

public class ScheduledTeleport implements Runnable {
	private MinigameUser user;
	private Location toTeleport;
	private boolean clearInventory = false;

	public ScheduledTeleport(MinigameUser user, Location toTeleport) {
		this.user = user;
		this.toTeleport = toTeleport;
	}

	public ScheduledTeleport(MinigameUser user, Location toTeleport,
			boolean clearInventory) {
		this.user = user;
		this.toTeleport = toTeleport;
		this.clearInventory = clearInventory;
	}

	@Override
	public void run() {
		if (user.isValid()) {
			user.getBase().teleport(toTeleport);
			if (clearInventory)
				clearInven(user.getBase());
		}
	}

	private void clearInven(Player player) {
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}
}
