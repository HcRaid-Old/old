package com.addongaming.prison.limit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.blackmarket.BMCombatData;

public class BMWieldingLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Combat"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent event) {
		checkPlayer((Player) event.getPlayer());
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK)
			checkPlayer(event.getPlayer());
	}

	@EventHandler
	public void playerDealDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			if (attacker.getItemInHand() != null
					&& attacker.getItemInHand().getType() != Material.AIR
					&& BMCombatData.getData(attacker.getItemInHand().getType()) != null) {
				DataReturn dr = BMCombatData.canUse(attacker, attacker
						.getItemInHand().getType());
				switch (dr) {
				case FAILURE:
					break;
				case NOLEVEL:
					attacker.sendMessage(err + "You are too weak to use this.");
					dropHand(attacker);
					break;
				case NOPERM:
					attacker.sendMessage(err
							+ "You need to learn to handle this weapon from the combat teacher.");
					dropHand(attacker);
					break;
				case SUCCESS:
					break;
				default:
					break;

				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void dropHand(Player attacker) {
		attacker.getWorld().dropItemNaturally(attacker.getLocation(),
				attacker.getItemInHand());
		attacker.setItemInHand(new ItemStack(Material.AIR));
		attacker.updateInventory();

	}

	@SuppressWarnings("deprecation")
	private void checkPlayer(Player player) {
		ItemStack[] armour = player.getInventory().getArmorContents();
		ItemStack[] allowed = new ItemStack[armour.length];
		List<DataReturn> usedTypes = new ArrayList<DataReturn>();
		boolean changed = false;
		for (int i = 0; i < armour.length; i++) {
			ItemStack is = armour[i];
			if (is == null || is.getType() == Material.AIR)
				continue;
			else if (BMCombatData.getData(is.getType()) != null) {
				DataReturn dr = BMCombatData.canUse(player, is.getType());
				switch (dr) {
				case FAILURE:
					break;
				case NOLEVEL:
					if (!usedTypes.contains(DataReturn.NOLEVEL)) {
						player.sendMessage(err
								+ "You need a higher level to use this.");
						usedTypes.add(DataReturn.NOLEVEL);
					}
					player.getInventory().addItem(is);
					changed = true;
					break;
				case NOPERM:
					if (!usedTypes.contains(DataReturn.NOPERM)) {
						player.sendMessage(err
								+ "You need to learn how to use this from the Combat Teacher.");
						usedTypes.add(DataReturn.NOPERM);
					}
					player.getInventory().addItem(is);
					changed = true;
					break;
				case SUCCESS:
					allowed[i] = is;
					break;
				default:
					break;
				}
			} else {
				allowed[i] = is;
			}
		}
		player.getPlayer().getInventory().setArmorContents(allowed);
		if (changed)
			player.updateInventory();
	}

}
