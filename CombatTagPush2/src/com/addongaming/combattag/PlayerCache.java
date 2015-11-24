package com.addongaming.combattag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerCache extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

	}

	@EventHandler
	public void playerDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player
				&& event.getDamager() instanceof Player) {
			log("Both player");
			Player damagee = (Player) event.getEntity(), damager = (Player) event
					.getDamager();
			damager.setNoDamageTicks(0);
			damager.setMaximumNoDamageTicks(0);
			log("Damagee: max no dmg" + damagee.getMaximumNoDamageTicks()
					+ " no dmg " + damagee.getNoDamageTicks());
			log("damager: max no dmg" + damager.getMaximumNoDamageTicks()
					+ " no dmg " + damager.getNoDamageTicks());
		}
	}

	private void log(String str) {
		System.out.println(str);
	}
}
