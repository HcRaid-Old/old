package hcmodtools.core.chatcontrol;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChatClearer implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("Hcraid.mod"))
			return false;
		for (int i = 0; i < 30; i++)
			Bukkit.broadcastMessage("");
		return true;
	}

}
