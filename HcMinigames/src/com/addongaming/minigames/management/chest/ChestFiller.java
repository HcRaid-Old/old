package com.addongaming.minigames.management.chest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.serialised.SerInventory;
import com.addongaming.minigames.management.arena.GameMode;

public class ChestFiller {
	private final File dir;
	private final File yamlFile;
	private GameMode gameMode;
	private HashMap<Integer, SerInventory> inventory = new HashMap<Integer, SerInventory>();
	private HashMap<Integer, Integer[]> minMap = new HashMap<Integer, Integer[]>();

	public ChestFiller(File dir, GameMode gameMode) {
		this.dir = dir;
		yamlFile = new File(dir, "save.yml");
		if (!dir.exists())
			dir.mkdirs();
		reload();
		this.gameMode = gameMode;
	}

	public void reload() {
		inventory.clear();
		minMap.clear();
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".ser")) {
				Integer i = Integer.parseInt(file.getName().substring(0,
						file.getName().indexOf('.')));
				inventory.put(i, (SerInventory) SerInventory.loadObject(file
						.getAbsolutePath()));
				if (yaml.contains("inv" + i)) {
					minMap.put(i,
							new Integer[] { yaml.getInt("inv" + i + ".min"),
									yaml.getInt("inv" + i + ".max") });
				}
			}
		}
	}

	public void save() {
		for (File file : dir.listFiles())
			file.delete();
		YamlConfiguration yaml = new YamlConfiguration();
		for (Integer i : inventory.keySet()) {
			SerInventory.saveObj(inventory.get(i),
					new File(dir, i + ".ser").getAbsolutePath());
			yaml.set("inv" + i + ".min", minMap.get(i)[0]);
			yaml.set("inv" + i + ".max", minMap.get(i)[1]);
		}
		try {
			yaml.save(yamlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addInventory(int id) {
		inventory.put(id, new SerInventory(new ItemStack[] {}));
		minMap.put(id, new Integer[] { 3, 5 });
	}

	public boolean hasInventory(int id) {
		return inventory.containsKey(id);
	}

	public void setInventory(int id, Inventory i) {
		List<ItemStack> actualItems = new ArrayList<ItemStack>();
		for (ItemStack is : i.getContents())
			if (is != null && is.getType() != Material.AIR)
				actualItems.add(is);
		this.inventory.put(
				id,
				new SerInventory(actualItems.toArray(new ItemStack[actualItems
						.size()])));
	}

	public ItemStack[] getItems(int i) {
		if (!inventory.containsKey(i))
			return null;
		return inventory.get(i).getContents();
	}

	public String getAllIds() {
		return StringUtils.join(inventory.keySet(), ", ");
	}

	public void removeId(int id) {
		this.inventory.remove(id);
		this.minMap.remove(id);
	}

	public void fillChest(Chest chest, int level) {
		ArrayList<ItemStack> unused = new ArrayList<ItemStack>();
		System.out.println("Unused is null: " + (unused == null));
		System.out.println("inventory is null: "
				+ (inventory.get(level) == null));
		System.out.println("contents is null: "
				+ (inventory.get(level).getContents() == null));
		Collections.addAll(unused, inventory.get(level).getContents());
		Collections.shuffle(unused);
		int min = minMap.get(level)[0], max = minMap.get(level)[1], itemsToGive = new Random()
				.nextInt((max - min) + 1) + min;
		Inventory inventory = chest.getBlockInventory();
		inventory.clear();
		for (int i = 1; i <= itemsToGive; i++) {
			int nextSlot = -1;
			if (unused.isEmpty()) {
				chest.update(true);
				return;
			}
			while (nextSlot == -1) {
				nextSlot = new Random().nextInt(inventory.getSize());
				if (inventory.getItem(nextSlot) != null
						&& inventory.getItem(nextSlot).getType() != Material.AIR)
					nextSlot = -1;
			}
			int item = new Random().nextInt(unused.size());
			inventory.setItem(nextSlot, unused.get(item));
			unused.remove(item);
		}
		chest.update(true);
	}

	public Set<Integer> getIds() {
		return inventory.keySet();
	}

	public void setMin(int tier, int amnt) {
		minMap.put(tier, new Integer[] { amnt, minMap.get(tier)[1] });
	}

	public void setMax(int tier, int amnt) {
		minMap.put(tier, new Integer[] { minMap.get(tier)[0], amnt });
	}
}
