package com.addongaming.prison.core;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.player.PrisonerManager;

public class ChatHandler implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void chat(AsyncPlayerChatEvent event) {
		PlayerClasses pc = PrisonerManager.getInstance()
				.getPrisonerInfo(event.getPlayer().getName()).getPlayerClass();
		ChatColor cc = ChatColor.GRAY;
		if (pc == PlayerClasses.GUARD)
			cc = ChatColor.BLUE;
		event.setFormat("<" + cc + pc.toText() + " " + ChatColor.RESET
				+ event.getPlayer().getDisplayName() + "> %2$s");
	}
}
