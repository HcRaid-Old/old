package com.addongaming.hcessentials.afk;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class AFKKicker implements SubPlugin, Listener {
	private final JavaPlugin jp;
	private int kickTime;
	private String bypassPerm;
	private HashMap<String, AFKRunnable> runnableMap = new HashMap<String, AFKRunnable>();

	public AFKKicker(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {
		for (AFKRunnable afk : runnableMap.values())
			jp.getServer().getScheduler().cancelTask(afk.getTaskId());
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("afk.enabled", true);
		fc.addDefault("afk.kicktimer", 15);
		fc.addDefault("afk.bypassperm", "hcraid.admin");
		fc.options().copyDefaults(true);
		jp.saveConfig();
		if (!jp.getConfig().getBoolean("afk.enabled"))
			return false;
		this.kickTime = jp.getConfig().getInt("afk.kicktimer");
		this.bypassPerm = jp.getConfig().getString("afk.bypassperm");
		jp.getServer().getPluginManager().registerEvents(this, jp);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission(bypassPerm)) {
				AFKRunnable afk = new AFKRunnable(player, kickTime);
				afk.setTaskId(jp.getServer().getScheduler()
						.scheduleSyncRepeatingTask(jp, afk, 20L, 20 * 60));
				runnableMap.put(player.getName(), afk);
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission(bypassPerm)) {
			AFKRunnable afk = new AFKRunnable(player, kickTime);
			afk.setTaskId(jp.getServer().getScheduler()
					.scheduleSyncRepeatingTask(jp, afk, 20L, 20 * 60));
			runnableMap.put(player.getName(), afk);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerLeave(PlayerQuitEvent event) {
		if (runnableMap.containsKey(event.getPlayer().getName())) {
			AFKRunnable afk = runnableMap.get(event.getPlayer().getName());
			jp.getServer().getScheduler().cancelTask(afk.getTaskId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerchat(AsyncPlayerChatEvent event) {
		if (runnableMap.containsKey(event.getPlayer().getName())) {
			runnableMap.get(event.getPlayer().getName()).resetCounter();
		}
	}
}
