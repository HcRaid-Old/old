package com.addongaming.hcessentials.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.logging.DataLog;

public class CmdBug implements CommandExecutor {
	private String title = ChatColor.GOLD + "[" + ChatColor.RED + "Bug"
			+ ChatColor.GOLD + "] " + ChatColor.RESET;
	DataLog dl;

	public CmdBug() {
		dl = HcEssentials.getDataLogger().addLogger("Bugs");
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length <= 2) {
			arg0.sendMessage(title + "Please use /bug <bug message>");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String str : arg3)
			sb.append(str + " ");
		dl.log(arg0.getName() + " | " + sb.toString());
		arg0.sendMessage(title
				+ "Thank you for your bug report! Please do not abuse this system.");
		arg0.sendMessage(title
				+ "For large bugs please use the bug tracker at http://forum.hcraid.com/tracker/");
		return true;
	}

}
