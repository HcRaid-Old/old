package com.addongaming.prison.farm.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TreeManager implements Listener {
	Material[] treeBased = { Material.LEAVES_2, Material.LEAVES, Material.LOG,
			Material.LOG_2 };

	List<String> treeFarms = new ArrayList<String>();

	public TreeManager(JavaPlugin jp) {
		initConfig(jp);
		loadConfig(jp);
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void blockBreakEvent(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getBlock().getWorld()).getApplicableRegions(
				event.getBlock().getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			ProtectedRegion pr = it.next();
			for (String region : treeFarms) {
				if (pr.getId().equalsIgnoreCase(region)) {
					if ((event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIRT || event
							.getBlock().getRelative(BlockFace.DOWN).getType() == Material.GRASS)
							&& (event.getBlock().getType() == Material.LOG || event
									.getBlock().getType() == Material.LOG_2)) {
						boolean log2 = event.getBlock().getType() == Material.LOG_2;
						short dura = event.getBlock().getData();
						if (log2)
							dura += 4;
						event.getBlock().breakNaturally(
								event.getPlayer().getItemInHand());
						event.getBlock().setType(Material.SAPLING);
						event.getBlock().setData((byte) dura);
						event.setCancelled(true);
						return;
					} else if (!isTreeBased(event.getBlock().getType()))
						event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getBlock().getWorld()).getApplicableRegions(
				event.getBlock().getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			ProtectedRegion pr = it.next();
			for (String region : treeFarms) {
				if (pr.getId().equalsIgnoreCase(region)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	private void initConfig(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.farms.tree.name", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				this.add("btreefarm");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	private boolean isTreeBased(Material m) {
		for (Material mat : treeBased)
			if (m == mat)
				return true;
		return false;
	}

	private void loadConfig(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		treeFarms = fc.getStringList("prison.farms.tree.name");
	}

	@EventHandler
	public void saplingBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getBlock().getType() != Material.SAPLING)
			return;
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getBlock().getWorld()).getApplicableRegions(
				event.getBlock().getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			for (String region : treeFarms)
				if (it.next().getId().equalsIgnoreCase(region)) {
					event.setCancelled(true);
					return;
				}
		}
	}

}
