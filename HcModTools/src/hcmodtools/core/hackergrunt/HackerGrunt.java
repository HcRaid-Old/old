package hcmodtools.core.hackergrunt;

import hcmodtools.core.Main;
import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HackerGrunt extends Tools implements ModTool, CommandExecutor {
	private final JavaPlugin jp;

	public HackerGrunt(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.GREEN + "HcHackerGrunt"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_RED
				+ "[" + ChatColor.RED + "HcHackerGrunt" + ChatColor.DARK_RED
				+ "] " + ChatColor.RESET);
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (!p.hasPermission("HcRaid.mod"))
				return true;
			if (arg3.length != 2) {
				msg(p, "/hg add <playername> - Adds a player to hackergrunt");
				if (p.hasPermission("HcRaid.admin"))
					msg(p,
							"/hg remove <playername> - Removes a player from hackergrunt");
				return true;
			}
			switch (arg3[0]) {
			case "add":
				OfflinePlayer hacker = Bukkit.getOfflinePlayer(arg3[1]);
				if (!hacker.hasPlayedBefore()) {
					warn(p, "Player " + arg3[1] + " has not played before.");
					return true;
				}
				List<String> groups = new ArrayList<String>();
				// groups.add(Main.permission.getPrimaryGroup("world",
				// arg3[1]));
				String mainGroup = Main.permission.getPrimaryGroup("world",
						hacker.getName());
				if (mainGroup.equalsIgnoreCase("enderdragon"))
					mainGroup = "ender";
				boolean hackerPremium = false;
				for (String group : Main.permission.getGroups())
					if (group.equalsIgnoreCase("hacker" + mainGroup)) {
						hackerPremium = true;
						mainGroup = group;
						break;
					}
				for (String group : Main.permission.getPlayerGroups("world",
						arg3[1]))
					groups.add(group);
				for (String group : groups)
					Main.permission.playerRemoveGroup("world", arg3[1], group);
				Main.permission.playerAddGroup("world", arg3[1],
						(hackerPremium ? mainGroup : "Hackergrunt"));
				for (String group : groups)
					if (!group.contains("g:"))
						Main.permission.playerAddGroup("world", arg3[1], group);
				msg(p, arg3[1] + " has been added to hackergrunt.");
				break;
			case "remove":
				if (!p.hasPermission("HcRaid.admin")) {
					warn(p,
							"Sorry, you need administrative priviledges to do that.");
					return true;
				}
				OfflinePlayer hacky = Bukkit.getOfflinePlayer(arg3[1]);
				if (!hacky.hasPlayedBefore()) {
					warn(p, "Player " + arg3[1] + " has not played before.");
					return true;
				} else if (!Main.permission.getPrimaryGroup("world", arg3[1])
						.equalsIgnoreCase("HackerGrunt")) {
					warn(p, arg3[1] + "'s main group isn't hackergrunt.");
				}
				Main.permission.playerRemoveGroup("world", arg3[1],
						"Hackergrunt");
				String group = "Grunt";
				for (String str : Main.permission.getPlayerGroups("world",
						arg3[1]))
					if (!str.equalsIgnoreCase("grunt") && !str.startsWith("g:"))
						group = str;
				Main.permission.playerRemoveGroup("world", arg3[1], group);
				System.out.println("Group: " + group);
				Main.permission.playerAddGroup("world", arg3[1], group);

				msg(p, arg3[1] + " has been removed from hackergrunt.");
				break;
			}

		}
		return false;
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginCommand("hg").setExecutor(this);

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
