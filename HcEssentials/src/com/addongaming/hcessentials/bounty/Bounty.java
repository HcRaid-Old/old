package com.addongaming.hcessentials.bounty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.kitteh.tag.TagAPI;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.events.BountyClaimedEvent;
import com.addongaming.hcessentials.utils.Utils;

public class Bounty implements SubPlugin, Listener, CommandExecutor {
	private List<BountyHeadObj> bountyHeadList = new ArrayList<BountyHeadObj>();

	private ArrayList<BountyObj> bountyList = new ArrayList<BountyObj>();
	private HashMap<String, Date> bountySet = new HashMap<String, Date>();
	@SuppressWarnings("unused")
	private boolean head = false;

	private JavaPlugin jp;

	private int minBounty;
	String negTitle = ChatColor.GOLD + "[" + ChatColor.DARK_RED + "HcBounty"
			+ ChatColor.GOLD + "] " + ChatColor.DARK_GRAY;

	String posTitle = ChatColor.GOLD + "[" + ChatColor.GRAY + "HcBounty"
			+ ChatColor.GOLD + "] " + ChatColor.GRAY;

	SQLite sqlite;

	private int maxBountiesAHead;

	public Bounty(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("bounty.enabled", Boolean.FALSE);
		fc.addDefault("bounty.minspend", 1000);
		fc.addDefault("bounty.headreward", Boolean.TRUE);
		fc.addDefault("bounty.maxahead", 10);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	private int timeBetweenBounties = 20;

	private BountyObj addToBounty(String playerName, int amount,
			Player playerGiving) {
		try {
			if (bountySet.containsKey(playerGiving.getName())) {
				if (new Date().before(new Date(bountySet.get(
						playerGiving.getName()).getTime()
						+ (timeBetweenBounties * 1000)))) {
					Date difference = new Date(
							(bountySet.get(playerGiving.getName()).getTime() + (timeBetweenBounties * 1000))
									- new Date().getTime());
					message(playerGiving, "You need to wait "
							+ new SimpleDateFormat("ss").format(difference)
							+ " seconds before setting another bounty.");
					return null;
				}
			}
			for (Iterator<BountyObj> it = bountyList.iterator(); it.hasNext();) {
				BountyObj bo = it.next();
				if (bo.getPlayerName().equalsIgnoreCase(playerName)) {
					if (bo.getPlayersGivenBounty().contains(
							playerGiving.getName())) {
						message(playerGiving,
								"You have already given this player a bounty.");
						return null;
					} else if (bo.getPlayersGivenBounty().size() >= maxBountiesAHead) {
						message(playerGiving,
								"This player already has the maximum amount of bounties.");
						return null;
					}
					bountySet.put(playerGiving.getName(), new Date());
					bo.incrementBounty(amount);
					bo.addBountyGive(playerGiving.getName());
					sqlite.query(
							"INSERT INTO bounty(Playername, Bountycost, Cashedin, playerGiving) VALUES ('"
									+ playerName + "'," + amount + ",0,'"
									+ playerGiving.getName() + "');").close();
					return bo;
				}
			}
			sqlite.query(
					"INSERT INTO bounty(Playername, Bountycost, Cashedin, playerGiving) VALUES ('"
							+ playerName + "'," + amount + ",0,'"
							+ playerGiving.getName() + "');").close();
			BountyObj bo = new BountyObj(playerName, amount);
			bo.addBountyGive(playerGiving.getName());
			bountyList.add(bo);
			TagAPI.refreshPlayer(Bukkit.getPlayer(playerName));
			bountySet.put(playerGiving.getName(), new Date());
			return bo;
		} catch (SQLException e) {
			e.printStackTrace();
			message(playerGiving, "Sorry, something seems to have gone wrong.");
			return null;
		}
	}

	private void checkBounty(Player player, String[] arg3) {
		if (arg3.length == 1) {
			message(player, "/bounty check <playername>");
			return;
		}
		String str = arg3[1];
		for (BountyObj bo : bountyList)
			if (bo.getPlayerName().equalsIgnoreCase(str)) {
				message(player, "Bounty for " + bo.getPlayerName()
						+ " is set at $" + bo.getCurrentBounty());
				return;
			}
		message(player, "There are currently no bounties for " + str);
	}

	private boolean fileExists(final String fileName) {
		File file = new File(jp.getDataFolder().getAbsolutePath() + "/"
				+ fileName);
		return file.exists();
	}

	private void listBounty(Player player) {
		message(player, "Online bounties");
		int counter = 0;
		for (BountyObj bo : bountyList) {
			Player p = Bukkit.getPlayer(bo.getPlayerName());
			if (p == null || !p.isOnline()
					|| HcEssentials.essentials.getUser(p).isHidden())
				continue;
			counter++;
			message(player, bo.getPlayerName() + " - $" + bo.getCurrentBounty());
		}
		if (counter == 0)
			message(player, "There are currently no online bounties");

	}

	private void listCommands(Player player) {
		message(player, "");
		smessage(player, ChatColor.GRAY
				+ "   /bounty set <username> <amount> - Set a players bounty");
		smessage(player, ChatColor.GRAY
				+ "   /bounty list - See all online bounties");
		smessage(player, ChatColor.GRAY
				+ "   /bounty check <username> - Checks a players bounty");
		message(player, "");
	}

	private Object load(final String fileName) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(jp.getDataFolder().getAbsolutePath() + "/"
						+ fileName));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	private void loadBounties() {
		try {
			ResultSet rs = sqlite
					.query("SELECT * FROM  bounty WHERE Cashedin = 0 ORDER BY Playername");
			String playerName = null;
			BountyObj bo = null;
			while (rs.next()) {
				if (!rs.getString("Playername").equalsIgnoreCase(playerName)
						|| playerName == null) {
					if (bo != null)
						bountyList.add(bo);
					bo = new BountyObj(rs.getString("Playername"),
							rs.getInt("Bountycost"));
					bo.addBountyGive(rs.getString("playerGiving"));
					playerName = bo.getPlayerName();
					continue;
				}
				bo.incrementBounty(rs.getInt("Bountycost"));
				bo.addBountyGive(rs.getString("playerGiving"));
			}
			if (bo != null)
				bountyList.add(bo);
			rs.close();
			System.out.println("Bounty information loaded.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void message(Player p, String message) {
		smessage(p, posTitle + message);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			return false;
		}
		Player player = (Player) arg0;
		if (arg3.length == 0) {
			listCommands(player);
			return true;
		}
		switch (arg3[0]) {
		case "set":
			setBounty(player, arg3);
			return true;
		case "list":
			listBounty(player);
			return true;
		case "check":
			checkBounty(player, arg3);
			return true;
		default:
			listCommands(player);
			return true;
		}
	}

	@Override
	public void onDisable() {
		if (sqlite != null) {
			System.out.println("Closing bounty connection");
			sqlite.close();
		}
		try {
			save(bountyHeadList, "BountyHead.sav");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("bounty.enabled"))
			return false;
		System.out.println("Enabling bounties!");
		minBounty = jp.getConfig().getInt("bounty.minspend");
		head = jp.getConfig().getBoolean("bounty.headreward");
		maxBountiesAHead = jp.getConfig().getInt("bounty.maxahead");
		jp.getCommand("bounty").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		startSqlConnection();
		sqlTableCheck();
		loadBounties();
		// Loading head bounties
		try {
			if (fileExists("BountyHead.sav"))
				bountyHeadList = (ArrayList<BountyHeadObj>) load("BountyHead.sav");
			else
				save(bountyHeadList, "BountyHead.sav");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onNameTag(AsyncPlayerReceiveNameTagEvent event) {
		System.out.println("Player " + event.getPlayer().getName()
				+ " Version " + Utils.getPlayerVersion(event.getPlayer()));
		if (Utils.getPlayerVersion(event.getPlayer()) >= 47)
			return;
		for (BountyObj bo : bountyList)
			if (bo.getPlayerName().equalsIgnoreCase(
					event.getNamedPlayer().getName())) {
				event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
				return;
			}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDied(PlayerDeathEvent event) {
		BountyObj bo = null;
		for (BountyObj boo : bountyList) {
			if (boo.getPlayerName().equalsIgnoreCase(
					event.getEntity().getName())) {
				bo = boo;
				break;
			}
		}
		if (bo == null)
			return;
		if (event.getEntity().getKiller() != null) {
			if (event.getEntity().getKiller().getName()
					.equalsIgnoreCase(event.getEntity().getName()))
				return;
			BountyClaimedEvent ev = new BountyClaimedEvent(event.getEntity()
					.getKiller().getName());
			Bukkit.getPluginManager().callEvent(ev);
			if (ev.isCancelled())
				return;
			message(event.getEntity().getKiller(),
					"You recieved " + bo.getPlayerName() + "'s bounty of $"
							+ bo.getCurrentBounty() + ".");
			Bukkit.broadcastMessage(posTitle
					+ event.getEntity().getKiller().getName() + " got "
					+ event.getEntity().getName() + "'s bounty of $"
					+ bo.getCurrentBounty());
			HcEssentials.economy.depositPlayer(event.getEntity().getKiller()
					.getName(), bo.getCurrentBounty());

			resetBounties(bo);
			bountyList.remove(bo);
			TagAPI.refreshPlayer(event.getEntity());
		}
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isDead())
			return;
		for (Iterator<BountyHeadObj> it = bountyHeadList.iterator(); it
				.hasNext()
				&& event.getPlayer().getInventory().firstEmpty() > -1;) {
			BountyHeadObj bo = it.next();
			if (bo.getNames().contains(event.getPlayer().getName())) {
				event.getPlayer().getInventory().addItem(bo.getHead());
				bo.removeName(event.getPlayer().getName());
				if (bo.getNames().size() == 0)
					it.remove();
			}
		}
	}

	private void resetBounties(BountyObj bo) {
		try {
			sqlite.query(
					"UPDATE bounty SET Cashedin = 1 WHERE Playername = '"
							+ bo.getPlayerName() + "'").close();
			List<String> notGiven = new ArrayList<String>();
			for (String str : bo.getPlayersGivenBounty()) {
				Player p = Bukkit.getPlayer(str);
				if (p == null || !p.isOnline()
						|| p.getInventory().firstEmpty() == -1)
					notGiven.add(str);
				else
					p.getInventory().addItem(bo.getPlayersHead());
			}
			if (notGiven.size() == 0)
				return;
			BountyHeadObj bho = new BountyHeadObj(bo.getPlayerName());
			for (String str : notGiven)
				bho.addName(str);
			bountyHeadList.add(bho);
			try {
				save(bountyHeadList, "BountyHead.sav");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void save(final Object obj, final String fileName) throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(jp.getDataFolder().getAbsolutePath() + "/"
						+ fileName));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	private void setBounty(Player player, String[] arg3) {
		if (arg3.length < 3) {
			message(player, "/bounty set <username> <amount>");
			return;
		}
		String playerName = arg3[1];
		String amount = arg3[2];
		Player p = Bukkit.getPlayer(arg3[1]);
		if (p == null || !p.isOnline()) {
			message(player, playerName
					+ " cannot be found, are they on the server?");
			return;
		}
		if (HcEssentials.essentials.getUser(p).isHidden()) {
			message(player, playerName
					+ " cannot be found, are they on the server?");
			return;
		}
		int amnt;
		if (p.getName().equalsIgnoreCase(player.getName())) {
			message(player, "You cannot place a bounty on yourself!");
			return;
		}
		try {
			amnt = Integer.parseInt(amount);
		} catch (NumberFormatException ex) {
			message(player, amount + " is not a valid amount.");
			return;
		}
		if (amnt < minBounty) {
			message(player, "Please set a minimum bounty of $" + minBounty);
			return;
		}
		if (HcEssentials.economy.getBalance(player.getName()) < amnt) {
			message(player, "You do not have enough money to set this bounty.");
			return;
		} else {

			if (addToBounty(p.getName(), amnt, player) != null) {
				HcEssentials.economy.withdrawPlayer(player.getName(), amnt);
				message(player,
						"Set a bounty of $" + amnt + " on " + p.getName());
				Bukkit.broadcastMessage(posTitle
						+ "A bounty has been placed on " + playerName
						+ "'s head!");
				Bukkit.getPluginManager().callEvent(
						new BountyClaimedEvent(p.getName()));
			}
		}
	}

	private void smessage(Player p, String message) {
		p.sendMessage(message);
	}

	public void sqlTableCheck() {
		try {
			java.sql.DatabaseMetaData dbm = sqlite.getConnection()
					.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "bounty", null);
			if (!tables.next()) {
				System.out.println("Tables doesn't exist, making.");
				sqlite.query("CREATE TABLE bounty (id INTEGER PRIMARY KEY AUTOINCREMENT,Playername VARCHAR(16),Bountycost INTEGER, Cashedin INTEGER, playerGiving VARCHAR(16));");
				tables.close();
			} else {
				jp.getLogger().info("Tables have been created");
				tables.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void playerRightClickHead(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& event.getClickedBlock() != null
				&& event.getClickedBlock().getType().equals(Material.SKULL)) {
			Skull s = (Skull) event.getClickedBlock().getState();
			if (!s.hasOwner())
				return;
			event.getPlayer().sendMessage(
					posTitle + "This is " + s.getOwner() + "'s head.");
		}
	}

	public void startSqlConnection() {
		sqlite = new SQLite(jp.getLogger(), "HcBounty", jp.getDataFolder()
				.getAbsolutePath(), "HcBounty");
		try {
			sqlite.open();
		} catch (Exception e) {
			jp.getLogger().info(e.getMessage());
		}
	}
}
