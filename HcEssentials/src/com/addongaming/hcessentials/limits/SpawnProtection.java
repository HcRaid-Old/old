package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.addongaming.hcessentials.HcEssentials;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SpawnProtection implements Listener {
	private final String world, errorMsg, region;
	boolean canInteractItemFrame;
	private List<Material> blocks = new ArrayList<Material>();

	public SpawnProtection(String world, String region, String errorMsg,
			List<String> blocks, boolean canInteractItemFrame) {
		this.canInteractItemFrame = canInteractItemFrame;
		this.world = world;
		this.region = region;
		this.errorMsg = ChatColor.translateAlternateColorCodes('&', errorMsg);
		for (String str : blocks)
			this.blocks.add(Material.valueOf(str));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled()
				|| !event.getPlayer().getWorld().getName()
						.equalsIgnoreCase(world) || event.getPlayer().isOp())
			return;
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getPlayer().getWorld()).getApplicableRegions(
				event.getClickedBlock().getLocation());
		if (ars == null || ars.size() == 0)
			return;
		for (Iterator<ProtectedRegion> iter = ars.iterator(); iter.hasNext();) {
			if (iter.next().getId().equalsIgnoreCase(region)
					&& event.hasBlock()
					&& blocks.contains(event.getClickedBlock().getType())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(errorMsg);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
		if (event.isCancelled()
				|| !event.getPlayer().getWorld().getName()
						.equalsIgnoreCase(world) || event.getPlayer().isOp()
				|| canInteractItemFrame)
			return;
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getPlayer().getWorld()).getApplicableRegions(
				event.getRightClicked().getLocation());
		if (ars == null || ars.size() == 0)
			return;
		for (Iterator<ProtectedRegion> iter = ars.iterator(); iter.hasNext();) {
			if (iter.next().getId().equalsIgnoreCase(region)
					&& event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(errorMsg);
				return;
			}
		}
	}
}
