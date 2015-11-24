package com.addongaming.hcessentials.vote;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;

public class Voting implements SubPlugin {
	private JavaPlugin jp;

	public Voting(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("voting.enabled", false);
		fc.addDefault("voting.linkoverride", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("voting.enabled")) {
			return false;
		}
		HcEssentials.getDataLogger().addLogger("Voting");
		File mainFolder = new File(jp.getDataFolder() + File.separator
				+ "Voting");
		mainFolder.mkdirs();
		if (jp.getConfig().getBoolean("voting.linkoverride"))
			jp.getServer()
					.getPluginManager()
					.registerEvents(
							new VotingCommandOverride(new File(mainFolder
									+ File.separator + "VotingSites.txt")), jp);
		return true;
	}
}
