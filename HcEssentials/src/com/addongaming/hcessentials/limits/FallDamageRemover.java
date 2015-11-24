package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class FallDamageRemover implements SubPlugin, Listener {
	private JavaPlugin jp;

	@EventHandler
	public void fallDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player
				&& event.getCause() == DamageCause.FALL
				&& event.getDamage() > 0.0) {
			ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
					event.getEntity().getWorld()).getApplicableRegions(
					event.getEntity().getLocation());
			if (safeLocations.contains("*")) {
				event.setCancelled(true);
				return;
			}
			for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
				ProtectedRegion pr = it.next();
				if (pr.getId().startsWith(prefix)
						|| safeLocations.contains(pr.getId())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@Override
	public void onDisable() {

	}

	private List<String> safeLocations = new ArrayList<String>();
	private String prefix;

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("worldguard.falldamageremover.enabled"))
			return false;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		safeLocations = jp.getConfig().getStringList(
				"worldguard.falldamageremover.regions");
		prefix = jp.getConfig()
				.getString("worldguard.falldamageremover.prefix");
		return true;
	}

	@SuppressWarnings("serial")
	public FallDamageRemover(JavaPlugin jp) {
		this.jp = jp;
		jp.getConfig().addDefault("worldguard.falldamageremover.enabled", true);
		jp.getConfig().addDefault("worldguard.falldamageremover.regions",
				new ArrayList<String>() {
					{
						this.add("outerspawn");
					}
				});
		jp.getConfig().addDefault("worldguard.falldamageremover.prefix",
				"nofall");
		jp.getConfig().options().copyDefaults(true);
	}
}
