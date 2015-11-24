package com.addongaming.minigames.hub;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.hub.inven.GameModeItem;
import com.addongaming.minigames.hub.lootbox.ELootBox;
import com.addongaming.minigames.hub.npc.NPCManager;
import com.addongaming.minigames.management.ScoreManagement;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.scheduling.HubInventory;
import com.addongaming.minigames.management.scheduling.ScheduledTeleport;
import com.addongaming.minigames.minigames.ArenaGame;

public class Hub implements Listener, CommandExecutor {
	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "Hub"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;
	private HcMinigames minigames;
	private Location spawnLocation;
	private HashMap<GameMode, Location> gamemodeLocation = new HashMap<GameMode, Location>();
	private final static HashMap<String, MinigameUser> playerCache = new HashMap<String, MinigameUser>();
	private final static ArrayList<GameModeItem> gameModeItem = new ArrayList<GameModeItem>();
	private GameModeItem gameSelectionItem;
	private Inventory gameSelectionInventory;
	private NPCManager npcManager;

	public Hub(HcMinigames minigames) {
		this.npcManager = new NPCManager(minigames);
		gameSelectionItem = new GameModeItem(Material.BOOK, ChatColor.GOLD
				+ "Minigame Selection", new ArrayList<String>(), null);
		Location loc = Bukkit.getWorld("world").getBlockAt(0, 0, 0)
				.getLocation();
		FileConfiguration fc = minigames.getConfig();
		fc.addDefault("lobbyloc.hub", Utils.locationToSaveString(loc));
		for (GameMode gm : GameMode.values()) {
			fc.addDefault("lobbyloc." + gm.name(),
					Utils.locationToSaveString(loc));
			fc.addDefault("mgmenu." + gm.name() + ".enabled", true);
			fc.addDefault("mgmenu." + gm.name() + ".material",
					Material.GRASS.name());
			fc.addDefault("mgmenu." + gm.name() + ".displayname",
					"&o" + gm.name());
			fc.addDefault("mgmenu." + gm.name() + ".lore",
					new ArrayList<String>() {
						{
							this.add("&o" + "Line 1");
							this.add("&o" + "Line 2");
						}
					});
		}
		fc.options().copyDefaults(true);
		minigames.saveConfig();
		fc = minigames.getConfig();
		spawnLocation = Utils.loadLoc(fc.getString("lobbyloc.hub"));
		for (GameMode gm : GameMode.values()) {
			gamemodeLocation.put(gm,
					Utils.loadLoc(fc.getString("lobbyloc." + gm.name())));
			if (fc.getBoolean("mgmenu." + gm.name() + ".enabled"))
				gameModeItem.add(new GameModeItem(Material.getMaterial(fc
						.getString("mgmenu." + gm.name() + ".material")), fc
						.getString("mgmenu." + gm.name() + ".displayname"), fc
						.getStringList("mgmenu." + gm.name() + ".lore"), gm));
		}
		setupInventory();
		this.minigames = minigames;
		minigames.getCommand("hubman").setExecutor(this);
		minigames.getServer().getPluginManager()
				.registerEvents(this, minigames);
		if (spawnLocation != null)
			for (Player player : Bukkit.getOnlinePlayers()) {
				playerCache.put(player.getName(), new MinigameUser(player));
				if (!player.isOp()) {
					player.teleport(spawnLocation);
					clearInven(player);
					minigames
							.getServer()
							.getScheduler()
							.scheduleSyncDelayedTask(
									minigames,
									new HubInventory(getMinigameUser(player
											.getName()), this), 2l);
				}
			}
	}

	private void onDisable() {
		for (MinigameUser mu : playerCache.values())
			if (mu.getPet() != null)
				mu.destroyPet();
		npcManager.stop();
	}

	private void setupInventory() {
		double size = gameModeItem.size() * 100;
		int rows = (int) ((Math.round(size) / 9) / 100);
		if (gameModeItem.size() % 9 > 0)
			rows++;
		if (rows <= 0)
			return;
		gameSelectionInventory = Bukkit.createInventory(null, rows * 9,
				ChatColor.GOLD + "Game Selection");
		for (GameModeItem gmi : gameModeItem)
			gameSelectionInventory.addItem(gmi.getItemStack());
	}

	public boolean hasItem(ItemStack is) {
		for (GameModeItem gmi : gameModeItem)
			if (gmi.equals(is))
				return true;
		return false;
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		if (!event.hasItem()
				|| minigames.getManagement().getQueueManagement()
						.getLobby(getMinigameUser(event.getPlayer().getName())) != null
				|| event.hasBlock())
			return;
		if (event.getItem().getType() == Material.GOLD_INGOT) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "Placed " + event.getItem().getAmount()
							+ " gold into your bank.");
			event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
			event.getPlayer().updateInventory();
			return;
		} else if (event.getItem().getType() == Material.CHEST) {
			ItemStack lootBox = event.getItem();
			ItemMeta lootBoxMeta = lootBox.getItemMeta();
			if (lootBoxMeta.getDisplayName().equalsIgnoreCase(
					ChatColor.stripColor(lootBoxMeta.getDisplayName())))
				return;
			MinigameUser mu = playerCache.get(event.getPlayer().getName());
			if (mu == null) {
				event.getPlayer()
						.sendMessage(
								ChatColor.RED
										+ "Sorry your user wasn't located in the cache. Please try again or contact an admin.");
				return;
			}
			event.setCancelled(true);
			ELootBox eLootBox = null;
			String name = ChatColor.stripColor(lootBoxMeta.getDisplayName())
					.split("( )")[0];
			for (ELootBox boxes : ELootBox.values())
				if (boxes.name().equalsIgnoreCase(name))
					eLootBox = boxes;
			if (eLootBox == null) {
				event.getPlayer().sendMessage(
						ChatColor.RED
								+ "Somethings gone wrong, contact an admin.");
				return;
			}
			ItemStack is = new ItemStack(Material.CHEST, 1);
			ItemMeta im = is.getItemMeta();
			StringBuilder str = new StringBuilder();
			str.append(eLootBox.name());
			str.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			im.setDisplayName(ChatColor.GREEN + str.toString() + " Lootbox");
			im.setLore(new ArrayList<String>() {
				{
					this.add(ChatColor.AQUA + "Looted from your previous");
					this.add(ChatColor.AQUA + "foes corpses is a lootbox");
					this.add(ChatColor.AQUA + "often used to secure gold");
					this.add(ChatColor.AQUA + "");
					this.add(ChatColor.AQUA + "Right click to reap your reward");
				}
			});
			is.setItemMeta(im);
			Utils.removeFromInventory(event.getPlayer(), is);
			int score = eLootBox.getRandomScore();
			ItemStack gold = new ItemStack(Material.GOLD_INGOT,
					eLootBox.getBankIngots(score));
			ItemMeta goldMeta = gold.getItemMeta();
			goldMeta.setDisplayName(ChatColor.GOLD + "Gold Ingot");
			goldMeta.setLore(new ArrayList<String>() {
				{
					add(ChatColor.AQUA + "This is the main currency");
					add(ChatColor.AQUA + "    on HcMinigames");
					add("");
					add(ChatColor.AQUA + "   Right click to bank");
					add(ChatColor.AQUA + "Or spend it straight away");
				}
			});
			gold.setItemMeta(goldMeta);
			event.getPlayer().getInventory().addItem(gold);
			event.getPlayer().updateInventory();
			ScoreManagement scoreManagement = minigames.getManagement()
					.getScoreManagement();
			mu.setBankPoints(mu.getBankPoints() + gold.getAmount());
			mu.setOverallScore(mu.getScoreLeft() + score);
			scoreManagement.incrementOverallPointsEarnt(mu.getName(), score);
			scoreManagement.incrementPlayerBankCurrency(mu.getName(),
					gold.getAmount());
			switch (eLootBox) {
			case large:
				scoreManagement.decrementPlayerLargeLoot(mu.getName());
				mu.setLargeLoot(mu.getLargeLoot() - 1);
				break;
			case medium:
				scoreManagement.decrementPlayerMedLoot(mu.getName());
				mu.setMedLoot(mu.getMedLoot() - 1);
				break;
			case small:
				scoreManagement.decrementPlayerSmallLoot(mu.getName());
				mu.setSmallLoot(mu.getSmallLoot() - 1);
				break;
			default:
				break;

			}
			event.getPlayer().sendMessage(
					ChatColor.GOLD + "You found "
							+ eLootBox.getBankIngots(score)
							+ " gold in a lootbox and earned an extra " + score
							+ " points due to your efforts in the game.");

		}
	}

	public void itemClicked(ItemStack is, Player player) {
		if (is == null)
			return;
		if (!hasItem(is))
			return;
		for (GameModeItem gmi : gameModeItem)
			if (gmi.equals(is)) {
				GameMode gm = gmi.getGameMode();
				if (minigames.getManagement().getArenaManagement()
						.getGame(player.getName()) == null) {
					MinigameUser mg = getMinigameUser(player.getName());
					if (minigames.getManagement().getQueueManagement()
							.hasPlayer(mg.getName()))
						return;
					minigames
							.getServer()
							.getScheduler()
							.scheduleSyncDelayedTask(
									minigames,
									new ScheduledTeleport(mg, gamemodeLocation
											.get(gm)), 2L);
					minigames.getManagement().getQueueManagement()
							.queuePlayer(mg, gm);
					mg.getBase().getInventory().clear();
				}
			}
	}

	public void queuePlayer(Player player, GameMode gm) {
		MinigameUser mg = getMinigameUser(player.getName());
		if (minigames.getManagement().getQueueManagement()
				.hasPlayer(mg.getName()))
			return;
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(minigames,
						new ScheduledTeleport(mg, gamemodeLocation.get(gm)), 2L);
		minigames.getManagement().getQueueManagement().queuePlayer(mg, gm);
		mg.getBase().getInventory().clear();
	}

	public boolean isGameSelection(ItemStack is) {
		return gameSelectionItem.equals(is);
	}

	public void openInventory(Player player) {
		player.openInventory(gameSelectionInventory);
	}

	@EventHandler
	public void playerLogin(final PlayerLoginEvent event) {
		minigames.getManagement().getScoreManagement()
				.registerPlayer(event.getPlayer());
		MinigameUser mg;
		if (playerCache.containsKey(event.getPlayer().getName())) {
			mg = playerCache.get(event.getPlayer().getName());
			mg.setBase(event.getPlayer());
			mg.clean();
			mg.update();
		} else
			mg = new MinigameUser(event.getPlayer());
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(mg);
		playerCache.put(mg.getName(), mg);
		if (ag == null) {
			// TODO Add Minigame hub items, atm just clear inventory
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(minigames,
							new ScheduledTeleport(mg, spawnLocation, true), 2l);
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(minigames,
							new HubInventory(mg, this),
							!event.getPlayer().hasPlayedBefore() ? 20l : 5l);
			clearInven(event.getPlayer());
		} else {
			ag.getLobby().reLogin(mg);
		}
	}

	private void clearInven(Player player) {
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}

	@EventHandler
	public void asyncPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().startsWith("/joinminigame")) {
			String msg = event.getMessage().substring(13,
					event.getMessage().length());
			event.setCancelled(true);
			GameMode gm = GameMode.getByName(msg);
			if (gm == null)
				return;
			if (minigames.getManagement().getArenaManagement()
					.getGame(event.getPlayer().getName()) == null) {
				MinigameUser mg = getMinigameUser(event.getPlayer().getName());
				if (minigames.getManagement().getQueueManagement()
						.hasPlayer(mg.getName()))
					return;
				minigames
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								minigames,
								new ScheduledTeleport(mg, gamemodeLocation
										.get(gm)), 2L);
				minigames.getManagement().getQueueManagement()
						.queuePlayer(mg, gm);
			}
		}
	}

	private void setLocation(String node, Location loc) {
		minigames.getConfig().set(node, Utils.locationToSaveString(loc));
		minigames.saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(prefix + "This can only be executed in-game.");
			return true;
		} else if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED + "This is only available for ops.");
			return true;
		}
		if (arg3.length == 0) {
			sendAdminCommands(arg0);
			return true;
		}
		Player player = (Player) arg0;
		switch (arg3[0].toLowerCase()) {
		case "gamemodes":
			arg0.sendMessage(prefix + "Available gamemodes: "
					+ GameMode.asString());
			return true;
		case "setgm":
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "Usage: /hubman setgm <gamemode> - Sets the gamemode lobbies location.");
				arg0.sendMessage(ChatColor.AQUA
						+ "/hubman gamemodes - For all available gamemodes");
				return true;
			}
			GameMode gm = GameMode.getByName(arg3[1]);
			if (gm == null) {
				arg0.sendMessage(prefix + "GameMode " + arg3[1]
						+ " not found. Available gamemodes: "
						+ GameMode.asString());
				return true;
			}
			gamemodeLocation.put(gm, player.getLocation());
			setLocation("lobbyloc." + gm.name(), player.getLocation());
			arg0.sendMessage(prefix + "Set " + gm.name() + "'s lobby location!");
			return true;
		case "setspawn":
			spawnLocation = player.getLocation();
			setLocation("lobbyloc.hub", player.getLocation());
			arg0.sendMessage(prefix + "Set hub location.");
			return true;
		default:
			sendAdminCommands(arg0);
			return true;
		}
	}

	private void sendAdminCommands(CommandSender arg0) {
		arg0.sendMessage(prefix + " All Hub Commands");
		arg0.sendMessage(ChatColor.AQUA
				+ " /hubman gamemodes - gets all gamemodes");
		arg0.sendMessage(ChatColor.AQUA
				+ " /hubman setgm <gamemode> - Sets a gamemode lobby location");
		arg0.sendMessage(ChatColor.AQUA
				+ " /hubman setspawn - Sets hubs spawn location");
		arg0.sendMessage(prefix);
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void playerChat(Player player, String message) {
		String prefix;
		// TODO Ranks
		if (player.hasPermission("hcraid.premium.chat")) {
			prefix = ChatColor.BLUE + "Patron " + ChatColor.AQUA
					+ player.getName() + " " + ChatColor.WHITE;
		} else {
			prefix = ChatColor.GRAY + player.getName() + " ";
		}
		for (Player play : Bukkit.getOnlinePlayers()) {
			ArenaGame ar = minigames.getManagement().getArenaManagement()
					.getGame(play.getName());
			if (ar != null)
				continue;
			play.sendMessage(prefix + message);
		}
	}

	public MinigameUser getMinigameUser(String name) {
		return playerCache.get(name);
	}

	@SuppressWarnings("deprecation")
	public void populateInve(MinigameUser mg) {
		if (minigames.getManagement().getQueueManagement().getLobby(mg) != null)
			return;
		clearInven(mg.getBase());
		mg.getBase().getInventory().addItem(gameSelectionItem.getItemStack());
		boolean loot = false;
		if (mg.getSmallLoot() > 0) {
			ItemStack is = new ItemStack(Material.CHEST,
					mg.getSmallLoot() > 10 ? 10 : mg.getSmallLoot());
			ItemMeta im = is.getItemMeta();
			StringBuilder name = new StringBuilder();
			name.append(ELootBox.small.name());
			name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			im.setDisplayName(ChatColor.GREEN + name.toString() + " Lootbox");
			im.setLore(new ArrayList<String>() {
				{
					this.add(ChatColor.AQUA + "Looted from your previous");
					this.add(ChatColor.AQUA + "foes corpses is a lootbox");
					this.add(ChatColor.AQUA + "often used to secure gold");
					this.add(ChatColor.AQUA + "");
					this.add(ChatColor.AQUA + "Right click to reap your reward");
				}
			});
			is.setItemMeta(im);
			mg.getBase().getInventory().addItem(is);
			loot = true;
		}
		if (mg.getMedLoot() > 0) {
			ItemStack is = new ItemStack(Material.CHEST,
					mg.getMedLoot() > 10 ? 10 : mg.getMedLoot());
			ItemMeta im = is.getItemMeta();
			StringBuilder name = new StringBuilder();
			name.append(ELootBox.medium.name());
			name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			im.setDisplayName(ChatColor.GREEN + name.toString() + " Lootbox");
			im.setLore(new ArrayList<String>() {
				{
					this.add(ChatColor.AQUA + "Looted from your previous");
					this.add(ChatColor.AQUA + "foes corpses is a lootbox");
					this.add(ChatColor.AQUA + "often used to secure gold");
					this.add(ChatColor.AQUA + "");
					this.add(ChatColor.AQUA + "Right click to reap your reward");
				}
			});
			is.setItemMeta(im);
			mg.getBase().getInventory().addItem(is);
			loot = true;
		}
		if (mg.getLargeLoot() > 0) {
			ItemStack is = new ItemStack(Material.CHEST,
					mg.getLargeLoot() > 10 ? 10 : mg.getLargeLoot());
			ItemMeta im = is.getItemMeta();
			StringBuilder name = new StringBuilder();
			name.append(ELootBox.large.name());
			name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			im.setDisplayName(ChatColor.GREEN + name.toString() + " Lootbox");
			im.setLore(new ArrayList<String>() {
				{
					this.add(ChatColor.AQUA + "Looted from your previous");
					this.add(ChatColor.AQUA + "foes corpses is a lootbox");
					this.add(ChatColor.AQUA + "often used to secure gold");
					this.add(ChatColor.AQUA + "");
					this.add(ChatColor.AQUA + "Right click to reap your reward");
				}
			});
			is.setItemMeta(im);
			mg.getBase().getInventory().addItem(is);
			loot = true;
		}

		if (loot) {
			mg.getBase()
					.sendMessage(
							ChatColor.GOLD
									+ "Right clicking with lootboxes in your hand will open them.");
		}
		mg.getBase().updateInventory();
	}

	public String getInventoryName() {
		return gameSelectionInventory.getTitle();
	}

	public void stop() {
		npcManager.stop();
	}
}
