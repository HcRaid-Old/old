package com.addongaming.minigames.management.weapon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.addongaming.minigames.core.HcMinigames;

public class Gun implements Weapon {
	private Weapons weapons;
	private WeaponType weaponType;
	private File yamlFile;
	private GunFireType gft = GunFireType.FULLY_AUTOMATIC;
	private int clipSize = 30, damage = 2, rof = 200, reload = 450,
			accuracy = 7, recoil = 12, overallammo = 120;
	private Material material = Material.GOLD_HOE;
	private HcMinigames minigames;

	public Gun(HcMinigames minigames, File yamlFile, Weapons weapons) {
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
			gft = GunFireType.getType(yaml.getString("firetype"));
			clipSize = yaml.getInt("clipsize");
			damage = yaml.getInt("damage");
			rof = yaml.getInt("rof");
			reload = yaml.getInt("reload");
			accuracy = yaml.getInt("accuracy");
			recoil = yaml.getInt("recoil");
			overallammo = yaml.getInt("overallammo");
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

	@Override
	public ItemStack getWeapon() {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + weapons.toReadableText() + " + ("
				+ clipSize + "/" + (overallammo - clipSize) + ")");
		List<String> lore = new ArrayList<String>();
		lore.add(getLore("Fire type", gft.toReadableText()));
		lore.add(getLore("Clip size", clipSize));
		lore.add(getLore("Damage", damage));
		lore.add(getLore("Rate of fire", rof + "ms"));
		lore.add(getLore("Reload", reload + "ms"));
		lore.add(getLore("Accuracy", accuracy));
		lore.add(getLore("Recoil", recoil));
		lore.add(getLore("Max ammo", overallammo));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public boolean canFire(ItemStack is) {
		if (is.getType() == material) {
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.hasDisplayName()) {
					String name = im.getDisplayName();
					if (name.contains("Reloading"))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the current clip ammo
	 * 
	 * @param is
	 *            ItemStack that's being checked
	 * @return Number of bullets left in the clip, -1 means reloading and -2 it
	 *         isn't the correct weapon instance
	 */
	public int getClipAmmo(ItemStack is) {
		if (is.getType() == material) {
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.hasDisplayName()) {
					String name = ChatColor.stripColor(im.getDisplayName());
					if (name.contains("Reloading"))
						return -1;
					name = name.substring(getWeapons().toReadableText()
							.length());
					Pattern pattern = Pattern.compile("[0-9]{1,3}");
					Matcher match = pattern.matcher(name);
					match.find();
					return Integer.parseInt(match.group());
				}
			}
		}
		return -2;
	}

	/**
	 * Gets the current reserved ammo
	 * 
	 * @param is
	 *            ItemStack that's being checked
	 * @return Number of bullets reserved, -1 means reloading and -2 it isn't
	 *         the correct weapon instance
	 */
	public int getReservedAmmo(ItemStack is) {
		if (is.getType() == material) {
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.hasDisplayName()) {
					String name = ChatColor.stripColor(im.getDisplayName());
					if (name.contains("Reloading"))
						return -1;
					name = name.substring(getWeapons().toReadableText()
							.length());
					Pattern pattern = Pattern.compile("[0-9]{1,3}");
					Matcher match = pattern.matcher(name);
					match.find();
					match.find();
					return Integer.parseInt(match.group());
				}
			}
		}
		return -2;
	}

	/**
	 * Gets the guns total ammo
	 * 
	 * @param is
	 *            ItemStack that's being checked
	 * @return Number of bullets, -1 means reloading and -2 it isn't the correct
	 *         weapon instance
	 */
	public int getTotalAmmo(ItemStack is) {
		if (is.getType() == material) {
			if (is.hasItemMeta()) {
				ItemMeta im = is.getItemMeta();
				if (im.hasDisplayName()) {
					String name = ChatColor.stripColor(im.getDisplayName());
					if (name.contains("Reloading"))
						return -1;
					String[] split = name.split("( )");
					String ammo = split[3];
					Pattern pattern = Pattern.compile("[0-9]{1,3}");
					Matcher match = pattern.matcher(ammo);
					match.find();
					int clip = Integer.parseInt(match.group());
					match.find();
					clip += Integer.parseInt(match.group());
					return clip;
				}
			}
		}
		return -2;
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
					if (ChatColor.stripColor(name).startsWith(
							this.weapons.toReadableText()))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setType(Material material) {
		this.material = material;
	}

	public GunFireType getGft() {
		return gft;
	}

	public void setGft(GunFireType gft) {
		this.gft = gft;
	}

	public int getClipSize() {
		return clipSize;
	}

	public void setClipSize(int clipSize) {
		this.clipSize = clipSize;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getRof() {
		return rof;
	}

	public void setRof(int rof) {
		this.rof = rof;
	}

	public int getReload() {
		return reload;
	}

	public void setReload(int reload) {
		this.reload = reload;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public int getRecoil() {
		return recoil;
	}

	public void setRecoil(int recoil) {
		this.recoil = recoil;
	}

	public int getOverallammo() {
		return overallammo;
	}

	public void setOverallammo(int overallammo) {
		this.overallammo = overallammo;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setFiremode(GunFireType gft) {
		this.gft = gft;
	}

	@Override
	public void save() {
		YamlConfiguration yaml = new YamlConfiguration();
		yaml.set("firetype", this.gft.name());
		yaml.set("clipsize", clipSize);
		yaml.set("damage", damage);
		yaml.set("rof", rof);
		yaml.set("reload", reload);
		yaml.set("accuracy", accuracy);
		yaml.set("recoil", recoil);
		yaml.set("overallammo", overallammo);
		yaml.set("material", material.name());
		try {
			yaml.save(yamlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean fire(Player player) {
		if (getClipAmmo(player.getItemInHand()) == -1)
			return false;
		else if (getClipAmmo(player.getItemInHand()) == 0) {
			if (getReservedAmmo(player.getItemInHand()) > 0)
				reload(player);
			return false;
		} else {
			ItemStack is = player.getItemInHand();
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + weapons.toReadableText()
					+ " + (" + (getClipAmmo(is) - 1) + "/"
					+ getReservedAmmo(is) + ")");
			is.setItemMeta(im);
			player.setItemInHand(is);
			int accuracy = (getAccuracy() * -1) + 20;
			if (accuracy <= 0)
				accuracy = 1;
			Arrow proj = null;
			switch (gft) {
			case SCATTER_SHOT: {
				for (int i = 0; i <= 8; i++) {
					float acc = (new Random().nextInt(accuracy)) + 1;
					proj = player.getWorld().spawnArrow(
							player.getEyeLocation(),
							player.getEyeLocation().getDirection(), 6f, acc);
					proj.setShooter(player);
					proj.setMetadata("dmg", new FixedMetadataValue(minigames,
							getDamage()));
				}
			}
				break;
			case FULLY_AUTOMATIC:
			case SEMI_AUTOMATIC:
			case SINGLE_SHOT: {
				float acc = (new Random().nextFloat() * accuracy);
				proj = player.getWorld().spawnArrow(player.getEyeLocation(),
						player.getEyeLocation().getDirection(), 6f, acc);
				proj.setShooter(player);
				proj.setMetadata("dmg", new FixedMetadataValue(minigames,
						getDamage()));
			}
				break;
			default:
				break;
			}
			return true;
		}
	}

	public void reloaded(Player player) {
		ItemStack itemInHand = player.getItemInHand();
		ItemMeta im = itemInHand.getItemMeta();
		if (!im.getDisplayName().contains("Reloading"))
			return;
		String oldName = ChatColor.stripColor(im.getDisplayName());
		String[] split = oldName.split("( )");
		oldName = oldName.substring(getWeapons().toReadableText().length());
		Pattern pattern = Pattern.compile("[0-9]{1,3}");
		Matcher match = pattern.matcher(oldName);
		match.find();
		int currAmmo = Integer.parseInt(match.group());
		int clip;
		if (currAmmo - clipSize < 0) {
			clip = currAmmo;
			currAmmo = 0;
		} else {
			clip = clipSize;
			currAmmo = currAmmo -= clipSize;
		}
		im.setDisplayName(ChatColor.BLUE + weapons.toReadableText() + " + ("
				+ clip + "/" + currAmmo + ")");
		itemInHand.setItemMeta(im);
		player.setItemInHand(itemInHand);
	}

	public void cancelReload(Player player) {
		ItemStack itemInHand = player.getItemInHand();
		ItemMeta im = itemInHand.getItemMeta();
		if (!im.getDisplayName().contains("Reloading"))
			return;
		String oldName = ChatColor.stripColor(im.getDisplayName());
		oldName = oldName.substring(getWeapons().toReadableText().length());
		Pattern pattern = Pattern.compile("[0-9]{1,3}");
		Matcher match = pattern.matcher(oldName);
		match.find();
		int currAmmo = Integer.parseInt(match.group());
		im.setDisplayName(ChatColor.BLUE + weapons.toReadableText() + " + (0"
				+ "/" + currAmmo + ")");
		itemInHand.setItemMeta(im);
		player.setItemInHand(itemInHand);
	}

	public void reload(Player player) {
		ItemStack itemInHand = player.getItemInHand();
		ItemMeta im = itemInHand.getItemMeta();
		if (im.getDisplayName().contains("Reloading")
				|| getReservedAmmo(player.getItemInHand()) == 0)
			return;
		im.setDisplayName(ChatColor.BLUE + weapons.toReadableText()
				+ " + Reloading (" + getReservedAmmo(player.getItemInHand())
				+ ")");
		itemInHand.setItemMeta(im);
		player.setItemInHand(itemInHand);
	}

	public boolean isReloading(ItemStack itemInHand) {
		return itemInHand.getItemMeta().getDisplayName().contains("Reloading");
	}

}
