package com.addongaming.hcessentials.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.commands.teleport.CmdTeleRandom;

public class CommandManager implements SubPlugin {
	private JavaPlugin jp;

	public CommandManager(JavaPlugin jp) {
		this.jp = jp;
		checkConfig();
	}

	private void checkConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("cmd.teleport.random.enabled", false);
		fc.addDefault("cmd.teleport.random.distance", 500);
		fc.addDefault("cmd.teleport.random.offset", 500);
		fc.addDefault("cmd.teleport.random.cooldownmins", 10);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		jp.getCommand("bug").setExecutor(new CmdBug());
		jp.getCommand("suggest").setExecutor(new CmdSuggestion());
		if (fc.getBoolean("cmd.teleport.random.enabled")) {
			jp.getCommand("tprandom").setExecutor(
					new CmdTeleRandom(jp, fc
							.getInt("cmd.teleport.random.distance"), fc
							.getInt("cmd.teleport.random.offset"), fc
							.getInt("cmd.teleport.random.cooldownmins")));
		}
		jp.getCommand("ping").setExecutor(new CmdPing());
		return true;
	}

}
