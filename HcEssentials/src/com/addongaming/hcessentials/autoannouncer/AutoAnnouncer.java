package com.addongaming.hcessentials.autoannouncer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class AutoAnnouncer implements SubPlugin {
	private JavaPlugin jp;
	List<String> broadcastJList;
	int scheduler = -1;
	String prefix = "&6[&4Broadcast&6]";

	public AutoAnnouncer(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {

	}

	int timeInMinutes;

	@Override
	public boolean onEnable() {
		setupConfig();
		if (!jp.getConfig().getBoolean("announcer.enabled"))
			return false;
		broadcastJList = jp.getConfig().getStringList("announcer.json");
		timeInMinutes = jp.getConfig().getInt("announcer.minutesbetween");
		prefix = jp.getConfig().getString("announcer.prefix");
		resetJScheduler();
		return true;
	}

	private void resetJScheduler() {
		if (scheduler >= 0)
			jp.getServer().getScheduler().cancelTask(scheduler);
		Collections.shuffle(broadcastJList);
		jp.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(jp,
						new AnnouncingJTask(prefix, broadcastJList), 20 * 60L,
						20 * 60 * timeInMinutes);
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("announcer.enabled", true);
		fc.addDefault("announcer.minutesbetween", 10);
		List<String> configJList = new ArrayList<String>() {
			{
				this.add("{text:\"[\",color:gold,extra:[{text:\"Broadcast\",color:dark_red},{text:\"]\",color:gold},{text:\" Click this to visit our forums!\",color:green,clickEvent:{action:open_url,value:\"http://www.hcraid.com\"}}]}");
				this.add(" &aJoin an talk on our Temspeak server! ts.hcraid.com");
			}
		};
		fc.addDefault("announcer.prefix", prefix);
		fc.addDefault("announcer.json", configJList);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

}
