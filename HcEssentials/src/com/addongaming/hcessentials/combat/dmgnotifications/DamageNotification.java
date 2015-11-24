package com.addongaming.hcessentials.combat.dmgnotifications;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

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

import com.addongaming.hcessentials.combat.filehandler.SerHandling;

public class DamageNotification implements Listener, CommandExecutor {
	private JavaPlugin jp;
	ArrayList<String> peopleToggled = new ArrayList<String>();
	File saveTo;

	private final String title = ChatColor.GREEN + "[" + ChatColor.YELLOW
			+ "HcDamage" + ChatColor.GREEN + "] " + ChatColor.GRAY;

	@SuppressWarnings("unchecked")
	public DamageNotification(File dataFolder, JavaPlugin jp) {
		this.jp = jp;
		File toAlter = new File(dataFolder + File.separator
				+ "DamageNotification");
		if (!toAlter.exists())
			toAlter.mkdirs();
		toAlter = new File(toAlter + File.separator + "toggled.sav");
		saveTo = toAlter;
		if (!toAlter.exists()) {
			try {
				toAlter.createNewFile();
				save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				peopleToggled = (ArrayList<String>) SerHandling.load(toAlter
						.getAbsolutePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void msg(Player p, String str) {
		p.sendMessage(title + str);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("You are console, you can't get damaged!");
			return true;
		}
		Player p = (Player) arg0;
		if (arg1.getName().equalsIgnoreCase("dtoggle")) {
			if (peopleToggled.contains(p.getName())) {
				msg(p,
						"You will no longer get damage notifications. Use /dtoggle to toggle it on.");
				peopleToggled.remove(p.getName());
				save();
			} else {
				msg(p,
						"You will now get damage notifications. Use /dtoggle to toggle it off.");
				peopleToggled.add(p.getName());
				save();
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerAttack(final EntityDamageByEntityEvent event) {
		if (event.isCancelled() || event.getDamage() == 0)
			return;
		if (event.getDamager() instanceof Player
				&& peopleToggled.contains(((Player) (event.getDamager()))
						.getName()) && event.getEntity() instanceof Player) {
			final Player p = (Player) event.getDamager();
			final Player attacked = (Player) event.getEntity();
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							double currHealth = (double) attacked.getHealth();
							if (currHealth % 2 == 0 || event.getDamage() > 0.5)
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

	private void save() {
		try {
			SerHandling.save(peopleToggled, saveTo.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
