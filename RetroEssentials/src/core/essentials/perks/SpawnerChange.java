package core.essentials.perks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import core.essentials.Main;
import core.essentials.objects.Config;

public class SpawnerChange implements Listener {
	private final ItemStack[] inventoryItems;
	private HashMap<String, Block> changeMap = new HashMap<String, Block>();
	private final short[] mobTypes = { 50, 51, 52, 54, 57, 90, 91, 92, 93 };
	private final String[] mobNames = { "Creeper", "Skeleton", "Spider",
			"Zombie", "Pig Zombie", "Pig", "Sheep", "Cow", " Chicken" };
	private final int[] costs = { 300, 700, 300, 300, 700, 500, 200, 200, 700 };

	private final String negTitle = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "RetroPvP" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
	private final String postTitle = ChatColor.AQUA + "[" + ChatColor.GOLD
			+ "RetroPvP" + ChatColor.AQUA + "] " + ChatColor.RESET;

	public SpawnerChange() {
		List<ItemStack> tempInve = new ArrayList<ItemStack>();

		for (int counter = 0; counter < mobTypes.length; counter++) {
			ItemStack is = new ItemStack(Material.MONSTER_EGG, 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("Change spawner to " + mobNames[counter]);
			List<String> lore = new ArrayList<String>();
			lore.add("Changing to " + mobNames[counter] + " will cost $"
					+ costs[counter]);
			im.setLore(lore);
			is.setItemMeta(im);
			is.setDurability(mobTypes[counter]);
			tempInve.add(is);
		}
		inventoryItems = tempInve.toArray(new ItemStack[tempInve.size()]);
	}

	@EventHandler
	public void inveClick(InventoryClickEvent ice) {
		if (ice.getInventory().getName()
				.equalsIgnoreCase("Spawner changer - Esc to exit")) {
			if (!changeMap.containsKey(ice.getWhoClicked().getName())) {
				ice.setCancelled(true);
				ice.getWhoClicked().closeInventory();
				changeMap.remove(ice.getWhoClicked().getName());
			} else {
				int id = ice.getSlot();
				ice.setCancelled(true);
				if (id > costs.length - 1)
					return;
				ice.getWhoClicked().closeInventory();
				int cost = this.costs[id];
				Player p = (Player) ice.getWhoClicked();
				if (Main.economy.getBalance(ice.getWhoClicked().getName()) < cost) {
					p.sendMessage(negTitle
							+ "You do not have enough money to do this. Cost of changing is $"
							+ cost
							+ " and your current balance is $"
							+ new DecimalFormat("##.#").format(Main.economy
									.getBalance(p.getName())));
				} else {
					Main.economy.withdrawPlayer(p.getName(), cost);
					p.sendMessage(postTitle
							+ "Successfully changed spawner to "
							+ mobNames[id]
							+ " your new balance is $"
							+ new DecimalFormat("##.#").format(Main.economy
									.getBalance(p.getName())));
					CreatureSpawner cs = (CreatureSpawner) changeMap.get(
							p.getName()).getState();
					cs.setCreatureTypeByName(mobNames[id].replaceAll(" ", ""));
					p.getInventory().remove(new ItemStack(Material.BEDROCK, 1));
				}
				changeMap.remove(ice.getWhoClicked().getName());
			}
		}
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& pie.getClickedBlock().getType() == Material.MOB_SPAWNER
				&& pie.getPlayer().getItemInHand() != null
				&& pie.getPlayer().getItemInHand().getType() == Material.BEDROCK) {
			Inventory inve = Bukkit.createInventory(pie.getPlayer(), 9,
					"Spawner changer - Esc to exit");
			inve.setContents(inventoryItems);
			changeMap.put(pie.getPlayer().getName(), pie.getClickedBlock());
			pie.getPlayer().openInventory(inve);
		}
	}

	@EventHandler
	public void playerPlacedBedrock(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.BEDROCK
				&& !Config.bypass.contains(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
}
