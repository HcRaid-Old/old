package com.addongaming.hcessentials.minigames.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.events.BountyClaimedEvent;
import com.addongaming.hcessentials.events.CombatLogStartEvent;
import com.addongaming.hcessentials.events.EXPKeepEvent;
import com.addongaming.hcessentials.events.TeamProtectEvent;
import com.addongaming.hcessentials.minigames.MiniGames;
import com.addongaming.hcessentials.minigames.Minigame;
import com.addongaming.hcessentials.minigames.games.onevone.OneVOneGame;
import com.addongaming.hcessentials.utils.Utils;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class OneVOne implements Minigame, Listener {
	List<OneVOneGame> runningGames = new ArrayList<OneVOneGame>();
	private JavaPlugin jp;
	private Location pos1 = null;
	private Location pos2 = null;
	private static OneVOne instance;
	private final String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "1v1" + ChatColor.GOLD + "] " + ChatColor.RESET;
	public final static HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();
	public final static HashMap<String, ItemStack[]> inven = new HashMap<String, ItemStack[]>();

	public static enum Tier {
		tier1, tier2, tier3, instasoup, archery, sniper, custom, random
	}

	public OneVOne(JavaPlugin jp) {
		this.jp = jp;
	}

	public static OneVOne getInstance() {
		return instance;
	}

	public void setLocation1(Location loc) {
		this.pos1 = loc;
		String toSave = loc.getWorld().getName() + "|" + loc.getX() + "|"
				+ loc.getY() + "|" + loc.getZ() + "|" + loc.getYaw() + "|"
				+ loc.getPitch();
		jp.getConfig().set("minigames.onevone.pos1", toSave);
		jp.saveConfig();
	}

	public void setLocation2(Location loc) {
		this.pos2 = loc;
		String toSave = loc.getWorld().getName() + "|" + loc.getX() + "|"
				+ loc.getY() + "|" + loc.getZ() + "|" + loc.getYaw() + "|"
				+ loc.getPitch();
		jp.getConfig().set("minigames.onevone.pos2", toSave);
		jp.saveConfig();
	}

	public Location getLocation1() {
		return pos1;
	}

	public Location getLocation2() {
		return pos2;
	}

	@Override
	public String getMinigameName() {
		return "1v1";
	}

	@Override
	public boolean isInGame(String str) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(str))
				return true;
		return false;
	}

	@Override
	public void commandIssued(CommandSender sender, String[] args) {
		if (args.length == 0) {
			listCommands(sender);
			return;
		}
		switch (args[0]) {
		case "set1":
			if (sender.isOp())
				setLocation1(((Player) (sender)).getLocation());
			return;
		case "set2":
			if (sender.isOp())
				setLocation2(((Player) (sender)).getLocation());
			return;
		case "debug":
			if (sender.isOp())
				debug(sender);
			break;
		case "accept":
			acceptGame(sender);
			return;
		case "decline":
			declineGame(sender);
			return;
		case "leave":
			break;
		default:
			String playerName = args[0];
			Player p = Bukkit.getPlayer(playerName);
			if (p == null || !p.isOnline()) {
				sender.sendMessage(prefix + playerName + " not found.");
				return;
			}
			startGame(sender, p, args);
		}

	}

	private void debug(CommandSender s) {
		s.sendMessage("Games: " + runningGames.size());
		int game = 1;
		for (OneVOneGame og : runningGames) {
			s.sendMessage("Game " + game + " " + og.getIssuer()
					+ " Has started: " + og.hasStarted() + " should remove? "
					+ og.shouldRemove());
			og.debug(s);
			game++;
		}
	}

	private void declineGame(CommandSender sender) {
		for (OneVOneGame og : runningGames) {
			if (!og.hasStarted() && og.isInGame(sender.getName())) {
				og.decline();
			}
		}
		checkGames();
	}

	private void acceptGame(CommandSender sender) {
		boolean flag = false;
		String rec = "";
		for (OneVOneGame og : runningGames)
			if (og.isInGame(sender.getName()) && og.hasStarted()) {
				return;
			} else if (og.isInGame(sender.getName()) && !flag) {
				if (og.getIssuer().equalsIgnoreCase(sender.getName())) {
					sender.sendMessage(prefix
							+ "You cannot start the game by yourself.");
					return;
				}
				rec = og.getIssuer();
				og.start();
			} else if ((og.isInGame(sender.getName()) && flag)
					|| rec.length() > 1 && og.isInGame(rec)) {
				og.markRemoval();
			}
		checkGames();
	}

	private void stopGame(String playerName) {

	}

	private void startGame(CommandSender sender, Player p, String[] args) {
		if (!(sender instanceof Player))
			return;
		Player issuer = (Player) sender;
		if (MiniGames.getMinigameInstance().isInGame(issuer.getName())) {
			issuer.sendMessage(prefix + "You are currently in a game.");
			return;
		} else if (MiniGames.getMinigameInstance().isInGame(p.getName())) {
			issuer.sendMessage(prefix + p.getName()
					+ " is currently in a game.");
			return;
		}
		if (!issuer.getWorld().getName().equalsIgnoreCase("world")) {
			issuer.sendMessage(prefix + "You need to be in spawn to play 1v1.");
			return;
		} else if (!p.getWorld().getName().equalsIgnoreCase("world")) {
			issuer.sendMessage(prefix
					+ "Your partner needs to be in spawn to play 1v1.");
			return;
		}
		if (!HcEssentials.worldGuard
				.getRegionManager(p.getWorld())
				.getRegion("innerspawn")
				.contains(p.getLocation().getBlockX(),
						p.getLocation().getBlockY(),
						p.getLocation().getBlockZ())) {
			issuer.sendMessage(prefix
					+ "Your partner needs to be in spawn to play 1v1.");
			return;
		} else if (!HcEssentials.worldGuard
				.getRegionManager(issuer.getWorld())
				.getRegion("innerspawn")
				.contains(issuer.getLocation().getBlockX(),
						issuer.getLocation().getBlockY(),
						issuer.getLocation().getBlockZ())) {
			issuer.sendMessage(prefix + "You need to be in spawn to play 1v1.");
			return;
		}
		if (sender.getName().equalsIgnoreCase(p.getName())) {
			sender.sendMessage(prefix
					+ "Hey now, I'm sure there's someone online that will play with you.");
			return;
		}
		Tier tier = null;
		if (args.length == 2) {
			boolean flag = false;
			for (Tier t : Tier.values())
				if (t.name().equalsIgnoreCase(args[1])) {
					tier = Tier.valueOf(args[1].toLowerCase());
					flag = true;
					break;
				}
			if (!flag) {
				sender.sendMessage(prefix
						+ "Please use /1v1 <playername> followed by either tier1, tier2, tier3 or custom.");
				return;
			}
		}
		OneVOneGame o;
		if (tier != null) {
			o = new OneVOneGame(issuer.getName(), p.getName(), tier);
			o.initInvite();
			runningGames.add(o);
		} else {
			o = new OneVOneGame(issuer.getName(), p.getName(), null);
			runningGames.add(o);
			openInventory(sender);
		}

	}

	private void openInventory(CommandSender sender) {
		Inventory i = Bukkit.createInventory((Player) sender, 9, "1v1 Menu");
		ItemStack tier = new ItemStack(Material.IRON_INGOT);
		tier = Utils.setLore(Utils.setName("Tier 1", tier), "This tier gives",
				"full Protection II", "diamond armour with", "Sharpness 3",
				"Fire Aspect 1");
		i.addItem(tier);
		tier = new ItemStack(Material.GOLD_INGOT);
		tier = Utils.setLore(Utils.setName("Tier 2", tier), "This tier gives",
				"full Protection 3", "diamond armour with", "Sharpness 4,",
				"Knockback 1 sword.");
		i.addItem(tier);
		tier = new ItemStack(Material.DIAMOND);
		tier = Utils.setLore(Utils.setName("Tier 3", tier), "This tier gives",
				"full Protection 4", "Thorns 1 diamond armour with",
				"Sharpness 4,", "Knockback 1", "Durability 2 sword.");
		i.addItem(tier);

		tier = new ItemStack(Material.MUSHROOM_SOUP);
		tier = Utils.setLore(Utils.setName("Insta soup", tier),
				"This tier gives", "full Protection 3", "diamond armour with",
				"Sharpness 4,", "Knockback 1 sword", "5 Mushroom stews with",
				"Insta eat enabled");
		i.addItem(tier);
		tier = new ItemStack(Material.BOW);
		tier = Utils.setLore(Utils.setName("Archery", tier), "This tier gives",
				"Full leather prot 1", "Bow with power 1",
				"Wooden sword sharp 1", "32 arrows.");
		i.addItem(tier);
		tier = new ItemStack(Material.MUSHROOM_SOUP);
		tier = new ItemStack(Material.ARROW);
		tier = Utils.setLore(Utils.setName("Sniper", tier), "This tier gives",
				"Full leather prot 1", "Bow with power 1",
				"Wooden sword sharp 1", "10 arrows.", "Instant shooting.");
		i.addItem(tier);
		tier = new ItemStack(Material.CHEST);
		tier = Utils.setLore(Utils.setName("Custom", tier),
				"Bring your own items.", "These will be returned",
				"after the fight");
		i.addItem(tier);
		tier = new ItemStack(Material.values()[new Random().nextInt(Material
				.values().length / 2) + 1]);
		tier = Utils.setLore(Utils.setName("Random", tier),
				"Can't make your mind up?", "Randomly pick Tier1/2/3/soup");
		i.addItem(tier);

		Player p = (Player) sender;
		p.openInventory(i);
	}

	private void listCommands(CommandSender sender) {
		String[] commands = { "/1v1 <playername> - Requests a 1V1 with a player" };
		for (String str : commands)
			sender.sendMessage(prefix + str);
	}

	@Override
	public String getMinigameNotation() {
		return getMinigameName();
	}

	@Override
	public boolean onEnable() {
		loadConfig();
		if (!jp.getConfig().getBoolean("minigames.onevone.enabled"))
			return false;
		if (!jp.getConfig().getString("minigames.onevone.pos1")
				.equalsIgnoreCase("null"))
			pos1 = Utils.loadLoc(jp.getConfig().getString(
					"minigames.onevone.pos1"));
		if (!jp.getConfig().getString("minigames.onevone.pos2")
				.equalsIgnoreCase("null"))
			pos2 = Utils.loadLoc(jp.getConfig().getString(
					"minigames.onevone.pos2"));
		instance = this;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						checkGames();
					}
				}, 20 * 60, 20 * 10);
		return true;
	}

	@Override
	public void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("minigames.onevone.enabled", false);
		fc.addDefault("minigames.onevone.pos1", "null");
		fc.addDefault("minigames.onevone.pos2", "null");
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@EventHandler
	public void expKeep(EXPKeepEvent ev) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(ev.getClaimer())) {
				if (game.hasStarted())
					ev.setCancelled(true);
				return;
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDied(PlayerDeathEvent event) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(event.getEntity().getName()) && game.hasStarted()) {
				game.playerDied(event.getEntity().getName());
				event.getDrops().clear();
				event.setKeepLevel(true);
				event.setDroppedExp(0);
				break;
			}
		checkGames();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().isOp())
			return;
		else if (isInGame(event.getPlayer().getName())
				&& gameHasStarted(event.getPlayer().getName())) {
			String[] allowed = { "team", "msg", "w", "t", "tell", "whisper",
					"help", "bounty", "1v1" };
			String first = "";
			if (!event.getMessage().contains(" "))
				first = event.getMessage();
			else
				first = event.getMessage().split("( )")[0];
			first = first.toLowerCase();
			for (String str : allowed)
				if (first.equalsIgnoreCase("/" + str))
					return;
			event.getPlayer().sendMessage(
					prefix + event.getPlayer().getName() + "You cannot use "
							+ first + " whilst in 1v1!");
			event.setCancelled(true);
		}
	}

	private boolean gameHasStarted(String name) {
		for (OneVOneGame og : runningGames)
			if (og.isInGame(name) && og.hasStarted())
				return true;
		return false;
	}

	@EventHandler
	public void bountyClaimed(BountyClaimedEvent event) {
		if (isInGame(event.getClaimer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void combatTagInitiated(CombatLogStartEvent event) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(event.getPlayer1())
					&& game.isInGame(event.getPlayer2()) && game.hasStarted()) {
				event.setCancelled(true);
				return;
			}
	}

	@EventHandler
	public void teamProtection(TeamProtectEvent event) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(event.getPlayer1())
					&& game.isInGame(event.getPlayer2()) && game.hasStarted()) {
				event.setCancelled(true);
				return;
			}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		for (OneVOneGame game : runningGames)
			if (game.isInGame(event.getPlayer().getName()) && game.hasStarted()) {
				game.playerDied(event.getPlayer().getName());
				break;
			} else if (game.isInGame(event.getPlayer().getName())) {
				game.markRemoval();
				break;
			}
		checkGames();
	}

	private void checkGames() {
		for (Iterator<OneVOneGame> iter = runningGames.iterator(); iter
				.hasNext();) {
			OneVOneGame og = iter.next();
			if (og.shouldRemove()) {
				if (og.isCurrentTier(null)) {
					Player p = Bukkit.getPlayer(og.getIssuer());
					if (p != null && p.isOnline()
							&& p.getOpenInventory() != null)
						p.getOpenInventory().close();
				}
				iter.remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void itemThrown(PlayerDropItemEvent event) {
		if (event.getPlayer().getOpenInventory() != null
				&& event.getPlayer().getOpenInventory().getTitle()
						.equalsIgnoreCase("1v1 menu")) {
			event.setCancelled(true);
		} else {
			for (Iterator<OneVOneGame> iter = runningGames.iterator(); iter
					.hasNext();) {
				OneVOneGame og = iter.next();
				if (og.hasStarted() && og.isInGame(event.getPlayer().getName())) {
					event.setCancelled(true);

					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryClickEvent(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("1v1 menu")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null
					&& event.getCurrentItem().getType() != Material.AIR) {
				String tier = event.getCurrentItem().getItemMeta()
						.getDisplayName();
				Tier t = Tier.valueOf(tier.toLowerCase().replaceAll("[ ]", ""));
				if (t == Tier.random) {
					int ran = new Random().nextInt(Tier.values().length - 2);
					t = Tier.values()[ran];
				}
				for (OneVOneGame ovo : runningGames) {
					if (ovo.getIssuer().equalsIgnoreCase(
							event.getWhoClicked().getName())) {
						ovo.setTier(t);
						ovo.initInvite();
						event.getWhoClicked().closeInventory();
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryCloseEvent(final InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("1v1 menu")) {
			for (Iterator<OneVOneGame> iter = runningGames.iterator(); iter
					.hasNext();) {
				OneVOneGame ovo = iter.next();
				if (ovo.getIssuer().equalsIgnoreCase(
						event.getPlayer().getName())
						&& ovo.isCurrentTier(null)) {
					ovo.markRemoval();
					jp.getServer().getScheduler()
							.scheduleSyncDelayedTask(jp, new Runnable() {

								@Override
								public void run() {
									checkGames();
								}
							});
					return;
				}
			}
		}
	}

	@EventHandler
	public void playerRespawnEvent(final PlayerRespawnEvent event) {

		/*
		 * if (inven.containsKey(event.getPlayer().getName()))
		 * jp.getServer().getScheduler() .scheduleSyncDelayedTask(jp, new
		 * Runnable() {
		 * 
		 * @Override public void run() { if (event.getPlayer().isOnline()) {
		 * event.getPlayer() .getInventory() .setContents(
		 * inven.get(event.getPlayer() .getName())); event.getPlayer()
		 * .getInventory() .setArmorContents( armour.get(event.getPlayer()
		 * .getName())); armour.remove(event.getPlayer().getName());
		 * inven.remove(event.getPlayer().getName()); } } }, 3L);
		 */

	}

	public void scheduleItemBack(final Player player, ItemStack[] contents1,
			ItemStack[] armour1) {

		inven.put(player.getName(), contents1);
		armour.put(player.getName(), armour1);
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						if (player.isOnline()) {
							player.getInventory().setContents(
									inven.get(player.getName()));
							player.getInventory().setArmorContents(
									armour.get(player.getName()));
							armour.remove(player.getName());
							inven.remove(player.getName());
							System.out.println("Given " + player.getName()
									+ "'s items back.");
						} else {
							System.out.println(player.getName()
									+ " isn't online");
						}
					}
				}, 0L);

	}

	@EventHandler
	public void instaSoup(final PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_AIR
				|| pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (pie.getItem() != null
					&& pie.getItem().getType() == Material.MUSHROOM_SOUP) {
				for (OneVOneGame game : runningGames)
					if (game.isInGame(pie.getPlayer().getName())
							&& game.hasStarted() && !game.shouldRemove()) {
						if (game.isCurrentTier(Tier.instasoup)
								&& pie.getPlayer().getHealth() < 19.0d) {
							jp.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(jp,
											new Runnable() {

												@Override
												public void run() {
													pie.getPlayer()
															.setItemInHand(null);
												}
											}, 0l);

							pie.getPlayer()
									.setHealth(
											(pie.getPlayer().getHealth() + 6.0d > 20.0d) ? 20.0d
													: pie.getPlayer()
															.getHealth() + 6.0d);
						}
					}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void arrowFire(ProjectileHitEvent event) {
		if (event.getEntityType() == EntityType.ARROW) {
			if (event.getEntity().getShooter() != null
					&& event.getEntity().getShooter() instanceof Player) {
				Player p = (Player) event.getEntity().getShooter();
				for (OneVOneGame ovo : runningGames) {
					if (ovo.hasStarted() && ovo.isInGame(p.getName())) {
						event.getEntity().remove();
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void pickupItem(PlayerPickupItemEvent event) {
		if (event.getPlayer() != null) {
			for (OneVOneGame ovo : runningGames) {
				if (ovo.hasStarted()
						&& ovo.isInGame(event.getPlayer().getName())) {
					event.getItem().remove();
					event.setCancelled(true);
					return;
				}

			}
		}
	}

	@EventHandler
	public void inveOpen(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		for (OneVOneGame ovo : runningGames) {
			if (ovo.hasStarted() && ovo.isInGame(p.getName())) {
				event.setCancelled(true);
				p.sendMessage(prefix
						+ "Sorry you cannot currently do this inside 1v1.");
				return;
			}
		}
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent pme) {
		if (pme.getPlayer().isOp() || pme.isCancelled())
			return;
		if (pme.getFrom().distance(pme.getTo()) > 0.1) {
			RegionManager s = HcEssentials.worldGuard.getGlobalRegionManager()
					.get(pme.getTo().getWorld());
			if (s.hasRegion("1v1")) {
				if (s.getRegion("1v1").contains(pme.getTo().getBlockX(),
						pme.getTo().getBlockY(), pme.getTo().getBlockZ())
						&& !isInGame(pme.getPlayer().getName())) {
					try {
						pme.getPlayer().teleport(
								HcEssentials.essentials.getWarps().getWarp(
										"spawn"));
					} catch (WarpNotFoundException e) {
						e.printStackTrace();
					} catch (InvalidWorldException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@EventHandler
	public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			Player p2 = null;
			if (event.getDamager() instanceof Player) {
				p2 = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				Projectile pro = (Projectile) event.getDamager();
				if (pro.getShooter() instanceof Player)
					p2 = (Player) pro.getShooter();
			}
			if (p2 == null)
				return;
			if (isInGame(p.getName()) || isInGame(p2.getName())) {
				for (OneVOneGame game : runningGames) {
					if (game.hasStarted() && game.isInGame(p.getName()))
						if (game.isInGame(p2.getName()))
							return;
						else {
							System.out.println("Cancelled event");
							event.setCancelled(true);
							return;
						}
				}

			}
		}
	}

	@EventHandler
	public void instaBow(final PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_AIR
				|| pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (pie.getItem() != null
					&& pie.getItem().getType() == Material.BOW) {
				for (OneVOneGame game : runningGames)
					if (game.isInGame(pie.getPlayer().getName())
							&& game.hasStarted() && !game.shouldRemove()) {
						if (game.isCurrentTier(Tier.sniper)
								&& pie.getPlayer().getInventory()
										.contains(Material.ARROW)) {
							pie.setCancelled(true);
							System.out.println("Fired arrow.");
							Projectile arrow = pie.getPlayer()
									.launchProjectile(Arrow.class);
							arrow.setShooter(pie.getPlayer());
							arrow.setVelocity(arrow.getVelocity()
									.multiply(3.0f));
							jp.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(jp,
											new Runnable() {

												@Override
												public void run() {
													int slot = pie
															.getPlayer()
															.getInventory()
															.first(Material.ARROW);
													ItemStack is = pie
															.getPlayer()
															.getInventory()
															.getItem(slot);
													if (is.getAmount() > 1)
														is.setAmount(is
																.getAmount() - 1);
													else
														is = null;
													pie.getPlayer()
															.getInventory()
															.setItem(slot, is);
												}
											}, 0l);
						}
					}
			}
		}
	}
}
