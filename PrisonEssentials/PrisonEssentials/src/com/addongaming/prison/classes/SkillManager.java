package com.addongaming.prison.classes;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.classes.skills.assassin.SneakAttack;
import com.addongaming.prison.classes.skills.murderer.DeepCut;
import com.addongaming.prison.classes.skills.thief.PickPocket;
import com.addongaming.prison.jail.PvPScheduler;

public class SkillManager {
	private JavaPlugin jp;

	public SkillManager(JavaPlugin jp) {
		this.jp = jp;
		setupGeneral();
		setupThief();
		setupMurderer();
		setupAssassin();
	}

	private void setupAssassin() {
		jp.getServer().getPluginManager().registerEvents(new SneakAttack(), jp);

	}

	private void setupGeneral() {
		// Character
		jp.getServer().getPluginManager()
				.registerEvents(new CharacterLeveller(), jp);
		jp.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(jp, new PvPScheduler(jp), 10L,
						20 * 60L);
	}

	private void setupMurderer() {
		jp.getServer().getPluginManager().registerEvents(new DeepCut(), jp);

	}

	private void setupThief() {
		jp.getServer().getPluginManager().registerEvents(new PickPocket(), jp);
	}
}
