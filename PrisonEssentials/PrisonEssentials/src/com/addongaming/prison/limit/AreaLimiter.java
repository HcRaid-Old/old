package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.utilities.AreaData;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;

public class AreaLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Areas"
			+ ChatColor.GRAY + "] " + ChatColor.GRAY;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void regionEntered(RegionEnterEvent bbe) {
		Player p = bbe.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		DataReturn dr = AreaData.hasPermission(p, bbe.getRegion().getId());
		AreaData ad = AreaData.getAreaByName(bbe.getRegion().getId());
		switch (dr) {
		case NOLEVEL:
			p.sendMessage(err + "You need a higher level to enter here.");
			p.teleport(ad.getSafeLocation());
			return;
		case NOPERM:
			p.sendMessage(err
					+ "You need to learn how to enter here from the Area Teacher");
			p.sendMessage(err + "Island: " + ChatColor.AQUA + ad.getPrison()
					+ ChatColor.GRAY + " Area: " + ChatColor.AQUA
					+ ad.getName());
			p.teleport(ad.getSafeLocation());
			return;
		case SUCCESS:
			return;
		default:
			return;
		}
	}
}
