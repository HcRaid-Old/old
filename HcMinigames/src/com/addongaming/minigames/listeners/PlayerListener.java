package com.addongaming.minigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.scheduling.DelayedReload;
import com.addongaming.minigames.management.scheduling.LogoutScheduler;
import com.addongaming.minigames.management.scheduling.RespawnScheduler;
import com.addongaming.minigames.management.weapon.PhysicalWeapon;
import com.addongaming.minigames.management.weapon.Weapon;
import com.addongaming.minigames.minigames.ArenaGame;
import com.addongaming.minigames.minigames.ArenaGame.Status;

public class PlayerListener implements Listener {
	private HcMinigames minigames;

	public PlayerListener(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getServer().getPluginManager()
				.registerEvents(this, minigames);
	}

	@EventHandler
	public void weaponUse(PlayerInteractEvent event) {
		/**
		 * if (event.getAction() == Action.RIGHT_CLICK_AIR && event.hasItem()) {
		 * if (minigames.getManagement().getWeaponManagement()
		 * .isWeapon(event.getItem())) { System.out.println("Weapon"); ArenaGame
		 * ag = minigames.getManagement().getArenaManagement()
		 * .getGame(event.getPlayer().getName()); if (ag == null &&
		 * !event.getPlayer().isOp()) return;
		 * minigames.getManagement().getWeaponManagement()
		 * .useWeapon(event.getItem(), ag, event.getPlayer()); } else
		 * System.out.println("Not Weapon"); }
		 */
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (event.getTo().getBlockY() < 0
				&& event.getPlayer().getHealth() > 0.1d)
			event.getPlayer().setHealth(0.0);
		if (ag != null)
			event.setCancelled(!ag.playerMove(event.getFrom(), event.getTo(),
					event.getPlayer()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void weaponSwitch(PlayerItemHeldEvent event) {
		minigames
				.getManagement()
				.getWeaponManagement()
				.switchWeapon(event.getPlayer(), event.getPreviousSlot(),
						event.getNewSlot());
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent event) {
		EntityDamageEvent ev = event.getEntity().getLastDamageCause();
		event.setDroppedExp(0);
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getEntity().getName());
		if (!ag.getArena().getBoolean(ArenaProperty.ITEM_DROP))
			event.getDrops().clear();
		if (ev instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityEv = (EntityDamageByEntityEvent) ev;
			Player player2 = null;
			if (entityEv.getDamager() instanceof Player)
				player2 = (Player) entityEv.getDamager();
			else if (entityEv.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) entityEv.getDamager();
				System.out.println("Player death1");
				if (proj.getShooter() instanceof Player) {
					player2 = (Player) proj.getShooter();
				}
			}
			if (player2 != null) {
				ag.onKill(ag.getPlayer(player2.getName()),
						ag.getPlayer(event.getEntity().getName()));
			} else {
				ag.onDeath(ag.getPlayer(event.getEntity().getName()));
			}
		} else if (ag != null)
			ag.onDeath(ag.getPlayer(event.getEntity().getName()));
	}

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		if (minigames.getManagement().getQueueManagement()
				.hasPlayer(event.getPlayer().getName())) {
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							minigames,
							new LogoutScheduler(event.getPlayer().getName(),
									minigames), 90 * 20);
		}
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag != null)
			ag.onInteract(event.getPlayer(), event.getItem(),
					event.getClickedBlock());
		else if (minigames.getHub().isGameSelection(event.getItem())) {
			minigames.getHub().openInventory(event.getPlayer());
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void playerInteractPlayer(PlayerInteractEntityEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (!(event.getRightClicked() instanceof Player))
			return;
		if (ag != null)
			ag.playerInteractPlayer(ag.getPlayer(event.getPlayer().getName()),
					ag.getPlayer(((Player) event.getRightClicked()).getName()));
	}

	@EventHandler
	public void playerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(player.getName());
		if (ag == null || ag.getStatus() == Status.LOBBY) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		Player player2 = null;
		if (event.getDamager() instanceof Player) {
			player2 = (Player) event.getDamager();
			if (player2.getItemInHand() != null
					&& player2.getItemInHand().hasItemMeta()) {
				ItemMeta im = player2.getItemInHand().getItemMeta();
				if (im.hasLore()) {
					for (String str : im.getLore())
						if (str.equalsIgnoreCase(ChatColor.DARK_RED
								+ "Insta-kill"))
							event.setDamage(player.getHealth());
				}
			}
		} else if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				player2 = (Player) proj.getShooter();
				if (proj.hasMetadata("dmg")) {
					event.setDamage(proj.getMetadata("dmg").get(0).asInt());
					player.setNoDamageTicks(0);
				}
			} else
				return;
		}
		if (player2 == null)
			return;
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(player.getName());
		if (ag != null
				&& ((ag.getPlayer(player2.getName()).getTeam() != ag.getPlayer(
						player.getName()).getTeam()) || (ag.getPlayer(
						player2.getName()).getTeam() == ag.getPlayer(
						player.getName()).getTeam() && ag.getArena()
						.getBoolean(ArenaProperty.FRIENDLY_FIRE_ENABLED)))) {
			if (event.getDamager() instanceof Player) {
				ItemStack wep = player2.getItemInHand();
				System.out.println("Weapon is null: " + (wep == null));
				System.out.println("Punch_Damage: "
						+ ag.getArena().getBoolean(ArenaProperty.PUNCH_DAMAGE));
				if ((wep == null || wep.getType() == Material.AIR)
						&& !ag.getArena()
								.getBoolean(ArenaProperty.PUNCH_DAMAGE))
					event.setDamage(0.0);
				else if (minigames.getManagement().getWeaponManagement()
						.isWeapon(wep)) {
					Weapon weapon = minigames.getManagement()
							.getWeaponManagement().getWeapon(wep);
					if (weapon instanceof PhysicalWeapon) {
						if (ag.onDamage(ag.getPlayer(player2.getName()),
								ag.getPlayer(player.getName())))
							ag.onWeaponUse(weapon,
									ag.getPlayer(player2.getName()),
									ag.getPlayer(player.getName()));
						event.setDamage(weapon.getDamage());
					}
				}
			}
			if (ag.getArena().getBoolean(ArenaProperty.INSTANT_KILL)
					&& ag.onDamage(ag.getPlayer(player2.getName()),
							ag.getPlayer(player.getName())))
				event.setDamage(40.0);
		}
		if (ag == null
				|| ag.getStatus() == Status.LOBBY
				|| (ag.getPlayer(player2.getName()).getTeam() == ag.getPlayer(
						player.getName()).getTeam() && !ag.getArena()
						.getBoolean(ArenaProperty.FRIENDLY_FIRE_ENABLED))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerRespawn(PlayerRespawnEvent event) {
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag != null) {
			final ArenaPlayer ap = ag.getPlayer(event.getPlayer().getName());
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(minigames,
							new RespawnScheduler(ap, ag), 2L);
		}
	}

	@EventHandler
	public void healthRegain(EntityRegainHealthEvent event) {
		if (event.getRegainReason() == RegainReason.MAGIC
				|| event.getRegainReason() == RegainReason.MAGIC_REGEN)
			return;
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			final ArenaGame ag = minigames.getManagement().getArenaManagement()
					.getGame(player.getName());
			if (ag != null) {
				if (event.getRegainReason() != RegainReason.SATIATED
						&& !ag.getArena().getBoolean(
								ArenaProperty.HUNGER_REGEN_HEALTH))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void healthRegain(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(player.getName());
		if (ag != null) {
			if (ag.getArena().getBoolean(ArenaProperty.HUNGER_19)) {
				event.setFoodLevel(19);
			}
		} else
			event.setFoodLevel(20);
	}

	@EventHandler
	public void itemDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(player.getName());
		if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
			event.getItemDrop().remove();
			return;
		}
		if (ag != null) {
			if (!ag.getArena().getBoolean(ArenaProperty.ITEM_DROP)) {
				event.setCancelled(true);
			}
			if (minigames.getManagement().getWeaponManagement()
					.isWeapon(event.getItemDrop().getItemStack())) {
				minigames
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								minigames,
								new DelayedReload(event.getPlayer(), event
										.getItemDrop().getItemStack(),
										minigames.getManagement()
												.getWeaponManagement()), 1l);
				event.setCancelled(true);
			}
		} else if (player.isOp()) {
			return;
		} else
			event.setCancelled(true);
	}

	@EventHandler
	public void itemConsume(PlayerItemConsumeEvent event) {
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag != null) {
			ag.onConsume(event.getItem(),
					ag.getPlayer(event.getPlayer().getName()));
			if (event.getItem().getType() == Material.GRILLED_PORK
					&& ag.getArena().getBoolean(ArenaProperty.PORK_CHOP_HEALTH))
				event.getPlayer()
						.setHealth(
								event.getPlayer().getHealth()
										+ ag.getArena()
												.getInt(ArenaProperty.PORK_CHOP_INCREASE) > 20 ? 20
										: event.getPlayer().getHealth()
												+ ag.getArena()
														.getInt(ArenaProperty.PORK_CHOP_INCREASE));
		}
	}

	// Instant portal
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerMoveEvent(PlayerMoveEvent event) {
		if (!event
				.getFrom()
				.getWorld()
				.getName()
				.equalsIgnoreCase(
						minigames.getHub().getSpawnLocation().getWorld()
								.getName()))
			return;
		Block b = event.getTo().getBlock();
		if (b.isEmpty())
			return;
		if (b.getType() == Material.PORTAL
				|| b.getType() == Material.ENDER_PORTAL) {
			PlayerPortalEvent ppe = new PlayerPortalEvent(event.getPlayer(),
					event.getTo(), event.getTo(), null);
			Bukkit.getPluginManager().callEvent(ppe);
		} else if (b.getY() <= 12 && b.isLiquid()) {
			ArenaGame ag = minigames.getManagement().getArenaManagement()
					.getGame(event.getPlayer().getName());
			if (ag == null) {
				event.setTo(minigames.getHub().getSpawnLocation());
			}
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent event) {
		if (event.hasBlock() && event.getClickedBlock().getState() != null
				&& event.getClickedBlock().getState() instanceof Sign) {
			Player player = event.getPlayer();
			final ArenaGame ag = minigames.getManagement().getArenaManagement()
					.getGame(player.getName());
			if (ag != null)
				ag.signClicked((Sign) event.getClickedBlock().getState(),
						ag.getPlayer(player.getName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void tagUpdate(AsyncPlayerReceiveNameTagEvent event) {
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag == null)
			return;
		final ArenaPlayer tagChange = ag.getPlayer(event.getNamedPlayer()
				.getName()), viewer = ag.getPlayer(event.getPlayer().getName());
		if (tagChange == null || viewer == null)
			return;
		String name = ag.getNameChange(tagChange, viewer);
		if (name == null || name.equalsIgnoreCase("null"))
			return;
		event.setTag(name);
	}

	@EventHandler
	public void playerPickupItem(PlayerPickupItemEvent event) {
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag == null)
			return;
		int id = ag.onItemPickup(event.getItem(),
				ag.getPlayer(event.getPlayer().getName()));
		switch (id) {
		case -2:
			event.getItem().remove();
		case -1:
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerTalk(AsyncPlayerChatEvent event) {
		System.out.println(event.getPlayer() + "> " + event.getMessage());
		final ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		String message = event.getMessage();
		if (ag == null)
			minigames.getHub().playerChat(event.getPlayer(), message);
		else if ((ag != null && ag.getStatus() == Status.LOBBY)) {
			ag.getLobby().playerChat(event.getPlayer(), message);
		} else {
			boolean global = false;
			if (ChatColor.stripColor(message).startsWith("!")) {
				global = true;
				if (message.charAt(0) == (ChatColor.COLOR_CHAR))
					message = message.substring(3);
				else
					message = message.substring(1);
			}
			ag.playerChat(event.getPlayer(), message, global);
		}
		event.setCancelled(true);
	}
}
