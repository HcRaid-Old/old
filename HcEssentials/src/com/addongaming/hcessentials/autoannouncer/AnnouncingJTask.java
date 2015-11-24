package com.addongaming.hcessentials.autoannouncer;

import java.util.List;

import me.spoony.chatlib.MessageSender;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AnnouncingJTask implements Runnable {
	private List<String> statements;
	private int index = 0;
	private String prefix;

	public AnnouncingJTask(String prefix, List<String> statements) {
		this.prefix = prefix;
		this.statements = statements;
	}

	@Override
	public void run() {
		try {	
			for (Player p : Bukkit.getOnlinePlayers())
				MessageSender.sendRawMessage(p, statements.get(index));
		} catch (Exception e) {

			for (Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						prefix + statements.get(index)));
		}
		index++;
		if (index >= statements.size())
			index = 0;
	}
}
