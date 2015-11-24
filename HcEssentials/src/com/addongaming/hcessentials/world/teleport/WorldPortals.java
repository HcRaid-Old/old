package com.addongaming.hcessentials.world.teleport;

import java.util.Iterator;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldPortals implements Listener, SubPlugin {
	private final JavaPlugin jp;

	public WorldPortals(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("world.portals.enabled", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("world.portals.enabled"))
			return false;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler
	public void playerWorldChangeEvent(PlayerPortalEvent event) {
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				event.getFrom().getWorld()).getApplicableRegions(
				event.getFrom());
		if (ars != null) {
			for (Iterator<ProtectedRegion> iter = ars.iterator(); iter
					.hasNext();) {
				ProtectedRegion pr = iter.next();
				if (pr.getId().toLowerCase().startsWith("warp")) {
					try {
						Location warp = HcEssentials.essentials.getWarps()
								.getWarp(
										pr.getId().substring(4,
												pr.getId().length()));
						event.setCancelled(true);
						event.getPlayer().teleport(warp);
						return;
					} catch (WarpNotFoundException | InvalidWorldException e) {
						e.printStackTrace();
						return;
					}

				}
			}
		}
	}
}
