package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HungerRemover implements SubPlugin, Listener {
	private JavaPlugin jp;

	@EventHandler
	public void hungerDamage(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player
				&& event.getFoodLevel() < ((Player) (event.getEntity()))
						.getFoodLevel()) {
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
		// TODO Auto-generated method stub

	}

	private List<String> safeLocations = new ArrayList<String>();
	private String prefix;

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("worldguard.hungerremover.enabled"))
			return false;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		safeLocations = jp.getConfig().getStringList(
				"worldguard.hungerremover.regions");
		prefix = jp.getConfig()
				.getString("worldguard.falldamageremover.prefix");
		return true;
	}

	@SuppressWarnings("serial")
	public HungerRemover(JavaPlugin jp) {
		this.jp = jp;
		jp.getConfig().addDefault("worldguard.hungerremover.enabled", true);
		jp.getConfig().addDefault("worldguard.hungerremover.regions",
				new ArrayList<String>() {
					{
						this.add("innerspawn");
					}
				});
		jp.getConfig()
				.addDefault("worldguard.hungerremover.prefix", "nohunger");
		jp.getConfig().options().copyDefaults(true);
	}
}
