package com.addongaming.hcessentials.perks;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.data.Ranks;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.perks.near.ENear;
import com.addongaming.hcessentials.perks.near.NearInstance;
import com.addongaming.hcessentials.utils.Utils;

public class NearCommand implements SubPlugin, CommandExecutor {
	private JavaPlugin jp;
	HashMap<String, Long> endDate = new HashMap<String, Long>();
	HashMap<String, NearInstance> nearMap = new HashMap<String, NearInstance>();
	private DataLog dl;
	private int cost;

	public NearCommand(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player))
			return false;
		if (arg3.length == 0) {
			arg0.sendMessage("Please use /near <amount>");
			if (arg0.hasPermission("HcRaid.mod")) {
				// arg0.sendMessage("Please use /near <tracker> - for all recently tracked players.");
				// arg0.sendMessage("Please use /near <tracker> <trackee> - for all trackings by the tracker to the trackee.");
			}
			return true;
		}
		if (!isInteger(arg3[0])) {
			arg0.sendMessage(ChatColor.RED + "Please user /near <radius>");
		} else {
			int radius = Integer.parseInt(arg3[0]);
			Ranks r = Ranks.getHighestRank((Player) arg0);
			ENear en = ENear.getByRank(r);
			if (radius > 2000) {
				arg0.sendMessage(ChatColor.RED + "The maximum radius is 2000.");
				return true;
			}
			int radiusExtra = radius > en.getRadius() ? (radius - en
					.getRadius()) * cost : 0;
			if (HcEssentials.economy.getBalance(arg0.getName()) < radiusExtra) {
				arg0.sendMessage(ChatColor.RED
						+ "You need an extra $"
						+ new DecimalFormat("##.#").format(radiusExtra
								- HcEssentials.economy.getBalance(arg0
										.getName())) + " for that radius.");
				return true;
			}
			if (timeIsUp(arg0.getName())) {
				doNear((Player) arg0, radius, radiusExtra);
			} else {
				timeLeft((Player) arg0);
				return true;
			}
		}
		return true;
	}

	private boolean timeIsUp(String name) {
		if (!endDate.containsKey(name)) {
			return true;
		}
		return endDate.get(name) < new Date().getTime();
	}

	private void doNear(final Player p, long radius, int cost) {
		ArrayList<Player> allPlayers = new ArrayList<Player>(
				Arrays.asList(Bukkit.getOnlinePlayers()));
		for (Iterator<Player> iter = allPlayers.iterator(); iter.hasNext();) {
			Player pl = iter.next();
			if (p == pl
					|| !p.getLocation().getWorld().getName()
							.equalsIgnoreCase(pl.getWorld().getName()))
				iter.remove();
		}
		Collections.sort(allPlayers, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return (int) (p.getLocation().distance(o1.getLocation()) - p
						.getLocation().distance(o2.getLocation()));
			}
		});
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GOLD + "> " + ChatColor.RESET);

		for (Iterator<Player> it = allPlayers.iterator(); it.hasNext();) {
			Player play = it.next();
			if (play == p
					|| !p.getWorld().getName()
							.equalsIgnoreCase(play.getWorld().getName())) {
				it.remove();
				continue;
			}
			int currRadius = (int) play.getLocation().distance(p.getLocation());
			if (currRadius > radius) {
				it.remove();
				continue;
			}
			if (HcEssentials.essentials.getUser(play).isHidden()) {
				if (p.hasPermission("hcraid.mod"))
					sb.append(play.getName() + ChatColor.RED + " V"
							+ ChatColor.BLUE + " [" + currRadius + "m] "
							+ ChatColor.RESET + ", ");
			} else {
				sb.append(play.getName() + ChatColor.BLUE + " [" + currRadius
						+ "m]" + ChatColor.RESET + ", ");
				dl.logPlayer(
						play,
						"Tracked by " + p.getName() + " @ " + currRadius
								+ " m to "
								+ Utils.locationToString(play.getLocation()));
			}
		}

		if (allPlayers.isEmpty()) {
			if (cost > 0) {
				cost /= 2;
				HcEssentials.economy.withdrawPlayer(p.getName(), cost);
			}
			p.sendMessage(ChatColor.RED
					+ "No players found. "
					+ (cost > 0 ? "$" + cost
							+ " has been subtracted from your account." : ""));
			radius = (radius * 100) / 4;
			radius += new Date().getTime();
			endDate.put(p.getName(), (long) radius);
			return;
		}
		radius = (radius * 100) / 2;
		radius += new Date().getTime();
		endDate.put(p.getName(), (long) radius);
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(".");
		dl.logPlayer(p, sb.toString());
		HcEssentials.economy.withdrawPlayer(p.getName(), cost);
		if (allPlayers.size() == 1)
			p.sendMessage(ChatColor.GREEN
					+ " The following player has been located! "
					+ (cost > 0 ? "$" + cost
							+ " has been subtracted from your account." : ""));
		else
			p.sendMessage(ChatColor.GREEN
					+ " The following players have been located! "
					+ (cost > 0 ? "$" + cost
							+ " has been subtracted from your account." : ""));
		p.sendMessage(sb.toString());
	}

	private void timeLeft(Player p) {
		if (!endDate.containsKey(p.getName()))
			return;
		long diff = endDate.get(p.getName()) - new Date().getTime();
		p.sendMessage(ChatColor.RED + "Command is on cooldown: "
				+ new SimpleDateFormat("m:ss").format(new Date(diff)));
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void onDisable() {
		saveData();
	}

	private void saveData() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("perks.near.enabled", false);
		fc.addDefault("perks.near.costperm", 2);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		if (!fc.getBoolean("perks.near.enabled"))
			return false;
		cost = fc.getInt("perks.near.costperm");
		jp.getCommand("near").setExecutor(this);
		dl = HcEssentials.getDataLogger().addLogger("Near");
		loadData();
		return true;
	}

	private void loadData() {
		// TODO Auto-generated method stub

	}

}
