package com.addongaming.hcessentials.faq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class FAQCore implements SubPlugin, CommandExecutor {
	private List<FAQSelection> faqList = new ArrayList<FAQSelection>();
	private JavaPlugin jp;

	public FAQCore(JavaPlugin jp) {
		this.jp = jp;
	}

	private void loadConfig() {
		faqList.clear();
		File f = new File(jp.getDataFolder() + File.separator + "FAQ");
		if (!f.exists())
			f.mkdirs();
		for (File file : f.listFiles())
			if (file.getName().endsWith(".txt"))
				faqList.add(new FAQSelection(file));
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		String title = ChatColor.GOLD + "[" + ChatColor.AQUA + "HcFaq"
				+ ChatColor.GOLD + "] " + ChatColor.BLUE;
		if (arg3.length == 0) {
			arg0.sendMessage(title);
			for (FAQSelection f : faqList)
				arg0.sendMessage(ChatColor.ITALIC + "" + ChatColor.AQUA
						+ "    " + f.getFaqName());
			arg0.sendMessage(title
					+ "Use /faq followed by the faq title to see more information");
		} else {
			String str = arg3[0];
			if (str.equalsIgnoreCase("reload") && arg0.isOp()) {
				loadConfig();
				arg0.sendMessage(title + "Reloaded faq files.");
				return true;
			}
			for (FAQSelection f : faqList) {
				if (f.getFaqName().equalsIgnoreCase(str)) {
					arg0.sendMessage(title + ChatColor.AQUA
							+ " Frequently asked questions about "
							+ ChatColor.BLUE + "" + ChatColor.BOLD
							+ f.getFaqName());
					for (String s : f.getFaqList())
						arg0.sendMessage(ChatColor.BLUE
								+ ChatColor
										.translateAlternateColorCodes('&', s));
					arg0.sendMessage(title);
					return true;
				}
			}
			arg0.sendMessage(title + "No faq's found for " + str);
		}
		return true;
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onEnable() {
		jp.getConfig().addDefault("faq.enabled", Boolean.TRUE);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		if (!jp.getConfig().getBoolean("faq.enabled"))
			return false;
		jp.getCommand("faq").setExecutor(this);
		loadConfig();
		return true;
	}
}
