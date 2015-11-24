package com.addongaming.hcessentials.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.chatmanager.ChatFilter;

public class Name implements CommandExecutor, SubPlugin {

	private boolean filter;

	private JavaPlugin jp;

	private int maxChars;

	private String permission;

	public Name(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length == 0) {
			arg0.sendMessage(ChatColor.GRAY + "/name <Item Name>");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		for (String str : arg3) {
			if (filter && ChatFilter.filteredWords.contains(str)) {
				arg0.sendMessage(ChatColor.GRAY
						+ "It seems you've entered a naughty word \"" + str
						+ "\"");
				return true;
			}
			sb.append(str + " ");
		}
		sb.deleteCharAt(sb.length() - 1);
		if (sb.length() > maxChars) {
			arg0.sendMessage(ChatColor.GRAY
					+ "Too many characters, you can use " + maxChars
					+ " but have used " + sb.length());
			return true;
		} else if (!sb.toString().matches("[0-9a-zA-Z ]*")) {
			arg0.sendMessage(ChatColor.GRAY
					+ "Please use regular alphanumerical characters.");
			return true;
		}
		Player p = (Player) arg0;
		if (p.getItemInHand() == null
				|| p.getItemInHand().getType() == Material.AIR) {
			p.sendMessage(ChatColor.RED
					+ "You must be holding a valid in your hand to issue this command.");
			return true;
		}
		ItemMeta im = p.getItemInHand().getItemMeta();
		if (sb.toString().equalsIgnoreCase("off")) {
			im.setDisplayName(null);
			p.getItemInHand().setItemMeta(im);
			return true;
		}
		if (!arg0.hasPermission(permission)) {
			arg0.sendMessage(ChatColor.GRAY
					+ "You do not have permission to use this command. You may use /name off");
			return true;
		}
		if (p.getItemInHand().getType() != Material.NAME_TAG)
			im.setDisplayName(ChatColor.AQUA + "[*] " + ChatColor.BLUE
					+ sb.toString() + ChatColor.RESET);
		else
			im.setDisplayName(sb.toString());
		p.getItemInHand().setItemMeta(im);
		arg0.sendMessage(ChatColor.GRAY + "Set item name!");
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("itemnaming.enabled", Boolean.FALSE);
		fc.addDefault("itemnaming.permission", "HcRaid.MOD");
		fc.addDefault("itemnaming.maxchars", 30);
		fc.addDefault("itemnaming.namefilterenabled", Boolean.TRUE);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		if (!fc.getBoolean("itemnaming.enabled"))
			return false;
		permission = fc.getString("itemnaming.permission");
		maxChars = fc.getInt("itemnaming.maxchars");
		filter = fc.getBoolean("itemnaming.namefilterenabled");
		jp.getCommand("name").setExecutor(this);
		return true;
	}
}
