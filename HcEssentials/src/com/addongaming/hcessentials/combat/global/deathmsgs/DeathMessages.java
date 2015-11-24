package com.addongaming.hcessentials.combat.global.deathmsgs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.config.Config;

public class DeathMessages implements Listener, CommandExecutor {

	private static String posTitle = ChatColor.GREEN + "[" + ChatColor.GOLD
			+ "HcCombat" + ChatColor.GREEN + "] ";

	public static void message(CommandSender p, String msg) {
		p.sendMessage(posTitle + msg);
	}

	private final JavaPlugin jp;

	public DeathMessages(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED
					+ "Sorry, you don't have permission to execute this command.");
			return true;
		}

		if (arg3.length != 1) {
			message(arg0, "Please use /deathmsg <off | on | nonames>");
			return true;
		}
		String str = arg3[0];
		switch (str.toLowerCase()) {
		case "off":
			Config.Combat.dmt = DMTypes.OFF;
			jp.getConfig().set("deathmessages.type", Config.Combat.dmt.name());
			jp.saveConfig();
			message(arg0, "Set death messages to off.");
			return true;
		case "on":
			Config.Combat.dmt = DMTypes.ON;
			jp.getConfig().set("deathmessages.type", Config.Combat.dmt.name());
			jp.saveConfig();
			message(arg0, "Set death messages to on.");
			return true;
		case "nonames":
			Config.Combat.dmt = DMTypes.NONAMES;
			jp.getConfig().set("deathmessages.type", Config.Combat.dmt.name());
			jp.saveConfig();
			message(arg0, "Set death messages to on but regular weapon names.");
			return true;
		}
		message(arg0, "Value " + str
				+ " not recognised. Please use /deathmsg <off | on | nonames>");
		return true;
	}

	@EventHandler
	public void playerDied(PlayerDeathEvent pde) {
		if (!(pde.getEntity().getKiller() instanceof Player))
			return;
		if (pde.getEntity().getKiller() == null)
			return;
		Player killer = (Player) pde.getEntity().getKiller();
		switch (Config.Combat.dmt) {
		case OFF:
			pde.setDeathMessage(null);
			System.out.println("Cancelled death message");
			return;
		case ON:
			return;
		case NONAMES:
			ItemStack killing = killer.getItemInHand();
			if (killing == null || killing.getType() == Material.AIR)
				return;
			if (killing.getType() == Material.BOW) {
				pde.setDeathMessage(pde.getEntity().getName() + " was shot by "
						+ killer.getName());
			} else {
				pde.setDeathMessage(pde.getEntity().getName()
						+ " was slain by "
						+ killer.getName()
						+ " with a "
						+ killing.getType().name().toLowerCase()
								.replace('_', ' '));
			}
			return;

		}

	}
}
