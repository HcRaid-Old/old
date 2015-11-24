package core.essentials.limit;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CombatHandler implements Listener {
	Material[] armour = { Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
			Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
			Material.IRON_BOOTS, Material.IRON_CHESTPLATE,
			Material.IRON_HELMET, Material.IRON_LEGGINGS,
			Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS,
			Material.GOLD_BOOTS, Material.GOLD_CHESTPLATE,
			Material.GOLD_HELMET, Material.GOLD_LEGGINGS,
			Material.DIAMOND_BOOTS, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_HELMET, Material.DIAMOND_LEGGINGS };

	@EventHandler
	public void checkDefense(PlayerInteractEvent event) {
		if (event.getPlayer() != null
				&& (event.getAction() != Action.RIGHT_CLICK_AIR || event
						.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;
		Material[] weps = { Material.WOOD_SWORD, Material.STONE_SWORD,
				Material.IRON_SWORD, Material.GOLD_SWORD,
				Material.DIAMOND_SWORD };
		if (event.getPlayer().getItemInHand() != null) {
			for (Material m : weps)
				if (event.getPlayer().getItemInHand().getType().equals(m)) {
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					return;
				}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerEquipArmour(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| event.getAction().equals(Action.RIGHT_CLICK_AIR))
			if (Arrays.asList(armour)
					.contains(player.getItemInHand().getType())) {
				event.setCancelled(true);
				player.updateInventory();
			}
	}

	@EventHandler
	public void playerAnimation(PlayerAnimationEvent event) {
	}

	@EventHandler
	public void skeleShoot(EntityShootBowEvent event) {
		if (event.getEntityType() == EntityType.SKELETON) {
			if (new Random().nextInt(10) > 5)
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void entityDamage(
			org.bukkit.event.entity.EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			if (event.getDamage() > 1)
				event.setDamage(event.getDamage() / 2);
			event.getDamager().remove();
		} else if (!(event.getDamager() instanceof Player)
				&& event.getEntity() instanceof Player) {
			// Attacker isn't a player, reciever is
			event.setDamage((event.getDamage() * (new Random().nextDouble() + 1)));
		} else if (event.getDamager() instanceof Player) {
			if (!event.getDamager().isOnGround()) {
				event.setDamage((double) new Random().nextInt(3));
			}
		}
	}

	@EventHandler
	public void arrowFire(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			ItemStack is = p.getItemInHand();
			if (is.getType().equals(Material.BOW)) {
				is.setDurability((short) 0);
				p.setItemInHand(is);
			}
		}
	}
}
