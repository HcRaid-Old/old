package com.addongaming.hcessentials.minigames.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.events.TeamProtectEvent;
import com.addongaming.hcessentials.minigames.Minigame;
import com.addongaming.hcessentials.minigames.RedvBlueMethods.ScheduledWallRemover;
import com.addongaming.hcessentials.minigames.RedvBlueMethods.WallRebuilder;
import com.addongaming.hcessentials.teams.Team;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RedvBlue implements Minigame, Listener {

	Team blue;
	Team red;
	String title = ChatColor.BLUE + "[" + ChatColor.RED + "HcRedVBlue"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;

	JavaPlugin jp;
	public boolean readyToJoin = false;
	private boolean setup = false;
	HashMap<String, Integer> lifeMap = new HashMap<String, Integer>();
	String rtitle = ChatColor.RED + "[RedTeam]";
	String btitle = ChatColor.BLUE + "[BlueTeam]";
	public List<String> rchatToggled = new ArrayList<String>();
	public List<String> bchatToggled = new ArrayList<String>();
	private ScheduledWallRemover instance;
	private WallRebuilder inst;

	public RedvBlue(JavaPlugin jp) {
		instance = new ScheduledWallRemover(jp);
		inst = new WallRebuilder(jp);
		this.jp = jp;
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getChannel(String name) {
		if (rchatToggled.contains(name))
			return "red";
		if (bchatToggled.contains(name))
			return "blue";
		else {
			return null;
		}
	}

	@EventHandler
	public void BluePlayerDamaged(EntityDamageByEntityEvent event) {
		System.out.println("Blue Player Damage");
		if (blue == null)
			return;
		System.out.println("Blue team isn't null.");
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			System.out.println("Both entities are players");
			String dmgr = ((Player) event.getDamager()).getName().toLowerCase();
			String dmge = ((Player) event.getEntity()).getName().toLowerCase();
			System.out.println("Damager: " + dmgr + "  Damagee: " + dmge);
			if (blue.containsPlayer(dmgr) && blue.containsPlayer(dmge)) {
				System.out.println("Both players are in blue team.");
				TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, blue);
				Bukkit.getPluginManager().callEvent(ev);
				System.out.println("TeamProtectEvent is cancelled: "
						+ ev.isCancelled());
				if (!ev.isCancelled())
					event.setCancelled(true);
				return;
			}
		} else if (event.getDamager() instanceof Arrow
				&& event.getEntity() instanceof Player) {
			Arrow arr = (Arrow) event.getDamager();
			if (arr.getShooter() instanceof Player) {
				String dmgr = ((Player) arr.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (blue.containsPlayer(dmgr) && blue.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, blue);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;
				}
			}
		} else if (event.getDamager() instanceof org.bukkit.entity.ThrownPotion
				&& event.getEntity() instanceof Player) {
			ThrownPotion tp = (ThrownPotion) event.getDamager();
			if (tp.getShooter() instanceof Player) {
				String dmgr = ((Player) tp.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (blue.containsPlayer(dmgr) && blue.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, blue);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;

				}
			}
		}
	}

	@EventHandler
	public void RedPlayerDamaged(EntityDamageByEntityEvent event) {
		System.out.println("red Player Damage");
		if (red == null)
			return;
		System.out.println("red team isn't null.");
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			System.out.println("Both entities are players");
			String dmgr = ((Player) event.getDamager()).getName().toLowerCase();
			String dmge = ((Player) event.getEntity()).getName().toLowerCase();
			System.out.println("Damager: " + dmgr + "  Damagee: " + dmge);
			if (red.containsPlayer(dmgr) && red.containsPlayer(dmge)) {
				System.out.println("Both players are in red team.");
				TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, red);
				Bukkit.getPluginManager().callEvent(ev);
				System.out.println("TeamProtectEvent is cancelled: "
						+ ev.isCancelled());
				if (!ev.isCancelled())
					event.setCancelled(true);
				return;
			}
		} else if (event.getDamager() instanceof Arrow
				&& event.getEntity() instanceof Player) {
			Arrow arr = (Arrow) event.getDamager();
			if (arr.getShooter() instanceof Player) {
				String dmgr = ((Player) arr.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (red.containsPlayer(dmgr) && red.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, red);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;
				}
			}
		} else if (event.getDamager() instanceof org.bukkit.entity.ThrownPotion
				&& event.getEntity() instanceof Player) {
			ThrownPotion tp = (ThrownPotion) event.getDamager();
			if (tp.getShooter() instanceof Player) {
				String dmgr = ((Player) tp.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (red.containsPlayer(dmgr) && red.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, red);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;

				}
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player dead = e.getPlayer();
		Team team = null;
		if (red.containsPlayer(dead.getPlayer().getName().toLowerCase())) {
			team = red;
		} else if (blue
				.containsPlayer(dead.getPlayer().getName().toLowerCase())) {
			team = blue;
		}
		if (team == null) {
			return;
		}
		lifeMap.put(dead.getPlayer().getName().toLowerCase(),
				lifeMap.get(dead.getPlayer().getName().toLowerCase()) - 1);
		if (lifeMap.get(dead.getPlayer().getName().toLowerCase()) == 0) {
			if (team == red)
				red.removePlayer(dead.getPlayer().getName().toLowerCase());
			rchatToggled.remove(dead.getPlayer().getName().toLowerCase());
			if (team == blue)
				blue.removePlayer(dead.getPlayer().getName().toLowerCase());
			bchatToggled.remove(dead.getPlayer().getName().toLowerCase());
			lifeMap.remove(dead.getPlayer().getName().toLowerCase());
			dead.sendMessage(title + "You full on died, bro.");
			try {
				e.getPlayer().teleport(
						HcEssentials.essentials.getWarps().getWarp("spawn"));
			} catch (WarpNotFoundException | InvalidWorldException ex) {
				ex.printStackTrace();
			}
		} else if (red.containsPlayer(e.getPlayer().getPlayer().getName()
				.toLowerCase())) {
			e.getPlayer().setHealth(20);
			e.setRespawnLocation(red.getHome());
			e.getPlayer().performCommand("rvb join");
		}

		else if (blue.containsPlayer(e.getPlayer().getPlayer().getName()
				.toLowerCase())) {
			e.getPlayer().setHealth(20);
			e.setRespawnLocation(blue.getHome());
			e.getPlayer().performCommand("rvb join");
		}
	}

	@Override
	public String getMinigameName() {
		return "rvb";
	}

	@Override
	public boolean isInGame(String str) {
		if (readyToJoin) {
			return true;
		}
		return false;
	}

	@Override
	public void commandIssued(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(title
					+ "Please choose from one of the following:");
			sender.sendMessage("    " + "/rvb - Lists all commands.");
			sender.sendMessage("    " + "/rvb join - Joins RedVBlue.");
			sender.sendMessage("     " + "/rvb c - Toggles chat.");
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				sender.sendMessage("    " + "/rvb setup - Sets up teams.");
				sender.sendMessage("    "
						+ "/rvb add <player> <team> - Adds a player to specified team.");
				sender.sendMessage("    "
						+ "/rvb del <player> <team> - Removes a player from specified team.");
				sender.sendMessage("    "
						+ "/rvb bluelist - Lists all blue members.");
				sender.sendMessage("    "
						+ "/rvb redlist - Lists all red members.");
				sender.sendMessage("    " + "/rvb selred - Selects red wall.");
				sender.sendMessage("    " + "/rvb selblue - Selects blue wall.");
				sender.sendMessage("    "
						+ "/rvb setblue - Sets blue team's spawn.");
				sender.sendMessage("    "
						+ "/rvb setlives - Sets the player's lives limits.");
				sender.sendMessage("    "
						+ "/rvb setred - Sets red team's spawn.");
				sender.sendMessage("    " + "/rvb start - Starts the game.");
				sender.sendMessage("    " + "/rvb wall1 - Sets wall 1's flags.");
				sender.sendMessage("    " + "/rvb wall2 - Sets wall 2's flags.");
				sender.sendMessage("    " + "/rvb wall3 - Sets wall 3's flags.");
				sender.sendMessage("    " + "/rvb wall4 - Sets wall 4's flags.");
				sender.sendMessage("    "
						+ "/rvb mid - Sets mid section's flags.");
				sender.sendMessage("    "
						+ "/rvb starttimer - Starts the walls timer.");
				sender.sendMessage("    "
						+ "/rvb rebuild - Rebuilds red and blue walls.");
				sender.sendMessage("    " + "/rvb end - Ends game.");
			}
			sender.sendMessage(title);
			return;
		}
		switch (args[0]) {
		case "join":
			if (blue.containsPlayer(sender.getName().toLowerCase())
					|| red.containsPlayer(sender.getName().toLowerCase())) {
				if (!this.readyToJoin && !this.setup) {
					sender.sendMessage(title
							+ "Red v Blue has not started yet.");
					if (sender.isOp()
							|| sender.hasPermission("hcraid.minigames")
							&& !this.readyToJoin && !this.setup) {
						sender.sendMessage(title + "Please setup RvB first.");
					}
				}
				Player p = (Player) sender;
				if (blue.containsPlayer(sender.getName().toLowerCase())
						&& readyToJoin == true && setup == true) {
					p.teleport(blue.getHome());
					p.sendMessage(title + "Joined Red v Blue.");
				}
				if (red.containsPlayer(sender.getName().toLowerCase())
						&& readyToJoin == true && setup == true) {
					p.teleport(red.getHome());
					p.sendMessage(title + "Joined Red v Blue.");
				}
			} else {
				sender.sendMessage(title + "Sorry, you are not in this game.");
			}
			if (blue.containsPlayer(sender.getName().toLowerCase())
					|| red.containsPlayer(sender.getName().toLowerCase())) {
				if (!this.readyToJoin && !this.setup) {
					sender.sendMessage(title
							+ "Red v Blue has not started yet.");
					if (sender.isOp() && !this.readyToJoin && !this.setup) {
						sender.sendMessage(title + "Please setup RvB first.");
					}
				}
			}
			break;
		case "lobby":
			if (blue.containsPlayer(sender.getName().toLowerCase())
					|| red.containsPlayer(sender.getName().toLowerCase())) {
				Player p = (Player) sender;
				try {
					p.teleport(HcEssentials.essentials.getWarps().getWarp(
							"rvblobby"));
				} catch (WarpNotFoundException | InvalidWorldException e) {
					e.printStackTrace();
				}
			}
			break;
		case "setlobby":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player pl = (Player) sender;
				try {
					HcEssentials.essentials.getWarps().setWarp("rvblobby",
							pl.getLocation());
					sender.sendMessage(title + "Lobby set.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case "setlives":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				if (isInteger(args[1])) {
					int i = Integer.parseInt(args[1]);
					for (String member : blue.getMembers()) {
						lifeMap.put(member, i);
					}
					for (String member : red.getMembers()) {
						lifeMap.put(member, i);
					}
					sender.sendMessage(title + "Player lives set.");
				}
			}
			break;
		case "setup":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player player = (Player) sender;
				blue = new Team(player.getName().toLowerCase(), "Blue", null);
				red = new Team(player.getName().toLowerCase(), "Red", null);
				if (blue.getHome() == null && red.getHome() == null) {
					blue.setHome(player.getLocation());
					red.setHome(player.getLocation());
				}
				player.sendMessage(title
						+ "Set-up teams, please set spawns with /rvb set<colour>");
				setup = true;
			}
			break;
		case "starttimer":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				instance.setReady(true);
				sender.sendMessage(title + "Timer has started.");
				instance.run();
			}
			break;
		case "bluelist":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				String[] text = blue.getListedPlayers();
				if (text[0].length() < 3)
					text[0] = title + "There are no blue members online.";
				else
					text[0] = title + "Online blue members: " + text[0];
				if (text[1].length() < 3)
					text[1] = title + "All blue members are online.";
				else
					text[1] = title + "Offline blue members: " + text[1];
				p.sendMessage(text);
			}
			if (sender.isOp() && setup == false) {
				sender.sendMessage(title + "Please execute /rvb setup first.");
			}
			break;
		case "redlist":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				String[] text = red.getListedPlayers();
				if (text[0].length() < 3)
					text[0] = title + "There are no red members online.";
				else
					text[0] = title + "Online red members: " + text[0];
				if (text[1].length() < 3)
					text[1] = title + "All red members are online.";
				else
					text[1] = title + "Offline red members: " + text[1];
				p.sendMessage(text);
			}
			if (sender.isOp() && setup == false) {
				sender.sendMessage(title + "Please execute /rvb setup first.");
			}
		case "add":
			if (args.length == 3 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				if (blue.containsPlayer(args[1].toLowerCase())
						|| red.containsPlayer(args[1].toLowerCase())) {
					sender.sendMessage(title + "Player already added.");
				}
				if (args[2].equalsIgnoreCase("blue")) {
					sender.sendMessage(title + "Player added to blue.");
					blue.addPlayer(args[1].toLowerCase());
				}
				if (args[2].equalsIgnoreCase("red")) {
					sender.sendMessage(title + "Player added to red.");
					red.addPlayer(args[1].toLowerCase());
				}
				if (args.length == 3 && sender.isOp() && setup == false) {
					sender.sendMessage(title
							+ "Please execute /rvb setup first.");
				}
			}
			break;
		case "del":
			if (args.length == 2 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				if (blue.containsPlayer(args[1].toLowerCase())
						&& args[2].equalsIgnoreCase("blue")) {
					blue.removePlayer(args[1]);
					sender.sendMessage(title + "Player removed from blue.");
				} else {
					if (red.containsPlayer(args[1].toLowerCase())
							&& args[2].equalsIgnoreCase("red"))
						red.removePlayer(args[1]);
					sender.sendMessage(title + "Player removed from red.");
				}
			}
			break;
		case "setblue":
			if (blue.getHome() == null || blue.getHome() != null
					&& sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				sender.sendMessage(title + "Blue location set.");
				blue.setHome(p.getLocation());
			}
			if (blue.getHome() == null && sender.isOp() && setup == false) {
				sender.sendMessage(title + "Please execute /rvb setup first.");
			}
			break;
		case "c":
		case "chat":
			Player p = (Player) sender;
			if (args.length != 0
					&& red.containsPlayer(p.getName().toLowerCase())) {
				if (rchatToggled.contains(sender.getName())) {
					sender.sendMessage(title + "Red chat is now off!");
					rchatToggled.remove(sender.getName());
					return;
				} else {
					if (getChannel(p.getName()) != null)
						return;
					sender.sendMessage(title + "Red chat is now on!");
					rchatToggled.add(sender.getName());
					return;
				}
			}
			if (args.length != 0
					&& blue.containsPlayer(p.getName().toLowerCase())) {
				if (bchatToggled.contains(sender.getName())) {
					sender.sendMessage(title + "Blue chat is now off!");
					bchatToggled.remove(sender.getName());
					return;
				} else {
					if (getChannel(p.getName()) != null)
						return;
					sender.sendMessage(title + "Blue chat is now on!");
					bchatToggled.add(sender.getName());
					return;
				}
			}
			break;
		case "setred":
			if (red.getHome() == null || red.getHome() != null && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player pl = (Player) sender;
				sender.sendMessage(title + "Red location set.");
				red.setHome(pl.getLocation());
			}
			if (red.getHome() == null && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")
					&& setup == false) {
				sender.sendMessage(title + "Please execute /rvb setup first.");
			}
			break;
		case "unsetblue":
			if (blue.getHome() != null && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				sender.sendMessage(title + "Blue location unset.");
				blue.setHome(null);
			}
			break;
		case "unsetred":
			if (red.getHome() != null && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				sender.sendMessage(title + "Red location unset.");
				red.setHome(null);
			}
			break;
		case "selred":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin plugin = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (plugin != null) {
					WorldEditPlugin wep = (WorldEditPlugin) plugin;
					Selection sr = wep.getSelection((Player) sender);
					if (sr == null || sr.getMinimumPoint() == null
							|| sr.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					LocationZone rw = new LocationZone(sr.getMinimumPoint(),
							sr.getMaximumPoint());
					instance.setRedWall(rw);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					Location minvec = instance.getRedWall().getMin();
					Vector mivec = BukkitUtil.toVector(minvec);
					BlockVector minbv = new BlockVector(mivec);
					Location maxvec = instance.getRedWall().getMax();
					Vector mavec = BukkitUtil.toVector(maxvec);
					BlockVector maxbv = new BlockVector(mavec);
					ProtectedRegion rpr = new ProtectedCuboidRegion("redwall",
							minbv, maxbv);
					manager.addRegion(rpr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						rpr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
					sender.sendMessage(title + "Red wall and flags set.");
				}
			}
			break;
		case "mid":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location miw1 = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(miw1);
					Location maw1 = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(maw1);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion wpr = new ProtectedCuboidRegion("wall1",
							mibv, mabv);
					manager.addRegion(wpr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						wpr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Mid section set.");
			}
			break;
		case "wall1":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location miw1 = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(miw1);
					Location maw1 = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(maw1);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion wpr = new ProtectedCuboidRegion("wall1",
							mibv, mabv);
					manager.addRegion(wpr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						wpr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Wall one set.");
			}
			break;
		case "wall2":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location miw2 = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(miw2);
					Location maw2 = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(maw2);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion w2pr = new ProtectedCuboidRegion("wall2",
							mibv, mabv);
					manager.addRegion(w2pr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						w2pr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Wall two set.");
			}
			break;
		case "wall3":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location miw3 = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(miw3);
					Location maw3 = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(maw3);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion w3pr = new ProtectedCuboidRegion("wall3",
							mibv, mabv);
					manager.addRegion(w3pr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						w3pr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Wall three set.");
			}
			break;
		case "wall4":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location miw4 = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(miw4);
					Location maw4 = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(maw4);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion w4pr = new ProtectedCuboidRegion("wall4",
							mibv, mabv);
					manager.addRegion(w4pr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						w4pr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Wall four set.");
			}
			break;
		case "roof":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					Location mir = sb.getMinimumPoint();
					Vector mivec = BukkitUtil.toVector(mir);
					Location mar = sb.getMaximumPoint();
					Vector mavec = BukkitUtil.toVector(mar);
					BlockVector mibv = new BlockVector(mivec);
					BlockVector mabv = new BlockVector(mavec);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					ProtectedRegion rpr = new ProtectedCuboidRegion("roof",
							mibv, mabv);
					manager.addRegion(rpr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						rpr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(title + "Roof set.");
			}
			break;
		case "selblue":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection sb = wep.getSelection((Player) sender);
					if (sb == null || sb.getMinimumPoint() == null
							|| sb.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					LocationZone bw = new LocationZone(sb.getMinimumPoint(),
							sb.getMaximumPoint());
					instance.setBlueWall(bw);
					Player plp = (Player) sender;
					World w = plp.getWorld();
					RegionManager manager = HcEssentials.worldGuard
							.getRegionManager(w);
					Location minvec = instance.getBlueWall().getMin();
					Vector mivec = BukkitUtil.toVector(minvec);
					BlockVector minbv = new BlockVector(mivec);
					Location maxvec = instance.getBlueWall().getMax();
					Vector mavec = BukkitUtil.toVector(maxvec);
					BlockVector maxbv = new BlockVector(mavec);
					ProtectedRegion bpr = new ProtectedCuboidRegion("bluewall",
							minbv, maxbv);
					manager.addRegion(bpr);
					StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
					try {
						bpr.setFlag(build, build.parseInput(
								HcEssentials.worldGuard, sender, "deny"));
					} catch (InvalidFlagFormat e) {
						e.printStackTrace();
					}

					sender.sendMessage(title + "Blue wall and flags set.");
				}
			}
			break;
		case "rebuild":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				inst.WallRebuild();
				sender.sendMessage(title + "Walls rebuilt.");
			}
			break;
		case "start":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")
					&& setup == true) {
				readyToJoin = true;
				setup = true;
				sender.sendMessage(title + "Red v Blue started.");
			}
			if (sender.isOp() && setup == false) {
				sender.sendMessage(title + "Please execute /rvb setup first.");
			}
			break;
		case "end":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				for (Player pp : Bukkit.getOnlinePlayers()) {
					if (blue.containsPlayer(pp.getName().toLowerCase())
							|| red.containsPlayer(pp.getName().toLowerCase())) {
						try {
							pp.teleport(HcEssentials.essentials.getWarps()
									.getWarp("spawn"));
						} catch (WarpNotFoundException | InvalidWorldException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					HcEssentials.essentials.getWarps().removeWarp("rvblobby");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				blue = null;
				red = null;
				rchatToggled.clear();
				bchatToggled.clear();
				inst.WallRebuild();
				sender.sendMessage(title + "Red v Blue ended.");
			}
			return;
		}
	}

	@Override
	public String getMinigameNotation() {
		return getMinigameName();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		if (getChannel(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
			if (rchatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (red.containsPlayer(p.getName().toLowerCase())) {
						p.sendMessage(title + " <"
								+ e.getPlayer().getDisplayName() + "> "
								+ e.getMessage());
					}
			}
		}
		if (getChannel(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
			if (bchatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (blue.containsPlayer(p.getName().toLowerCase())) {
						p.sendMessage(title + " <"
								+ e.getPlayer().getDisplayName() + "> "
								+ e.getMessage());
					}
			}
		}
	}

	@Override
	public void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("minigames.rvb.enabled", false);
		fc.addDefault("minigames.rvb.timer", 10);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public boolean onEnable() {
		loadConfig();
		if (!jp.getConfig().getBoolean("minigames.rvb.enabled"))
			return false;
		instance.setTimer(jp.getConfig().getInt("minigames.rvb.timer"));
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

}
