package com.addongaming.hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffects implements Listener {
	private final JavaPlugin jp;
	private HashMap<PotionEffectType, Integer> potionMap = new HashMap<PotionEffectType, Integer>();

	public PotionEffects(JavaPlugin jp) {
		this.jp = jp;
		initConfig();
		loadConfig();
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		List<String> tempList = fc.getStringList("potioneffects");
		for (String temp : tempList) {
			String[] split = temp.split("[|]");
			for (String str : split)
				System.out.println(str);
			PotionEffectType pet = PotionEffectType.getByName(split[0]);
			potionMap.put(pet, Integer.parseInt(split[1]));
		}
	}

	private void initConfig() {
		FileConfiguration fc = jp.getConfig();
		List<String> temp = new ArrayList<String>();
		temp.add(PotionEffectType.SPEED.getName() + "|" + 1);
		fc.addDefault("potioneffects", temp);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
			event.getPlayer().removePotionEffect(effect.getType());
		}
		for (PotionEffectType pet : potionMap.keySet())
			event.getPlayer()
					.addPotionEffect(
							new PotionEffect(pet, Integer.MAX_VALUE, potionMap
									.get(pet)));
	}
}
