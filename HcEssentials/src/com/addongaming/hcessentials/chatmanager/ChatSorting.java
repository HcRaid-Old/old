package com.addongaming.hcessentials.chatmanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatSorting implements Listener {

	public static HashMap<String, String> AntiRepeat = new HashMap<String, String>();
	private final List<String> swears = new ArrayList<String>();
	private JavaPlugin jp;

	public ChatSorting(JavaPlugin jp) {
		this.jp = jp;
		try {
			Scanner scanner = new Scanner(new File(jp.getDataFolder()
					+ File.separator + "chatmanager" + File.separator
					+ "bannedWords.txt"));
			while (scanner.hasNextLine())
				swears.add(scanner.nextLine());
			scanner.close();
			/**
			 * Path filePath = (Path) new File(jp.getDataFolder() +
			 * File.separator + "chatmanager" + File.separator +
			 * "bannedWords.txt") .toPath(); Charset charset =
			 * Charset.defaultCharset(); List<String> stringList; stringList =
			 * Files.readAllLines((java.nio.file.Path) filePath, charset);
			 * String[] stringArray = stringList.toArray(new String[stringList
			 * .size()]);
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("HcRaid.ADMIN"))
			e.setMessage(ChatColor.YELLOW + e.getMessage());
		else if (p.hasPermission("HcRaid.MOD"))
			e.setMessage(ChatColor.RED + e.getMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void AntiRepeatingMessage(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage();
		if (p.hasPermission("HcRaid.MOD")
				|| e.getMessage().startsWith("[FLTRD]")) {
			return;
		}

		if (checkRepeat(e.getPlayer(), e.getMessage())) {
			e.setCancelled(true);
			return;
		}
		if (lagComplaining(e.getMessage(), e.getPlayer())) {
			e.setCancelled(true);
			return;
		}
		if (countSwears(e.getMessage(), e.getPlayer())) {
			e.setCancelled(true);
			return;
		}
		if (!e.getPlayer().isOp()
				&& !e.getPlayer().hasPermission("HcRaid.owner"))
			if (checkCaps(e.getMessage())) {
				e.getPlayer()
						.sendMessage(
								ChatColor.GOLD
										+ "["
										+ ChatColor.GREEN
										+ "HcChat"
										+ ChatColor.GOLD
										+ "] "
										+ ChatColor.RED
										+ " Please refrain from spamming with capital letters.");
				e.setCancelled(true);
				return;
			}
	}

	private boolean checkCaps(String message) {
		String s = message;
		int upperCaseCount = 0;
		for (int i = 0; i < s.length(); i++) {
			for (char c = 'A'; c <= 'Z'; c = (char) (c + '\001')) {
				if (s.charAt(i) == c) {
					upperCaseCount++;
				}
			}
		}
		if (upperCaseCount > 9) {
			return true;
		}
		return false;

	}

	private boolean checkRepeat(Player p, String message) {
		boolean retu = false;
		if (AntiRepeat.containsKey(p.getName())) {
			String compare = (String) AntiRepeat.get(p.getName());
			if (message.startsWith(compare)) {
				if (message.length() > 15) {
					p.sendMessage(ChatColor.GOLD
							+ "["
							+ ChatColor.GREEN
							+ "HcChat"
							+ ChatColor.GOLD
							+ "] "
							+ ChatColor.RED
							+ "Please reframe from posting the same message twice or having similarities!");
					retu = true;
				} else if (message.equalsIgnoreCase(compare)) {
					p.sendMessage(ChatColor.GOLD
							+ "["
							+ ChatColor.GREEN
							+ "HcChat"
							+ ChatColor.GOLD
							+ "] "
							+ ChatColor.RED
							+ "Please reframe from posting the same message twice or having similarities!");
					retu = true;
				}
			}
		}
		AntiRepeat.put(p.getName(), message);
		return retu;
	}

	private boolean countSwears(String message, Player player) {
		int swear = 0;
		if (message.contains(" "))
			for (String str : message.split("[ ]"))
				if (swears.contains(str))
					swear++;
		if (swear > 4) {
			player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
					+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.RED
					+ "Hey! Please calm down with all those swears.");
			return true;
		}
		return false;
	}

	private boolean lagComplaining(String msg, Player player) {

		if ((msg.matches("(?i)[l]{1,}[a]{1,}[g]{1,}.*{0,}"))
				&& (!player.hasPermission("HcRaid.LagKick.Bypass"))) {
			player.sendMessage(ChatColor.RED
					+ "Lag! LAG! OMG LAG! Lag isn't something you need to tell the whole server, use /ping to test lag.");

			return true;
		} else if (msg.contains("(?i)server is lag")) {
			player.sendMessage(ChatColor.RED
					+ "Lag! LAG! OMG LAG! Lag isn't something you need to tell the whole server, use /ping to test lag.");

			return true;
		}
		return false;
	}

}
