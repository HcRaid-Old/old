package hcmodtools.core.staffchat;

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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StaffChat extends Tools implements CommandExecutor, ModTool,
		Listener {

	String title = ChatColor.BLUE + "[" + ChatColor.GREEN + "HcStaff"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;
	String mtitle = ChatColor.BLUE + "[" + ChatColor.GREEN + "HcModChat"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;
	String atitle = ChatColor.BLUE + "[" + ChatColor.GREEN + "HcAdminChat"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;
	String otitle = ChatColor.BLUE + "[" + ChatColor.GREEN + "HcOpChat"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;
	public static List<String> mchatToggled = new ArrayList<String>();
	public static List<String> achatToggled = new ArrayList<String>();
	public static List<String> ochatToggled = new ArrayList<String>();

	JavaPlugin jp;

	public String getChannel(String name) {
		if (mchatToggled.contains(name))
			return "Mod";
		else if (achatToggled.contains(name))
			return "Admin";
		else if (ochatToggled.contains(name))
			return "Operator";
		else
			return null;
	}

	public StaffChat(JavaPlugin jp) {
		super(ChatColor.BLUE + "[" + ChatColor.GREEN + "HcStaff"
				+ ChatColor.BLUE + "]" + " " + ChatColor.RESET,
				ChatColor.DARK_RED + "[" + ChatColor.RED + "HcStaff"
						+ ChatColor.DARK_RED + "]" + " " + ChatColor.RESET);
		this.jp = jp;
	}

	@Override
	public void onStop() {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		if (getChannel(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
			if (mchatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.hasPermission("hcraid.mod") || p.isOp())
						p.sendMessage(mtitle + " <" + e.getPlayer().getName()
								+ "> " + e.getMessage());
			}
			if (achatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.hasPermission("hcraid.admin") || p.isOp())
						p.sendMessage(atitle + " <" + e.getPlayer().getName()
								+ "> " + e.getMessage());
			}
			if (ochatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.isOp())
						p.sendMessage(otitle + " <" + e.getPlayer().getName()
								+ "> " + e.getMessage());
			}
		}
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getCommand("staff").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.hasPermission("hcraid.mod")) {
			if (args.length == 0) {
				sender.sendMessage(title
						+ "Please choose from one of the following:");
				sender.sendMessage("    "
						+ "/staff m - Toggles mod chat on/off.");
				if (sender.hasPermission("hcraid.admin")) {
					sender.sendMessage("    "
							+ "/staff a - Toggles admin chat on/off.");
				}
				if (sender.isOp() || sender.hasPermission("*")) {
					sender.sendMessage("    "
							+ "/staff o - Toggles op chat on/off.");
				}
				sender.sendMessage(title);
				return true;
			}

			switch (args[0]) {
			case "m":
				if (args.length != 0 && sender.hasPermission("hcraid.mod")) {
					Player p = (Player) sender;

					if (mchatToggled.contains(sender.getName())) {
						sender.sendMessage(mtitle + "Mod chat is now off!");
						mchatToggled.remove(sender.getName());
						return true;
					} else {
						if (getChannel(p.getName()) != null)
							return warn(sender, "You are in the "
									+ getChannel(p.getName()) + " channel.");
						sender.sendMessage(mtitle + "Mod chat is now on!");
						mchatToggled.add(sender.getName());
						return true;
					}
				} else {
					if (!sender.hasPermission("hcraid.mod")) {
						warn(sender,
								"You do not have permission to execute this command.");
					}
				}
				break;
			case "a":
				if (args.length != 0 && sender.hasPermission("hcraid.admin")) {
					if (achatToggled.contains(sender.getName())) {
						sender.sendMessage(atitle + "Admin chat is now off!");
						achatToggled.remove(sender.getName());
						return true;
					} else {
						if (getChannel(sender.getName()) != null)
							return warn(sender, "You are in the "
									+ getChannel(sender.getName())
									+ " channel.");
						sender.sendMessage(atitle + "Admin chat is now on!");
						achatToggled.add(sender.getName());
						return true;
					}

				} else {
					if (!sender.hasPermission("hcraid.admin") || !sender.isOp()) {
						warn(sender,
								"You do not have permission to execute this command.");
					}
				}
				break;
			case "o":
				if (args.length != 0 && sender.isOp()) {

					if (ochatToggled.contains(sender.getName())) {
						sender.sendMessage(otitle + "Operator chat is now off!");
						ochatToggled.remove(sender.getName());
						return true;
					} else {
						if (getChannel(sender.getName()) != null)
							return warn(sender, "You are in the "
									+ getChannel(sender.getName())
									+ " channel.");
						sender.sendMessage(otitle + "Operator chat is now on!");
						ochatToggled.add(sender.getName());
						return true;
					}

				} else {
					if (!sender.isOp()) {
						warn(sender,
								"You do not have permission to execute this command.");
					}
					return true;
				}

			}
			return true;
		}
		return true;
	}
}