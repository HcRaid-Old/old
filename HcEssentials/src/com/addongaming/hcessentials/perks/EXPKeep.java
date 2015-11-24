package com.addongaming.hcessentials.perks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.events.EXPKeepEvent;
import com.addongaming.hcessentials.logging.DataLog;

public class EXPKeep implements Listener, SubPlugin {

	private JavaPlugin jp;
	private int hero, ender, ghast, blaze, creeper, grunt;

	public EXPKeep(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("keepexp.enabled", false);
		fc.addDefault("keepexp.hero", 100);
		fc.addDefault("keepexp.ender", 80);
		fc.addDefault("keepexp.ghast", 60);
		fc.addDefault("keepexp.blaze", 40);
		fc.addDefault("keepexp.creeper", 20);
		fc.addDefault("keepexp.grunt", 20);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("keepexp.enabled"))
			return false;
		else {
			hero = jp.getConfig().getInt("keepexp.hero");
			ender = jp.getConfig().getInt("keepexp.ender");
			ghast = jp.getConfig().getInt("keepexp.ghast");
			blaze = jp.getConfig().getInt("keepexp.blaze");
			creeper = jp.getConfig().getInt("keepexp.creeper");
			grunt = jp.getConfig().getInt("keepexp.grunt");
			jp.getServer().getPluginManager().registerEvents(this, jp);
			dl = HcEssentials.getDataLogger().addLogger("EXPKeeper");
			return true;
		}
	}

	private DataLog dl = null;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getLevel() > 0) {
			int keep;
			if (event.getEntity().hasPermission("hcraid.hero")) {
				keep = getPercentage(event.getEntity().getLevel(), hero);
			} else if (event.getEntity().hasPermission("hcraid.ender")) {
				keep = getPercentage(event.getEntity().getLevel(), ender);
			} else if (event.getEntity().hasPermission("hcraid.ghast")) {
				keep = getPercentage(event.getEntity().getLevel(), ghast);
			} else if (event.getEntity().hasPermission("hcraid.blaze")) {
				keep = getPercentage(event.getEntity().getLevel(), blaze);
			} else if (event.getEntity().hasPermission("hcraid.creeper")) {
				keep = getPercentage(event.getEntity().getLevel(), creeper);
			} else if (event.getEntity().hasPermission("hcraid.grunt")) {
				keep = getPercentage(event.getEntity().getLevel(), grunt);
			} else
				return;
			dl.logPlayer(event.getEntity(), "Old level "
					+ event.getEntity().getLevel() + " New level " + keep);
			if (keep > 0) {
				EXPKeepEvent newEv = new EXPKeepEvent(event.getEntity()
						.getName(), event.getEntity().getLevel(), keep);
				Bukkit.getServer().getPluginManager().callEvent(newEv);
				if (newEv.isCancelled())
					return;
				event.setDroppedExp(0);
				event.setNewLevel(keep);
				event.getEntity().sendMessage(
						ChatColor.GREEN + "You recovered " + keep
								+ " levels from your death!");
			}
		}
	}

	private int getPercentage(double total, int percentage) {
		System.out.println(total + "|" + percentage);
		double level = total / 100d;
		System.out.println(level);
		return (int) (level * percentage);
	}
}
