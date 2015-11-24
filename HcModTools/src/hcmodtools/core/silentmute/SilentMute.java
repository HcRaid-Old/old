package hcmodtools.core.silentmute;

import hcmodtools.core.ModTool;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SilentMute implements Listener, CommandExecutor, ModTool {
	List<String> muted = new ArrayList<String>();
	private JavaPlugin jp;

	public SilentMute(JavaPlugin jp) {
		this.jp = jp;

	}

	@Override
	public void onStart() {
		jp.getCommand("smute").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void playerTalk(AsyncPlayerChatEvent event) {
		if (muted.contains(event.getPlayer().getName())) {
			event.getPlayer().sendMessage(
					String.format(event.getFormat(), event.getPlayer()
							.getDisplayName(), event.getMessage()));
			Bukkit.broadcast(ChatColor.GRAY + "[SilentMute] <"
					+ event.getPlayer().getName() + "> " + event.getMessage(),
					"HcRaid.Mod");
			event.setCancelled(true);
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (!p.hasPermission("HcRaid.mod"))
				return false;
			if (arg3.length != 1) {
				arg0.sendMessage(ChatColor.GREEN + "[SilentMute] "
						+ ChatColor.AQUA + " /silentmute <player>");
				return true;
			}
			String name = arg3[0];
			Player pl = Bukkit.getPlayer(name);
			if (muted.contains(name)) {
				muted.remove(name);
				arg0.sendMessage(ChatColor.GREEN + "[SilentMute] "
						+ ChatColor.AQUA + " Removed " + name
						+ " from silent mute.");
				return true;
			} else if (pl.isOnline()) {
				arg0.sendMessage(ChatColor.GREEN + "[SilentMute] "
						+ ChatColor.AQUA + " Added " + name
						+ " to silent mute.");
				muted.add(pl.getName());
				return true;
			} else {
				arg0.sendMessage(ChatColor.GREEN + "[SilentMute] "
						+ ChatColor.AQUA + " Player " + name + " not found.");
				return true;
			}
		}
		return false;
	}

}
