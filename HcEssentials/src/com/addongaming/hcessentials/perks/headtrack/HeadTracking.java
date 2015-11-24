package com.addongaming.hcessentials.perks.headtrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ess3.api.InvalidWorldException;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.utils.Utils;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class HeadTracking implements SubPlugin, Listener, CommandExecutor {
	private JavaPlugin jp;
	private int perheadRadius, dmgPerUse, maxDura;
	private HashMap<String, String> trackingChestHead = new HashMap<String, String>();
	private DataLog dl;

	public HeadTracking(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("tracking.head.enabled", false);
		fc.addDefault("tracking.head.perheadradius", 250);
		fc.addDefault("tracking.head.randomradius", 50);
		fc.addDefault("tracking.head.dmgPerUse", 50);
		fc.addDefault("tracking.head.maxDura", 1000);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		if (!fc.getBoolean("tracking.head.enabled"))
			return false;
		loadConfig();
		dl = HcEssentials.getDataLogger().addLogger("HeadTracking");
		jp.getCommand("headtrack").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		perheadRadius = fc.getInt("tracking.head.perheadradius");
		TrackInstance.randomRadius = fc.getInt("tracking.head.randomradius");
		dmgPerUse = fc.getInt("tracking.head.dmgPerUse");
		maxDura = fc.getInt("tracking.head.maxDura");
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player))
			return true;
		Player p = (Player) arg0;
		if (!p.isOp())
			return true;
		setTrackingChest(p);
		return true;
	}

	@EventHandler
	public void trackPlayer(PlayerInteractEvent event) {
		if (event.getPlayer().isSneaking())
			return;
		if (event.getPlayer().getItemInHand() == null
				|| event.getPlayer().getItemInHand().getType() != Material.COMPASS)
			return;
		if (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack is = event.getPlayer().getItemInHand();
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
				String name = is.getItemMeta().getDisplayName();
				if (name.startsWith(ChatColor.DARK_GREEN + "")
						&& name.endsWith(")")) {
					String playerName = ChatColor.stripColor(name.substring(0,
							name.indexOf('\'')));
					Player player = Bukkit.getPlayerExact(playerName);
					if (player == null
							|| !player.isOnline()
							|| HcEssentials.essentials.getUser(player)
									.isHidden()) {
						msg(event.getPlayer(), playerName
								+ " is not currently online.", false);
						return;
					}
					Pattern pattern = Pattern.compile("\\d{1,}");
					Matcher match = pattern.matcher(ChatColor.stripColor(is
							.getItemMeta().getLore().get(0)));
					match.find();
					int radius = Integer.parseInt(match.group(0));
					if (player.getWorld() != event.getPlayer().getWorld()) {
						msg(event.getPlayer(),
								"You cannot track a player who is in another world.",
								false);
						return;
					}
					match = pattern.matcher(ChatColor.stripColor(name));
					match.find();
					int maxDistance = perheadRadius
							* Integer.parseInt(match.group(0));
					if (event.getPlayer().getLocation()
							.distance(player.getLocation()) > maxDistance) {
						msg(event.getPlayer(),
								"The compass cannot track at your given distance",
								false);
						return;
					}
					TrackInstance ti = new TrackInstance(player,
							event.getPlayer());
					if (ti.isInRectangle()) {
						msg(event.getPlayer(),
								"The compass is detecting the players location around here... It cannot pin-point where though.",
								false);
						return;
					}
					Location nextLoc = ti.getNextTrackingLocation(radius);
					dl.logPlayer(player,
							"Tracked by " + event.getPlayer().getName()
									+ " to " + Utils.locationToString(nextLoc));
					dl.logPlayer(
							event.getPlayer(),
							"Tracking " + player.getName() + " to "
									+ Utils.locationToString(nextLoc));
					event.getPlayer().setCompassTarget(nextLoc);
					msg(event.getPlayer(), "Tracking " + player.getName(), true);
					if (getDura(event.getPlayer().getItemInHand()) <= dmgPerUse) {
						event.getPlayer().setItemInHand(
								new ItemStack(Material.COMPASS, 1));
					} else {
						ItemStack hand = event.getPlayer().getItemInHand();
						hand = Utils.setName(
								ChatColor.RED
										+ ChatColor
												.stripColor(hand.getItemMeta()
														.getDisplayName()),
								setDura(hand, getDura(hand) - dmgPerUse));
						event.getPlayer().setItemInHand(hand);
					}
				}
			}
		}
	}

	private int getDura(ItemStack compass) {
		ItemMeta im = compass.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>(im.getLore());
		String dura = lore.get(lore.size() - 1);
		Pattern pattern = Pattern.compile("\\d{1,}");
		Matcher match = pattern.matcher(ChatColor.stripColor(dura));
		match.find();
		return Integer.parseInt(match.group(0));
	}

	private ItemStack setDura(ItemStack compass, int newDura) {
		ItemMeta im = compass.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>(im.getLore());
		lore.remove(0);
		String dura = lore.get(lore.size() - 1);
		Pattern pattern = Pattern.compile("\\d{1,}");
		Matcher match = pattern.matcher(ChatColor.stripColor(dura));
		dura = match.replaceFirst(newDura + "");
		lore.set(lore.size() - 1, dura);
		im.setLore(lore);
		compass.setItemMeta(im);
		return compass;
	}

	@EventHandler
	public void initiateTracking(PlayerInteractEvent event) {
		if (!event.getPlayer().isSneaking())
			return;
		if (event.getPlayer().getItemInHand() == null
				|| event.getPlayer().getItemInHand().getType() != Material.COMPASS)
			return;
		if (event.hasBlock())
			return;
		ItemStack is = event.getPlayer().getItemInHand();
		if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
			String name = is.getItemMeta().getDisplayName();
			if (name.startsWith(ChatColor.DARK_RED + "") && name.endsWith(")")) {
				// Un-activated tracking compass
				if (name.endsWith("(0)")) {
					msg(event.getPlayer(),
							"You need to have entered some heads into this compass!",
							false);
					return;
				}
				is = Utils.setName(ChatColor.RED + ChatColor.stripColor(name),
						Utils.setLore(is, maxDura + " / " + maxDura));
				event.getPlayer().setItemInHand(is);
				String last = name.substring(name.lastIndexOf('(') + 1);
				last = last.substring(0, last.length() - 1);
				int headCount = Integer.parseInt(last);
				String playerName = ChatColor.stripColor(name.substring(0,
						name.indexOf('\'')));
				msg(event.getPlayer(), "You can now track " + playerName
						+ " up to " + (headCount * perheadRadius)
						+ ". Shift right click to fuel the compass.", true);
				return;
			} else if (name.startsWith(ChatColor.RED + "")
					&& name.endsWith(")")) {
				fuelItem(event.getPlayer());
			} else if (name.startsWith(ChatColor.DARK_GREEN + "")
					&& name.endsWith(")")) {
				msg(event.getPlayer(),
						"This item is already fueled. Right click to initiate tracking.",
						false);
			}
		}
	}

	private void msg(Player p, String msg, boolean good) {
		p.sendMessage(good ? ChatColor.GOLD + "[" + ChatColor.DARK_GREEN
				+ "HcTrack" + ChatColor.GOLD + "] " + ChatColor.GREEN + msg
				: ChatColor.DARK_RED + "[" + ChatColor.RED + "HcTrack"
						+ ChatColor.DARK_RED + "] " + ChatColor.RED + msg);
	}

	private void fuelItem(Player player) {
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_GREEN
				+ "Tracking Re-fueler");
		for (int i = 0; i < ItemFuel.values().length; i++) {
			ItemFuel itemFuel = ItemFuel.values()[i];
			ItemStack is = new ItemStack(itemFuel.getMaterial());
			is = Utils.setLore(is, "Lets you track another", itemFuel.getFuel()
					+ " blocks.");
			inv.setItem(i, is);
		}
		player.openInventory(inv);
	}

	@EventHandler
	public void playerLogin(PlayerJoinEvent event) {
		try {
			event.getPlayer().setCompassTarget(
					HcEssentials.essentials.getWarps().getWarp("spawn"));
		} catch (WarpNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidWorldException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void playerLogin(
			org.bukkit.event.player.PlayerChangedWorldEvent event) {
		try {
			event.getPlayer().setCompassTarget(
					HcEssentials.essentials.getWarps().getWarp("spawn"));
		} catch (WarpNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidWorldException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void playerClickFuelingStation(InventoryClickEvent event) {
		if (!event.getInventory().getTitle()
				.equalsIgnoreCase(ChatColor.DARK_GREEN + "Tracking Re-fueler"))
			return;
		event.setCancelled(true);
		if (event.getCurrentItem() == null)
			return;
		ItemFuel iff = ItemFuel.getByMaterial(event.getCurrentItem().getType());
		Player p = (Player) event.getWhoClicked();
		if (Utils
				.count(event.getWhoClicked().getInventory(), iff.getMaterial()) <= 0) {
			msg(p, "You do not have any of those.", false);
			return;
		}
		Utils.removeFromInventory(p, new ItemStack(iff.getMaterial(), 1));
		ItemStack hand = event.getWhoClicked().getItemInHand();
		hand = Utils.setName(
				ChatColor.DARK_GREEN
						+ ChatColor.stripColor(hand.getItemMeta()
								.getDisplayName()), Utils.setLore(
						hand,
						"Fuel for " + iff.getFuel() + " blocks.",
						hand.getItemMeta().getLore()
								.get(hand.getItemMeta().getLore().size() - 1)));
		msg(p, "The next track you find will be " + iff.getFuel() + " closer.",
				true);
		event.getWhoClicked().setItemInHand(hand);
		event.getWhoClicked().closeInventory();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerInteractTrackingChest(PlayerInteractEvent event) {
		if (event.hasBlock()
				&& event.getClickedBlock().getType() == Material.CHEST
				&& event.getClickedBlock().getData() == (byte) Config.Chests.TRACKING) {
			event.setCancelled(true);
			if (event.getPlayer().getItemInHand() == null
					|| event.getPlayer().getItemInHand().getType() != Material.COMPASS) {
				msg(event.getPlayer(),
						"Please click the chest with a compass in your hand",
						false);
				return;
			}
			ItemStack hand = event.getPlayer().getItemInHand();
			if (hand.getAmount() > 1) {
				msg(event.getPlayer(),
						"You may only have one compass in your hand.", false);
				return;
			}
			Inventory i = Bukkit
					.createInventory(null, 9, "Playerhead Tracking");
			if (hand.hasItemMeta()
					&& hand.getItemMeta().getDisplayName() != null) {
				if (!hand.getItemMeta().getDisplayName()
						.startsWith(ChatColor.DARK_RED + "")) {
					msg(event.getPlayer(),
							"You have already activated that compass.", false);
					return;
				}
				String name = ChatColor
						.stripColor(hand.getItemMeta().getDisplayName())
						.replaceAll("(\\[\\*\\] )", "")
						.replaceAll("(tracker)", "");
				String playername = name.substring(0, name.indexOf('\''));
				String last = name.substring(name.lastIndexOf('(') + 1);
				last = last.substring(0, last.length() - 1);
				trackingChestHead.put(event.getPlayer().getName(), playername);
				int headCount = Integer.parseInt(last);
				for (int ii = 0; ii < headCount; ii++) {
					ItemStack is = new ItemStack(Material.SKULL_ITEM, 1,
							(byte) 3);
					SkullMeta sm = (SkullMeta) is.getItemMeta();
					sm.setOwner(playername);
					is.setItemMeta(sm);
					i.setItem(ii, is);
				}
			}
			event.getPlayer().openInventory(i);
		}
	}

	@EventHandler
	public void inventoryClickTrackingChest(InventoryClickEvent event) {
		if (!event.getInventory().getTitle()
				.equalsIgnoreCase("Playerhead Tracking"))
			return;
		event.setCancelled(true);
		if (event.getCurrentItem() != null
				&& event.getCurrentItem().getType() == Material.SKULL_ITEM) {
			if (event.getRawSlot() <= 8) {
				if (event.getView().getBottomInventory().firstEmpty() > -1) {
					event.getView()
							.getBottomInventory()
							.setItem(
									event.getView().getBottomInventory()
											.firstEmpty(),
									event.getCurrentItem());
					event.getView().setItem(event.getRawSlot(), null);
				}
			} else {
				Player play = (Player) event.getWhoClicked();
				SkullMeta skull = (SkullMeta) event.getCurrentItem()
						.getItemMeta();
				if (!trackingChestHead.containsKey(event.getWhoClicked()
						.getName())) {
					trackingChestHead.put(event.getWhoClicked().getName(),
							skull.getOwner());
				} else if (trackingChestHead.containsKey(event.getWhoClicked()
						.getName())
						&& !skull.getOwner().equalsIgnoreCase(
								trackingChestHead.get(event.getWhoClicked()
										.getName()))) {
					msg(play,
							"The head you are placing in needs to be the same as the others.",
							false);
					return;
				}
				if (event.getView().getItem(event.getRawSlot()).getAmount() > 1) {
					msg(play, "Please unstack your heads first.", false);
					return;
				}
				if (event.getView().getTopInventory().firstEmpty() >= 0) {
					event.getView()
							.getTopInventory()
							.setItem(
									event.getView().getTopInventory()
											.firstEmpty(),
									event.getView().getItem(event.getRawSlot()));
					event.getView().setItem(event.getRawSlot(), null);
				} else {
					msg(play, "The chest is full.", false);
					return;
				}
			}
		}
	}

	@EventHandler
	public void inventoryCloseTrackingCheck(InventoryCloseEvent event) {
		if (!event.getInventory().getTitle()
				.equalsIgnoreCase("Playerhead Tracking"))
			return;
		Player p = (Player) event.getPlayer();
		if (!trackingChestHead.containsKey(p.getName())) {
			return;
		}
		int skullsInInventory = Utils.count(event.getView().getTopInventory(),
				Material.SKULL_ITEM);
		ItemMeta im = p.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.DARK_RED
				+ trackingChestHead.get(p.getName()) + "'s tracker ("
				+ skullsInInventory + ")");
		p.getItemInHand().setItemMeta(im);
		trackingChestHead.remove(p.getName());
		if (skullsInInventory > 0) {
			msg(p, "Shift right-click your compass to lock in your heads.",
					true);
		}
	}

	@SuppressWarnings("deprecation")
	private static void setTrackingChest(Player p) {
		if (p.getTargetBlock(null, 20).getType().equals(Material.CHEST)) {
			p.getTargetBlock(null, 20).setData((byte) Config.Chests.TRACKING);
			p.sendMessage(ChatColor.RED + "Made a tracking chest.");
		} else {
			p.sendMessage(ChatColor.RED + "Target block is not chest.");
		}
	}
}
