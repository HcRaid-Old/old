package com.addongaming.prison.commands.cmd;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.addongaming.prison.data.utilities.CommandData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class Pay implements CommandExecutor {
	private double getDouble(String string) {
		try {
			Double dd = Double.parseDouble(string);
			if (dd < 0)
				dd = dd * -1;
			return dd;
		} catch (NumberFormatException nfe) {
			return -1d;
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			Prisoner prison = PrisonerManager.getInstance()
					.getPrisonerInfo(p.getName());
			if (!prison.hasPermission(CommandData.PAY.getPermission())) {
				p.sendMessage(ChatColor.RED
						+ "You have not bought the permission to execute this command.");
				return true;
			}
			if (arg3.length != 2) {
				arg0.sendMessage(ChatColor.RED
						+ "Please use /pay <playername> [amount]");
				return true;
			}
			String name = arg3[0];
			Player reciever = Bukkit.getPlayer(name);
			if (reciever == null || !reciever.isOnline()) {
				arg0.sendMessage(ChatColor.RED + name + " is not online.");
				return true;
			}
			if (getDouble(arg3[1]) <= 0) {
				arg0.sendMessage(ChatColor.RED
						+ "Please enter a valid amount. /pay <playername> [amount]");
				return true;
			}
			Double d = getDouble(arg3[1]);
			if (!prison.hasBalance(d)) {
				arg0.sendMessage(ChatColor.RED
						+ "You do not have enough money for this transaction.");
				return true;
			}
			prison.removeBalance(d);
			Prisoner recPrisoner = PrisonerManager.getInstance()
					.getPrisonerInfo(reciever.getName());
			recPrisoner.addBalance(d);
			p.sendMessage(ChatColor.GREEN + "You sent "
					+ new DecimalFormat("##.#").format(d) + " rupees to "
					+ recPrisoner.getName() + ".");
			reciever.sendMessage(ChatColor.GREEN + "You recieved "
					+ new DecimalFormat("##.#").format(d) + " rupees from "
					+ p.getName() + ".");
		}
		return true;
	}
}
