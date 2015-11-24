package com.addongaming.hcessentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.utils.Utils;

public class CmdPing implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player player = (Player) arg0;
			int ping = Utils.getPlayerPing(player);
			if (ping > -1) {
				arg0.sendMessage("Pong! Your ping is " + ping + "ms.");
				return true;
			}
		}
		arg0.sendMessage("Pong!");
		return true;
	}
}
