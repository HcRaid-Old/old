package core.essentials.perks;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

import core.essentials.objects.Config;

public class TntHandler implements Listener {
	@EventHandler
	public void tntHit(BlockDamageEvent bde) {
		if (bde.getBlock().getType() == Material.TNT) {
			if (bde.getPlayer().hasPermission(Config.tntPerm)) {
				if (bde.getItemInHand() != null
						&& bde.getItemInHand().getType() == Config.tntDis) {
					bde.setInstaBreak(true);
					return;
				}
			}
			bde.getBlock().setType(Material.AIR);
			bde.getBlock()
					.getWorld()
					.spawnEntity(bde.getBlock().getLocation(),
							EntityType.PRIMED_TNT);
		}
	}

}
