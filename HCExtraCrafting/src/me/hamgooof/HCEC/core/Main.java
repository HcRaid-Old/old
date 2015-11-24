package me.hamgooof.HCEC.core;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void prepareItem(final InventoryClickEvent event) {
		if (!(event.getInventory() instanceof CraftingInventory))
			return;
		final CraftingInventory ci = (CraftingInventory) event.getInventory();
		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						boolean flag = false;
						int goldApples = 0;
						for (ItemStack is : ci.getContents()) {
							if (is.getTypeId() == 322) {
								goldApples += 1;
							} else if (is.getDurability() == (short) 8193
									|| is.getDurability() == (short) 8257
									|| is.getDurability() == (short) 8225) {
								flag = true;
							}
						}
						if (flag && goldApples >= 8) {
							ItemStack is = new ItemStack(322);
							is.setDurability((short) 1);
							ci.setResult(is);
							Player p = (Player) event.getWhoClicked();
							p.updateInventory();
						}
					}
				}, 1);
	}
}
