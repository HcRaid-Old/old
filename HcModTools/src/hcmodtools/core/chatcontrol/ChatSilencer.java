package hcmodtools.core.chatcontrol;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSilencer implements CommandExecutor, Listener {
	private boolean chatSilenced = false;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("hcraid.mod"))
			return false;
		chatSilenced = !chatSilenced;
		Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.AQUA
				+ "HcChatSilencer" + ChatColor.GOLD + "] " + ChatColor.GREEN
				+ "Chat has been " + (chatSilenced ? "silenced" : "unsilenced")
				+ " by " + arg0.getName());
		return true;
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		if (chatSilenced && !event.getPlayer().hasPermission("hcraid.mod"))
			event.setCancelled(true);
	}
}
