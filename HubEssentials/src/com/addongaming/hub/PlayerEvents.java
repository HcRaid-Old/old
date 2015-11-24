package com.addongaming.hub;

import java.util.ArrayList;
import java.util.List;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.addongaming.hcessentials.HcEssentials;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class PlayerEvents implements Listener {
	List<String> blindPlayers = new ArrayList<String>();
	private Location spawn;
	private ItemStack redstone, glowstone;
	private JavaPlugin jp;

	public PlayerEvents(JavaPlugin jp) {
		this.jp = jp;
		ItemStack is = new ItemStack(Material.REDSTONE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Hide players");
		im.setLore(new ArrayList<String>() {
			{
				this.add(ChatColor.GREEN + "Right click to hide all players");
			}
		});
		is.setItemMeta(im);
		redstone = is;
		is = new ItemStack(Material.GLOWSTONE_DUST);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Show players");
		im.setLore(new ArrayList<String>() {
			{
				this.add(ChatColor.GREEN + "Right click to show all players");
			}
		});
		is.setItemMeta(im);
		glowstone = is;
	}

	@EventHandler
	public void playerJoinEvent(final PlayerJoinEvent event) {
		event.getPlayer().getInventory().clear();
		// Server swapping
		ItemStack is = new ItemStack(Material.DIAMOND);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Quick-Travel");
		is.setItemMeta(im);
		event.getPlayer().getInventory().addItem(is);
		event.getPlayer()
				.getInventory()
				.setItem(
						8,
						(blindPlayers.contains(event.getPlayer().getName()) ? glowstone
								: redstone));
		if (blindPlayers.contains(event.getPlayer().getName())) {
			for (Player player : Bukkit.getOnlinePlayers())
				if (player != event.getPlayer())
					event.getPlayer().hidePlayer(player);
		}
		for (Player player : Bukkit.getOnlinePlayers())
			if (blindPlayers.contains(player.getName()))
				player.hidePlayer(event.getPlayer());
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						try {
							event.getPlayer().teleport(
									HcEssentials.essentials.getWarps().getWarp(
											"spawn"));
						} catch (WarpNotFoundException | InvalidWorldException e) {
							e.printStackTrace();
						}
					}
				}, 4L);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_AIR)
				|| (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (event.getPlayer().getItemInHand() != null
					&& event.getPlayer().getItemInHand().getType() != Material.AIR) {
				Material inHand = event.getPlayer().getItemInHand().getType();
				if (inHand == Material.GLOWSTONE_DUST
						|| inHand == Material.REDSTONE) {
					toggleInvisibility(event.getPlayer(), inHand);
					event.setCancelled(true);
				}
			}
		}

		if (event.getAction() == Action.PHYSICAL)
			event.getPlayer().setVelocity(
					event.getPlayer().getLocation().getDirection().multiply(2)
							.add(new Vector(0, 5, 0)));
	}

	private void toggleInvisibility(Player player, Material mat) {
		player.getInventory().remove(mat);
		ItemStack is;
		if (mat == Material.GLOWSTONE_DUST) {
			for (Player play : Bukkit.getOnlinePlayers()) {
				if (player != play) {
					player.showPlayer(play);
				}
			}
			blindPlayers.remove(player.getName());
			is = redstone;
		} else {
			for (Player play : Bukkit.getOnlinePlayers()) {
				if (player != play) {
					player.hidePlayer(play);
				}
			}
			blindPlayers.add(player.getName());
			is = glowstone;
		}
		player.getInventory().setItem(8, is);
		player.updateInventory();
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().isOp())
			event.setCancelled(true);
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().isOp())
			event.setCancelled(true);
	}

	@EventHandler
	public void dropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player player = (Player) e.getEntity();
			player.setFoodLevel(20);
			player.setSaturation(20.0F);
		}
	}

	// Instant portal
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerMoveEvent(PlayerMoveEvent event) {
		Block b = event.getTo().getBlock();
		if (b.isEmpty())
			return;
		if (b.getType() == Material.PORTAL
				|| b.getType() == Material.ENDER_PORTAL) {
			PlayerPortalEvent ppe = new PlayerPortalEvent(event.getPlayer(),
					event.getTo(), event.getTo(), null);
			Bukkit.getPluginManager().callEvent(ppe);
		}
	}
}
