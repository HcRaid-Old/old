package com.addongaming.hcessentials.serverlogger;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.utils.AtomicArrayList;

public class LogListenerDB implements Listener {
	private final JavaPlugin jp;
	private DataLog dl;
	private ServerLoggingDB dh;
	private final AtomicArrayList<PlayerChat> playerChat = new AtomicArrayList<PlayerChat>();
	private final AtomicArrayList<PlayerCmd> playerCmd = new AtomicArrayList<PlayerCmd>();
	/*
	 * private final AtomicArrayList<PlayerTp> playerTp = new
	 * AtomicArrayList<PlayerTp>();
	 */
	private final AtomicArrayList<PlayerLoginout> playerLog = new AtomicArrayList<PlayerLoginout>();

	public LogListenerDB(JavaPlugin jp, ServerLoggingDB dh) {
		this.dh = dh;
		this.jp = jp;
		setupScheduler();
	}

	private void setupScheduler() {
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						final AtomicArrayList<PlayerChat> playerChatRun = playerChat
								.clone();
						final AtomicArrayList<PlayerCmd> playerCmdRun = playerCmd
								.clone();
						/*
						 * final AtomicArrayList<PlayerTp> playerTpRun =
						 * playerTp .clone();
						 */
						final AtomicArrayList<PlayerLoginout> playerLoginoutRun = playerLog
								.clone();
						playerChat.clear();
						playerCmd.clear();
						/* playerTp.clear(); */
						playerLog.clear();
						jp.getServer().getScheduler()
								.runTaskAsynchronously(jp, new Runnable() {

									@Override
									public void run() {
										dh.insertChatIntoTable(playerChatRun);
										dh.insertCMDIntoTable(playerCmdRun);
										/* dh.insertTPIntoTable(playerTpRun); */
										dh.insertLogIntoTable(playerLoginoutRun);
									}
								});
					}
				}, 20 * 60, 20 * 10);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		String msg = ChatColor.stripColor(e.getMessage());
		long time = System.currentTimeMillis();
		playerChat.add(new PlayerChat(msg, e.getPlayer().getUniqueId(), time));
	}

	@EventHandler
	public void onPlayerCMD(PlayerCommandPreprocessEvent e) {
		String msg = e.getMessage();
		long time = System.currentTimeMillis();
		playerCmd.add(new PlayerCmd(msg, e.getPlayer().getUniqueId(), time));
	}

	/*
	 * @EventHandler public void onPlayerTeleport(PlayerTeleportCommandEvent e)
	 * { int userId = UUIDSystem.getInstance()
	 * .getId(e.getPlayer().getUniqueId()); long time = new Date().getTime();
	 * int toLocY = e.getTo().getBlockY(); int toLocX = e.getTo().getBlockX();
	 * int toLocZ = e.getTo().getBlockZ(); playerTp.add(new PlayerTp(userId,
	 * toLocX, toLocY, toLocZ, time)); }
	 */

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		String msg = "in";
		long time = System.currentTimeMillis();
		playerLog
				.add(new PlayerLoginout(e.getPlayer().getUniqueId(), msg, time));
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		String msg = "out";
		long time = System.currentTimeMillis();
		playerLog
				.add(new PlayerLoginout(e.getPlayer().getUniqueId(), msg, time));
	}

}