package hcmodtools.core.playerspy;

import hcmodtools.core.ModTool;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageMonitor implements Listener, CommandExecutor, ModTool {
	HashMap<String, ArrayList<String>> peopleToggled = new HashMap<String, ArrayList<String>>();
	private JavaPlugin jp;

	public DamageMonitor(JavaPlugin jp) {
		this.jp = jp;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerAttack(final EntityDamageByEntityEvent event) {
		if (event.isCancelled() || event.getDamage() == 0)
			return;
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			final List<String> toAdd = new ArrayList<String>();
			final Player attacked = (Player) event.getEntity();
			for (String modName : peopleToggled.keySet())
				for (String playerName : peopleToggled.get(modName))
					if (playerName.equalsIgnoreCase(attacked.getName()))
						toAdd.add(playerName);
			for (final String modName : toAdd)
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@Override
							public void run() {
								Player p = Bukkit.getPlayer(modName);
								if (p == null || !p.isOnline())
									return;
								double currHealth = (double) attacked
										.getHealth();
								if (currHealth % 2 == 0
										|| event.getDamage() > 0.5)
									msg(p,
											attacked.getName()
													+ "'s current health "
													+ ChatColor.GREEN
													+ "[ "
													+ new DecimalFormat("#.#")
															.format(currHealth / 2)
													+ "/10 ]");
							}
						}, 0L);

		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("You are console, you can't get damaged!");
			return true;
		} else if (!arg0.hasPermission("HcRaid.MOD")) {
			msg((Player) arg0,
					"Sorry you do not have permission for this command.");
			return true;
		}
		Player p = (Player) arg0;
		if (arg3.length > 1 || arg3.length == 0) {
			msg(p, "Please use /dmgmonitor <playername>");
			return true;
		}
		ArrayList<String> playersWatching;
		if (peopleToggled.containsKey(p.getName()))
			playersWatching = peopleToggled.get(p.getName());
		else
			playersWatching = new ArrayList<String>();
		Player player = Bukkit.getPlayer(arg3[0]);
		if ((player == null || !player.isOnline())
				&& !playersWatching.contains(arg3[0])) {
			msg(p, "Player " + arg3[0] + " not found.");
			return true;
		}
		if (playersWatching.contains(player.getName())) {
			msg(p,
					"You will no longer get damage notifications for "
							+ player.getName()
							+ ". Use /dmgmonitor <player> to toggle it on.");
			playersWatching.remove(arg3[0]);
		} else {
			msg(p,
					"You will now get damage notifications for "
							+ player.getName()
							+ ". Use /dmgmonitor <player> to toggle it off.");
			playersWatching.add(arg3[0]);
		}
		peopleToggled.put(p.getName(), playersWatching);
		return true;
	}

	private final String title = ChatColor.GREEN + "[" + ChatColor.DARK_PURPLE
			+ "HcModDamage" + ChatColor.GREEN + "] " + ChatColor.GRAY;

	private void msg(Player p, String str) {
		p.sendMessage(title + str);
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {
		peopleToggled.clear();
	}
}
