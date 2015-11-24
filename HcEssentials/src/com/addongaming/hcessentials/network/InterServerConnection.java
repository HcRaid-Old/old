package com.addongaming.hcessentials.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.combat.antilogging.CombatLogging;
import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.logging.DataLog;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class InterServerConnection implements SubPlugin, Listener {
	private JavaPlugin jp;
	private String thisServer, instantperm;
	private DataLog dl;
	private List<WhitelistData> serverWhitelists = new ArrayList<WhitelistData>();
	private DatabaseHandler dbHandler = null;
	private int guiId = -1;
	private boolean dbUpdating = true;

	@SuppressWarnings({ "serial", "deprecation" })
	public InterServerConnection(JavaPlugin jp) {
		dl = HcEssentials.getDataLogger().addLogger("ServerSwap");
		this.jp = jp;
		jp.getConfig().addDefault("network.thisserver", "hub");
		jp.getConfig().addDefault("network.instantperm", "hcraid.mod");
		jp.getConfig().addDefault("network.updateenabled", dbUpdating);
		jp.getConfig().addDefault("network.servers", new ArrayList<String>() {
			{
				this.add("play");
				this.add("overkill");
				this.add("hub");
				this.add("retro");
			}
		});
		jp.getConfig().addDefault("network.itemfordisplay.enabled", false);
		jp.getConfig().addDefault("network.itemfordisplay.item",
				Material.DIAMOND.getId());
		jp.getConfig().addDefault("network.display.itemid",
				Material.DIAMOND_CHESTPLATE.getId());
		jp.getConfig().addDefault("network.display.name", "&5Hub");
		jp.getConfig().addDefault("network.display.lore",
				"&3Hub server to|&3connect via all our servers");
		jp.getConfig().addDefault("network.display.priority", 2);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
		dl.log("Unregistering BungeeCord channel");
		jp.getServer().getMessenger()
				.unregisterOutgoingPluginChannel(jp, "BungeeCord");
		dl.log("Unregistered BungeeCord channel");

	}

	List<String> serverNames = new ArrayList<String>();
	List<String> playerTeleporting = new ArrayList<String>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerMove(PlayerMoveEvent event) {
		if (playerTeleporting.contains(event.getPlayer().getName())
				&& event.getFrom().distanceSquared(event.getTo()) > 0.02) {
			event.getPlayer().sendMessage(
					ChatColor.BLUE + "Teleportation cancelled.");
			playerTeleporting.remove(event.getPlayer().getName());
		}
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		if (guiId < 0 || !event.hasItem())
			return;
		if (event.getItem() != null && event.getItem().getTypeId() == guiId) {
			event.setCancelled(true);
			openGui(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerCommand(final PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/servers")) {
			// listServers(event.getPlayer());
			openGui(event.getPlayer());
			event.setCancelled(true);
			return;
		}
		for (final String str : serverNames) {
			if (event.getMessage().equalsIgnoreCase("/" + str)) {
				if (CombatLogging.combatMap.containsKey(event.getPlayer()
						.getName())
						&& CombatLogging.combatMap.get(
								event.getPlayer().getName()).isInCombat()) {
					event.getPlayer()
							.sendMessage(
									ChatColor.BLUE
											+ "You cannot switch server whilst in combat!");
					event.setCancelled(true);
					return;
				}

				if (playerTeleporting.contains(event.getPlayer().getName())) {
					event.getPlayer().sendMessage(
							ChatColor.BLUE + "You are currently teleporting!");
					event.setCancelled(true);
					return;
				} else {
					if (str.endsWith(thisServer)) {
						event.getPlayer().sendMessage(
								ChatColor.BLUE
										+ "You are connected to this server!");
						event.setCancelled(true);
						return;
					} else {
						event.getPlayer()
								.sendMessage(
										(event.getPlayer().hasPermission(
												instantperm) ? ChatColor.GREEN
												+ "Connecting you to " + str
												: ChatColor.GREEN
														+ "You are connecting to "
														+ str
														+ " please wait 5 seconds."));
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(
										jp,
										new Runnable() {

											@Override
											public void run() {
												if (event.getPlayer() == null
														|| !event.getPlayer()
																.isOnline()
														|| event.getPlayer()
																.isDead())
													return;
												if (playerTeleporting
														.contains(event
																.getPlayer()
																.getName())) {
													playerTeleporting
															.remove(event
																	.getPlayer()
																	.getName());
													if (CombatLogging.combatMap
															.containsKey(event
																	.getPlayer()
																	.getName())
															&& CombatLogging.combatMap
																	.get(event
																			.getPlayer()
																			.getName())
																	.isInCombat()) {
														event.getPlayer()
																.sendMessage(
																		ChatColor.BLUE
																				+ "You cannot switch server whilst in combat!");
														return;
													} else {
														try {
															event.getPlayer()
																	.teleport(
																			HcEssentials.essentials
																					.getWarps()
																					.getWarp(
																							"spawn"));
															connect(str,
																	event.getPlayer());
														} catch (
																WarpNotFoundException
																| InvalidWorldException e) {
															event.getPlayer()
																	.sendMessage(
																			"Sorry, spawn location was not found. Please create a bug ticket on the forums.");
															e.printStackTrace();
														}
													}

												}
											}
										},
										(event.getPlayer().hasPermission(
												instantperm) ? 1 : 20 * 5));
						playerTeleporting.add(event.getPlayer().getName());
						event.setCancelled(true);
					}
				}
			}

		}
	}

	private void openGui(Player player) {
		// int rows = (Math.round((serverWhitelists.size() * 100) / 9)) / 100;
		double size = serverWhitelists.size() * 100;
		int rows = (int) ((Math.round(size) / 9) / 100);
		if (serverWhitelists.size() % 9 > 0)
			rows++;
		if (rows <= 0)
			return;
		Inventory inv = Bukkit.createInventory(player, rows * 9,
				ChatColor.DARK_GRAY + "Server Hopping");
		for (WhitelistData wd : serverWhitelists) {
			if (wd.isWhitedlisted(player.getUniqueId().toString()))
				inv.addItem(wd.getIs());
		}
		player.openInventory(inv);
	}

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		if (!event.getInventory().getTitle()
				.equalsIgnoreCase(ChatColor.DARK_GRAY + "Server Hopping"))
			return;
		event.setCancelled(true);
		if (event.getCurrentItem() == null)
			return;
		ItemStack is = event.getCurrentItem();
		if (is == null || is.getItemMeta() == null)
			return;
		String name = is.getItemMeta().getDisplayName();
		for (WhitelistData wd : serverWhitelists)
			if (wd.getIs().getItemMeta().getDisplayName()
					.equalsIgnoreCase(name)) {
				connect(wd.getBungeeId(), (Player) event.getWhoClicked());
				event.getWhoClicked().closeInventory();
				return;
			}
	}

	String title = ChatColor.GRAY + "[" + ChatColor.GOLD + "HcNet"
			+ ChatColor.GRAY + "] " + ChatColor.GREEN;

	@Deprecated
	private void listServers(Player player) {
		String servers = Arrays.deepToString(serverNames
				.toArray(new String[serverNames.size()]));
		servers = servers.substring(1, servers.length() - 1);
		player.sendMessage(title + "You are currently connected to "
				+ thisServer + ".");
		player.sendMessage(title + "The servers you can connect to are "
				+ servers);
		player.sendMessage(ChatColor.GOLD
				+ "To connect to one, just run the server name as a command, i.e. /hub");

	}

	private final void connect(String connection, Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(connection);
		} catch (IOException ex) {
			dl.log("An error occured. Player " + player.getName()
					+ " Message: " + ex.getMessage());
		}
		if (b.toByteArray() == null)
			dl.log("Byte Array null");
		try {
			player.sendPluginMessage(jp, "BungeeCord", b.toByteArray());
			b.close();
			out.close();
		} catch (Exception e) {
			dl.log("error " + e.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable() {
		thisServer = jp.getConfig().getString("network.thisserver");
		instantperm = jp.getConfig().getString("network.instantperm");
		dl.log("Registered server name: " + thisServer);
		serverNames = (List<String>) jp.getConfig().getList("network.servers");
		for (String str : serverNames)
			dl.log("Registered possible server connections: " + str);
		dbUpdating = jp.getConfig().getBoolean("network.updateenabled");
		jp.getServer().getMessenger()
				.registerOutgoingPluginChannel(jp, "BungeeCord");
		/**
		 * jp.getConfig().addDefault("network.itemfordisplay.enabled", false);
		 * jp.getConfig().addDefault("network.itemfordisplay.item",
		 * Material.DIAMOND.getId());
		 */
		if (jp.getConfig().getBoolean("network.itemfordisplay.enabled")) {
			guiId = jp.getConfig().getInt("network.itemfordisplay.item");
		}
		if (DatabaseManagement.hasInstance()) {
			dbHandler = DatabaseManagement.getInstance().addDatabase(
					"globalBan");
			setupTables();
			populateWhitelists();
			loadConfig();
		}
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		final int itemId = fc.getInt("network.display.itemid"), priority = fc
				.getInt("network.display.priority");
		final String displayName = fc.getString("network.display.name"), lore = fc
				.getString("network.display.lore");
		if (!dbUpdating)
			return;
		jp.getServer().getScheduler()
				.runTaskTimerAsynchronously(jp, new Runnable() {

					@Override
					public void run() {
						try {
							final StringBuilder uuids = new StringBuilder();
							for (OfflinePlayer op : jp.getServer()
									.getWhitelistedPlayers())
								uuids.append(op.getUniqueId().toString() + ",");
							for (OfflinePlayer op : jp.getServer()
									.getOperators()) {
								if (uuids.indexOf(op.getUniqueId().toString()) == -1)
									uuids.append(op.getUniqueId().toString()
											+ ",");
							}
							if (uuids.length() > 3)
								uuids.deleteCharAt(uuids.length() - 1);
							PreparedStatement statement = getConnection()
									.prepareStatement(
											"INSERT INTO whitelist (priority,bungeeserver,whitelist,whitelisted,item,itemname,itemlore)"
													+ " VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE"
													+ " priority = ?, whitelist = ?, whitelisted = ?, item = ?, itemname = ?, itemlore = ? ;");
							statement.setInt(1, priority);
							statement.setString(2, thisServer);
							statement.setInt(3,
									jp.getServer().hasWhitelist() ? 1 : 0);
							statement.setString(4, uuids.toString());
							statement.setInt(5, itemId);
							statement.setString(6, displayName);
							statement.setString(7, lore);
							statement.setInt(8, priority);
							statement.setInt(9,
									jp.getServer().hasWhitelist() ? 1 : 0);
							statement.setString(10, uuids.toString());
							statement.setInt(11, itemId);
							statement.setString(12, displayName);
							statement.setString(13, lore);
							statement.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 20L, 20 * 60);

	}

	/**
	 * server = servername whitelist = (0=non-whitelisted, 1=whitelisted)
	 * whitelisted = all whitelisted UUID's item = Item ID to be shown on GUI
	 * itemname = Item name to be used itemlore = itemlore seperated with a |
	 */
	private void setupTables() {
		try {
			String query = "CREATE TABLE IF NOT EXISTS whitelist(bungeeserver VARCHAR(10) UNIQUE,priority INT, whitelist INT,"
					+ " whitelisted MEDIUMTEXT, item INT, itemname VARCHAR(30), itemlore MEDIUMTEXT);";
			dbHandler.getConnection().prepareStatement(query).execute();
		} catch (Exception e) {
		}
	}

	private void populateWhitelists() {
		if (!dbUpdating)
			return;
		jp.getServer().getScheduler()
				.runTaskTimerAsynchronously(jp, new Runnable() {

					@Override
					public void run() {
						try {
							String query = "SELECT * FROM whitelist WHERE bungeeserver != '"
									+ thisServer + "';";
							ResultSet rs = getConnection().prepareStatement(
									query).executeQuery();
							final List<WhitelistData> tempList = new ArrayList<WhitelistData>();
							while (rs.next()) {
								if (rs.getString("bungeeserver")
										.equalsIgnoreCase(thisServer))
									continue;
								tempList.add(new WhitelistData(rs
										.getString("bungeeserver"), rs
										.getInt("priority"), getItemStack(
										rs.getInt("item"),
										rs.getString("itemname"),
										rs.getString("itemlore")), rs
										.getInt("whitelist") == 1, rs
										.getString("whitelisted").split("(,)")));
							}
							rs.close();
							Collections.sort(tempList,
									new Comparator<WhitelistData>() {
										@Override
										public int compare(WhitelistData o1,
												WhitelistData o2) {
											return o1.getPriority()
													- o2.getPriority();
										}
									});
							jp.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(jp,
											new Runnable() {

												@Override
												public void run() {
													serverWhitelists = tempList;
												}
											});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 30L, 20 * 60);
	}

	private Connection con = null;

	private final Connection getConnection() {
		try {
			if (con == null || con.isClosed()) {
				con = dbHandler.getConnection();
				return con;
			} else
				return con;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	private final ItemStack getItemStack(int itemId, String name, String lore) {
		ItemStack is = new ItemStack(itemId, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> finalisedLore = new ArrayList<String>();
		for (String str : lore.split("[|]")) {
			finalisedLore.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		im.setLore(finalisedLore);
		is.setItemMeta(im);
		return is;
	}
}
