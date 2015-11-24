package core.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Bank {
	private final int grade;
	private Inventory inven = null;
	private HashMap<String, HashMap<Integer, ItemStack>> ranks = new HashMap<String, HashMap<Integer, ItemStack>>();
	private final Player player;

	public Bank(int grade, final Player player) {
		this.player = player;
		this.grade = grade;
		setupRanks();
		loadInventory();
	}

	public Set<Integer> getSlots(String kind) {
		return ranks.get(kind).keySet();
	}

	public void setInventoryItems(ItemStack[] is) {
		inven.setContents(is);
	}

	private void setupRanks() {
		HashMap<Integer, ItemStack> temp = new HashMap<Integer, ItemStack>();
		// DECLARING
		ItemStack paper1 = new ItemStack(Material.PAPER, 1);
		ItemStack paper2 = new ItemStack(Material.PAPER, 1);
		ItemMeta imTemp2 = paper2.getItemMeta();
		ItemMeta imTemp = paper1.getItemMeta();
		ItemStack paper3 = new ItemStack(Material.PAPER, 1);
		ItemMeta imTemp3 = paper3.getItemMeta();
		ItemStack paper4 = new ItemStack(Material.PAPER, 1);
		ItemMeta imTemp4 = paper4.getItemMeta();
		// GHAST CHEST//
		if (player.hasPermission("HcRaid.Ender")
				|| player.hasPermission("HcRaid.Enderdragon")) {
			imTemp.setDisplayName(ChatColor.GREEN
					+ "Click to see your Enderdragon bank");
		} else {
			imTemp.setDisplayName(ChatColor.YELLOW
					+ "Upgrade to Enderdragon to access the next bank");
		}
		paper1.setItemMeta(imTemp);
		temp.put(8, paper1);
		ranks.put("ghast", temp);

		// END OF GHAST CHEST
		imTemp2.setDisplayName(player.hasPermission("HCraid.Hero") ? ChatColor.GREEN
				+ "Click to see your Hero bank"
				: ChatColor.YELLOW + "Upgrade to Hero to access the next bank.");
		imTemp3.setDisplayName(ChatColor.GREEN + "Click to see your Ghast bank");
		paper2.setItemMeta(imTemp2);
		paper3.setItemMeta(imTemp3);
		HashMap<Integer, ItemStack> temp2 = new HashMap<Integer, ItemStack>();
		temp2.put(8, paper2);
		temp2.put(0, paper3);
		ranks.put("ender", temp2);
		// HERO CHEST
		imTemp4.setDisplayName(ChatColor.GREEN
				+ "Click to see your Enderdragon bank");
		paper4.setItemMeta(imTemp4);
		HashMap<Integer, ItemStack> temp3 = new HashMap<Integer, ItemStack>();
		temp3.put(0, paper4);
		ranks.put("hero", temp3);

	}

	public ItemStack[] getNormalInventory() {

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack is : this.getInventory().getContents())
			list.add(is);
		HashMap<Integer, ItemStack> temp = new HashMap<Integer, ItemStack>();
		switch (grade) {
		case 0:
			temp = ranks.get("ghast");
			break;
		case 1:
			temp = ranks.get("ender");
			break;
		case 2:
			temp = ranks.get("hero");
			break;
		}
		for (ItemStack i : temp.values()) {
			list.set(list.indexOf(i), null);
		}
		return list.toArray(new ItemStack[list.size()]);
	}

	private void loadInventory() {
		HashMap<Integer, ItemStack> temp = new HashMap<Integer, ItemStack>();
		String bankType = "";
		switch (grade) {
		case 0:
			temp = ranks.get("ghast");
			bankType = "Ghast ";
			break;
		case 1:
			temp = ranks.get("ender");
			bankType = "Enderdragon ";
			break;
		case 2:
			temp = ranks.get("hero");
			bankType = "Hero ";
			break;
		}
		Inventory inve = Bukkit.createInventory(player, 18, bankType + "bank");
		try {
			ItemStack is[] = (BankFileHandler.loadInventory(player.getName(),
					grade).getContents());
			int counter = 0;
			for (ItemStack item : is) {
				if (item != null)
					inve.setItem(counter, item);
				counter++;
			}
		} catch (NullPointerException npe) {
		}
		for (Integer i : temp.keySet()) {
			if (inve.getItem(i) == null) {
				inve.setItem(i, temp.get(i));
			} else {
				ItemStack tempStack = inve.getItem(i);
				inve.remove(i);
				inve.setItem(i, temp.get(i));
				inve.setItem(inve.firstEmpty(), tempStack);
			}
		}
		this.inven = inve;
	}

	public Inventory getInventory() {
		return inven;
	}

	public String getPlayerName() {
		return player.getName();
	}

	public String getRank() {
		switch (grade) {
		case 0:
			return "ghast";
		case 1:
			return "ender";
		case 2:
			return "hero";
		}
		return null;
	}
}
