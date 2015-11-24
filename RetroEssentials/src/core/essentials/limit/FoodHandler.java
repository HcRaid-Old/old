package core.essentials.limit;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.enums.EFood;

public class FoodHandler implements Listener {
	public FoodHandler(JavaPlugin jp) {
		this.jp = jp;
	}

	private final JavaPlugin jp;

	@EventHandler
	public void foodEaten(FoodLevelChangeEvent event) {
		event.setFoodLevel(2);
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		event.getPlayer().setFoodLevel(2);
	}

	@EventHandler
	public void playerRespawn(final PlayerRespawnEvent pre) {
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {
					@Override
					public void run() {
						pre.getPlayer().setFoodLevel(2);
					}
				}, 0l);
	}

	@EventHandler
	public void playerEat(PlayerItemConsumeEvent event) {
		if (EFood.isFood(event.getItem().getType())) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void playerHealthIncrease(EntityRegainHealthEvent event) {
		if (event.getRegainReason().name()
				.equalsIgnoreCase(RegainReason.REGEN.name())
				|| event.getRegainReason().name()
						.equalsIgnoreCase(RegainReason.MAGIC.name())
				|| event.getRegainReason().name()
						.equalsIgnoreCase(RegainReason.SATIATED.name())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerEatInteract(PlayerInteractEvent pie) {
		if (pie.getItem() == null || pie.getItem().getType() == null)
			return;
		if (pie.getAction() == Action.LEFT_CLICK_AIR
				|| pie.getAction() == Action.LEFT_CLICK_BLOCK
				|| pie.getAction() == Action.PHYSICAL)
			return;
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& pie.getClickedBlock().getType() == Material.CHEST)
			return;
		if (EFood.isFood(pie.getItem().getType())) {
			EFood ef = EFood.getByName(pie.getItem().getType());
			Damageable d = (Damageable) pie.getPlayer();

			if (d.getHealth() == 20) {
				pie.setCancelled(true);
				return;
			}
			pie.getPlayer().setHealth(
					(d.getHealth() + ef.getHealing() > 20 ? 20 : d.getHealth()
							+ ef.getHealing()));
			final Player p = pie.getPlayer();
			final ItemStack current = pie.getPlayer().getItemInHand();
			if (current.getType() == Material.MUSHROOM_SOUP) {
				pie.getPlayer().getInventory()
						.addItem(new ItemStack(Material.BOWL, 1));
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								p.updateInventory();

							}
						}, 0);
			}
			pie.setCancelled(true);
			if (current.getAmount() > 1) {
				current.setAmount(current.getAmount() - 1);
				pie.getPlayer().setItemInHand(current);
			} else {
				pie.getPlayer().setItemInHand(null);
			}

		}

	}
}
