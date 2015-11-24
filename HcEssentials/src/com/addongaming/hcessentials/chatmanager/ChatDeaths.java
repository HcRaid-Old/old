package com.addongaming.hcessentials.chatmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatDeaths implements Listener, CommandExecutor {
	ArrayList<String> allToggled = new ArrayList<String>();
	ArrayList<String> currentlyToggled = new ArrayList<String>();
	private final JavaPlugin jp;

	public ChatDeaths(JavaPlugin jp) {
		this.jp = jp;
		if (ChatManager.fileExists("chatdeaths.sav")) {
			try {
				allToggled = (ArrayList<String>) ChatManager
						.load("chatdeaths.sav");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (allToggled.contains(p.getName()))
				currentlyToggled.add(p.getName());
		}
	}

	private void logout(Player player) {
		if (currentlyToggled.contains(player.getName()))
			currentlyToggled.remove(player.getName());
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {

		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (currentlyToggled.contains(p.getName())
					|| allToggled.contains(p.getName())) {
				currentlyToggled.remove(p.getName());
				allToggled.remove(p.getName());
				arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
						+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
						+ "Death messages enabled.");
			} else {
				currentlyToggled.add(p.getName());
				allToggled.add(p.getName());
				arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
						+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
						+ "Death messages disabled.");
			}
			try {
				ChatManager.save(allToggled, "chatdeaths.sav");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDeath(PlayerDeathEvent event) {
		if (event.getDeathMessage() == null)
			return;
		Set<Player> offPlayers = new HashSet<Player>(Arrays.asList(Bukkit
				.getOnlinePlayers()));
		for (Iterator<Player> it = offPlayers.iterator(); it.hasNext();) {
			Player next = it.next();
			if (currentlyToggled.contains(next.getName())) {
				it.remove();
			}
		}
		for (Player pla : offPlayers)
			pla.sendMessage(event.getDeathMessage());
		event.setDeathMessage(null);
	}

	@EventHandler
	public void playerJoin(PlayerLoginEvent event) {
		if (allToggled.contains(event.getPlayer().getName()))
			currentlyToggled.add(event.getPlayer().getName());
	}

	@EventHandler
	public void playerKick(PlayerKickEvent event) {
		logout(event.getPlayer());
	}

	@EventHandler
	public void playerLogout(PlayerQuitEvent event) {
		logout(event.getPlayer());
	}

}
