package com.addongaming.minigames.listeners;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.scheduling.SchedEntityRemover;
import com.addongaming.minigames.minigames.ArenaGame;

public class BlockListener implements Listener {
	private HcMinigames minigames;

	public BlockListener(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getServer().getPluginManager()
				.registerEvents(this, minigames);
	}

	public boolean affectsMinigame(Location loc) {
		for (Arena arena : minigames.getManagement().getArenaManagement()
				.getAllArenas()) {
			if (arena.hasCurrentGame()
					&& arena.getLocationZone(ArenaProperty.ARENA) != null
					&& arena.getLocationZone(ArenaProperty.ARENA).isInZone(loc)) {
				return true;
			}
		}
		return false;
	}

	public ArenaGame getMinigame(Location loc) {
		for (Arena arena : minigames.getManagement().getArenaManagement()
				.getAllArenas()) {
			if (arena.hasCurrentGame()
					&& arena.getLocationZone(ArenaProperty.ARENA) != null
					&& arena.getLocationZone(ArenaProperty.ARENA).isInZone(loc)) {
				return arena.getCurrentGame();
			}
		}
		return null;
	}

	@EventHandler
	public void blockBreakEvent(BlockBreakEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag == null && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		} else if (ag == null && event.getPlayer().isOp())
			return;
		else if (ag != null) {
			if (ag.getArena().getBoolean(ArenaProperty.BLOCK_BREAK)) {
				if (ag.getArena().getGameMode() == GameMode.RVB
						&& event.getBlock().getType() == Material.GLOWSTONE)
					event.setCancelled(true);
				else if (!ag.blockBroken(
						ag.getPlayer(event.getPlayer().getName()),
						event.getBlock()))
					event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void inventoryOpen(InventoryOpenEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (event.getInventory().getHolder() != null) {
			if (!event
					.getView()
					.getTitle()
					.equalsIgnoreCase(
							ChatColor.stripColor(event.getView().getTitle())))
				return;
			if (ag == null && !event.getPlayer().isOp()) {
				event.setCancelled(true);
			} else if (ag == null && event.getPlayer().isOp())
				return;
			else if (ag != null) {
				if (ag.getArena().getBoolean(ArenaProperty.INVENTORY_OPEN)) {
					if (!ag.chestInteraction(event.getInventory().getHolder()))
						event.setCancelled(true);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle()
				.equalsIgnoreCase(minigames.getHub().getInventoryName())) {
			minigames.getHub().itemClicked(event.getCurrentItem(),
					(Player) event.getWhoClicked());
			event.setCancelled(true);
			return;
		}
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getWhoClicked().getName());
		if (ag == null && !event.getWhoClicked().isOp())
			return;
		else if (ag == null && event.getWhoClicked().isOp())
			return;
		else if (ag != null) {
			if (event.getCurrentItem() != null)
				if (!event
						.getView()
						.getTitle()
						.equalsIgnoreCase(
								ChatColor
										.stripColor(event.getView().getTitle()))) {
					if (!ag.onGUIClick(
							ag.getPlayer(event.getWhoClicked().getName()),
							event.getInventory(), event.getCurrentItem()))
						event.setCancelled(true);
					return;
				}
			if (event.getInventory().getType() == InventoryType.PLAYER
					&& (event.getRawSlot() <= 8 && event.getRawSlot() >= 5)) {
				if (!ag.getArena().getBoolean(ArenaProperty.ARMOUR_REMOVABLE))
					event.setCancelled(true);
			} else if (!ag.getArena().getBoolean(ArenaProperty.INVENTORY_CLICK))
				event.setCancelled(true);
			else if (!ag.onItemClick(
					ag.getPlayer(event.getWhoClicked().getName()),
					event.getInventory(), event.getCursor()))
				// TODO getCursor() might need to be swapped for
				// getCurrentItem()
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void inventoryDrag(InventoryDragEvent event) {
		// TODO Manipulate the maps etc to make them less buggy with passing
		// info to arena, INCL for the IGS
		if (event.getInventory().getTitle() != null
				&& ChatColor.stripColor(event.getInventory().getTitle())
						.equalsIgnoreCase(event.getInventory().getTitle())) {
			event.setCancelled(true);
			return;
		}
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getWhoClicked().getName());
		if (ag == null && !event.getWhoClicked().isOp())
			return;
		else if (ag == null && event.getWhoClicked().isOp())
			return;
		else if (ag != null) {
			if (!ag.getArena().getBoolean(ArenaProperty.INVENTORY_CLICK))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent event) {
		ArenaGame ag = minigames.getManagement().getArenaManagement()
				.getGame(event.getPlayer().getName());
		if (ag != null)
			System.out.println("Player is in a game.");
		if (ag == null && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		} else if (ag == null && event.getPlayer().isOp())
			return;
		else if (ag != null) {
			if (ag.getArena().getBoolean(ArenaProperty.BLOCK_PLACE)) {
				if (!ag.blockPlaced(ag.getPlayer(event.getPlayer().getName()),
						event.getBlock()))
					event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void redstoneChange(BlockRedstoneEvent event) {
		if (!affectsMinigame(event.getBlock().getLocation()))
			return;
		ArenaGame ag = getMinigame(event.getBlock().getLocation());
		if (ag != null) {
			if (!ag.redstoneEvent(event.getBlock()))
				event.setNewCurrent(0);
		}
	}

	@EventHandler
	public void tntExplore(EntityExplodeEvent event) {
		if (affectsMinigame(event.getLocation())) {
			ArenaGame ag = getMinigame(event.getLocation());
			for (Iterator<Block> iter = event.blockList().iterator(); iter
					.hasNext();)
				if (!ag.entityExplode(iter.next()))
					iter.remove();
		}
	}

	@EventHandler
	public void arrowHit(ProjectileHitEvent event) {
		if (event.getEntityType() == EntityType.ARROW) {
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(minigames,
							new SchedEntityRemover(event.getEntity()), 3l);
		}
	}
}
