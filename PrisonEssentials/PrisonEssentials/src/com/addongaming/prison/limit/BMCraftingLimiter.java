package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.blackmarket.BMSmitherData;

public class BMCraftingLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Smithing"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void itemCrafted(CraftItemEvent bbe) {
		Player p = (Player) bbe.getViewers().iterator().next();
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		if (BMSmitherData.getData(bbe.getRecipe().getResult().getType()) != null) {
			DataReturn dr = BMSmitherData.canCraft(p, bbe.getRecipe()
					.getResult().getType());
			switch (dr) {
			case NOLEVEL:
				p.sendMessage(err + "You need a higher level to smith this.");
				bbe.setCancelled(true);
				clearResult(p);
				return;
			case NOPERM:
				p.sendMessage(err
						+ "You need to learn how to make this from the Smithing Teacher.");
				bbe.setCancelled(true);
				clearResult(p);
				return;
			case SUCCESS:
				return;
			default:
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void clearResult(Player p) {
		InventoryView inv = p.getOpenInventory();
		if (inv == null)
			return;
		Inventory i = inv.getTopInventory();
		if (i == null)
			return;
		if (i instanceof CraftingInventory) {
			((CraftingInventory) (i)).setResult(null);
		} else if (i instanceof PlayerInventory) {
			((PlayerInventory) (i)).setItem(0, null);
		}
		p.updateInventory();
	}
}
