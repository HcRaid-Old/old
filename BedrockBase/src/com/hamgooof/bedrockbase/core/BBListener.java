package com.hamgooof.bedrockbase.core;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class BBListener implements Listener {
	// Teleport - Enderpearl
	// EntityExplode
	private Essentials essentials;

	public BBListener() {
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin(
				"Essentials");
	}

	private boolean inWorld(World world) {
		return inWorld(world.getName());
	}

	private boolean inWorld(String world) {
		return world.equalsIgnoreCase(BBPlugin.world);
	}

	@EventHandler
	public void vehicleEnter(VehicleEnterEvent event) {
		if (!inWorld(event.getVehicle().getWorld()))
			return;
		event.setCancelled(true);
	}

	private RegionManager getRegionManager() {
		return BBPlugin.worldGuard.getRegionManager(Bukkit
				.getWorld(BBPlugin.world));
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		if (event.getPlayer().isOp()
				|| !inWorld(event.getTo().getWorld())
				|| (event.getFrom().getBlockX() == event.getTo().getBlockX() && event
						.getFrom().getBlockZ() == event.getTo().getBlockZ()))
			return;
		ApplicableRegionSet ars = getRegionManager().getApplicableRegions(
				event.getTo());
		if (ars.size() == 0) {
			try {
				event.getPlayer().teleport(
						essentials.getWarps().getWarp("spawn"));
			} catch (WarpNotFoundException e) {
				event.setCancelled(true);
				e.printStackTrace();
			} catch (InvalidWorldException e) {
				event.setCancelled(true);
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void pistonPush(BlockPistonExtendEvent event) {
		if (!inWorld(event.getBlock().getWorld()))
			return;
		Block extended = event.getBlocks().get(event.getBlocks().size() - 1);
		extended = extended.getRelative(event.getDirection());
		RegionManager rm = getRegionManager();
		if (rm.getApplicableRegions(extended.getLocation()).size() == 0) {
			event.setCancelled(true);
			incrementUsage(event.getBlock());
			return;
		}
	}

	@EventHandler
	public void playerTeleport(PlayerTeleportEvent event) {
		if (!inWorld(event.getTo().getWorld())
				|| event.getCause() != TeleportCause.ENDER_PEARL)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void entityBoom(EntityExplodeEvent event) {
		if (inWorld(event.getLocation().getWorld()))
			event.setCancelled(true);
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().isOp() || !inWorld(event.getBlock().getWorld()))
			return;
		if (getRegionManager().getApplicableRegions(
				event.getBlock().getLocation()).size() == 0)
			event.setCancelled(true);
	}

	@EventHandler
	public void blockRemove(BlockBreakEvent event) {
		if (event.getPlayer().isOp() || !inWorld(event.getBlock().getWorld()))
			return;
		if (getRegionManager().getApplicableRegions(
				event.getBlock().getLocation()).size() == 0)
			event.setCancelled(true);
	}

	private void incrementUsage(Block block) {
		// TODO Auto-generated method stub

	}
}
