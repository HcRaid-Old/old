package com.hamgooof.hubsystem.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	List<Minigame> miniGames = new ArrayList<Minigame>();
	private String serverName;
	Location hub;
	private WorldGuardPlugin wg;

	@Override
	public void onEnable() {
		setupWorldGuard();
		setupConfig();
		initConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent event) {

		for (Minigame mg : miniGames) {
			if (event.getMessage().toLowerCase()
					.startsWith("/" + mg.getCommand())
					&& !event
							.getPlayer()
							.getWorld()
							.getName()
							.equalsIgnoreCase(
									mg.getMiniGameHub().getWorld().getName())) {
				event.getPlayer()
						.sendMessage(
								serverName
										+ " Sorry, you need to go to the correct minigame world.");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void playerTeleport(PlayerPortalEvent event) {
		Location loc = getTeleportLocation(event.getPlayer());
		if (loc != null) {
			event.getPlayer().teleport(loc);
			event.setCancelled(true);
		}
	}

	private void setupWorldGuard() {
		for (Plugin pl : this.getServer().getPluginManager().getPlugins())
			if (pl instanceof WorldGuardPlugin) {
				wg = (WorldGuardPlugin) pl;
				return;
			}
		System.err.println("World guard not found.");
		this.getServer().getPluginManager().disablePlugin(this);
	}

	private void setupConfig() {
		FileConfiguration fc = getConfig();
		Location test = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("server.name", "&3MyServer");
		fc.addDefault("spawn.location", locationToSaveString(test));
		fc.addDefault("minigames.minigame1.location",
				locationToSaveString(test));
		fc.addDefault("minigames.minigame1.cmd", "test");
		fc.options().copyDefaults(true);
		saveConfig();
		reloadConfig();
	}

	private void recalcConfig() {
		FileConfiguration fc = getConfig();
		fc.set("minigames", null);
		int counter = 1;
		for (Minigame mg : miniGames) {
			fc.set("minigames.minigame" + counter + ".location",
					locationToSaveString(mg.getMiniGameHub()));
			fc.set("minigames.minigame" + counter++ + ".cmd", mg.getCommand());
		}
		fc.set("spawn.location", locationToSaveString(hub));
		saveConfig();
		reloadConfig();

	}

	private void initConfig() {
		miniGames.clear();
		FileConfiguration fc = getConfig();
		for (String str : fc.getConfigurationSection("minigames")
				.getKeys(false)) {
			Minigame mg = new Minigame(
					fc.getString("minigames." + str + ".cmd"),
					loadLoc(fc.getString("minigames." + str + ".location")));
			miniGames.add(mg);
		}
		serverName = ChatColor.translateAlternateColorCodes('&',
				fc.getString("server.name") + ChatColor.RESET);
		hub = loadLoc(fc.getString("spawn.location"));
	}

	public boolean isPlayerInMinigameWorld(Player play) {
		return play.getWorld().getName()
				.equalsIgnoreCase(hub.getWorld().getName());
	}

	public Location getTeleportLocation(Player play) {
		ApplicableRegionSet ars = wg.getRegionManager(play.getWorld())
				.getApplicableRegions(play.getLocation());
		for (Iterator<ProtectedRegion> pr = ars.iterator(); pr.hasNext();) {
			ProtectedRegion pp = pr.next();
			if (pp.getId().equalsIgnoreCase("hub")) {
				System.out.println("Hub teleport");
				return hub;
			}
			for (Minigame mg : this.miniGames) {
				if (pp.getId().equalsIgnoreCase(mg.getCommand())) {
					System.out.println("Found minigame: " + mg.getCommand());
					return mg.getMiniGameHub();
				}
			}
		}
		System.out.println("None found");
		return null;
	}

	public String locationToSaveString(Location loc) {
		if (loc == null)
			return null;
		return loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY()
				+ "|" + loc.getZ() + "|" + loc.getYaw() + "|" + loc.getPitch();
	}

	public Location loadLoc(String string) {
		String[] split = string.split("[|]");
		double x, y, z;
		float yaw = -1, pitch = -1;
		World world = Bukkit.getWorld(split[0]);
		x = Double.parseDouble(split[1]);
		y = Double.parseDouble(split[2]);
		z = Double.parseDouble(split[3]);
		if (split.length == 4)
			return new Location(world, x, y, z);
		else if (split.length == 6)
			return new Location(world, x, y, z, Float.parseFloat(split[4]),
					Float.parseFloat(split[5]));
		else
			return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.isOp() || !(sender instanceof Player)) {
			sender.sendMessage(serverName
					+ " Sorry you do not have permission to do this.");
			return true;
		}
		// /mgman set
		if (args.length < 2) {
			sender.sendMessage(serverName + " Please use /mgman set spawn");
			sender.sendMessage(serverName + " Or /mgman list minigame");
			sender.sendMessage(serverName
					+ " Or /mgman <set|del> minigame <cmd>");
			return true;
		}
		switch (args[1].toLowerCase()) {
		case "spawn":
			hub = ((Player) (sender)).getLocation();
			sender.sendMessage(serverName
					+ " Set spawn location. Saving and reloading.");
			recalcConfig();
			return true;
		case "minigame":
			switch (args[0].toLowerCase()) {
			case "set":
				if (args.length <= 2) {
					sender.sendMessage(serverName
							+ " Please use /mgman set spawn");
					sender.sendMessage(serverName
							+ " Or /mgman set minigame <cmd>");
					return true;
				}
				Minigame mg = new Minigame(args[2],
						((Player) (sender)).getLocation());
				for (Minigame m : miniGames)
					if (m.getCommand().equalsIgnoreCase(mg.getCommand())) {
						sender.sendMessage(serverName
								+ " Sorry, this minigame already exists, please use /mgman del minigame <cmd>");
						return true;
					}
				miniGames.add(mg);
				recalcConfig();
				sender.sendMessage(serverName + " Added " + mg.getCommand());
				break;
			case "del":
				if (args.length <= 2) {
					sender.sendMessage(serverName
							+ " Please use /mgman set spawn");
					sender.sendMessage(serverName
							+ " Or /mgman set minigame <cmd>");
					return true;
				}
				for (Iterator<Minigame> iter = miniGames.iterator(); iter
						.hasNext();)
					if (iter.next().getCommand().equalsIgnoreCase(args[2])) {
						iter.remove();
						sender.sendMessage(serverName
								+ " Removed minigame command: " + args[2]);
						recalcConfig();
						return true;
					}
				sender.sendMessage(serverName
						+ " Could not find minigame with command: " + args[2]);
				return true;
			case "list":
				for (Minigame mini : miniGames)
					sender.sendMessage("Minigame: " + mini.getCommand()
							+ " World: "
							+ mini.getMiniGameHub().getWorld().getName());
				return true;
			}
			return true;
		}
		return true;
	}
}
