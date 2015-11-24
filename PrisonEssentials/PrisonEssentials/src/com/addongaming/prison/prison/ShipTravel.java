package com.addongaming.prison.prison;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.player.PrisonerManager;

public class ShipTravel implements Listener {
	private final String doorTitle = ChatColor.GOLD + "[" + ChatColor.BLUE
			+ "HcRide" + ChatColor.GOLD + "]";
	final Map<String, Location> doorMap = new HashMap<String, Location>();
	private JavaPlugin jp;

	public ShipTravel(JavaPlugin jp) {
		this.jp = jp;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		setupConfig();
		loadConfig();
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		for (String str : fc.getConfigurationSection("prison.ride").getKeys(
				false)) {
			Location loc = Utils.loadLoc(fc.getString("prison.ride." + str));
			doorMap.put(str, loc);
		}
	}

	private void setupConfig() {
		Location loc = new Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0);
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.ride.beginner", loc);
		fc.addDefault("prison.ride.kolguyev", loc);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@EventHandler
	public void signPlaceEvent(SignChangeEvent event) {
		if (event.getPlayer().isOp()) {
			if (event.getLine(0).equalsIgnoreCase("HcRide")) {
				if (PrisonManager.getInstance().getPrisonByName(
						event.getLine(2)) != null
						&& isNumber(event.getLine(3)))
					event.setLine(0, doorTitle);
				else
					event.getPlayer().sendMessage(
							event.getLine(2) + " is not a valid island.");
			}
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent event) {
		if (event.hasBlock() && event.getClickedBlock().getState() != null
				&& event.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase(doorTitle)) {
				int cost = Integer.parseInt(sign.getLine(3));
				if (PrisonerManager.getInstance()
						.getPrisonerInfo(event.getPlayer().getName())
						.hasBalance(cost)) {
					PrisonerManager.getInstance()
							.getPrisonerInfo(event.getPlayer().getName())
							.removeBalance(cost);
					event.getPlayer().teleport(
							doorMap.get(sign.getLine(2).toLowerCase()));
					event.getPlayer()
							.sendMessage(
									doorTitle
											+ ChatColor.GREEN
											+ " "
											+ cost
											+ " rupees have been withdrawn from your account.");
				} else {
					event.getPlayer().sendMessage(
							doorTitle + ChatColor.RED
									+ " Sorry you do not have enough rupees.");
				}
			}
		}
	}

	private boolean isNumber(String line) {
		try {
			Integer.parseInt(line);
			return true;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

}
