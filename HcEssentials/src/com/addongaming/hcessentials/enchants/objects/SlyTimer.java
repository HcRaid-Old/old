package com.addongaming.hcessentials.enchants.objects;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlyTimer implements Runnable {

	@Override
	public void run() {
		if (Sly.hiddenPlayers.isEmpty())
			return;
		for (Iterator<String> iter = Sly.hiddenPlayers.iterator(); iter
				.hasNext();) {
			Player p = Bukkit.getPlayer(iter.next());
			if (p == null) {
				iter.remove();
				continue;
			} else if (!p.isSneaking()) {
				Sly.addToSlyTime(p.getName());
				p.removePotionEffect(PotionEffectType.BLINDNESS);
				iter.remove();
				for (Player pl : Bukkit.getOnlinePlayers())
					pl.showPlayer(p);
				continue;
			}
			if (p.getInventory().getBoots().getDurability() >= p.getInventory()
					.getBoots().getType().getMaxDurability()) {
				iter.remove();
				p.removePotionEffect(PotionEffectType.BLINDNESS);
				p.getInventory().setBoots(new ItemStack(Material.AIR));
				for (Player pl : Bukkit.getOnlinePlayers())
					pl.showPlayer(p);
				Sly.addToSlyTime(p.getName());
				continue;
			} else {
				if (!p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.BLINDNESS, 160, 1));
				} else {
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.BLINDNESS, 160, 1));
				}
				int currentDura = 0;
				p.getInventory()
						.getBoots()
						.setDurability(
								(short) (p.getInventory().getBoots()
										.getDurability() + 1));
				if (p.getInventory().getBoots().getType()
						.equals(Material.DIAMOND_BOOTS)) {
					currentDura = p.getInventory().getBoots().getType()
							.getMaxDurability()
							- p.getInventory().getBoots().getDurability();
					p.sendMessage(ChatColor.YELLOW
							+ "Sly I  "
							+ currentDura
							+ " / "
							+ p.getInventory().getBoots().getType()
									.getMaxDurability());
				}
			}
		}
	}
}
