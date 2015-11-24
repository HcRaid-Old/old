package com.addongaming.hcessentials.teams;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamMethods {
	protected ChatColor AQU = ChatColor.AQUA;
	protected ChatColor BLU = ChatColor.BLUE;
	protected ChatColor DAQU = ChatColor.DARK_AQUA;
	protected ChatColor DGREN = ChatColor.DARK_GREEN;
	protected ChatColor DRED = ChatColor.DARK_RED;
	protected ChatColor GOLD = ChatColor.GOLD;
	protected ChatColor GREEN = ChatColor.DARK_GREEN;
	protected ChatColor GRY = ChatColor.GRAY;
	protected ChatColor RED = ChatColor.RED;
	protected ChatColor WHT = ChatColor.WHITE;

	protected void msg(CommandSender cs, String message) {
		msg((Player) cs, message);
	}

	protected void msg(Player cs, String message) {
		cs.sendMessage(DGREN + "[" + AQU + "HcTeams" + DGREN + "] " + GREEN
				+ message);
	}

	protected void safeWarn(CommandSender cs, String message) {
		cs.sendMessage(ChatColor.RED + message);
	}

	protected boolean warn(CommandSender cs, String message) {
		warn((Player) cs, message);
		return true;
	}

	protected void warn(Player p, String message) {
		p.sendMessage(DRED + "[" + AQU + "HcTeams" + DRED + "] "
				+ ChatColor.RED + message);
	}

}
