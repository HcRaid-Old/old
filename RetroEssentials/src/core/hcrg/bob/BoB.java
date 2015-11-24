package core.hcrg.bob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import core.essentials.objects.Config;
import core.hcrg.Minigame;

public class BoB implements Minigame, Listener {
	private JavaPlugin jp;
	public static World world = null;
	private HashSet<String> players = null;
	private boolean started = false;
	private String pluginName = "[BoB] ";
	private BobTeam red = null;
	private BobTeam blue = null;
	private Scoreboard scoreboard = null;
	@SuppressWarnings("serial")
	private HashMap<String, Location> spawnLocs = new HashMap<String, Location>() {
		{
			this.put("blue", new Location(Bukkit.getWorld("minigames"), 101,
					85, 205));
			this.put("red", new Location(Bukkit.getWorld("minigames"), 181, 85,
					205));

		}
	};

	public BoB(JavaPlugin jp) {
		this.jp = jp;
		for (World w : jp.getServer().getWorlds())
			if (w.getName().equalsIgnoreCase(
					jp.getConfig().getString("minigames.bob.world"))) {
				world = w;
				break;
			}
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		scoreboard.registerNewObjective("Lives", "dummy");
		scoreboard.registerNewTeam("Red").setPrefix(ChatColor.RED + "");
		scoreboard.registerNewTeam("Blue").setPrefix(ChatColor.BLUE + "");
	}

	public void updateScoreboard() {
		Objective sidebar = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		for (String str : red.getPlayersByScore()) {
			sidebar.getScore(Bukkit.getOfflinePlayer(str)).setScore(
					red.getPlayerByName(str).getLivesRemaining());
		}
		for (String str : blue.getPlayersByScore()) {
			sidebar.getScore(Bukkit.getOfflinePlayer(str)).setScore(
					blue.getPlayerByName(str).getLivesRemaining());
		}
	}

	@Override
	public String getName() {
		return "Bridge Out Battle";
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
		} else {
			players.add(p.getName());
			msg(p, "You have joined the BoB game.");
			Random r = new Random();
			int i = r.nextInt(3);
			// p.teleport(spleefWaiting[i]);
			// TODO Make waiting area.
			return true;
		}
	}

	@Override
	public boolean leave(Player p) {
		if (players == null || !players.contains(p.getName())) {
			warn(p, "You are not in a BoB game");
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
			// TODO tp to spawn
			return true;
		}
	}

	private List<BlockChanges> origBlockChanges = null;

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
			origBlockChanges = new ArrayList<BlockChanges>();
			jp.getServer().broadcastMessage(
					ChatColor.GREEN + pluginName + ChatColor.WHITE
							+ "A game has been started! Join with /bob join!");
			return true;
		}
	}

	private static int taskid = -1;

	@Override
	public boolean run(Player p) {
		if (BoB.taskid >= 0) {
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
								System.out.println("Stage 1");
								started = true;
								randomiseTeams();
								System.out.println("Stage 2");
								setupChests();
								System.out.println("Stage 3");
								updateScoreboard();
								for (String s : red.getPlayersByScore()) {
									System.out.println("Name: " + s);
									Player p = jp.getServer().getPlayer(s);
									System.out.println("Got player instance: "
											+ s);
									if (p != null) {
										System.out.println("Not null: " + s);
										msg(jp.getServer().getPlayer(s),
												"Game has started!");
										System.out.println("Teleporting: " + s);
										p.teleport(spawnLocs.get("red"));
									}
								}
								System.out.println("Stage 4");
								for (String s : blue.getPlayersByScore()) {
									Player p = jp.getServer().getPlayer(s);
									if (p != null) {
										msg(jp.getServer().getPlayer(s),
												"Game has started!");
										p.teleport(spawnLocs.get("blue"));
									}
								}
								System.out.println("Stage 5");
								Bukkit.getServer().getScheduler()
										.cancelTask(BoB.taskid);
								BoB.taskid = -1;
								System.out.println("Stage 6");
							}
							counter--;
						}

					}, 0L, 20L);

			return true;
		}
	}

	private void setupChests() {

	}

	@EventHandler
	public void blockBreak(BlockBreakEvent bbe) {
		if (!bbe.getBlock().getWorld().getName().equalsIgnoreCase("minigames"))
			return;
		if (isInArena(bbe.getBlock().getLocation())) {
			origBlockChanges.add(new BlockChanges(bbe.getBlock().getLocation(),
					bbe.getBlock().getType()));
		}
	}

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent bbe) {
		if (!bbe.getBlock().getWorld().getName().equalsIgnoreCase("minigames"))
			return;
		if (isInArena(bbe.getBlock().getLocation())) {
			origBlockChanges.add(new BlockChanges(bbe.getBlock().getLocation(),
					bbe.getBlock().getType()));
		}
	}

	private Location topCorner = new Location(Bukkit.getWorld("minigames"),
			302, 150, 314);
	private Location bottomCorner = new Location(Bukkit.getWorld("minigames"),
			-33, 32, 87);
	public BlockWrapper bw = null;
	int blockScheduler = -1;

	private void setupBlockFalling() {
		
		LocationZone redZone = new LocationZone(null, null);
		LocationZone blueZone = new LocationZone(null, null);
		HashSet<Location> redLocs = new HashSet<Location>();
		for (int i = 0; i < 30; i++) {
			redLocs.add(redZone.getRandomLocation());
		}
		HashSet<Location> blueLocs = new HashSet<Location>();
		for (int i = 0; i < 30; i++) {
			blueLocs.add(blueZone.getRandomLocation());
		}
		bw = new BlockWrapper();
		int y = 100;
		Material[] mat = { Material.WOOD, Material.LEAVES,
				Material.COBBLESTONE, Material.WOOL, Material.WOOL,
				Material.GRAVEL, Material.LOG, Material.LOG, Material.STONE,
				Material.COAL_ORE, Material.WOOL };

		blockScheduler = jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

					}
				}, 30, 5);
	}

	private boolean isInArena(Location loc) {
		return loc.getBlockX() > bottomCorner.getBlockX()
				&& loc.getBlockX() < topCorner.getBlockX()
				&& loc.getBlockY() > bottomCorner.getBlockY()
				&& loc.getBlockY() < topCorner.getBlockY()
				&& loc.getBlockZ() > bottomCorner.getBlockZ()
				&& loc.getBlockZ() < topCorner.getBlockZ();
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent pme) {
		if (!pme.getTo().getWorld().getName().equalsIgnoreCase("minigames"))
			return;
		if (!isInArena(pme.getTo())) {
			// Player die
		}
	}

	public static List<Integer> entityIds = new ArrayList<Integer>();

	@EventHandler
	public void blockFormed(EntityChangeBlockEvent event) {
		if (!event.getBlock().getLocation().getWorld().getName()
				.equalsIgnoreCase("minigames"))
			return;
		if (entityIds.contains(event.getEntity().getEntityId())) {
			if (event.getBlock().getY() < 30)
				event.setCancelled(true);
			else {
				int amount = 0;
				Block b = event.getBlock();
				for (int counter = 0; counter < 14; counter++) {
					b = b.getRelative(BlockFace.DOWN);
					if (b.getType() == event.getBlock().getType())
						amount++;
				}
				if (amount >= 10)
					event.setCancelled(true);
				else
					origBlockChanges.add(new BlockChanges(event.getBlock()
							.getLocation(), event.getTo()));
			}
		}
	}

	private void randomiseTeams() {
		red = new BobTeam("red");
		blue = new BobTeam("blue");
		Team redTeam = scoreboard.getTeam("Red");
		Team blueTeam = scoreboard.getTeam("Blue");
		for (Iterator<String> it = players.iterator(); it.hasNext();) {
			String next = it.next();
			red.addPlayer(next);
			redTeam.addPlayer(Bukkit.getOfflinePlayer(next));
			if (it.hasNext()) {
				next = it.next();
				blue.addPlayer(next);
				blueTeam.addPlayer(Bukkit.getOfflinePlayer(next));
			}
		}
	}

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
	public boolean stop(Player sender, boolean force) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void listPlayers(Player sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkEnd() {
		// TODO Auto-generated method stub

	}
}
