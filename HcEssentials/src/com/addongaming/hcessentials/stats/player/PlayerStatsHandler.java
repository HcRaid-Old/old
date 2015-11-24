package com.addongaming.hcessentials.stats.player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.events.BountyClaimedEvent;
import com.addongaming.hcessentials.events.BountyIssueEvent;
import com.addongaming.hcessentials.events.ItemRecycledEvent;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.stats.player.runnables.CheckPlayerExistence;
import com.addongaming.hcessentials.stats.player.runnables.ComparePlayers;
import com.addongaming.hcessentials.stats.player.runnables.EconomyChecker;
import com.addongaming.hcessentials.stats.player.runnables.PlayerStatReader;
import com.addongaming.hcessentials.stats.player.runnables.PlayerStatUpdater;

public class PlayerStatsHandler implements SubPlugin, Listener, CommandExecutor {
	HashMap<String, PlayerStatInstance> pli = new HashMap<String, PlayerStatInstance>();
	private JavaPlugin jp;

	public HashMap<String, PlayerStatInstance> getPlayerMap() {
		return pli;
	}

	public void addToStat(String playerName, EPlayerStat stat, double amount) {
		pli.get(playerName).incrementStat(stat, amount);
	}

	public PlayerStatsHandler(JavaPlugin jp) {
		this.jp = jp;
		// Setup configuration
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("playerstats.enabled", false);
		fc.addDefault("playerstats.minutesbetweenupdate", 5);
		// Database
		fc.addDefault("playerstats.db.url", "localhost");
		fc.addDefault("playerstats.db.port", "3306");
		fc.addDefault("playerstats.db.database", "playerstats");
		fc.addDefault("playerstats.db.username", "Username");
		fc.addDefault("playerstats.db.password", "password");
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	int updaterID = -1;

	@Override
	public void onDisable() {
		if (updaterID > 0)
			jp.getServer().getScheduler().cancelTask(updaterID);
		System.out.println("Running player updater.");
		new PlayerStatUpdater(new ArrayList<PlayerStatInstance>(pli.values()),
				dh).run();
		System.out.println("Finished player updater.");
	}

	private DataLog dl;
	private DatabaseHandling dh;

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("playerstats.enabled"))
			return false;
		dl = HcEssentials.getDataLogger().addLogger("Playerstats");
		String url = jp.getConfig().getString("playerstats.db.url");
		String port = jp.getConfig().getString("playerstats.db.port");
		String database = jp.getConfig().getString("playerstats.db.database");
		String username = jp.getConfig().getString("playerstats.db.username");
		String password = jp.getConfig().getString("playerstats.db.password");
		dh = new DatabaseHandling(url, database, username, password, port);
		if (!dh.firstSetup())
			return false;
		updaterID = jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(
						jp,
						new Runnable() {

							@Override
							public void run() {
								jp.getServer()
										.getScheduler()
										.runTaskAsynchronously(
												jp,
												new PlayerStatUpdater(
														new ArrayList<PlayerStatInstance>(
																pli.values()),
														dh));
								pli.clear();
								pli = new HashMap<String, PlayerStatInstance>();
								for (Player play : Bukkit.getOnlinePlayers())
									pli.put(play.getName(),
											new PlayerStatInstance(play
													.getName()));
							}
						},
						60,
						jp.getConfig().getInt(
								"playerstats.minutesbetweenupdate") * 20 * 60);
		jp.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(jp, new EconomyChecker(this), 10l,
						20 * 20l);
		Connection con = dh.getConnection();
		dh.createTables(con);
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getCommand("stats").setExecutor(this);
		return true;
	}

	@EventHandler
	public void playerLogon(PlayerJoinEvent event) {
		logon(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent event) {
		pli.get(event.getPlayer().getName()).incrementStat(
				EPlayerStat.blocksBroken);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockPlace(BlockPlaceEvent event) {
		pli.get(event.getPlayer().getName()).incrementStat(
				EPlayerStat.blocksPlaced);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void itemEnchant(EnchantItemEvent event) {
		pli.get(event.getEnchanter().getName()).incrementStat(
				EPlayerStat.itemsEnchanted);
	}

	@EventHandler
	public void itemRecycled(ItemRecycledEvent event) {
		pli.get(event.getRecycler()).incrementStat(EPlayerStat.itemsRecycled);
	}

	@EventHandler
	public void bountyIssued(BountyIssueEvent event) {
		pli.get(event.getIssuer()).incrementStat(EPlayerStat.bountiesIssues);
	}

	@EventHandler
	public void bountyClaimed(BountyClaimedEvent event) {
		pli.get(event.getClaimer()).incrementStat(EPlayerStat.bountiesClaimed);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerMove(PlayerMoveEvent pme) {
		/*
		 * pli.get(pme.getPlayer().getName()).incrementStat(
		 * EPlayerStat.distanceMoved,
		 * pme.getFrom().distanceSquared(pme.getTo()));
		 */
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void entityKilled(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			pli.get(p.getName()).incrementStat(EPlayerStat.timesDied);
		}
		Player p = event.getEntity().getKiller();
		if (p == null)
			return;
		if (event.getEntity() instanceof Player) {
			pli.get(p.getName()).incrementStat(EPlayerStat.playersKilled);
		} else {
			pli.get(p.getName()).incrementStat(EPlayerStat.entitiesKilled);
		}
	}

	private List<String> playersChecked = new ArrayList<String>();

	private void logon(Player p) {

		if (!playersChecked.contains(p.getName())) {
			jp.getServer()
					.getScheduler()
					.runTaskAsynchronously(
							jp,
							new CheckPlayerExistence(p.getName(), dh
									.getConnection()));
			playersChecked.add(p.getName());
		}
		if (pli.containsKey(p.getName())) {
			pli.get(p.getName()).restoreTimer();
			System.out.println("Restored player " + p.getName());
		} else {
			pli.put(p.getName(), new PlayerStatInstance(p.getName()));
			System.out.println("Added player " + p.getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerLogout(PlayerQuitEvent event) {
		if (pli.containsKey(event.getPlayer().getName()))
			pli.get(event.getPlayer().getName()).pauseTimer();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player))
			return true;
		Player sender = (Player) arg0;
		if (arg3.length == 0)
			jp.getServer()
					.getScheduler()
					.runTaskAsynchronously(
							jp,
							new PlayerStatReader((Player) arg0, dh, pli
									.get(sender.getName())));
		else {
			if (arg3.length == 2) {
				String name = arg3[1];
				Player p = Bukkit.getPlayer(name);
				PlayerStatInstance psi1 = pli.get(((Player) (arg0)).getName());
				PlayerStatInstance psi2 = null;
				if (p != null && p.isOnline())
					psi2 = pli.get(p.getName());
				jp.getServer()
						.getScheduler()
						.runTaskAsynchronously(
								jp,
								new ComparePlayers((Player) arg0, psi1, name,
										psi2, dh.getConnection(), dh));
			}
		}
		return true;
	}
}
