package com.addongaming.hcessentials.rankup;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.utils.AtomicArrayList;
import com.addongaming.hcessentials.uuid.UUIDSystem;

public class RankUpdater extends Utils implements CommandExecutor, SubPlugin,
		Listener {
	private HashMap<String, Submission> hashyMap = new HashMap<String, Submission>();
	private String req;
	private String sub;
	private DataLog dl;
	private String server;
	private boolean hackerGroups = true;
	private JavaPlugin jp;
	private DatabaseHandler dbh = null;

	public RankUpdater(JavaPlugin jp) {
		this.jp = jp;
		dl = HcEssentials.getDataLogger().addLogger("Rank Updater");
	}

	// /rank {name,0} {rank,1} {email,2} {transaction,3} {price,4}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			Player p = Bukkit.getPlayer(args[0]);
			String rank = args[1];
			System.out.println("Rank: " + rank);
			for (String str : HcEssentials.permission.getPlayerGroups(p)) {
				if (str.equalsIgnoreCase(rank)) {
					msg(p, "You already have " + rank
							+ " if this is incorrect please try /rank [key]");
					dl.log(p.getName(), "They already have the rank " + str);
					return true;
				}
			}
			String[] subNames = { "HcRaid.MOD", "HcRaid.ADMIN" };
			for (String str : subNames) {
				if (p.hasPermission(str)) {
					HcEssentials.permission.playerAddGroup(p, rank);
					msg(p, "Enjoy your new " + rank + " rank!");
					hashyMap.remove(p.getName());
					addToBeVerified(p, rank);
					return true;
				}
			}
			if (p.hasPermission("HcRaid.hackergrunt") && hackerGroups) {

				HcEssentials.permission.playerRemoveGroup(p,
						HcEssentials.permission.getPrimaryGroup(p));
				HcEssentials.permission
						.playerAddGroup(
								p,
								"hacker"
										+ (rank.equalsIgnoreCase("enderdragon") ? "ender"
												: rank));
			} else {
				String mainGroup = HcEssentials.permission.getPrimaryGroup(p);
				for (int i = 0; i < 3
						&& HcEssentials.permission.playerInGroup(p, mainGroup); i++)
					HcEssentials.permission.playerRemoveGroup(p, mainGroup);
				HcEssentials.permission.playerAddGroup(p, rank);
			}
			msg(p, "Enjoy your new " + rank + " rank!");
			// Verification has been removed. To re-enable un-comment below
			// line(s)
			// addToBeVerified(p, rank);
			return true;
		}
		Player player = (Player) sender;
		/**
		 * if (chatLocked.contains(player.getName())) {
		 * player.sendMessage(ChatColor.AQUA +
		 * "Your chat has been toggled back on.");
		 * chatLocked.remove(player.getName()); return true; }
		 */
		if (args.length == 0) {
			if (hashyMap.containsKey(player.getName())) {
				if (hashyMap.get(player.getName()).canReset()) {
					msg(player,
							"Sorry your code has timed out. Please try again /rank [code]");
				} else if (hashyMap.get(player.getName()).isReal()) {
					msg(player,
							"Please use /rank claim to claim your rank. Or /rank [key] to use a different key.");
				} else {
					msg(player, "Please wait 5 minutes before trying again.");
				}
			} else {
				msg(player, "Please use /rank [code]");
			}
			return true;
		}
		String str = args[0];
		if (str.equalsIgnoreCase("claim")) {
			if (hashyMap.containsKey(player.getName())) {
				if (hashyMap.get(player.getName()).canReset()) {
					msg(player,
							"Sorry your code has timed out. Please input your code again /rank [code]");
					return true;
				} else {
					giveRank(player);
					return true;
				}
			}
		} else {
			if (!str.matches("[a-z A-Z 0-9 -]*")) {
				msg(player, "Please check your code and try again.");
				return true;
			} else if (hashyMap.containsKey(player.getName())
					&& !hashyMap.get(player.getName()).canReset()) {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				Date differ = new Date(
						60000 * 5 - (new Date().getTime() - hashyMap.get(
								player.getName()).getTimeUsed()));
				msg(player, "Please wait " + sdf.format(differ)
						+ " before trying another key.");
				return true;
			} else {
				checkRank(player, str);
			}
			return true;
		}
		return false;
	}

	AtomicArrayList<Submission> myList = new AtomicArrayList<Submission>();

	private void addToBeVerified(Player p, String rank) {
		myList.add(new Submission(p.getName(), rank,
				System.currentTimeMillis(), server));
	}

	private void checkRank(Player p, String str) {
		JSONObject jo = null;
		try {
			System.out.println(req + "?license=" + str);
			jo = super.getJSON(new URL(req + "?license=" + str));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (jo == null) {
			msg(p, "Sorry something went wrong. Please try again in a minute.");
			return;
		}
		if (jo.getString("action").equalsIgnoreCase("failure")) {
			switch (jo.getString("message")) {
			case "license_already_used":
				msg(p,
						"Sorry, this license code has already been used. If you have upgraded please wait five minutes and try again.");
				hashyMap.put(p.getName(), new Submission(p.getName(), str, "",
						"", false));
				return;
			case "no_rank_found":
				msg(p, "Sorry, we couldn't find your rank.");
				hashyMap.put(p.getName(), new Submission(p.getName(), str, "",
						"", false));
				return;
			case "no_license_found":
				msg(p,
						"Sorry, we couldn't find your license. Please double check your license and try again in five minutes.");
				hashyMap.put(p.getName(), new Submission(p.getName(), str, "",
						"", false));
				return;
			case "incorrect_license_syntax":
				msg(p,
						"Sorry, that license key doesn't follow the correct pattern. Please try again in five minutes.");
				hashyMap.put(p.getName(), new Submission(p.getName(), str, "",
						"", false));
				return;
			}
		}

		String group = jo.getString("rank");
		String server = jo.getString("server");
		String currentGroup = HcEssentials.permission.getPrimaryGroup(p);
		if (server.equalsIgnoreCase(this.server)
				&& group.equalsIgnoreCase(currentGroup)) {
			msg(p, "This key entitles you to a rank you already have.");
			return;
		} else if (server.equalsIgnoreCase(this.server)) {
			msg(p, "Your key entitles you to " + group
					+ " on this server. Please use " + ChatColor.BOLD
					+ "/rank claim" + ChatColor.RESET + ChatColor.GREEN
					+ " to claim your rank.");
		} else {
			msg(p, "Your key entitles you to " + group + " on " + server
					+ ". This server is " + server
					+ ". Please try the license again there.");
		}
		hashyMap.put(p.getName(), new Submission(p.getName(), str, group,
				server, true));

	}

	// a-z, 0-9 and hyphens
	private void giveRank(Player p) {
		Submission sub = hashyMap.get(p.getName());
		if (sub.canReset()) {
			msg(p,
					"Sorry, your key session has timed out. Please use /rank [key]");
			return;
		} else if (!sub.isReal()) {
			msg(p,
					"The previous key you entered is incorrect. Please try again /rank [key]");
			return;
		} else if (!sub.getServer().equalsIgnoreCase(server)) {
			msg(p, "The key entered is for " + sub.getServer()
					+ " this server is " + server);
			return;
		}
		JSONObject jo = null;
		String update = this.sub + "?license=" + sub.getKey() + "&username="
				+ p.getName() + "&rank=" + sub.getRank() + "&server="
				+ sub.getServer();
		try {
			jo = super.getJSON(new URL(update));

		} catch (MalformedURLException e) {
			jo = null;
			e.printStackTrace();
		}
		if (jo == null) {
			msg(p, "Sorry, something went wrong. Please try again in a minute.");
			return;
		}
		if (jo.getString("action").equalsIgnoreCase("failure")) {
			switch (jo.getString("message")) {
			case "failed_to_insert_license":
				msg(p,
						"Sorry, something went wrong, please try again in a minute,");
				return;
			case "license_has_already_been_used":
				msg(p,
						"This license has already been used. Please try /rank [key]  again.");
				hashyMap.remove(p.getName());
				return;
			case "no_license_found":
				msg(p,
						"Sorry, your license was not found. Please try again with  /rank [key]");
				hashyMap.remove(p.getName());
				return;
			case "incorrect_license_syntax":
				msg(p,
						"Sorry, your license contains invalid syntax. Please try again with /rank [key]");
				hashyMap.remove(p.getName());
				return;
			}
		}
		String rank = sub.getRank();
		for (String str : HcEssentials.permission.getPlayerGroups(p)) {
			if (str.equalsIgnoreCase(rank)) {
				msg(p, "You already have " + rank
						+ " if this is incorrect please try /rank [key]");
				return;
			}
		}
		String[] subNames = { "HcRaid.MOD", "HcRaid.ADMIN" };
		for (String str : subNames) {
			if (p.hasPermission(str)) {
				HcEssentials.permission.playerAddGroup(p, rank);
				msg(p, "Enjoy your new " + rank + " rank!");
				hashyMap.remove(p.getName());
				return;
			}
		}
		if (p.hasPermission("HcRaid.hackergrunt")) {
			if (hackerGroups) {
				HcEssentials.permission.playerRemoveGroup(p,
						HcEssentials.permission.getPrimaryGroup(p));
				HcEssentials.permission
						.playerAddGroup(
								p,
								"hacker"
										+ (rank.equalsIgnoreCase("enderdragon") ? "ender"
												: rank));
			} else {
				HcEssentials.permission.playerAddGroup(p, rank);
			}

			msg(p, "Enjoy your new " + rank + " rank!");
			hashyMap.remove(p.getName());
			return;
		}
		HcEssentials.permission.playerRemoveGroup(p,
				HcEssentials.permission.getPrimaryGroup(p));
		HcEssentials.permission.playerAddGroup(p, rank);
		msg(p, "Enjoy your new " + rank + " rank!");
		hashyMap.remove(p.getName());
		return;
	}

	@Override
	public void onDisable() {
		Connection con = dbh.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("INSERT INTO rankpurchases(userId,rank,server,date,verified) VALUES(?,?,?,?,?)");
			AtomicArrayList<Submission> cloned = myList.clone();
			myList.clear();
			do {
				for (Iterator<Submission> iter = cloned.iterator(); iter
						.hasNext();) {
					Submission sub = iter.next();
					try {
						int userId = UUIDSystem.getInstance().getId(
								sub.getName());
						ps.setInt(1, userId);
						ps.setString(2, sub.getRank());
						ps.setString(3, server);
						ps.setLong(4, sub.getTimeUsed());
						ps.setInt(5, 0);
						ps.executeUpdate();
						iter.remove();
					} catch (SQLException ee) {
						ee.printStackTrace();
					}
				}
			} while (!cloned.isEmpty());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("rankup.enabled", false);
		fc.addDefault("rankup.server", "raid");
		fc.addDefault("rankup.request",
				"http://addongamers.com/licence/LicenceViewer.php/");
		fc.addDefault("rankup.submit",
				"http://addongamers.com/licence/UsedLicense.php/");
		fc.options().copyDefaults(true);
		jp.saveConfig();
		server = jp.getConfig().getString("rankup.server");
		req = jp.getConfig().getString("rankup.request");
		sub = jp.getConfig().getString("rankup.submit");
		if (!fc.getBoolean("rankup.enabled"))
			return false;
		if (DatabaseManagement.hasInstance())
			dbh = DatabaseManagement.getInstance().addDatabase("globalBan");
		if (dbh == null)
			return false;
		setupDb();
		jp.getCommand("rank").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getScheduler()
				.runTaskTimerAsynchronously(jp, new Runnable() {

					@Override
					public void run() {
						Connection con = dbh.getConnection();
						try {
							PreparedStatement ps = con
									.prepareStatement("INSERT INTO rankpurchases(userId,rank,server,date,verified) VALUES(?,?,?,?,?)");
							AtomicArrayList<Submission> cloned = myList.clone();
							myList.clear();
							do {
								for (Iterator<Submission> iter = cloned
										.iterator(); iter.hasNext();) {
									Submission sub = iter.next();
									try {
										int userId = UUIDSystem.getInstance()
												.getId(sub.getName());
										ps.setInt(1, userId);
										ps.setString(2, sub.getRank());
										ps.setString(3, server);
										ps.setLong(4, sub.getTimeUsed());
										ps.setInt(5, 0);
										ps.executeUpdate();
										iter.remove();
									} catch (SQLException ee) {
										ee.printStackTrace();
									}
								}
							} while (!cloned.isEmpty());
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}, 20 * 60L, 20 * 60L);
		return true;
	}

	private void setupDb() {
		try {
			String sql = "CREATE TABLE IF NOT EXISTS rankpurchases (id int AUTO_INCREMENT PRIMARY KEY, userId INT NOT NULL, rank VARCHAR(20) NOT NULL,"
					+ " server VARCHAR(20) NOT NULL, date BIGINT NOT NULL, verified INT NOT NULL, email VARCHAR(255))";
			dbh.getConnection().prepareStatement(sql).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	AtomicArrayList<String> chatLocked = new AtomicArrayList<String>();

	/*
	 * @EventHandler public void playerLogin(final PlayerJoinEvent event) {
	 * final long beginTime = System.currentTimeMillis();
	 * jp.getServer().getScheduler().runTaskAsynchronously(jp, new Runnable() {
	 * 
	 * @Override public void run() { try { PreparedStatement ps = dbh
	 * .getConnection() .prepareStatement(
	 * "SELECT * FROM rankpurchases WHERE userId = ? AND verified = 0");
	 * ps.setInt( 1, UUIDSystem.getInstance().getId(
	 * event.getPlayer().getUniqueId())); ResultSet rs = ps.executeQuery();
	 * StringBuilder sbranks = new StringBuilder(); long latestRank =
	 * Long.MAX_VALUE; if (!rs.next()) { rs.close(); ps.close(); return; } do {
	 * sbranks.append(rs.getString("rank") + ", "); if (latestRank >
	 * rs.getLong("date")) latestRank = rs.getLong("date"); } while (rs.next());
	 * Calendar cal = new GregorianCalendar(); cal.setTimeInMillis(latestRank);
	 * cal.add(Calendar.WEEK_OF_YEAR, 2); TimeUtils tu = new
	 * TimeUtils(cal.getTimeInMillis() - System.currentTimeMillis()); String
	 * timeLeft = (cal.getTimeInMillis() - System.currentTimeMillis() < 0) ?
	 * "Your time has run out. If you have sent the email then dont worry, if you have not you shall have your payment refunded and you shall be removed from our services (banned)"
	 * : "You have " + tu.toString() +
	 * " to do this and if you do not your payment shall be refunded and you will be removed from our services (banned). This is done to cut down on fraud."
	 * ; sbranks.deleteCharAt(sbranks.length() - 1);
	 * sbranks.deleteCharAt(sbranks.length() - 1); if (sbranks.indexOf(",") > 0)
	 * { sbranks.append(" ranks"); } else sbranks.append(" rank"); if
	 * (System.currentTimeMillis() - beginTime < 1000) {
	 * Thread.currentThread().sleep(2000); } if (!event.getPlayer().isOnline())
	 * return; if (!chatLocked.contains(event.getPlayer().getName()))
	 * chatLocked.add(event.getPlayer().getName()); String[] message = {
	 * ChatColor.DARK_GRAY + "---------------------------------------",
	 * ChatColor.AQUA +
	 * " In order to verify your payment to HcRaid please email confirm@addongaming.com from the email associated with the paypal account."
	 * , ChatColor.AQUA +
	 * " The contents of the e-mail must contain the following",
	 * ChatColor.YELLOW + "" + ChatColor.ITALIC +
	 * "\"I {name on your PayPal account} confirm the payment for the following transaction: {PayPal transaction ID}. I verify that I have recieved the goods and I have paid for them legally (either via paypal balance or card), of which I have permission to use and I have completed this transaction out of my own free will\""
	 * , ChatColor.RED + timeLeft, ChatColor.GREEN +
	 * "Use /rank to turn chat back on.", ChatColor.DARK_GRAY +
	 * "---------------------------------------" };
	 * event.getPlayer().sendMessage(message); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * } }); }
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void playerReceivedMessage(AsyncPlayerChatEvent event) {
		if (chatLocked.contains(event.getPlayer().getName())) {
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "Use /rank to turn chat on.");
			event.setCancelled(true);
			return;
		}
		for (Iterator<Player> iter = event.getRecipients().iterator(); iter
				.hasNext();)
			if (chatLocked.contains(iter.next().getName()))
				iter.remove();
	}
}
