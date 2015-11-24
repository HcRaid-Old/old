package org.KitPvP;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	public static final String posTitle = ChatColor.BLUE + "[" + ChatColor.AQUA
			+ "KitPvP" + ChatColor.BLUE + "] " + ChatColor.RESET;
	public static final String negTitle = ChatColor.DARK_RED + "["
			+ ChatColor.RED + "KitPvP" + ChatColor.DARK_RED + "] "
			+ ChatColor.RESET;

	@Override
	public void onEnable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("class")) {
			if (args.length < 1) {
				return false;
			} else {
				Player player = (Player) sender;

				if (!player.getWorld().getName().contains("kitpvp")) {
					sender.sendMessage(negTitle
							+ "You are not in the correct world.");
					return true;
				}

				for (Kits k : Kits.values()) {
					if (k.getName().equalsIgnoreCase(args[0])) {
						player.getInventory().setArmorContents(k.getArmour());
						player.getInventory().setContents(k.getHotbarItems());
						sender.sendMessage(posTitle + "Enjoy your "
								+ k.getName() + " kit.");
						return true;
					}
				}
			}
		}

		return false;

	}
}
