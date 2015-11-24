package com.addongaming.prison.commands.cmd;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.addongaming.prison.data.utilities.CommandData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class Balance implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			Prisoner prison = PrisonerManager.getInstance()
					.getPrisonerInfo(p.getName());
			if (!prison.hasPermission(CommandData.BAL.getPermission())) {
				p.sendMessage(ChatColor.RED
						+ "You have not bought the permission to execute this command.");
				return true;
			}
			p.sendMessage(ChatColor.GREEN + "Balance: " + ChatColor.RED
					+ new DecimalFormat("##.#").format(prison.getBalance())
					+ " rupees.");
		}
		return true;
	}
}
