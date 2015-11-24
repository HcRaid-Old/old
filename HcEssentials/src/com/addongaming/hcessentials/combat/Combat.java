package com.addongaming.hcessentials.combat;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.combat.antilogging.CombatLogging;
import com.addongaming.hcessentials.combat.antilogging.CombatThread;
import com.addongaming.hcessentials.combat.dmgnotifications.DamageNotification;
import com.addongaming.hcessentials.combat.global.deathmsgs.DMTypes;
import com.addongaming.hcessentials.combat.global.deathmsgs.DeathMessages;
import com.addongaming.hcessentials.config.Config;

public class Combat implements SubPlugin {
	private JavaPlugin jp;

	public Combat(JavaPlugin jp) {
		this.jp = jp;
	}

	private void initLoad() {
		if (!jp.getDataFolder().exists())
			jp.getDataFolder().mkdirs();
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onEnable() {
		initLoad();
		setupConfig();
		setupListenersAndExecutors();
		return true;
	}

	private void setupConfig() {
		Config.Combat.dmt = DMTypes.valueOf(jp.getConfig().getString(
				"deathmessages.type"));
	}

	static CombatLogging combatLog;

	public static CombatLogging getCombatInstance() {
		return combatLog;
	}

	private void setupListenersAndExecutors() {
		if (jp.getConfig().getBoolean("combatlog.enabled")) {
			combatLog = new CombatLogging(jp.getConfig().getInt(
					"combatlog.timeout"));
			jp.getCommand("combatlog").setExecutor(combatLog);
			jp.getServer().getPluginManager().registerEvents(combatLog, jp);
			Config.Combat.enderPearlsInCombat = jp.getConfig().getBoolean(
					"combatlog.enderpearlteleport");
			Config.Combat.teleportInCombat = jp.getConfig().getBoolean(
					"combatlog.anyteleport");
			jp.getServer().getScheduler()
					.scheduleSyncRepeatingTask(jp, new CombatThread(), 50, 20);
		}
		DamageNotification dn = new DamageNotification(jp.getDataFolder(), jp);
		jp.getServer().getPluginManager().registerEvents(dn, jp);
		jp.getCommand("dtoggle").setExecutor(dn);
		DeathMessages dm = new DeathMessages(jp);
		jp.getCommand("deathmsg").setExecutor(dm);
		jp.getServer().getPluginManager().registerEvents(dm, jp);
	}

}
