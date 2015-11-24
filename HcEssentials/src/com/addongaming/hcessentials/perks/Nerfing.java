package com.addongaming.hcessentials.perks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.SubPlugin;

public class Nerfing implements Listener, SubPlugin {
	private JavaPlugin jp;

	public Nerfing(JavaPlugin jp) {
		this.jp = jp;
	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			for (PotionEffect pe : p.getActivePotionEffects()) {
				if (pe.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
						event.setDamage(event.getDamage() * 0.63);					
				}
			}
		}
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}
}
