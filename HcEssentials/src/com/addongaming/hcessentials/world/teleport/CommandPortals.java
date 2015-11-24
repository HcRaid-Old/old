package com.addongaming.hcessentials.world.teleport;

import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CommandPortals implements Listener, SubPlugin {
	private final JavaPlugin jp;

	public CommandPortals(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("world.cmdportals.enabled", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("world.cmdportals.enabled"))
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
				if (pr.getId().toLowerCase().startsWith("cmd")) {
					String cmd = pr.getId().substring(3, pr.getId().length());
					final PlayerCommandPreprocessEvent eve = new PlayerCommandPreprocessEvent(
							event.getPlayer(), "/" + cmd);
					jp.getServer().getPluginManager().callEvent(eve);
					if (!eve.isCancelled())
						jp.getServer().dispatchCommand(event.getPlayer(), cmd);
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
