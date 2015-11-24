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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.logging.DataLog;
import com.earth2me.essentials.User;

public class ChatFilter implements Listener, CommandExecutor {
	public static ArrayList<String> filteredWords = new ArrayList<String>();
	private ArrayList<String> allToggled = new ArrayList<String>();
	private ArrayList<String> currentlyToggled = new ArrayList<String>();
	private final JavaPlugin jp;
	private ArrayList<String> wholeChatToggled = new ArrayList<String>();
	private DataLog dl;
	private final boolean filterSwears;

	@SuppressWarnings({ "unchecked" })
	public ChatFilter(JavaPlugin jp, boolean filterSwears) {
		this.filterSwears = filterSwears;
		dl = HcEssentials.getDataLogger().addLogger("ChatFilter");
		this.jp = jp;
		if (ChatManager.fileExists("chatfilter.sav")) {
			try {
				allToggled = (ArrayList<String>) ChatManager
						.load("chatfilter.sav");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (ChatManager.fileExists("chattoggle.sav")) {
			try {
				wholeChatToggled = (ArrayList<String>) ChatManager
						.load("chattoggle.sav");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (allToggled.contains(p.getName()))
				currentlyToggled.add(p.getName());
		}
		setupFilteredWords();
	}

	@Deprecated
	private String filt(String orig, String words, String replacement) {
		return orig.replaceAll("(?i)(" + words + ")", replacement);
	}

	private void logout(Player player) {
		if (currentlyToggled.contains(player.getName()))
			currentlyToggled.remove(player.getName());
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg1.getName().equalsIgnoreCase("chatfilter")) {
			if (arg0 instanceof Player) {
				Player p = (Player) arg0;
				if (currentlyToggled.contains(p.getName())
						|| allToggled.contains(p.getName())) {
					currentlyToggled.remove(p.getName());
					allToggled.remove(p.getName());
					arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
							+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
							+ "Chat Filter enabled.");
				} else {
					currentlyToggled.add(p.getName());
					allToggled.add(p.getName());
					arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
							+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
							+ "Chat Filter disabled.");
				}
				try {
					ChatManager.save(allToggled, "chatfilter.sav");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		} else if (arg1.getName().equalsIgnoreCase("chattoggle")) {
			if (arg0 instanceof Player) {
				Player p = (Player) arg0;
				if (wholeChatToggled.contains(p.getName())) {
					wholeChatToggled.remove(p.getName());
					arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
							+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
							+ "Chat enabled.");
				} else {
					wholeChatToggled.add(p.getName());
					arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
							+ "HcChat" + ChatColor.GOLD + "] " + ChatColor.BLUE
							+ "Chat disabled.");
				}
				try {
					ChatManager.save(wholeChatToggled, "chattoggle.sav");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerChat(final AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;
		if (ChatColor.stripColor(event.getMessage()).startsWith("[HcTeams]"))
			return;
		if (!this.filterSwears)
			return;
		if (ChatColor.stripColor(event.getMessage()).matches(
				"(?i)(connected with an ).{1,20}(using ).{1,10}")) {
			event.setCancelled(true);
			return;
		}
		if (wholeChatToggled.contains(event.getPlayer().getName())) {
			event.getPlayer()
					.sendMessage(
							ChatColor.GOLD
									+ "["
									+ ChatColor.GREEN
									+ "HcChat"
									+ ChatColor.GOLD
									+ "] "
									+ ChatColor.BLUE
									+ "You cannot send a message with your chat toggled off. Turn it on with /chattoggle");
			event.setCancelled(true);
			return;
		}
		if (ChatColor.stripColor(event.getMessage()).length() > 2
				&& ChatColor.stripColor(event.getMessage()).matches(".(/).*")) {
			StringBuilder sb = new StringBuilder(event.getMessage());
			sb.deleteCharAt(sb.indexOf("/") - 1);
			event.setMessage(sb.toString());
		}
		if (!event.getPlayer().isOp()
				|| !event.getPlayer().hasPermission("hcraid.owner")) {
			event.setMessage(event
					.getMessage()
					.replaceAll("(?i)donation", "payment")
					.replaceAll("(?i)donar|donor|donator|donater", "Subscriber")
					.replaceAll("(?i)donate", "pay"));
		}
		String currMessage = event.getMessage();
		event.setMessage(event
				.getMessage()
				.replaceAll(
						"(?i)( nigga | nigur | niggur | nigger | coon | niga | chinky | chink | chinkie | limey )",
						"****"));
		if (!currMessage.equalsIgnoreCase(event.getMessage())) {
			dl.logPlayer(event.getPlayer(), currMessage);
		}
		String filter = event.getMessage();
		filter = filt(filter, "faggot|gayest|dickhead|arsehole|motherfucker",
				"******");
		filter = filt(
				filter,
				"bitch|pussy|fucker|fucking|wanking|cunting|pricking|twating|bitchin|bitching|cocking|cocksuck|nigger|nigga|nige|niger|nigur|niggur|niggah|nigah|nigurh",
				"******");
		filter = filt(
				filter,
				"fuck|shit|cunt|arse|gaay|fukk|fucc|dick|cock|wank|twat|yolo|swag|porn",
				"****");
		filter = filt(filter, " fag | gay | dik ", "***");
		if (filter.equalsIgnoreCase(event.getMessage())) {
			for (Iterator<Player> it = event.getRecipients().iterator(); it
					.hasNext();)
				if (wholeChatToggled.contains(it.next().getName()))
					it.remove();
			return;
		}
		User p = HcEssentials.essentials.getUser(event.getPlayer());
		Set<Player> offPlayers = new HashSet<Player>(Arrays.asList(Bukkit
				.getOnlinePlayers()));
		for (Iterator<Player> it = offPlayers.iterator(); it.hasNext();) {
			Player play = it.next();
			if (play.hasPermission("HcRaid.mod"))
				continue;
			if (HcEssentials.essentials.getUser(play).isIgnoredPlayer(p)
					|| wholeChatToggled.contains(play.getName()))
				it.remove();
		}
		Set<Player> onPlayers = new HashSet<Player>();
		for (Iterator<Player> it = onPlayers.iterator(); it.hasNext();) {
			Player play = it.next();
			if (play.hasPermission("HcRaid.mod"))
				continue;
			if (HcEssentials.essentials.getUser(play).isIgnoredPlayer(p)
					|| wholeChatToggled.contains(play.getName()))
				it.remove();
		}
		for (Iterator<Player> it = offPlayers.iterator(); it.hasNext();) {
			Player next = it.next();
			if (currentlyToggled.contains(next.getName())) {
				onPlayers.add(next);
				it.remove();
			}
		}
		event.setMessage(event
				.getMessage()
				.replaceAll(
						"(?i)(nigga|nigur|niggur|nigger| coon |niga|chinky|chink|chinkie|limey)",
						"****"));
		final AsyncPlayerChatEvent filteredChat = new AsyncPlayerChatEvent(
				true, event.getPlayer(), filter, offPlayers);
		final AsyncPlayerChatEvent unfilteredChat = new AsyncPlayerChatEvent(
				true, event.getPlayer(), event.getMessage(), onPlayers);
		jp.getServer().getScheduler().runTaskAsynchronously(jp, new Runnable() {
			@Override
			public void run() {
				if (!unfilteredChat.isCancelled()) {
					for (Player p : unfilteredChat.getRecipients()) {
						p.sendMessage(String.format(event.getFormat(),
								unfilteredChat.getPlayer().getDisplayName(),
								unfilteredChat.getMessage()));
					}
				}
				if (!filteredChat.isCancelled()) {
					for (Player p : filteredChat.getRecipients()) {
						p.sendMessage(String.format(event.getFormat(),
								filteredChat.getPlayer().getDisplayName(),
								filteredChat.getMessage()));
					}
				}
			}
		});
		event.getRecipients().clear();
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

	private void setupFilteredWords() {
		String toSplit = "bitch|nigga|nigur|niggur|nigger|coon|niga|chinky|chink|chinkie|limey|faggot|gayest|dickhead|arsehole|motherfucker|pussy|fucker|fucking|wanking|cunting|pricking|twating|bitchin|bitching|cocking|cocksuck|nigger|nigga|nige|niger|nigur|niggur|niggah|nigah|nigurh|fuck|shit|cunt|arse|gaay|fukk|fucc|dick|cock|wank|twat|yolo|swag|porn|fag|gay|dik|dildo";
		for (String str : toSplit.split("[|]")) {
			ChatFilter.filteredWords.add(str);
		}
		System.out.println("[ChatFilter] " + ChatFilter.filteredWords.size()
				+ " words added to the chat filter.");

	}
}
