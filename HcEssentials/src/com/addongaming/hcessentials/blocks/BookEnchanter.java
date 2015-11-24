package com.addongaming.hcessentials.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.data.Enchantable;

public class BookEnchanter implements Listener, CommandExecutor {
	private final JavaPlugin jp;
	private String permission;

	private List<String> playerList = new ArrayList<String>();

	String pos = ChatColor.GRAY + "[" + ChatColor.BLUE + "HcBook"
			+ ChatColor.GRAY + "] " + ChatColor.GREEN;
	private boolean force = false;
	private boolean oponly;

	public BookEnchanter(JavaPlugin jp, boolean b) {
		oponly = !b;
		this.jp = jp;
		permission = jp.getConfig().getString("blocks.bookenchant.permission");
	}

	private void closeInventory(Player p) {
		if (playerList.contains(p.getName())) {
			if (p.getOpenInventory().getTopInventory().getItem(0) != null) {
				p.getInventory().addItem(
						p.getOpenInventory().getTopInventory().getItem(0));
				p.updateInventory();
			}
			if (p.getOpenInventory().getTopInventory().getItem(1) != null) {
				p.getInventory().addItem(
						p.getOpenInventory().getTopInventory().getItem(1));
				p.updateInventory();
			}
			if (p.getOpenInventory().getTopInventory().getItem(8) != null) {
				p.getInventory().addItem(
						p.getOpenInventory().getTopInventory().getItem(8));
				p.updateInventory();
			}
			playerList.remove(p.getName());
		}
	}

	/*
	 * @EventHandler public void playerInteracted(PlayerInteractEvent pie) { if
	 * (pie.getAction() == Action.RIGHT_CLICK_BLOCK &&
	 * pie.getClickedBlock().getType() == blockType) { if
	 * (playerList.contains(pie.getPlayer().getName())) {
	 * pie.setCancelled(true); return; } else { openInventory((Player)
	 * pie.getPlayer()); pie.setCancelled(true); } } }
	 */

	@EventHandler
	public void inveClose(InventoryCloseEvent pie) {
		closeInventory((Player) pie.getPlayer());
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent ice) {
		if (playerList.contains(ice.getWhoClicked().getName())) {
			if (!ice.getAction().name().toLowerCase().contains("place")
					&& !ice.getAction().name().toLowerCase().contains("pickup")) {
				ice.setCancelled(true);
				return;
			}
			if (ice.getRawSlot() <= 1) {
				ice.getInventory().setItem(8, null);
				updateInve((Player) ice.getWhoClicked());
			} else if (ice.getRawSlot() == 4
					|| (ice.getRawSlot() == 5 && ice.getInventory().getItem(5)
							.getType() == Material.WOOL)) {
				updateReward((Player) ice.getWhoClicked(), ice.getInventory(),
						ice.getRawSlot());
				ice.setCancelled(true);
			} else if (ice.getRawSlot() > 1 && ice.getRawSlot() < 8) {
				ice.setCancelled(true);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (oponly && !arg0.isOp())
			return true;
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("Opening inventory...");
			arg0.sendMessage("Item 1 - null... Console, why you do this?");
			return true;
		}
		Player p = (Player) arg0;
		if (!p.hasPermission(permission)) {
			p.sendMessage(pos
					+ "Sorry, you do not have permission for this command.");
			return true;
		}
		if (arg3.length == 0
				|| !(arg3[0].equalsIgnoreCase("gui")
						|| arg3[0].equalsIgnoreCase("enchant") || arg3[0]
							.equalsIgnoreCase("override"))) {
			p.sendMessage(pos + "Please use /book enchant or /book gui");
			return true;
		}
		switch (arg3[0]) {
		case "override":
			if (p.isOp())
				force = !force;
			return true;
		case "gui":
			openInventory(p);
			return true;
		case "enchant":
			enchantItems(p, arg3);
			return true;
		default:
			p.sendMessage(pos + "Please use /book enchant or /book gui");
			return true;
		}
	}

	private void enchantItems(Player p, String[] arg3) {
		if (p.getItemInHand() == null
				|| !Enchantable.isEnchantable(p.getItemInHand().getType())) {
			p.sendMessage(pos + "Please hold an enchantable item in your hand.");
			return;
		}
		if (arg3.length <= 1) {
			p.sendMessage(pos
					+ "Please use /book enchant <enchantment> <level>");
			return;
		}
		String enchant = arg3[1];
		Enchantment ench = com.earth2me.essentials.Enchantments
				.getByName(arg3[1]);
		if (ench == null) {
			p.sendMessage(pos + "Enchantment not found.");
			return;
		}
		if (p.isOp() && force) {
			p.getItemInHand().addUnsafeEnchantment(ench,
					Integer.parseInt(arg3[2]));
			return;
		}
		System.out.println("Enchantment looking for: " + ench.getName());
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack is : p.getInventory().getContents()) {
			if (is != null && is.getType() == Material.ENCHANTED_BOOK) {
				EnchantmentStorageMeta sem = (EnchantmentStorageMeta) is
						.getItemMeta();
				for (Enchantment earray : sem.getStoredEnchants().keySet()) {
					System.out.println("Book has " + earray.getName());
					if (earray.getName().equalsIgnoreCase(ench.getName())) {
						System.out.println("Adding - Book has "
								+ earray.getName());
						list.add(is);
					}
				}
			}
		}
		if (list.isEmpty()) {
			p.sendMessage(pos + "Cannot find any books with the enchantment "
					+ arg3[1] + " on.");
			return;
		}
		try {
			Integer.parseInt(arg3[2]);
		} catch (NumberFormatException ex) {
			p.sendMessage(pos
					+ "Please use /book enchant <enchantment> <level>");
			return;
		}
		int level = Integer.parseInt(arg3[2]);
		if (p.getItemInHand().containsEnchantment(ench)) {
			int currLevel = p.getItemInHand().getEnchantmentLevel(ench);
			if (currLevel >= level) {
				p.sendMessage(pos
						+ "You already have that enchantment with the same level, or higher!");
				return;
			}

		}
		for (ItemStack is : list) {
			EnchantmentStorageMeta esm = (EnchantmentStorageMeta) is
					.getItemMeta();
			if (esm.getStoredEnchantLevel(ench) == level) {
				int randomNum = new Random().nextInt((int) Math
						.floor(level * 0.8));
				if (level == 1)
					randomNum = 0;
				if (randomNum == 0) {
					p.sendMessage(pos + "Enjoy your new enchanted item!");
					p.getItemInHand().addUnsafeEnchantment(ench, level);
				} else {
					p.sendMessage(pos + "Your enchantment failed");
				}
				int slot = p.getPlayer().getInventory().first(is);
				if (p.getPlayer().getInventory().getItem(slot).getAmount() > 1) {
					is.setAmount(is.getAmount() - 1);
					p.getPlayer().getInventory().setItem(slot, is);
				} else
					p.getPlayer().getInventory()
							.setItem(slot, new ItemStack(Material.AIR));
				return;
			}
		}
		p.getPlayer()
				.sendMessage(
						pos + "Cannot find a " + enchant + " level " + level
								+ " book.");

	}

	private void openInventory(Player player) {
		playerList.add(player.getName());
		Inventory i = Bukkit.createInventory(player, 9, "Book -> Item");
		ItemStack first = new ItemStack(Material.LAVA, 1);
		ItemStack generic = new ItemStack(Material.WATER, 1);
		ItemStack second = new ItemStack(Material.LAVA, 1);
		second = setItem(second, "Output item", "-------->",
				"Here will be the item", "you will have a chance",
				"of obtaining.");
		generic = setItem(generic, " ", " ");
		first = setItem(first, "Input Item", "<--------",
				"Put your input item here", "along with the enchanted book",
				"and you'll have a chance",
				"of putting the book onto your item");
		i.setItem(2, first);
		for (int ii = 3; ii < 7; ii++) {
			i.setItem(ii, generic);
		}
		i.setItem(7, second);

		i.setItem(
				4,
				setItem(new Wool(DyeColor.LIME).toItemStack(1), "Enchant item",
						"Click this to process your", "enchantment."));
		player.openInventory(i);
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

	private void updateReward(final Player p, final Inventory i, final int slot) {
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						ItemStack book = null;
						if (i.getItem(0) != null
								&& i.getItem(0).getType() == Material.ENCHANTED_BOOK)
							book = i.getItem(0);
						else if (i.getItem(1) != null
								&& i.getItem(1).getType() == Material.ENCHANTED_BOOK)
							book = i.getItem(1);
						ItemStack item = null;
						ItemStack item2 = null;
						if (i.getItem(0) != null
								&& i.getItem(0).getType() != Material.AIR
								&& i.getItem(0).getType() != Material.ENCHANTED_BOOK) {
							item = i.getItem(0);
						} else if (i.getItem(1) != null
								&& i.getItem(1).getType() != Material.AIR
								&& i.getItem(1).getType() != Material.ENCHANTED_BOOK) {
							item = i.getItem(1);
						}

						if (book == null && item == null) {
							p.sendMessage(pos
									+ "Please enter an enchanted book plus the item you wish to enchant.");
							return;
						} else if (book == null) {
							p.sendMessage(pos
									+ "Please enter an enchanted book to enchant with.");
							return;
						} else if (item == null) {
							p.sendMessage(pos
									+ "Please enter an item you wish to enchant.");
							return;
						} else if (!Enchantable.isEnchantable(item.getType())) {
							p.sendMessage(pos
									+ "This item cannot be enchanted.");
							return;
						}
						item2 = item.clone();
						try {
							EnchantmentStorageMeta ems = (EnchantmentStorageMeta) book
									.getItemMeta();
							int amount = 0;
							for (Enchantment e : ems.getStoredEnchants()
									.keySet()) {
								if (slot == 5)
									item.addUnsafeEnchantment(e,
											ems.getStoredEnchantLevel(e));
								else
									item.addEnchantment(e,
											ems.getStoredEnchantLevel(e));
								amount += ems.getStoredEnchantLevel(e);
							}
							int randomNum = new Random().nextInt((int) Math
									.floor(amount * 1.25));
							if (randomNum == 0) {
								i.setItem(8, item);
								p.sendMessage(pos
										+ "Enjoy your new enchanted item!");
							} else {
								i.setItem(8, item2);
								p.sendMessage(pos + "Your enchantment failed");
							}
							i.setItem(0, null);
							i.setItem(1, null);
							if (slot == 5)
								i.setItem(5, new ItemStack(Material.WATER, 1));
							updateInve(p);
						} catch (IllegalArgumentException ex) {
							p.sendMessage(pos
									+ "You cannot apply that enchantment to that item. To do it unsafe click the red wool.");
							i.setItem(
									5,
									setItem(new Wool(DyeColor.RED)
											.toItemStack(), "Unsafe enchant",
											"This enchantment doesn't belong",
											"On this item.",
											"Click if you are prepared."));
							return;
						}
					}
				});
	}
}
