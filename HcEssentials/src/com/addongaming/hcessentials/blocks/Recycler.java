package com.addongaming.hcessentials.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;
import com.addongaming.hcessentials.events.ItemRecycledEvent;

public class Recycler implements Listener {
	private final Material blockType;
	private final JavaPlugin jp;
	private List<String> playerList = new ArrayList<String>();

	public Recycler(Material blockType, JavaPlugin jp) {
		this.blockType = blockType;
		this.jp = jp;
	}

	private void closeInventory(Player p) {
		if (playerList.contains(p.getName())) {
			if (p.getOpenInventory().getTopInventory().getItem(0) != null) {
				p.getInventory().addItem(
						p.getOpenInventory().getTopInventory().getItem(0));
				p.updateInventory();
			} else if (p.getOpenInventory().getTopInventory().getItem(8) != null) {
				p.getInventory().addItem(
						p.getOpenInventory().getTopInventory().getItem(8));
				p.updateInventory();
			}
			playerList.remove(p.getName());
		}
	}

	private int getNeeded(Recipe r, Material search) {
		int count = 0;
		if (r instanceof ShapedRecipe) {
			ShapedRecipe re = (ShapedRecipe) r;
			for (ItemStack is : re.getIngredientMap().values())
				if (is != null && is.getType() == search)
					count++;
		}
		return count;
	}

	private ItemStack getReward(ItemStack is) {
		Material rew = null;
		if (Enchantable.getItemType(is.getType()) == ItemType.UNDEFINED
				|| Enchantable.getItemType(is.getType()) == ItemType.BOW)
			return null;
		switch (is.getType().name().split("_")[0].toLowerCase()) {
		case "leather":
			rew = Material.LEATHER;
			break;
		case "diamond":
			rew = Material.DIAMOND;
			break;
		case "gold":
			rew = Material.GOLD_INGOT;
			break;
		case "iron":
			rew = Material.IRON_INGOT;
			break;
		default:
			return null;
		}
		ItemStack fresh = new ItemStack(is.getType(), 1);
		Recipe r = Bukkit.getRecipesFor(fresh).get(0);
		double rewNeeded = getNeeded(r, rew);
		double perc = 1 - ((double) (is.getDurability()) / (double) (is
				.getType().getMaxDurability()));
		return new ItemStack(rew, (int) (Math.round(rewNeeded * perc)));
	}

	@EventHandler
	public void inveClose(InventoryCloseEvent pie) {
		closeInventory((Player) pie.getPlayer());
	}

	@EventHandler
	public void inventoryClick(final InventoryClickEvent ice) {
		if (playerList.contains(ice.getWhoClicked().getName())) {
			if (!ice.getAction().name().toLowerCase().contains("place")
					&& !ice.getAction().name().toLowerCase().contains("pickup")) {
				ice.setCancelled(true);
				return;
			}
			if (ice.getRawSlot() == 0) {
				if (ice.getCursor().getType() != Material.AIR)
					updateReward((Player) ice.getWhoClicked(), ice.getCursor());
				else {
					ice.getInventory().setItem(8, null);
					updateInve((Player) ice.getWhoClicked());
				}
			} else if (ice.getRawSlot() == 8) {
				Inventory inv = ice.getWhoClicked().getOpenInventory()
						.getTopInventory();
				// If clicking last slot (8) and that has an item as well as the
				// original slot (before it's removed) then throw the event
				if (inv.getItem(0) != null
						&& inv.getItem(0).getType() != Material.AIR
						&& inv.getItem(8) != null
						&& inv.getItem(8).getType() != Material.AIR)
					Bukkit.getPluginManager()
							.callEvent(
									new ItemRecycledEvent(ice.getWhoClicked()
											.getName()));
				ice.getInventory().setItem(0, null);
				updateInve((Player) ice.getWhoClicked());
			} else if (ice.getRawSlot() > 0 && ice.getRawSlot() < 8) {
				ice.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void inventoryDrag(final InventoryDragEvent event) {
		if (playerList.contains(event.getWhoClicked().getName())) {
			if (event.getNewItems().size() == 1
					&& event.getNewItems().values().iterator().next().getType() != Material.AIR
					&& event.getRawSlots().size() == 1
					&& event.getRawSlots().iterator().next() == 0) {
				updateReward((Player) event.getWhoClicked(), event
						.getNewItems().values().iterator().next());
			}
		}

	}

	@EventHandler
	public void playerInteracted(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& pie.getClickedBlock().getType() == blockType) {
			if (pie.getPlayer().isSneaking()) {
				if (pie.getItem() != null && pie.getItem().getType() != null
						&& pie.getItem().getType() == Material.BONE) {
					pie.getClickedBlock().breakNaturally();
					pie.getClickedBlock()
							.getLocation()
							.getWorld()
							.dropItemNaturally(
									pie.getClickedBlock().getLocation(),
									new ItemStack(this.blockType, 1));
					return;
				}
			}
			if (playerList.contains(pie.getPlayer().getName())) {
				return;
			} else {
				playerList.add(pie.getPlayer().getName());
				Inventory i = Bukkit.createInventory(pie.getPlayer(), 9,
						"Recycler");
				ItemStack first = new ItemStack(Material.LAVA, 1);
				ItemStack generic = new ItemStack(Material.WATER, 1);
				ItemStack second = new ItemStack(Material.LAVA, 1);
				second = setItem(second, "Output item", "-------->",
						"Here your recycled items", "will be displayed and",
						"obtainable.");
				generic = setItem(generic, " ", " ");
				first = setItem(first, "Input Item", "<--------",
						"Put your input item here",
						"And depending on the durability",
						"you will be able to recycle", "some resources",
						"Types accepted: Diamond, Leather, Iron, Gold.");
				i.setItem(1, first);
				for (int ii = 2; ii < 7; ii++) {
					i.setItem(ii, generic);
				}
				i.setItem(7, second);
				pie.getPlayer().openInventory(i);
			}
		}
	}

	private ItemStack setItem(ItemStack is, String name, String... lore) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return is;
	}

	public void shutDown() {
		for (String str : playerList) {
			Player p = Bukkit.getPlayer(str);
			if (p == null || !p.isOnline()) {
				continue;
			} else {
				closeInventory(p);
				p.closeInventory();
			}
		}
	}

	private void updateInve(final Player p) {
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						p.updateInventory();
					}
				}, 0l);
	}

	private void updateReward(final Player p, ItemStack is) {
		Inventory i = p.getOpenInventory().getTopInventory();
		ItemStack reward = getReward(is);
		i.setItem(8, reward);
		updateInve(p);

	}
}
