package com.addongaming.hcessentials.serialised;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;

public class SerItemStack implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3296085073987914832L;
	private final int id, amount;
	private final byte data;
	private final short durability;
	private final Map<SerEnchantment, Integer> enchantments = new HashMap<SerEnchantment, Integer>();
	private final Map<SerEnchantment, Integer> enchantbook = new HashMap<SerEnchantment, Integer>();
	private final String itemName;
	private final List<String> lored = new ArrayList<String>();
	private final SerPotion serPotion;

	/**
	 * A serializable implementation of Bukkit's ItemStack class.
	 * 
	 * @param bukkitItemStack
	 */
	public SerItemStack(final ItemStack bukkitItemStack) {
		id = bukkitItemStack.getTypeId();
		amount = bukkitItemStack.getAmount();
		data = bukkitItemStack.getData().getData();
		durability = bukkitItemStack.getDurability();
		itemName = bukkitItemStack.getItemMeta().getDisplayName();
		if (bukkitItemStack.getEnchantments() != null
				&& !bukkitItemStack.getEnchantments().isEmpty()
				&& bukkitItemStack.getEnchantments().size() > 0) {
			for (final Entry<Enchantment, Integer> e : bukkitItemStack
					.getEnchantments().entrySet()) {
				if (e.getKey() != null) {
					enchantments.put(new SerEnchantment(e.getKey()),
							e.getValue());
				}
			}
		}
		if (bukkitItemStack.getItemMeta() != null
				&& bukkitItemStack.getItemMeta().getLore() != null)
			for (String s : bukkitItemStack.getItemMeta().getLore())
				lored.add(s);

		if (bukkitItemStack.getItemMeta() != null
				&& bukkitItemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta ems = (EnchantmentStorageMeta) bukkitItemStack
					.getItemMeta();
			if (!ems.getStoredEnchants().isEmpty()
					&& ems.getStoredEnchants().size() > 0) {
				for (final Entry<Enchantment, Integer> e : ems
						.getStoredEnchants().entrySet()) {
					if (e.getKey() != null) {
						enchantbook.put(new SerEnchantment(e.getKey()),
								e.getValue());
					}
				}
			}
		}
		if (bukkitItemStack.getType() == Material.POTION)
			serPotion = new SerPotion(bukkitItemStack);
		else
			serPotion = null;

	}

	public List<String> getLore() {
		return lored;
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return itemName;
	}

	/**
	 * Converts a SyncItemStack object into a Bukkit ItemStack object.
	 * 
	 * @param syncItemStack
	 * @return ItemStack
	 */
	public ItemStack getBukkitItemStack() {
		ItemStack is = new ItemStack(getId(), getAmount());
		is.getData().setData(getData());
		is.setDurability(getDurability());
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(getName());
		im.setLore(getLore());
		if (serPotion != null)
			im = serPotion.getPotion(im);
		is.setItemMeta(im);
		if (getEnchantments() != null && !getEnchantments().isEmpty()
				&& getEnchantments().size() > 0) {
			for (final Entry<SerEnchantment, Integer> e : getEnchantments()
					.entrySet()) {
				if (e.getKey() != null) {
					final Enchantment ench = Enchantment.getById(e.getKey()
							.getId());
					try {
						is.addEnchantment(ench, e.getValue());
					} catch (IllegalArgumentException iae) {
						is.addUnsafeEnchantment(ench, e.getValue());
					}
				}
			}
		}
		if (getEnchantmentsBook() != null && !getEnchantmentsBook().isEmpty()
				&& getEnchantmentsBook().size() > 0) {
			for (final Entry<SerEnchantment, Integer> e : getEnchantmentsBook()
					.entrySet()) {
				if (e.getKey() != null) {
					final Enchantment ench = Enchantment.getById(e.getKey()
							.getId());

					EnchantmentStorageMeta ems = (EnchantmentStorageMeta) is
							.getItemMeta();
					ems.addStoredEnchant(ench, e.getValue(), false);
					is.setItemMeta(ems);
				}
			}
		}
		return is;
	}

	public byte getData() {
		return data;
	}

	public short getDurability() {
		return durability;
	}

	public Map<SerEnchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public Map<SerEnchantment, Integer> getEnchantmentsBook() {
		return enchantbook;
	}

	public int getId() {
		return id;
	}

}