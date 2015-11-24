package com.addongaming.hcessentials.limits;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class CommandLimiter implements SubPlugin, Listener {
	private JavaPlugin jp;

	public CommandLimiter(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("limit.command.enabled", true);
		fc.addDefault("limit.command.commands", new ArrayList<String>() {
			{
				this.add("/rank");
			}
		});
		fc.options().copyDefaults(true);
	}

	@Override
	public void onDisable() {
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void command(PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			for (String str : al)
				if (event.getMessage().toLowerCase()
						.startsWith(str.toLowerCase())) {
					event.getPlayer()
							.sendMessage(
									"Sorry this command is currently unavailable. Please try later.");
					event.setCancelled(true);
					return;
				}
		}
	}

	ArrayList<String> al = new ArrayList<String>();

	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("limit.command.enabled")) {
			al = (ArrayList<String>) jp.getConfig().getList(
					"limit.command.commands");
			jp.getServer().getPluginManager().registerEvents(this, jp);
			return true;
		}
		return false;
	}

}
