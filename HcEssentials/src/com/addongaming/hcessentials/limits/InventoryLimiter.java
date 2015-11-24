package com.addongaming.hcessentials.limits;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryLimiter implements Listener {
	private List<InventoryType> list;
	private String errorMsg = ChatColor.RED
			+ "Sorry, you cannot access this interface.";

	public InventoryLimiter(List<InventoryType> list) {
		this.list = list;
	}

	@EventHandler
	public void inveOpen(InventoryOpenEvent event) {
		if (event.getPlayer().isOp())
			return;
		for (InventoryType it : list)
			if (event.getView().getType() == it) {
				Player p = (Player) (event.getPlayer());
				p.sendMessage(errorMsg);
				event.setCancelled(true);
				return;
			}
	}
}
