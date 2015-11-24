package com.addongaming.prison.limit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.prison.jail.JailManager;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.prison.PrisonManager;
import com.addongaming.prison.prison.warps.WarpSystem;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class DyingLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Respawn"
			+ ChatColor.GRAY + "] " + ChatColor.RED;
	String allw = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN + "Respawn"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void playerRespawnEvent(PlayerRespawnEvent event) {
		if (event.getPlayer().getName().equalsIgnoreCase("Guard"))
			return;
		if (JailManager.getInstance().isInJail(event.getPlayer())) {
			event.getPlayer().sendMessage(
					err + "I'm afraid you cannot escape that easily.");
			event.setRespawnLocation(JailManager.getInstance()
					.getJailFor(event.getPlayer()).getJailLoc());
		} else {
			event.setRespawnLocation(WarpSystem
					.getInstance()
					.getWarpByNameAndPrison(
							"Spawn",
							PrisonManager.getInstance()
									.getPrison(event.getPlayer()).getName())
					.getLocation());
		}
		double cost = PrisonerManager.getInstance()
				.getPrisonerInfo(event.getPlayer().getName()).getBalance();
		int finalCost;
		if (cost <= 1000)
			finalCost = 0;
		else if (cost < 5000)
			finalCost = 100;
		else
			finalCost = (int) ((cost / 100) * 5);
		if (finalCost <= 0)
			return;
		event.getPlayer().sendMessage(
				allw + "Your respawn cost " + finalCost + " rupees.");
		PrisonerManager.getInstance()
				.getPrisonerInfo(event.getPlayer().getName())
				.removeBalance(finalCost);
	}

	@EventHandler
	public void playerDrown(PlayerMoveEvent event) {
		if (event.getPlayer().getHealth() == 0.0d)
			return;
		if (event.getTo().getY() < 15
				&& (event.getTo().getBlock().getType() == Material.WATER || event
						.getTo().getBlock().getType() == Material.STATIONARY_WATER)) {
			ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
					event.getTo().getWorld()).getApplicableRegions(
					event.getTo());
			if (ars.iterator().hasNext())
				return;
			else {
				Bukkit.broadcastMessage(event.getPlayer().getName()
						+ " couldn't swim and drowned.");
				event.getPlayer().setHealth(0.0d);
			}

		}
	}
}
