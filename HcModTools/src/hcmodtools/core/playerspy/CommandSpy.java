package hcmodtools.core.playerspy;

import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandSpy extends Tools implements Listener, CommandExecutor,
		ModTool {
	private final JavaPlugin jp;
	private List<String> toggled = new ArrayList<String>();

	public CommandSpy(JavaPlugin jp) {
		super(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Cmd"
				+ ChatColor.DARK_GRAY + "] " + ChatColor.RESET, "");
		this.jp = jp;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("commandspy").setExecutor(this);
	}

	String[] blockedCmds = { "/r", "/msg", "/message", "/whisper", "/w",
			"/tell", "/t" };

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		if (toggled.contains(event.getPlayer().getName())
				&& !event.getPlayer().hasPermission("HcRaid.MOD")) {
			toggled.remove(event.getPlayer().getName());
		}
	}

	@EventHandler
	public void playercommand(PlayerCommandPreprocessEvent ape) {
		if (ape.isCancelled())
			return;
		if (ape.getPlayer().hasPermission("Hcraid.OWNER"))
			return;
		for (String str : blockedCmds) {
			if (ape.getMessage().contains(" ")
					&& ape.getMessage().split(" ")[0].equalsIgnoreCase(str))
				return;
			else if (ape.getMessage().equalsIgnoreCase(str))
				return;
		}
		for (String str : toggled) {
			Player p = Bukkit.getPlayer(str);
			if (p != null && p.isOnline()) {
				msg(p,
						ape.getPlayer().getName() + " issued: "
								+ ape.getMessage());
			}
		}

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (!p.hasPermission("HcRaid.mod"))
				return false;
			if (!toggled.contains(arg0.getName())) {
				arg0.sendMessage(ChatColor.GREEN
						+ "[CommandSpy] "
						+ ChatColor.AQUA
						+ " You have toggled Command Spy on, you will now see other players commands.");
				toggled.add(arg0.getName());
			} else {
				arg0.sendMessage(ChatColor.GREEN
						+ "[CommandSpy] "
						+ ChatColor.AQUA
						+ " You have toggled Command Spy off, you will no longer see other players commands.");
				toggled.remove(arg0.getName());
			}
			return true;

		}
		return false;
	}

}
