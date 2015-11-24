package com.addongaming.hcessentials.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.logging.DataLog;

public class CmdSuggestion implements CommandExecutor {
	private String title = ChatColor.GOLD + "[" + ChatColor.RED + "Suggest"
			+ ChatColor.GOLD + "] " + ChatColor.RESET;
	DataLog dl;

	public CmdSuggestion() {
		dl = HcEssentials.getDataLogger().addLogger("Suggestions");
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length <= 2) {
			arg0.sendMessage(title + "Please use /suggest <Suggestion>");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String str : arg3)
			sb.append(str + " ");
		dl.log(arg0.getName() + " | " + sb.toString());
		arg0.sendMessage(title
				+ "Thank you for your suggestion! Please do not abuse this system.");
		return true;
	}
}
