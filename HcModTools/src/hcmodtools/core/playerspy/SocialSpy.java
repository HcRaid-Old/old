package hcmodtools.core.playerspy;

import hcmodtools.core.Main;
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

public class SocialSpy extends Tools implements Listener, CommandExecutor,
		ModTool {
	private final JavaPlugin jp;
	private List<String> toggled = new ArrayList<String>();

	public SocialSpy(JavaPlugin jp) {
		super(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Social"
				+ ChatColor.DARK_GRAY + "] " + ChatColor.RESET, "");
		this.jp = jp;

	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		if (toggled.contains(event.getPlayer().getName())
				&& !event.getPlayer().hasPermission("HcRaid.MOD")) {
			toggled.remove(event.getPlayer().getName());
		}
	}

	String[] allowed = { "/r", "/msg", "/message", "/whisper", "/w", "/tell",
			"/t" };

	@EventHandler
	public void playercommand(PlayerCommandPreprocessEvent ape) {
		if (ape.isCancelled())
			return;
		if (ape.getPlayer().hasPermission("Hcraid.OWNER"))
			return;
		if (!ape.getMessage().contains(" "))
			return;
		boolean bool = false;
		boolean reply = false;
		for (String str : allowed) {
			if (ape.getMessage().contains(" ")
					&& ape.getMessage().split(" ")[0].equalsIgnoreCase(str)) {
				if (ape.getMessage().split(" ")[0].equalsIgnoreCase("/r"))
					reply = true;
				bool = true;
				break;
			}
		}
		if (!bool)
			return;
		StringBuilder sb = new StringBuilder();
		for (int i = reply ? 1 : 2; i < ape.getMessage().split(" ").length; i++) {
			sb.append(ape.getMessage().split(" ")[i] + " ");
		}
		for (String str : toggled) {
			Player p = Bukkit.getPlayer(str);
			if (p != null && p.isOnline()) {
				if (!reply)
					msg(p,
							ape.getPlayer().getName() + " -> "
									+ ape.getMessage().split(" ")[1] + " : "
									+ sb.toString());
				else if (Main.essentials.getUser(ape.getPlayer()).getReplyTo() != null)
					msg(p, ape.getPlayer().getName()
							+ " -> "
							+ Main.essentials.getUser(ape.getPlayer())
									.getReplyTo().getPlayer().getName() + " : "
							+ sb.toString());

			}
		}

	}

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("socialspy").setExecutor(this);
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
			if (!toggled.contains(arg0.getName())) {
				arg0.sendMessage(ChatColor.GREEN
						+ "[SocialSpy] "
						+ ChatColor.AQUA
						+ " You have toggled Social Spy on, you will now see other players conversations.");
				toggled.add(arg0.getName());
			} else {
				arg0.sendMessage(ChatColor.GREEN
						+ "[SocialSpy] "
						+ ChatColor.AQUA
						+ " You have toggled Social Spy off, you will no longer see other players conversations.");
				toggled.remove(arg0.getName());
			}
			return true;

		}
		return false;
	}

}
