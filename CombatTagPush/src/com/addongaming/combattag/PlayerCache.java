package com.addongaming.combattag;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.combat.antilogging.CombatLogging;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlayerCache implements Listener {
	private HashMap<Player, Location> locationMap = new HashMap<Player, Location>();
	private HashMap<Player, Integer> schedulerMap = new HashMap<Player, Integer>();
	private final JavaPlugin jp;
	private CombatLogging ct = null;
	private WorldGuardPlugin wgp = null;
	private RegionManager ars = null;
	private final String region = "pvpline";

	public PlayerCache(JavaPlugin jp) {
		this.jp = jp;
		for (Player p : Bukkit.getOnlinePlayers())
			schedulePlayer(p);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		ct = Combat.getCombatInstance();
		System.out.println("Combat tag is hooked : " + (ct != null));
		for (Plugin plugin : jp.getServer().getPluginManager().getPlugins())
			if (plugin instanceof WorldGuardPlugin) {
				wgp = (WorldGuardPlugin) plugin;
				ars = wgp.getRegionManager(Bukkit.getWorld("world"));
				break;
			}
		if (ct == null || wgp == null || ars == null) {
			System.err
					.println("Either combat logging or worldguard hooking has a problem.");
			System.err.println("Combatlogging status: "
					+ (ct != null ? "Active" : "Error"));
			System.err.println("WorldGuardPlugin status: "
					+ (wgp != null ? "Active" : "Error"));
			System.err.println("RegionManager status: "
					+ (ars != null ? "Active" : "Error"));
			jp.getServer().getPluginManager().disablePlugin(jp);
		}
	}

	@EventHandler
	public void playerJoinEvent(final PlayerJoinEvent event) {
		schedulePlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerMoveEvent(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (event.getFrom().getBlockX() == event.getTo().getBlockX()
				&& event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;
		if (ct.isInCombat(event.getPlayer().getName())
				&& player.getWorld().getName().equalsIgnoreCase("world")) {
			Iterator<ProtectedRegion> iter = ars.getApplicableRegions(
					player.getLocation()).iterator();
			while (iter.hasNext())
				if (iter.next().getId().equalsIgnoreCase(region)) {
					if (locationMap.containsKey(event.getPlayer())) {
						event.getPlayer().teleport(
								locationMap.get(event.getPlayer()),
								TeleportCause.PLUGIN);
						event.getPlayer().setNoDamageTicks(0);
						//player.setMaximumNoDamageTicks(2);
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(jp, new Runnable() {

									@Override
									public void run() {
										player.setNoDamageTicks(0);
									}
								});
						event.getPlayer().sendMessage(
								"You are still in combat! Stand and fight!");
					}
					return;
				}
			locationMap.put(event.getPlayer(), event.getPlayer().getLocation());
		}
	}

	@EventHandler
	public void playerQuitEvent(final PlayerQuitEvent event) {
		jp.getServer().getScheduler()
				.cancelTask(schedulerMap.remove(event.getPlayer()));
		locationMap.remove(event.getPlayer());
	}

	private void schedulePlayer(final Player p) {
		schedulerMap.put(p, jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {
					private Player player = p;

					@Override
					public void run() {
						if (player == null || !player.isOnline())
							return;
						Iterator<ProtectedRegion> iter = wgp
								.getRegionManager(player.getWorld())
								.getApplicableRegions(player.getLocation())
								.iterator();
						while (iter.hasNext())
							if (iter.next().getId().equalsIgnoreCase(region))
								return;
						locationMap.put(player, player.getLocation());
					}
				}, 5l, 5l));
	}

	public boolean hasPlayer(Player player) {
		return locationMap.containsKey(player);
	}

	public Location getLocation(Player player) {
		return locationMap.get(player);
	}
}
