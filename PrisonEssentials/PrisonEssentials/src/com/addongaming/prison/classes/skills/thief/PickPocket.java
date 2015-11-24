package com.addongaming.prison.classes.skills.thief;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.classes.ThiefData;
import com.addongaming.prison.events.PickPocketEvent;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PickPocket implements Listener {
	HashMap<String, Date> pickPocketList = new HashMap<String, Date>();

	// TODO Make permission node later on for
	// 1: Steal 1 item
	// 2: Steal a stack of items
	// 3: Open inventory and select an item
	private boolean inPvPZone(Entity entity) {
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				entity.getWorld()).getApplicableRegions(entity.getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			ProtectedRegion pr = it.next();
			if (pr.getFlag(DefaultFlag.PVP) != null
					&& pr.getFlag(DefaultFlag.PVP) == State.ALLOW)
				return true;
		}
		return false;
	}

	private DataReturn canPickPocket(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (prisoner.getPlayerClass() != PlayerClasses.THIEF)
			return DataReturn.NOPERM;
		if (!pickPocketList.containsKey(p.getName())
				|| pickPocketList.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	private ItemStack getRandomItemstack(ItemStack[] contents) {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		for (ItemStack is : contents)
			if (is != null)
				itemList.add(is);
		return itemList.get(new Random().nextInt(itemList.size()));
	}

	private void message(Player player, String message, boolean negative) {
		if (negative) {
			player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED
					+ "Thief" + ChatColor.DARK_RED + "] " + ChatColor.RESET
					+ message);
		} else {
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN
					+ "Thief" + ChatColor.DARK_GREEN + "] " + ChatColor.RESET
					+ message);
		}

	}

	@EventHandler
	public void playerPickpocket(PlayerInteractEntityEvent event) {
		if (event.getPlayer().isSneaking()) {
			DataReturn canPickpocket = canPickPocket(event.getPlayer());
			if (canPickpocket == DataReturn.FAILURE) {
				message(event.getPlayer(), "That skill is still on cooldown",
						true);
				return;
			} else if (canPickpocket == DataReturn.SUCCESS) {
				boolean guard = false;
				if (event.getRightClicked() instanceof Player) {
					Player target = (Player) event.getRightClicked();
					guard = target.getName().equalsIgnoreCase("Guard")
							|| PrisonerManager.getInstance()
									.getPrisonerInfo(target.getName())
									.getPlayerClass() == PlayerClasses.GUARD;
					Prisoner prisoner = PrisonerManager.getInstance()
							.getPrisonerInfo(event.getPlayer().getName());
					int ran = new Random().nextInt(4 + (guard ? 5 : 0));
					if (!inPvPZone(event.getPlayer())) {
						message(event.getPlayer(),
								"You are not in an area that allows pickpocketing.",
								true);
						return;
					}
					if (ran == 1) {
						ItemStack is = getRandomItemstack(target.getInventory()
								.getContents());
						int amount;
						if (!target.getName().equalsIgnoreCase("Guard")) {
							if (prisoner.hasPermission(ThiefData.PICKPOCKET_2
									.getPermission())) {
								amount = new Random().nextInt(is.getAmount()) + 1;
							} else
								amount = 1;
							is.setAmount(amount);
							target.getInventory().removeItem(
									new ItemStack[] { is });
							updateInventory(target);
						} else {
							Material[] ranGuardItems = { Material.STONE,
									Material.COBBLESTONE, Material.IRON_ORE,
									Material.ARROW, Material.FEATHER,
									Material.PORK, Material.COOKED_BEEF };
							Material ranItem = ranGuardItems[new Random()
									.nextInt(ranGuardItems.length)];
							is = new ItemStack(ranItem,
									ranItem.getMaxStackSize());
							if (prisoner.hasPermission(ThiefData.PICKPOCKET_2
									.getPermission())) {
								amount = new Random().nextInt(is.getAmount()) + 1;
							} else
								amount = 1;
							is.setAmount(amount);
						}
						event.getPlayer().getInventory().addItem(is);
						message(event.getPlayer(),
								"Your pickpocketing attempt succeeded.", false);
						pickPocketList.put(event.getPlayer().getName(),
								new Date(new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 10)));
						updateInventory(event.getPlayer());
						prisoner.giveClassExp((guard ? 10 : 5));
						Bukkit.getServer()
								.getPluginManager()
								.callEvent(
										new PickPocketEvent(event.getPlayer(),
												target, true));
					} else {
						message(event.getPlayer(),
								"Your pickpocketing attempt failed.", true);
						pickPocketList.put(event.getPlayer().getName(),
								new Date(new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 10)));
						prisoner.giveClassExp((guard ? 3 : 1));
						Bukkit.getServer()
								.getPluginManager()
								.callEvent(
										new PickPocketEvent(event.getPlayer(),
												target, false));
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void updateInventory(final Player play) {
		play.updateInventory();
	}
}
