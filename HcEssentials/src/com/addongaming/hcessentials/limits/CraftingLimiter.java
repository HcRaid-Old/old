package com.addongaming.hcessentials.limits;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.limits.objects.LimitedBlock;

public class CraftingLimiter implements Listener {
	private final JavaPlugin jp;

	private final List<LimitedBlock> list;

	public CraftingLimiter(JavaPlugin jp, List<LimitedBlock> list) {
		this.list = list;
		this.jp = jp;
		for (LimitedBlock lb : list) {
			removeRecipe(lb);
		}
	}

	private void removeRecipe(LimitedBlock lb) {
		for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext();) {
			if (lb.matches(it.next().getResult())) {
				it.remove();
				return;
			}
		}
	}

	@EventHandler
	public void itemCraft(final PrepareItemCraftEvent event) {
		for (LimitedBlock lb : list) {
			if (lb.matches(event.getRecipe().getResult())) {
				final Player p = (Player) event.getViewers().get(0);
				p.sendMessage(lb.getMessage());
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								event.getInventory().setItem(0,
										new ItemStack(Material.AIR));
								p.updateInventory();
							}
						}, 0l);
				return;
			}
		}

	}

}
