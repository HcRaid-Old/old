package hcmodtools.core.playerspy;

import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerFlagger extends Tools implements ModTool, Listener,
		CommandExecutor {

	private JavaPlugin jp;

	public PlayerFlagger(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.GREEN + "HcPlayerMonitor"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_RED
				+ "[" + ChatColor.RED + "HcPlayerMonitor" + ChatColor.DARK_RED
				+ "] " + ChatColor.RESET);
		this.jp = jp;

	}

	@EventHandler
	public void playerLoggedIn(PlayerJoinEvent event) {
		if (watching.containsKey(event.getPlayer().getName().toLowerCase())) {
			for (String mod : watching.get(
					event.getPlayer().getName().toLowerCase()).getWatchers()) {
				Player p = Bukkit.getPlayer(mod);
				if (p != null && p.isOnline()) {
					msg(p, event.getPlayer().getName() + " has just logged in.");
				}
			}
		}
	}

	@EventHandler
	public void playerLogOut(PlayerQuitEvent event) {
		playerLeft(event.getPlayer());
	}

	private void playerLeft(Player player) {
		if (watching.containsKey(player.getName().toLowerCase())) {
			for (String mod : watching.get(player.getName().toLowerCase())
					.getWatchers()) {
				Player p = Bukkit.getPlayer(mod);
				if (p != null && p.isOnline()) {
					msg(p, player.getName() + " has disconnected.");
				}
			}
		}
	}

	@EventHandler
	public void playerKick(PlayerKickEvent event) {
		playerLeft(event.getPlayer());
	}

	Map<String, WatchInstance> watching = new HashMap<String, WatchInstance>();

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (!p.hasPermission("hcraid.mod"))
				return false;
			if (arg3.length == 0) {
				warn(p,
						"Please use /watch <playername> to watch/unwatch a player");
				return true;
			}
			if (Bukkit.getPlayer(arg3[0]) == null
					&& !Bukkit.getOfflinePlayer(arg3[0]).hasPlayedBefore()) {
				warn(p, "Sorry, player " + arg3[0] + " was not found.");
				return true;
			}
			if (!watching.containsKey(arg3[0].toLowerCase())) {
				watching.put(arg3[0].toLowerCase(),
						new WatchInstance(arg0.getName()));
				msg(p, "You are now watching " + arg3[0]);
				return true;
			} else {
				if (watching.get(arg3[0].toLowerCase()).containsWatcher(
						arg0.getName())) {
					watching.get(arg3[0].toLowerCase()).removeWatcher(
							arg0.getName());
					msg(p, "You are no longer watching " + arg3[0]);
				} else {
					watching.get(arg3[0].toLowerCase()).addWatcher(
							arg0.getName());
					msg(p, "You are now watching " + arg3[0]);
				}
				return true;
			}

		}
		return false;
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("watch").setExecutor(this);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
