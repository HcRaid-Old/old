package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.utilities.InventoryData;

public class InventoryLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Inventory"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMined(InventoryOpenEvent bbe) {
		Player p = (Player) bbe.getViewers().iterator().next();
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		if (InventoryData.isInData(bbe.getInventory().getType())) {
			DataReturn dr = InventoryData.canOpenInventory(p, bbe
					.getInventory().getType());
			switch (dr) {
			case NOLEVEL:
				p.sendMessage(err + "You need a higher level to use this.");
				bbe.setCancelled(true);
				return;
			case NOPERM:
				p.sendMessage(err
						+ "You need to learn how to use this from the Inventory Teacher.");
				bbe.setCancelled(true);
				return;
			case SUCCESS:
				return;
			default:
				return;
			}
		}
	}
}
