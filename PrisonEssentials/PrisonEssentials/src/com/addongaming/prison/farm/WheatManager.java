package com.addongaming.prison.farm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WheatManager implements Listener {
	List<String> wheatFarms = new ArrayList<String>();
	private JavaPlugin jp;

	public WheatManager(JavaPlugin jp) {
		this.jp = jp;
		initConfig(jp);
		loadConfig(jp);
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockBreakEvent(final BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getBlock().getWorld()).getApplicableRegions(
				event.getBlock().getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			ProtectedRegion pr = it.next();
			for (String region : wheatFarms)
				if (pr.getId().equalsIgnoreCase(region)) {
					System.out.println("Matched region");
					System.out.println("Block type: "
							+ event.getBlock().getType().name());
					System.out.println("Block data: "
							+ event.getBlock().getData());
					if (event.getBlock().getType() == Material.CROPS) {
						if (event.getBlock().getData() == (byte) 7) {
							jp.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(jp,
											new Runnable() {

												@Override
												public void run() {
													event.getBlock().setType(
															Material.CROPS);
												}
											});
							return;
						} else if (event.getBlock().getData() < (byte) 7) {
							event.setCancelled(true);
							event.getPlayer()
									.sendMessage(
											ChatColor.RED
													+ "This wheat hasn't fully grown yet.");
							return;
						}
					}
					event.setCancelled(true);
					return;
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
			for (String region : wheatFarms) {
				if (pr.getId().equalsIgnoreCase(region)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()) {
			ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
					event.getClickedBlock().getWorld()).getApplicableRegions(
					event.getClickedBlock().getLocation());
			for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
				ProtectedRegion pr = it.next();
				for (String region : wheatFarms) {
					if (pr.getId().equalsIgnoreCase(region)) {
						if (blockPlace(event.getItem().getType(),
								event.getPlayer()))
							event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	Material[] dangerousItems = { Material.LAVA_BUCKET, Material.LAVA,
			Material.GRAVEL, Material.FLINT_AND_STEEL };

	public boolean blockPlace(Material place, Player player) {
		System.out.println(place.name());
		for (Material mat : dangerousItems)
			if (place == mat) {
				return true;
			}
		return false;
	}

	private void initConfig(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.farms.wheatfarm.name", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				this.add("bwheatfarm");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	private void loadConfig(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		wheatFarms = fc.getStringList("prison.farms.wheatfarm.name");
	}

}
