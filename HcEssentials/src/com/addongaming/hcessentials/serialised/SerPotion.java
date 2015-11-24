package com.addongaming.hcessentials.serialised;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class SerPotion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7920394569158123748L;
	private final Map<String, Object>[] potions;

	/**
	 * A serializable implementation of Bukkit's Enchantment class.
	 * 
	 * @param e
	 *            (Enchantment)
	 */
	@SuppressWarnings("unchecked")
	public SerPotion(final ItemStack potion) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		for (PotionEffect pe : pm.getCustomEffects()) {
			System.out
					.println("Adding potion effect " + pe.getType().getName());
			list.add(pe.serialize());
		}
		this.potions = list.toArray(new Map[list.size()]);
		System.out.println("Potions size: " + potions.length);
	}

	public PotionMeta getPotion(ItemMeta im) {
		System.out.println("Potions size: " + potions.length);
		PotionMeta pm = (PotionMeta) im;
		for (Map<String, Object> map : potions) {
			pm.addCustomEffect(new PotionEffect(map), true);
		}
		for (PotionEffect pe : pm.getCustomEffects())
			System.out.println("Potion effect " + pe.getType().getName()
					+ " duration " + pe.getDuration());
		return pm;
	}
}