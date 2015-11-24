package hcmodtools.core.xray;

import hcmodtools.core.CaseInsensitiveMap;
import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiXray extends Tools implements ModTool, Listener,
		CommandExecutor {
	final CaseInsensitiveMap xrayInstances = new CaseInsensitiveMap();
	final CaseInsensitiveMap offlineInstances = new CaseInsensitiveMap();
	private JavaPlugin jp;

	public AntiXray(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.GREEN + "HcAntiXray"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_RED
				+ "[" + ChatColor.RED + "HcAntiXray" + ChatColor.DARK_RED
				+ "] " + ChatColor.RESET);
		this.jp = jp;
	}

	@EventHandler
	public void login(PlayerLoginEvent ple) {
		if (!offlineInstances.containsKey(ple.getPlayer().getName())) {
			xrayInstances.put(ple.getPlayer().getName(), new XrayInstance(this,
					ple.getPlayer().getName()));
		} else {
			xrayInstances.put(ple.getPlayer().getName(),
					offlineInstances.get(ple.getPlayer().getName()));
			offlineInstances.remove(ple.getPlayer().getName());
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent bbe) {
		if (xrayInstances.containsKey(bbe.getPlayer().getName())) {
			xrayInstances.get(bbe.getPlayer().getName()).blockMined(
					bbe.getBlock().getType());
		}
	}

	@EventHandler
	public void logout(PlayerQuitEvent pqe) {
		left(pqe.getPlayer());
	}

	@EventHandler
	public void logout(PlayerKickEvent pqe) {
		left(pqe.getPlayer());
	}

	private void left(Player player) {
		if (xrayInstances.containsKey(player.getName())) {
			offlineInstances.put(player.getName(),
					xrayInstances.get(player.getName()));
			xrayInstances.remove(player.getName());
		}
	}

	@EventHandler
	public void kick(PlayerKickEvent pqe) {
		left(pqe.getPlayer());
	}

	private int sched = -1;
	private final Map<String, Long> suspiciousMap = new HashMap<String, Long>();

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("xray").setExecutor(this);
		for (Player p : Bukkit.getOnlinePlayers())
			xrayInstances.put(p.getName(), new XrayInstance(this, p.getName()));
		final String posTit = super.getPosTitle();
		sched = jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						synchronized (suspiciousMap) {
							List<String> toRemove = new ArrayList<String>();
							for (String str : suspiciousMap.keySet())
								if (new Date().after(new Date(suspiciousMap
										.get(str) + XrayValues.alertCooldown))) {
									suspiciousMap.remove(str);
								}
							for (String str : toRemove)
								suspiciousMap.remove(str);
						}
						List<String> toBroadcast = new ArrayList<String>();
						synchronized (xrayInstances) {

							for (XrayInstance xi : xrayInstances.values())
								if (xi.shouldBroadcast())
									toBroadcast.add(xi.getPlayerName());
						}
						for (String str : toBroadcast) {
							Player p = Bukkit.getPlayer(str);
							if (p == null || !p.isOnline())
								continue;
							Bukkit.broadcast(
									posTit
											+ str
											+ " has been flagged for Xray, use /xray check "
											+ str + " for more info.",
									"HcRaid.MOD");
							synchronized (suspiciousMap) {
								suspiciousMap.put(str, new Date().getTime());
							}
						}
					}
				}, 10l, 150l);
	}

	@Override
	public void onStop() {
		jp.getServer().getScheduler().cancelTask(sched);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (p.hasPermission("HcRaid.MOD")) {
				if (arg3.length == 0) {
					msg(p,
							"/xray check <playername> - Shows statistics on the player.");
					msg(p, "/xray flag - Shows flagged players");
				} else {
					switch (arg3[0].toLowerCase()) {
					case "check":
						if (arg3.length != 2)
							return warn(p, "/xray check <playername>");
						String name = arg3[1].toLowerCase();
						if (xrayInstances.containsKey(name)) {
							xrayInstances.get(name).asMessage(p);
							return true;
						} else if (offlineInstances.containsKey(name)) {
							offlineInstances.get(name).asMessage(p);
							return true;
						} else
							return warn(p, "Player not found.");
					case "flag":
						if (suspiciousMap.isEmpty()) {
							msg(p, "No players have been flagged!");
							return true;
						}
						StringBuilder sb = new StringBuilder();
						for (String str : suspiciousMap.keySet()) {
							if (Bukkit.getPlayer(str) != null
									&& Bukkit.getPlayer(str).isOnline()) {
								sb.append(str + ", ");
							}
						}
						if (sb.length() > 4) {
							sb.deleteCharAt(sb.length() - 2);
							msg(p, "The following players have been flagged: "
									+ sb.toString());
						} else {
							msg(p, "No online players have been flagged!");

						}
						return true;
					}
				}
			} else
				return false;
		}
		return false;
	}

}
