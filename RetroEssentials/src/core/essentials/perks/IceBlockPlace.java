package core.essentials.perks;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class IceBlockPlace implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void blockPlace(BlockPlaceEvent bpe) {
		if (bpe.isCancelled())
			return;
		if (bpe.getBlockPlaced().getType() == Material.ICE) {
			bpe.getBlockPlaced().setType(Material.WATER);
		}
	}
}
