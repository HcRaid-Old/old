package com.addongaming.hcessentials.raiding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.raiding.objects.Raid;

public class Raiding implements SubPlugin, CommandExecutor, Listener {

	private JavaPlugin jp;

	private final String neg = ChatColor.BLACK + "[" + ChatColor.DARK_RED
			+ "HcRaiding" + ChatColor.BLACK + "] " + ChatColor.RED;

	HashMap<String, Raid> playerMap = new HashMap<String, Raid>();

	private final String pos = ChatColor.GOLD + "[" + ChatColor.DARK_BLUE
			+ "HcRaiding" + ChatColor.GOLD + "] " + ChatColor.BLUE;

	List<String> teleList = new ArrayList<String>();

	public Raiding(JavaPlugin jp) {
		this.jp = jp;
	}

	private void checkCooldown(Player sender) {
		if (!playerMap.containsKey(sender.getName())) {
			warn(sender, "You don't currently have a raid.");
			return;
		}
		Raid r = playerMap.get(sender.getName());
		if (r.canCreateAnother()) {
			message(sender, "You can create another raid now!");
			return;
		} else {
			message(sender, "You need to wait " + r.getTimeBeforeAnother());
		}

	}

	private void checkTimeLeft(Player sender) {
		if (!playerMap.containsKey(sender.getName())) {
			warn(sender, "You don't currently have a raid.");
			return;
		}
		Raid r = playerMap.get(sender.getName());
		if (r.canCreateAnother()) {
			message(sender, "You can create another raid now!");
		} else if (r.hasFinished()) {
			message(sender,
					"You can no longer use your raid home. Please wait for the cooldown to expire.");
		} else {
			message(sender,
					"Time left before the raid expires: "
							+ r.getTimeRemaining());
		}
	}

	private Raid createRaid(Player play) {
		Location loc = play.getLocation();
		long duration = 5;
		long cooldown = 55;

		Set<String> ranks = jp.getConfig()
				.getConfigurationSection("raiding.ranks").getKeys(false);
		for (String rank : ranks)
			if (play.hasPermission("hcraid." + rank)) {
				duration = inMinutes(jp.getConfig().getInt(
						"raiding.ranks." + rank + ".homeduration"));
				cooldown = inMinutes(jp.getConfig().getInt(
						"raiding.ranks." + rank + ".cooldown"));
				break;
			}
		return new Raid(loc, new Date(), duration, cooldown);
	}

	private void delRaid(Player sender) {
		if (!playerMap.containsKey(sender.getName())) {
			warn(sender, "You haven't set a raid yet.");
			return;
		} else {
			Raid r = playerMap.get(sender.getName());
			if (!r.canCancel()) {
				warn(sender,
						"You cannot cancel the raid, maybe it has already expired?");
				return;
			}
			r.cancel();
			message(sender, "Cancelled your current raid.");
		}
	}

	private long inMinutes(int i) {
		return (long) (i * 60000);
	}

	private void message(Player sender, String string) {
		sender.sendMessage(pos + string);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("raid")) {
			if (args.length == 0 || args[0] == null)
				return wrongArgs((Player) sender);
			String s = args[0].toLowerCase();
			switch (s) {
			case "tel":
			case "teleport":
				teleport((Player) sender);
				return true;
			case "set":
				setRaid((Player) sender);
				return true;
			case "del":
				delRaid((Player) sender);
				return true;
			case "check":
				checkTimeLeft((Player) sender);
				return true;
			case "cooldown":
			case "cool":
				checkCooldown((Player) sender);
				return true;
			}
			return wrongArgs((Player) sender);
		}
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("raiding.enabled")) {
			System.out.println("Raiding is disabled.");
			return false;
		}
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getCommand("raid").setExecutor(this);
		return true;
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent pme) {
		if (teleList.contains(pme.getPlayer().getName())) {
			teleList.remove(pme.getPlayer().getName());
			warn(pme.getPlayer(), "Teleportation to raid has been cancelled.");
		}
	}

	private void setRaid(Player sender) {
		if (playerMap.containsKey(sender.getName())) {
			Raid r = playerMap.get(sender.getName());
			if (!r.canCreateAnother()) {
				warn(sender,
						"You still need to wait " + r.getTimeBeforeAnother()
								+ " before setting another raid.");
				return;
			}
		}
		playerMap.put(sender.getName(), createRaid(sender));
		message(sender, "Successfully set your raid.");
	}

	private void teleport(final Player sender) {
		if (!playerMap.containsKey(sender.getName())) {
			warn(sender, "You haven't set a raid yet!");
			return;
		} else if (teleList.contains(sender.getName())) {
			warn(sender, "You already have a teleportation queued.");
			return;
		}
		final Raid r = playerMap.get(sender.getName());
		if (r.hasFinished()) {
			warn(sender, "Your raid has expired.");
			return;
		}
		message(sender, "Teleporting in 5 seconds. Please don't move.");
		teleList.add(sender.getName());
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						if (teleList.contains(sender.getName())) {
							message(sender, "Teleporting...");
							teleList.remove(sender.getName());
							sender.teleport(r.getLocation());
						}
					}
				}, 5 * 20l);
	}

	private void warn(Player sender, String string) {
		sender.sendMessage(neg + string);
	}

	private boolean wrongArgs(Player p) {
		warn(p, "");
		message(p, "/raid tel           - Teleports to your current raid ");
		message(p, "/raid set          -  Sets your raid where you are");
		message(p, "/raid del          - Deletes your current raid ");
		message(p, "/raid check      - Checks the time left on your raid");
		message(p,
				"/raid cooldown  - Checks the cooldown time before you can set another raid.");
		warn(p, "");
		return true;
	}
}
