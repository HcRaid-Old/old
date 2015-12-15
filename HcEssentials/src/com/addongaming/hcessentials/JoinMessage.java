package com.addongaming.hcessentials;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinMessage implements Listener, SubPlugin {
	private String description;
	private JavaPlugin jp;
	private String server;
	private List<String> firstJoin;

	public JoinMessage(String server, String description,
			List<String> firstJoin, JavaPlugin jp) {
		this.server = server;
		this.description = description;
		this.firstJoin = firstJoin;
		this.jp = jp;
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	@EventHandler
	public void commandPreprocess(PlayerCommandPreprocessEvent event) {

	}

	protected String getOnline() {
		return Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers();
	}

	protected int getStaff() {
		int count = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("hcraid.mod"))
				if (HcEssentials.essentials == null
						|| (HcEssentials.essentials != null && !HcEssentials.essentials
								.getUser(p).isHidden()))
					count++;
		}
		return count;
	}

	@EventHandler
	public void playerKick(PlayerKickEvent pke) {
		pke.setLeaveMessage("");
	}

	@EventHandler
	public void playerLogin(PlayerLoginEvent event) {
		if (event.getPlayer().getName().toLowerCase().startsWith("idminecraft")) {
			event.setKickMessage("Sorry, idminecraft accounts are not allowed on this server.");
			event.setResult(Result.KICK_OTHER);
		}
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent pje) {
		sendWelcomeMessage(pje.getPlayer());
		pje.setJoinMessage("");
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent pqe) {
		pqe.setQuitMessage("");
	}

	// TODO add colours to welcome message
	private void sendWelcomeMessage(final Player player) {
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						if (!player.isOnline())
							return;
						for (int i = 0; i < 15; i++)
							player.sendMessage("");
						player.sendMessage(ChatColor.GRAY
								+ "-----------------------------------------");
						player.sendMessage(ChatColor.GOLD
								+ "                     AddOnGaming                      ");

						player.sendMessage(ChatColor.DARK_BLUE
								+ "                        " + server + "   ");
						player.sendMessage(ChatColor.DARK_AQUA + "    "
								+ description);
						player.sendMessage(ChatColor.YELLOW + "  "
								+ getOnline() + " players online and "
								+ getStaff() + " Moderator(s)");
						player.sendMessage("");
						player.sendMessage(ChatColor.GRAY
								+ "    By playing on this server you agree to");
						player.sendMessage(ChatColor.GRAY
								+ " both /rules and the Terms at addongaming.com");
						player.sendMessage(ChatColor.GRAY
								+ "-----------------------------------------");
						// TODO Send the player the first join message
						// if (!player.hasPlayedBefore())
						// player.sendMessage(firstJoin
						// .toArray(new String[firstJoin.size()]));
					}
				}, 5l);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onEnable() {
		return true;
	}
}
