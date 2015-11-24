package com.addongaming.hcessentials.perks;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.utils.Utils;

public class ColouredNames implements SubPlugin, Listener {
	JavaPlugin jp;

	public ColouredNames(JavaPlugin jp) {
		this.jp = jp;
		jp.getConfig().set("colourednames.enabled", Boolean.TRUE);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("colourednames.enabled"))
			return false;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onNameTag(AsyncPlayerReceiveNameTagEvent e) {
		System.out.println("Player " + e.getPlayer().getName() + " Version "
				+ Utils.getPlayerVersion(e.getPlayer()));
		if (Utils.getPlayerVersion(e.getPlayer()) >= 47)
			return;
		String modified = null;
		if (e.getNamedPlayer().getName().equalsIgnoreCase("avengeuiwill")) {
			modified = ChatColor.YELLOW + e.getNamedPlayer().getName();
		} else if (e.getNamedPlayer().hasPermission("HcRaid.OWNER")) {
			modified = ChatColor.GOLD + e.getNamedPlayer().getName();
		} else if (e.getNamedPlayer().hasPermission("HcRaid.ADMIN")) {
			modified = ChatColor.AQUA + e.getNamedPlayer().getName();
		} else if (e.getNamedPlayer().hasPermission("HcRaid.MOD")) {
			modified = ChatColor.BLUE + e.getNamedPlayer().getName();
		} else if (e.getNamedPlayer().hasPermission("HcRaid.VIP")) {
			modified = ChatColor.DARK_GREEN + e.getNamedPlayer().getName();
		} else
			modified = e.getNamedPlayer().getName();
		if (modified != null && modified.length() > 0
				&& modified.length() <= 16)
			e.setTag(modified);
	}
}
