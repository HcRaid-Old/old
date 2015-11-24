package core.hcrg.spleef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import core.essentials.Main;
import core.essentials.objects.Config;
import core.hcrg.Minigame;

public class Spleef implements Minigame, Listener {
	private HashSet<String> players = null;
	private boolean started = false;
	private String pluginName = "[HcSpleef] ";
	private final JavaPlugin jp;
	private Location lowCorner = new Location(Bukkit.getServer().getWorld(
			"minigames"), 272, 62, 568);
	private Location highCorner = new Location(Bukkit.getServer().getWorld(
			"minigames"), 317, 92, 614);
	private ArrayList<Location> diamondList = null;
	private ArrayList<Location> glowList = null;
	private Location[] spleefWaiting = new Location[] {
			new Location(Bukkit.getWorld("minigames"), 264, 64, 588),
			new Location(Bukkit.getWorld("minigames"), 293, 64, 616),
			new Location(Bukkit.getWorld("minigames"), 323, 64, 588),
			new Location(Bukkit.getWorld("minigames"), 293, 64, 588) };
	private Location lowPlaying = new Location(Bukkit.getWorld("minigames"),
			279, 63, 574);
	private Location highPlaying = new Location(Bukkit.getWorld("minigames"),
			307, 63, 602);

	public Spleef(JavaPlugin jp) {
		this.jp = jp;
	}

	@EventHandler
	public void playerLeft(PlayerQuitEvent pqe) {
		if (players != null && players.contains(pqe.getPlayer().getName())) {
			players.remove(pqe.getPlayer().getName());
			if (started)
				checkEnd();
		}
	}

	@EventHandler
	public void playerKick(PlayerKickEvent pqe) {
		if (players != null && players.contains(pqe.getPlayer().getName())) {
			players.remove(pqe.getPlayer().getName());
			if (started)
				checkEnd();
		}
	}

	@Override
	public void checkEnd() {
		if (players.size() == 1) {
			Player winner = jp.getServer().getPlayer(players.iterator().next());
			msg(winner, "You won $100 for winning!");
			if (Main.economy != null)
				Main.economy.depositPlayer(winner.getName(), 100.0);
			stop(winner, true);
			winner.teleport(new Location(jp.getServer().getWorld(
					Config.Spawn.world), Config.Spawn.x, Config.Spawn.y,
					Config.Spawn.z));
		}
	}

	@EventHandler
	public void playerHit(EntityDamageByEntityEvent edbee) {
		if (edbee.getDamager() instanceof Player
				&& edbee.getEntity() instanceof Player) {
			if (isInArena(edbee.getEntity().getLocation()))
				edbee.setCancelled(true);
		}
	}

	@EventHandler
	public void blockDamage(BlockDamageEvent bde) {
		if (isInArena(new Location(bde.getBlock().getLocation().getWorld(), bde
				.getBlock().getX(), bde.getBlock().getY() + 1, bde.getBlock()
				.getZ()))) {
			if (!started) {
				bde.setCancelled(true);
			} else if (bde.getBlock().getType() == Material.DIAMOND_BLOCK
					&& bde.getBlock().getLocation().getY() == 62) {
				bde.getBlock().setType(Material.AIR);
				diamondList.add(bde.getBlock().getLocation());
			} else if (bde.getBlock().getType() == Material.GLOWSTONE
					&& bde.getBlock().getLocation().getY() == 62) {
				bde.getBlock().setType(Material.AIR);
				glowList.add(bde.getBlock().getLocation());
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent bde) {
		if (isInArena(new Location(bde.getBlock().getLocation().getWorld(), bde
				.getBlock().getX(), bde.getBlock().getY() + 1, bde.getBlock()
				.getZ()))) {
			if (!started) {
				bde.setCancelled(true);
			} else if (bde.getBlock().getType() == Material.DIAMOND_BLOCK
					&& bde.getBlock().getLocation().getY() == 62) {
				bde.getBlock().setType(Material.AIR);
				diamondList.add(bde.getBlock().getLocation());
			} else if (bde.getBlock().getType() == Material.GLOWSTONE
					&& bde.getBlock().getLocation().getY() == 62) {
				bde.getBlock().setType(Material.AIR);
				glowList.add(bde.getBlock().getLocation());
			}
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent pme) {
		if (!started)
			return;
		if (!players.contains(pme.getPlayer().getName()))
			return;
		if (!isInArena(pme.getTo())) {
			for (String s : players.toArray(new String[players.size()])) {
				Player p = jp.getServer().getPlayer(s);
				if (p != null)
					msg(p, pme.getPlayer().getDisplayName()
							+ " has been eliminated!");
			}
			players.remove(pme.getPlayer().getName());
			pme.getPlayer().teleport(
					new Location(jp.getServer().getWorld(Config.Spawn.world),
							Config.Spawn.x, Config.Spawn.y, Config.Spawn.z));
			if (players.size() == 1) {
				Player winner = jp.getServer().getPlayer(
						players.iterator().next());
				msg(winner, "You won $200 for winning!");
				if (Main.economy != null)
					Main.economy.depositPlayer(winner.getName(), 200.0);
				stop(winner, true);
				winner.teleport(new Location(jp.getServer().getWorld(
						Config.Spawn.world), Config.Spawn.x, Config.Spawn.y,
						Config.Spawn.z));
			}
		}
	}

	private boolean isInArena(Location loc) {
		return loc.getBlockX() > lowCorner.getBlockX()
				&& loc.getBlockX() < highCorner.getBlockX()
				&& loc.getBlockY() > lowCorner.getBlockY()
				&& loc.getBlockY() < highCorner.getBlockY()
				&& loc.getBlockZ() > lowCorner.getBlockZ()
				&& loc.getBlockZ() < highCorner.getBlockZ();
	}

	@Override
	public String getName() {
		return "Spleef";
	}

	@Override
	public HashSet<String> getPlayers() {
		return players;
	}

	@Override
	public void warn(Player p, String warning) {
		p.sendMessage(ChatColor.RED + pluginName + ChatColor.WHITE + warning);
	}

	@Override
	public void msg(Player p, String message) {
		p.sendMessage(ChatColor.GREEN + pluginName + ChatColor.WHITE + message);
	}

	@Override
	public boolean join(Player p) {
		if (players == null) {
			warn(p, "No game has been started");
			return false;
		} else if (started) {
			warn(p, "Game is already in progress!");
			return false;
		} else if (!p.hasPermission("HcRaid.grunt")) {
			warn(p, "You do not have permission.");
			return false;
		} else {
			players.add(p.getName());
			msg(p, "You have joined the spleef game.");
			Random r = new Random();
			int i = r.nextInt(3);
			p.teleport(spleefWaiting[i]);
			return true;
		}
	}

	@Override
	public boolean leave(Player p) {
		if (players == null || !players.contains(p.getName())) {
			warn(p, "You are not in a spleef game");
			return false;
		} else if (started) {
			warn(p, "You are in the middle of a game! You can't leave now!");
			return false;
		} else {
			msg(p, "You have left the game.");
			players.remove(p.getName());
			p.teleport(new Location(
					jp.getServer().getWorld(Config.Spawn.world),
					Config.Spawn.x, Config.Spawn.y, Config.Spawn.z));
			return true;
		}
	}

	@Override
	public boolean start(Player p) {
		if (players != null) {
			warn(p, "There's already a game in progress!");
			return false;
		} else if (!p.hasPermission("HcRaid.MOD")) {
			warn(p, "You do not have permission.");
			return false;
		} else {
			players = new HashSet<String>();
			diamondList = new ArrayList<Location>();
			glowList = new ArrayList<Location>();
			jp.getServer()
					.broadcast(
							ChatColor.GREEN
									+ pluginName
									+ ChatColor.WHITE
									+ "A game has been started! Join with /spleef join!",
							"HcRaid.Grunt");
			return true;
		}
	}

	@Override
	public final boolean run(Player p) {
		if (Spleef.taskid >= 0) {
			warn(p, "Countdown already initiated.");
			return false;
		} else if (started) {
			warn(p, "Game already in progress");
			return false;
		} else if (players == null) {
			warn(p, "No game in progress.");
			return false;
		} else if (players.size() < 2) {
			warn(p, "Not enough players.");
			return false;
		} else if (!p.hasPermission("HcRaid.MOD")) {
			warn(p, "You do not have permission!");
			return false;
		} else {
			taskid = Bukkit.getServer().getScheduler()
					.scheduleSyncRepeatingTask(jp, new BukkitRunnable() {
						int counter = 10;

						@Override
						public void run() {
							if (counter > 0
									&& (counter % 2 == 0 || counter < 5)) {
								for (String s : players
										.toArray(new String[players.size()])) {
									Player p = jp.getServer().getPlayer(s);
									if (p != null) {
										msg(jp.getServer().getPlayer(s),
												"Game is starting in: "
														+ counter + " seconds.");
									}
								}
							} else if (counter <= 0) {
								started = true;
								for (String s : players
										.toArray(new String[players.size()])) {
									Player p = jp.getServer().getPlayer(s);
									if (p != null) {
										msg(jp.getServer().getPlayer(s),
												"Game has started!");
										Random r = new Random();
										int x = r.nextInt(highPlaying
												.getBlockX()
												- lowPlaying.getBlockX())
												+ lowPlaying.getBlockX();
										int z = r.nextInt(highPlaying
												.getBlockZ()
												- lowPlaying.getBlockZ())
												+ lowPlaying.getBlockZ();
										Location l = new Location(jp
												.getServer().getWorld(
														"minigames"), x, 63, z);
										p.teleport(l);
									}
								}
								Bukkit.getServer().getScheduler()
										.cancelTask(taskid);
								taskid = -1;
							}
							counter--;
						}

					}, 0L, 20L);

			return true;
		}
	}

	public static int taskid = -1;

	@Override
	public void executeCommand(Player sender, String[] args) {
		if (args.length > 0) {
			String arg = args[0];
			System.out.println(arg);
			if (arg.equalsIgnoreCase("join"))
				join(sender);
			else if (arg.equalsIgnoreCase("leave"))
				leave(sender);
			else if (arg.equalsIgnoreCase("start"))
				start(sender);
			else if (arg.equalsIgnoreCase("run"))
				run(sender);
			else if (arg.equalsIgnoreCase("stop"))
				stop(sender, false);
			else if (arg.equalsIgnoreCase("list"))
				listPlayers(sender);
			else if (arg.equalsIgnoreCase("end"))
				stop(sender, false);

		} else {
			warn(sender, "Usage: /spleef <start | run | end | leave | list>");
		}
	}

	@Override
	public void listPlayers(Player sender) {
		if (players == null) {
			warn(sender, "No game has been started!");
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("There are currently " + players.size()
				+ " people playing. \n");
		for (String s : players.toArray(new String[players.size()])) {
			sb.append(s + ", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		msg(sender, sb.toString());

	}

	@Override
	public boolean stop(Player sender, boolean force) {
		if (players == null) {
			warn(sender, "There is no game in progress");
			return false;
		} else if (!sender.hasPermission("HcRaid.mod") && !force) {
			warn(sender, "You do not have permission.");
			return false;
		}
		for (String s : players.toArray(new String[players.size()])) {
			Player p = jp.getServer().getPlayer(s);
			if (p == null)
				continue;
			msg(p, "Game is being ended.");
			p.teleport(new Location(
					jp.getServer().getWorld(Config.Spawn.world),
					Config.Spawn.x, Config.Spawn.y, Config.Spawn.z));
		}
		players = null;
		started = false;
		for (Location loc : diamondList
				.toArray(new Location[diamondList.size()]))
			loc.getBlock().setType(Material.DIAMOND_BLOCK);
		diamondList = null;
		for (Location loc : glowList.toArray(new Location[glowList.size()]))
			loc.getBlock().setType(Material.GLOWSTONE);
		glowList = null;
		return true;
	}
}
