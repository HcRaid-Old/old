package core.hc16;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	Material[] disallowedChests = { Material.LEASH, Material.IRON_BARDING,
			Material.GOLD_BARDING, Material.DIAMOND_BARDING, Material.NAME_TAG };

	@EventHandler
	public void inventoryOpen(InventoryOpenEvent event) {
		for (Material m : disallowedChests)
			while (event.getInventory().contains(m))
				event.getInventory().remove(m);
	}

	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent cse) {
		if (cse.getEntityType().equals(EntityType.HORSE))
			cse.setCancelled(true);
	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			for (PotionEffect pe : p.getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
					if (pe.getAmplifier() == 0) {
						event.setDamage(event.getDamage() * 0.63);
					} else if (pe.getAmplifier() == 1) {
						event.setDamage(event.getDamage() * 0.63);
					}
				}
			}
		}
	}
}
