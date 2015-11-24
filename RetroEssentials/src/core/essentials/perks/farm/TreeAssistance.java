package core.essentials.perks.farm;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import core.essentials.objects.Config;

public class TreeAssistance implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void logCut(BlockBreakEvent bbe) {
		if (bbe.isCancelled()
				|| !bbe.getPlayer().hasPermission("hcraid.creeper")
				|| Config.bypass.contains(bbe.getPlayer().getName()))
			return;
		if (bbe.getBlock().getType() == Material.LOG) {
			for (int x = bbe.getBlock().getX() - 1; x < bbe.getBlock().getX() + 1; x++) {
				for (int y = bbe.getBlock().getY() - 3; y < bbe.getBlock()
						.getY() + 10; y++) {
					for (int z = bbe.getBlock().getZ() - 1; z < bbe.getBlock()
							.getZ() + 1; z++) {
						Block b = bbe.getBlock().getWorld().getBlockAt(x, y, z);
						if (b.getType() == Material.LOG) {
							b.breakNaturally();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void appleDrop(ItemSpawnEvent ise) {
		if (ise.getEntityType() == EntityType.DROPPED_ITEM) {
			if (ise.getEntity().getItemStack().getType() == Material.APPLE
					&& ise.getEntity().getPickupDelay() == 10) {
				ise.setCancelled(true);
			}
		}
	}
}
