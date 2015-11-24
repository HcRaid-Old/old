package com.addongaming.hcessentials.items.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.hooks.logging.BlockLoggingHook;
import com.addongaming.hcessentials.items.InfCustomItem;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.redeem.SyncInventory;
import com.addongaming.hcessentials.utils.Utils;

public class ChestSaver implements InfCustomItem {
	private final boolean enabled;
	private final File folder;
	private final DataLog dl;
	private int duraDamage;

	public ChestSaver(JavaPlugin jp) {
		dl = HcEssentials.getDataLogger().addLogger("ChestSaver");
		folder = new File(jp.getDataFolder() + File.separator + "CustomItems");
		if (!folder.exists())
			folder.mkdirs();
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("customitems.chestsaver.enabled", false);
		fc.addDefault("customitems.chestsaver.duraDamage", 5);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		enabled = fc.getBoolean("customitems.chestsaver.enabled");
		duraDamage = fc.getInt("customitems.chestsaver.duraDamage");
	}

	@Override
	public ItemStack getItem() {
		ItemStack is = new ItemStack(Material.GOLD_HOE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Chest Saver");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public String getName() {
		return "Chest Saver";
	}

	private boolean save(final Object obj, final String path) {
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Object load(final String path) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(path));
			final Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public boolean isItem(ItemStack is) {
		if (is == null
				|| is.getType() != Material.GOLD_HOE
				|| !is.hasItemMeta()
				|| is.getItemMeta().getDisplayName() == null
				|| !is.getItemMeta().getDisplayName()
						.equalsIgnoreCase(ChatColor.GOLD + "Chest Saver"))
			return false;
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.hasBlock()
				&& event.hasItem()
				&& event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.TRAPPED_CHEST
					|| event.getClickedBlock().getType() == Material.CHEST) {
				if (isItem(event.getItem())) {
					event.setCancelled(true);
					Chest chest = (Chest) event.getClickedBlock().getState();
					if (chest.getInventory().getViewers().size() > 0) {
						event.getPlayer()
								.sendMessage(
										message
												+ "Please make sure nobody else is accessing the chest.");
						return;
					}
					Material item = event.getClickedBlock().getType();
					int id = getId();
					if (event.getPlayer().getInventory().firstEmpty() < 0) {
						event.getPlayer()
								.sendMessage(
										message
												+ "You need one free inventory slot to save a chest");
						return;
					}
					if (!HcEssentials.worldGuard.canBuild(event.getPlayer(),
							event.getClickedBlock())) {
						event.getPlayer().sendMessage(
								message + "You cannot save this chest.");
						return;
					}
					dl.log("SAVING CHEST - " + event.getPlayer().getName()
							+ " ID " + id, id + "");
					for (ItemStack is : chest.getBlockInventory().getContents())
						if (is != null) {
							dl.log(Utils.itemToDebug(is), id + "");
						}
					dl.log("FINISHED SAVING CHEST", id + "");
					SyncInventory si = new SyncInventory(chest
							.getBlockInventory().getContents());
					if (save(si,
							new File(folder + File.separator + id + ".sav")
									.getAbsolutePath())) {
						if (BlockLoggingHook.hasInstance())
							BlockLoggingHook
									.getInstance()
									.getApi()
									.logRemoval(
											event.getPlayer().getName(),
											event.getClickedBlock()
													.getLocation(),
											item.getId(),
											event.getClickedBlock().getData());
						ItemStack is = new ItemStack(item, 1);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.GOLD + "Saved Chest");
						List<String> lore = new ArrayList<String>();
						lore.add(ChatColor.AQUA + "#" + id);
						lore.add("Feel free to /name");
						lore.add("Do not change lore.");
						im.setLore(lore);
						is.setItemMeta(im);
						event.getPlayer().getInventory().addItem(is);
						event.getPlayer().sendMessage(message + "Saved chest");
						chest.getBlockInventory().clear();
						event.getClickedBlock().setType(Material.AIR);
						event.getPlayer().updateInventory();
						Player player = event.getPlayer();
						if (player.getItemInHand().getDurability() + duraDamage > player
								.getItemInHand().getType().getMaxDurability()) {
							player.setItemInHand(new ItemStack(Material.AIR));
						} else
							player.getItemInHand().setDurability(
									(short) (player.getItemInHand()
											.getDurability() + duraDamage));
					} else {
						event.getPlayer().sendMessage(
								"Issue saving chest: " + id
										+ " please try again in a minute.");
					}
				}
			}
		}
	}

	private final String message = ChatColor.GOLD + "[" + ChatColor.AQUA
			+ "Chest Saving" + ChatColor.GOLD + "] " + ChatColor.GREEN;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand() == null
				|| (event.getItemInHand().getType() != Material.CHEST && event
						.getItemInHand().getType() != Material.TRAPPED_CHEST))
			return;
		ItemStack is = event.getItemInHand();
		ItemMeta im = is.getItemMeta();
		if (im.getLore() == null || im.getLore().size() != 3)
			return;
		if (!im.getLore().get(0).startsWith(ChatColor.AQUA + ""))
			return;
		int id = Integer.parseInt(ChatColor.stripColor(im.getLore().get(0))
				.substring(1));
		if (usedBefore(id)) {
			event.getPlayer()
					.sendMessage(
							message
									+ "ID: "
									+ id
									+ " has already been used. Please create a bug report if this is in error.");
			return;
		}
		SyncInventory si = (SyncInventory) load(folder + File.separator + id
				+ ".sav");
		dl.log("LOADING CHEST AT COORDS: "
				+ Utils.locationToString(event.getBlock().getLocation()));
		dl.log("LOADING CHEST - " + event.getPlayer().getName() + " ID " + id,
				id + "");
		for (ItemStack i : si.getContents())
			if (i != null) {
				dl.log(Utils.itemToDebug(i), id + "");
			}
		dl.log("FINISHED LOADING CHEST", id + "");
		Chest chest = (Chest) event.getBlockPlaced().getState();
		chest.getBlockInventory().setContents(si.getContents());
		setUsed(id);
		event.getPlayer().sendMessage(message + "Restored chest " + id);
	}

	private int getId() {
		int counter = 0;
		for (File file : folder.listFiles()) {
			String name = file.getName().substring(0,
					file.getName().indexOf('.'));
			if (isInteger(name) && Integer.parseInt(name) > counter) {
				counter = Integer.parseInt(name);
			}
		}
		counter++;
		return counter;
	}

	private void setUsed(int i) {
		File file = new File(folder + File.separator + i + ".sav");
		file.renameTo(new File(folder + File.separator + i + ".sav.used"));
	}

	private boolean usedBefore(int i) {
		for (File file : folder.listFiles())
			if (file.getName().equalsIgnoreCase(i + ".sav.used"))
				return true;
			else if (file.getName().equalsIgnoreCase(i + ".sav"))
				return false;
		return false;
	}

	private boolean isInteger(String name) {
		try {
			Integer.parseInt(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "Right click a chest with the Chest Saver to pack it into a single item!";
	}

	List<String> ips = new ArrayList<String>();
	List<String> userName = new ArrayList<String>();

	@Override
	public String canBuy(Player player) {
		if (userName.contains(player.getName().toLowerCase())) {
			if (!ips.contains(player.getAddress().getAddress().getHostAddress()))
				ips.add(player.getAddress().getAddress().getHostAddress());
			return message + "You may only purchase one " + getName()
					+ " a day.";
		} else if (ips.contains(player.getAddress().getAddress()
				.getHostAddress())) {
			if (!userName.contains(player.getName().toLowerCase()))
				userName.add(player.getName().toLowerCase());
			return message
					+ "Only one ChestSaver may be purchased per IP per day.";
		}
		ips.add(player.getAddress().getAddress().getHostAddress());
		userName.add(player.getName().toLowerCase());
		return null;
	}

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		if (event.getInventory().getType() == InventoryType.PLAYER)
			return;
		ItemStack is = null;
		if (event.getCurrentItem() != null
				&& event.getCurrentItem().getType() != Material.AIR) {
			is = event.getCurrentItem();
		} else if (event.getCursor() != null
				&& event.getCursor().getType() != Material.AIR) {
			is = event.getCursor();
		}
		if (is == null
				|| (is.getType() != Material.CHEST && is.getType() != Material.TRAPPED_CHEST))
			return;
		ItemMeta im = is.getItemMeta();
		if (im.getLore() == null || im.getLore().size() != 3)
			return;
		if (!im.getLore().get(0).startsWith(ChatColor.AQUA + "#"))
			return;
		InventoryAction[] disallowed = { InventoryAction.DROP_ALL_CURSOR,
				InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR,
				InventoryAction.DROP_ONE_SLOT,
				InventoryAction.MOVE_TO_OTHER_INVENTORY,
				InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE,
				InventoryAction.PLACE_SOME };
		if ((event.getRawSlot() != event.getSlot() && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY)
				|| (event.getRawSlot() == event.getSlot() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY))
			return;
		for (InventoryAction ia : disallowed)
			if (event.getAction() == ia) {
				Player play = (Player) event.getWhoClicked();
				play.sendMessage(message
						+ "You cannot place this in another inventory.");
				event.setCancelled(true);
				return;
			}
	}

	@EventHandler
	public void inventoryMoveItem(InventoryMoveItemEvent event) {
		ItemStack is = event.getItem();
		if (is == null
				|| (is.getType() != Material.CHEST && is.getType() != Material.TRAPPED_CHEST))
			return;
		ItemMeta im = is.getItemMeta();
		if (im.getLore() == null || im.getLore().size() != 3)
			return;
		if (!im.getLore().get(0).startsWith(ChatColor.AQUA + "#"))
			return;
		event.setCancelled(true);
		return;

	}

	@EventHandler
	public void inventoryDrag(InventoryDragEvent event) {
		ItemStack is = event.getOldCursor();
		if (is == null
				|| (is.getType() != Material.CHEST && is.getType() != Material.TRAPPED_CHEST))
			return;
		ItemMeta im = is.getItemMeta();
		if (im.getLore() == null || im.getLore().size() != 3)
			return;
		if (!im.getLore().get(0).startsWith(ChatColor.AQUA + "#"))
			return;
		Integer[] invSlots = event.getInventorySlots().toArray(
				new Integer[event.getInventorySlots().size()]);
		Integer[] rawSlots = event.getRawSlots().toArray(
				new Integer[event.getRawSlots().size()]);
		for (int i = 0; i < rawSlots.length; i++) {
			if (invSlots[i] == rawSlots[i]) {
				Player play = (Player) event.getWhoClicked();
				play.sendMessage(message
						+ "You cannot place this in another inventory.");
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void itemDropEvent(PlayerDropItemEvent event) {
		ItemStack is = event.getItemDrop().getItemStack();
		if (is == null
				|| (is.getType() != Material.CHEST && is.getType() != Material.TRAPPED_CHEST))
			return;
		ItemMeta im = is.getItemMeta();
		if (im.getLore() == null || im.getLore().size() != 3)
			return;
		if (!im.getLore().get(0).startsWith(ChatColor.AQUA + "#"))
			return;
		event.getPlayer().sendMessage(
				message + "You cannot place this in another inventory.");
		event.setCancelled(true);
		return;

	}
}
