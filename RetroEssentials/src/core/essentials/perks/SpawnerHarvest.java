package core.essentials.perks;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerHarvest implements Listener {
	@EventHandler
	public void playerTeleport(PlayerTeleportEvent pte) {
		if (pte.getCause() == TeleportCause.ENDER_PEARL) {
			pte.setCancelled(true);
			pte.getPlayer().getInventory()
					.addItem(new ItemStack(Material.ENDER_PEARL, 1));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void spawnerPlaced(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() != Material.MOB_SPAWNER
				|| event.isCancelled())
			return;
		ItemStack is = event.getItemInHand();
		if (is.getItemMeta() != null
				&& is.getItemMeta().getDisplayName() != null) {
			String str = is.getItemMeta().getDisplayName().split(" ")[0];
			CreatureSpawner cs = (CreatureSpawner) event.getBlockPlaced()
					.getState();
			cs.setCreatureTypeByName(str);
		}
	}

	@EventHandler
	public void enderpearlThrown(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (pie.getPlayer() != null
					&& pie.getPlayer().getItemInHand() != null
					&& pie.getClickedBlock().getType() == Material.MOB_SPAWNER
					&& pie.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
				pie.setCancelled(true);
				CreatureSpawner ms = (CreatureSpawner) pie.getClickedBlock()
						.getState();
				String type = ms.getSpawnedType().getName();
				ItemStack is = new ItemStack(Material.MOB_SPAWNER, 1);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(type + " spawner");
				is.setItemMeta(im);
				pie.getClickedBlock().setType(Material.AIR);
				pie.getClickedBlock()
						.getLocation()
						.getWorld()
						.dropItemNaturally(pie.getClickedBlock().getLocation(),
								is);
				ItemStack inHand = pie.getPlayer().getItemInHand();
				inHand.setAmount(inHand.getAmount() - 1);
				pie.getPlayer().setItemInHand(inHand);
			}
		}
	}
}
