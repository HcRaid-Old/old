package core.essentials.limit;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import core.essentials.enums.EEntity;
import core.essentials.objects.Config;
import core.essentials.objects.ItemDrop;

public class EntityHandler implements Listener {

	EntityType[] allowed = { EntityType.ARROW, EntityType.BOAT,
			EntityType.CHICKEN, EntityType.COW, EntityType.CREEPER,
			EntityType.DROPPED_ITEM, EntityType.EGG, EntityType.GHAST,
			EntityType.GIANT, EntityType.MINECART, EntityType.MINECART_CHEST,
			EntityType.MINECART_FURNACE, EntityType.PIG, EntityType.PIG_ZOMBIE,
			EntityType.PLAYER, EntityType.PRIMED_TNT, EntityType.SHEEP,
			EntityType.SKELETON, EntityType.SLIME, EntityType.SNOWBALL,
			EntityType.SPIDER, EntityType.SPIDER, EntityType.SQUID,
			EntityType.ZOMBIE, };
	@SuppressWarnings("unused")
	private final String negTitle = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "RetroPvP" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
	private final String posTitle = ChatColor.AQUA + "[" + ChatColor.GOLD
			+ "RetroPvP" + ChatColor.AQUA + "] " + ChatColor.RESET;

	@EventHandler
	public void leaveEntity(PlayerInteractEvent pie) {
		if (!pie.getPlayer().isInsideVehicle())
			return;
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = pie.getClickedBlock();
			if (b.getType() == Material.STONE_BUTTON
					|| b.getType() == Material.LEVER)
				return;
			pie.getPlayer().getVehicle().eject();
		} else if (pie.getAction() == Action.RIGHT_CLICK_AIR)
			pie.getPlayer().getVehicle().eject();
	}

	@EventHandler
	public void initPlayerjoin(PlayerJoinEvent pje) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(pje.getPlayer().getName());
		if (op == null || !op.hasPlayedBefore()) {
			PlayerInventory pi = pje.getPlayer().getInventory();
			pi.addItem(new ItemStack(Material.STONE_AXE));
			pi.addItem(new ItemStack(Material.STONE_HOE));
			pi.addItem(new ItemStack(Material.STONE_PICKAXE));
			pi.addItem(new ItemStack(Material.STONE_SPADE));
			pi.addItem(new ItemStack(Material.IRON_SWORD));
			pi.addItem(new ItemStack(Material.PORK));
			pi.addItem(new ItemStack(Material.PORK));
			pi.addItem(new ItemStack(Material.LEATHER_BOOTS));
			pi.addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
			pi.addItem(new ItemStack(Material.LEATHER_HELMET));
			pi.addItem(new ItemStack(Material.LEATHER_LEGGINGS));
			pje.getPlayer()
					.sendMessage(
							posTitle
									+ " Here is your starter kit! Be careful as you only get one!");
		}
	}

	@EventHandler
	public void sheepRegrow(SheepRegrowWoolEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void sheepRegrow(PlayerShearEntityEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void stopDropXpDeath(EntityDeathEvent ede) {
		ede.setDroppedExp(0);
	}

	@EventHandler
	public void entityDestroy(EntityChangeBlockEvent event) {
		if (event.getEntityType().equals(EntityType.PRIMED_TNT)
				|| event.getEntityType().equals(EntityType.PLAYER)
				|| event.getEntityType().equals(EntityType.CREEPER)
				|| event.getEntityType().equals(EntityType.FALLING_BLOCK))
			event.setCancelled(false);
		else
			event.setCancelled(true);
	}

	@EventHandler
	public void entitySpecialDrops(EntityDeathEvent eve) {
		if (EEntity.isEntity(eve.getEntityType())) {
			eve.getDrops().clear();
			EEntity ee = EEntity.getEntity(eve.getEntityType());
			for (ItemDrop id : ee.getDrops()) {
				float f = new Random().nextFloat();
				if (id.getChance() > f)
					eve.getDrops().add(id.getItemStack());
			}
		}
	}

	@EventHandler
	public void stopDropXpBlock(BlockBreakEvent bbe) {
		bbe.setExpToDrop(0);
	}

	@EventHandler
	public void sheepSmacked(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Sheep) {
			Sheep sheep = (Sheep) event.getEntity();
			if (!sheep.isSheared()) {
				DyeColor dc = sheep.getColor();
				ItemStack is = new ItemStack(Material.WOOL, 1, dc.getWoolData());
				int ran = new Random().nextInt(20);
				if (ran >= 15)
					is.setAmount(2);
				sheep.getWorld().dropItemNaturally(sheep.getLocation(), is);
				sheep.setSheared(true);
			}
		}
	}

	@EventHandler
	public void playerSpeed(PlayerMoveEvent event) {
		if (event.getPlayer() != null)
			event.getPlayer().setWalkSpeed(0.165f);
	}

	/*
	 * @EventHandler public void playerWorldChange(PlayerChangedWorldEvent
	 * event) { if (event.getPlayer() != null)
	 * event.getPlayer().setWalkSpeed(0.25f); }
	 */

	@EventHandler(priority = EventPriority.MONITOR)
	public void entitySpawn(CreatureSpawnEvent ese) {
		if (ese.getEntityType() == EntityType.PLAYER)
			return;
		if (ese.getSpawnReason() == SpawnReason.BREEDING) {
			ese.setCancelled(true);
			return;
		}
		if (ese.getEntity() instanceof Ageable) {
			Ageable a = (Ageable) ese.getEntity();
			if (!a.isAdult())
				a.setAdult();
		}
		if (ese.getEntityType() == EntityType.ZOMBIE) {
			Zombie zo = (Zombie) ese.getEntity();
			if (zo.isVillager() || zo.isBaby()) {
				zo.setVillager(false);
				zo.setBaby(false);
				return;
			}
		}
		ese.getEntity().setCanPickupItems(false);
		ese.getEntity().getEquipment()
				.setArmorContents(new ItemStack[] { null, null, null, null });

		if (ese.getEntity().getType().equals(EntityType.SKELETON)) {
			Skeleton skele = (Skeleton) ese.getEntity();
			skele.setSkeletonType(SkeletonType.NORMAL);
			ese.getEntity().getEquipment()
					.setItemInHand(new ItemStack(Material.BOW, 1));
		}
		for (EntityType et : allowed) {
			if (ese.getEntityType() == et) {
				return;
			}
		}
		ese.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void entityDamage(EntityDamageEvent ese) {
		if (!(ese.getEntity() instanceof LivingEntity))
			return;
		LivingEntity le = (LivingEntity) ese.getEntity();
		if (ese.getEntity() instanceof Player) {
			if (!le.getCanPickupItems())
				le.setCanPickupItems(true);
			return;
		}
		if (le.getType() == EntityType.ZOMBIE) {
			Zombie zo = (Zombie) ese.getEntity();
			if (zo.isVillager()) {
				zo.setVillager(true);
				return;
			}
		}
		le.setCanPickupItems(false);
		boolean flag = false;
		for (ItemStack is : le.getEquipment().getArmorContents()) {
			if (is != null && is.getType() != Material.AIR) {
				flag = true;
			}
		}
		if (flag) {
			le.getEquipment().clear();
			if (le.getType() == EntityType.SKELETON)
				le.getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
		}
	}

	@EventHandler
	public void playerDamageEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (p.getGameMode() == GameMode.CREATIVE) {
				if (!(Config.bypass.contains(p.getName().toLowerCase()) && Config.fullBypass
						.contains(p.getName().toLowerCase()))) {
					event.setCancelled(true);
				}
			}
		}
	}
}
