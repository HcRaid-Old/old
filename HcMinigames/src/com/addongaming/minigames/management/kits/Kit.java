package com.addongaming.minigames.management.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.serialised.SerInventory;

public class Kit {
	private File kitDir;
	private String kitName;
	private SerInventory syncArmour = new SerInventory(new ItemStack[4]);
	private SerInventory syncInven = new SerInventory(new ItemStack[4]);
	private List<PotionEffect> potionEffect = new ArrayList<PotionEffect>();
	private List<String> desc = new ArrayList<String>();
	private Material material = Material.CHAINMAIL_LEGGINGS;

	public Kit(File kitDir, String kitName) {
		this.kitDir = kitDir;
		this.kitName = kitName;
		loadKits();
		loadConfig();
	}

	@SuppressWarnings("unchecked")
	private void loadConfig() {
		File configFile = new File(kitDir, kitName + ".yml");
		if (!configFile.exists()) {
			saveConfig();
		} else {
			YamlConfiguration config = YamlConfiguration
					.loadConfiguration(configFile);
			if (config.contains("potioneffects"))
				potionEffect.addAll((List<PotionEffect>) config
						.getList("potioneffects"));
			if (config.contains("description"))
				desc = config.getStringList("description");
			if (config.contains("material"))
				material = Material.getMaterial(config.getString("material"));
		}
	}

	private void saveConfig() {
		YamlConfiguration config = new YamlConfiguration();
		config.set("potioneffects", potionEffect);
		config.set("description", desc);
		config.set("material", material.name());
		try {
			config.save(new File(kitDir, kitName + ".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDescription(String str) {
		// TODO Split str up by | (newline) and set desc. array
	}

	public void addPotionEffect(PotionEffectType pet, int duration,
			int amplified) {
		potionEffect.add(new PotionEffect(pet, duration, amplified));
		saveConfig();
	}

	public boolean removePotionEffect(int amnt) {
		if (amnt < 0 || amnt >= potionEffect.size())
			return false;
		potionEffect.remove(amnt);
		saveConfig();
		return true;
	}

	private void loadKits() {
		Object oArmour = loadObject(kitDir.getAbsolutePath() + File.separator
				+ kitName + ".syncArmour");
		Object oInven = loadObject(kitDir.getAbsolutePath() + File.separator
				+ kitName + ".syncInven");
		if (oArmour != null && oArmour instanceof SerInventory)
			syncArmour = (SerInventory) oArmour;
		if (oInven != null && oInven instanceof SerInventory)
			syncInven = (SerInventory) oInven;
	}

	public void saveArmour(ItemStack[] armour) {
		syncArmour = new SerInventory(armour);
		saveObj(syncArmour, kitDir.getAbsolutePath() + File.separator + kitName
				+ ".syncArmour");
	}

	public void saveInven(ItemStack[] inven) {
		syncInven = new SerInventory(inven);
		saveObj(syncInven, kitDir.getAbsolutePath() + File.separator + kitName
				+ ".syncInven");
	}

	private Object loadObject(final String path) {
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

	private boolean saveObj(final Object obj, final String path) {
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

	public ItemStack[] getArmour() {
		return syncArmour.getContents();
	}

	public ItemStack[] getInven() {
		return syncInven.getContents();
	}

	public PotionEffect[] getPotionEffects() {
		return this.potionEffect.toArray(new PotionEffect[potionEffect.size()]);
	}

	public void setDisplayItem(Material type) {
		this.material = type;
		saveConfig();
	}

	public Material getDisplayItem() {
		return material;
	}

	public ItemStack getRegularDisplayItem() {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD
				+ EKit.getByName(kitName).toReadableText());
		im.setLore(desc);
		is.setItemMeta(im);
		return is;
	}

	public ItemStack getPremiumDisplayItem() {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD
				+ EKit.getByName(kitName).toReadableText());
		im.setLore(new ArrayList<String>() {
			{
				this.add(ChatColor.GOLD + "This is a premium kit");
			}
		});
		is.setItemMeta(im);
		return is;
	}
}
