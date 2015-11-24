package hcmodtools.core.chatcontrol;

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
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMonitor extends Tools implements Listener, CommandExecutor {

	private List<String> toggledStaff = new ArrayList<String>();
	private Player staff;

	public ChatMonitor() {
		super(ChatColor.BLUE + "[" + ChatColor.GREEN + "HcChatControl"
				+ ChatColor.BLUE + "]" + " " + ChatColor.RESET,
				ChatColor.DARK_RED + "[" + ChatColor.RED + "HcChatControl"
						+ ChatColor.DARK_RED + "]" + " " + ChatColor.RESET);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		int counter = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("hcraid.mod")
					&& toggledStaff.contains(e.getPlayer().getUniqueId()
							.toString())) {
				System.out.println(counter++);
				staff = p;
				String displayName = staff.getDisplayName();
				displayName = ChatColor.stripColor(displayName);
				if (displayName != null && displayName.length() > 2
						&& displayName.contains("#")) {
					displayName = displayName.substring(
							displayName.indexOf('#') + 1,
							displayName.length() - 1);
				}

				if (e.getMessage().contains(staff.getName())
						|| e.getMessage().matches("(?i).*(owner|mod|admin).*")
						|| (p.getDisplayName() != null && e.getMessage()
								.contains(displayName))) {
					msg(staff, "Player " + e.getPlayer().getName()
							+ " needs your assistance.");
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label,
			String[] args) {
		if (args.length == 0 && cs.hasPermission("hcraid.mod")
				|| args.length == 0 && cs.isOp()) {
			msg(cs, "Please choose one of the following:");
			msg(cs, "/chatmon - Lists all commands");
			msg(cs, "/chatmon on - Turns ChatMonitor on");
			msg(cs, "/chatmon off - Turns ChatMonitor off");
			return true;
		}
		switch (args[0]) {
		case "off":
			if (cs.hasPermission("hcraid.mod")) {
				if (toggledStaff.contains(cs.getName()))
					toggledStaff.remove(cs.getName());
				msg(cs, "ChatMonitor is now off.");
				saveStaff();
			}
			break;
		case "on":
			if (cs.hasPermission("hcraid.mod")) {
				if (!toggledStaff.contains(cs.getName()))
					toggledStaff.add(cs.getName());
				msg(cs, "ChatMonitor is now on.");
				saveStaff();
			}
			return true;
		}
		return true;
	}

	private void saveStaff() {

	}

	private void loadStaff() {

	}
}