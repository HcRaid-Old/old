package com.addongaming.minigames.management.weapon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.addongaming.minigames.core.HcMinigames;

public class PhysicalWeapon implements Weapon {
	private HcMinigames minigames;
	private File yamlFile;
	private Weapons weapons;
	private WeaponType weaponType;
	private Material material = Material.STONE_AXE;
	private int damage = 5;

	public PhysicalWeapon(HcMinigames minigames, File yamlFile, Weapons weapons) {
		this.minigames = minigames;
		this.yamlFile = yamlFile;
		this.weapons = weapons;
		this.weaponType = weapons.getWeaponType();
		loadGun();
	}

	private void loadGun() {
		if (!yamlFile.exists())
			this.save();
		else {
			YamlConfiguration yaml = YamlConfiguration
					.loadConfiguration(yamlFile);
			damage = yaml.getInt("damage");
			material = Material.getMaterial(yaml.getString("material"));
		}
	}

	@Override
	public WeaponType getWeaponType() {
		return weaponType;
	}

	@Override
	public Weapons getWeapons() {
		return weapons;
	}

	private String getLore(String name, Object value) {
		return ChatColor.GOLD + name + ": " + ChatColor.AQUA
				+ String.valueOf(value);
	}

	@Override
	public boolean isWeapon(ItemStack is) {
		if (is.getType() == material) {
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.hasDisplayName()) {
					String name = im.getDisplayName();
					if (name.startsWith(ChatColor.WHITE + "")
							&& ChatColor.stripColor(name).startsWith(
									this.weapons.toReadableText()))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack getWeapon() {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + weapons.toReadableText());
		List<String> lore = new ArrayList<String>();
		lore.add(getLore("Damage", damage));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public void setType(Material material) {
		this.material = material;
	}

	@Override
	public void save() {
		YamlConfiguration yaml = new YamlConfiguration();
		yaml.set("damage", damage);
		yaml.set("material", material.name());
		try {
			yaml.save(yamlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDamage(int damage) {
		this.damage = damage;

	}
}
