package com.addongaming.hcessentials.vote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.logging.DataLog;

public class VotingCommandOverride implements Listener {
	File file;
	ArrayList<String> voteList = new ArrayList<String>();
	DataLog dl;

	public VotingCommandOverride(File file) {
		this.file = file;
		dl = HcEssentials.getDataLogger().getLogger("Voting");
		loadVotes();
	}

	private void loadVotes() {
		if (!file.exists()) {
			try {
				dl.log("Attempting to create voting list file.");
				file.createNewFile();
				dl.log("Made voting list file");
			} catch (Exception e) {
				dl.log("Error making file. Please check logs. Message: "
						+ e.getMessage());
			}
		}
		dl.log("Loading vote list.");

		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine())
				voteList.add(scan.nextLine());
			dl.log("Loaded vote list. Amount loaded: " + voteList.size());
			scan.close();
		} catch (FileNotFoundException e) {
			dl.log("File not found exception. Check logs for details.");
			e.printStackTrace();
		}
	}

	public void saveVotingList() {
		try {
			dl.log("Writing voting url list to file");
			PrintWriter pw = new PrintWriter(file);
			for (String str : voteList) {
				dl.log("Writing string: " + str);
				pw.println(str);
			}
			dl.log("Finished writing voting url list to file");
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void command(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().startsWith("/vote")) {
			if (event.getPlayer().isOp()) {
				String msg = event.getMessage().toLowerCase();
				if (msg.startsWith("/vote add")
						|| msg.startsWith("/vote clear")) {
					if (msg.equalsIgnoreCase("/vote add")) {
						event.getPlayer().sendMessage(
								"Please use /vote add [message]");
					} else if (msg.equalsIgnoreCase("/vote clear")) {
						voteList.clear();
						event.getPlayer().sendMessage("Cleared voting list.");
					} else if (msg.startsWith("/vote add")) {
						String[] split = event.getMessage().split("[ ]");
						if (split.length == 2) {
							event.getPlayer().sendMessage(
									"Please use /vote add to see commands");
						} else {
							StringBuilder sb = new StringBuilder();
							for (int i = 2; i < split.length - 1; i++)
								sb.append(split[i] + " ");

							if (!checkURL(split[split.length - 1])) {
								voteList.add(ChatColor.BLUE
										+ ChatColor.translateAlternateColorCodes(
												'&',
												sb.toString()
														+ split[split.length - 1]));
								event.getPlayer().sendMessage(
										"Added coloured text!");
							} else {
								voteList.add(sb.toString()
										+ split[split.length - 1]);
								event.getPlayer().sendMessage("Added URL!");
							}
							saveVotingList();
						}
					}
					event.setCancelled(true);
					return;
				} else if (!msg.equalsIgnoreCase("/vote")) {
					event.getPlayer().sendMessage(
							ChatColor.RED + "--Not using override--");
					return;
				}
			}
			event.getPlayer().sendMessage(
					ChatColor.BLUE + "[::[" + ChatColor.GOLD + "HcVoting"
							+ ChatColor.BLUE + "]::] " + ChatColor.GREEN
							+ "Voting links!");
			for (int i = 1; i < voteList.size(); i++)
				event.getPlayer().sendMessage(voteList.get(i));
			event.setCancelled(true);
		}
	}

	private boolean checkURL(String string) {
		try {
			@SuppressWarnings("unused")
			URL url = new URL(string);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
