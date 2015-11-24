package com.addongaming.prison.jail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.core.Main;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PvPScheduler implements Runnable {
	private static boolean pvp = false;
	public static boolean isWarOn() {
		return pvp;
	}
	int counter = -1;
	private World defaultWorld = Bukkit.getWorld("world");

	List<String> pvpToggleable = new ArrayList<String>();

	public PvPScheduler(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.pvpareas.toggleable", new ArrayList<String>() {
			private static final long serialVersionUID = 4196976682916466525L;

			{
				add("corridor1");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
		jp.reloadConfig();
		pvpToggleable = fc.getStringList("prison.pvpareas.toggleable");
	}

	private void broadcastpvp(boolean pvpAllow) {
		if (pvpAllow)
			Bukkit.broadcastMessage(ChatColor.RED
					+ ""
					+ ChatColor.BOLD
					+ "A rebellion has started in the prison, PvP is now on around the prison.");
		else
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD
					+ "The rebellion has been contained. PvP is now disabled.");
	}

	@Override
	public void run() {
		if (counter == -1) {
			setFlagStates(false);
			broadcastpvp(false);
			pvp = false;
		}
		counter++;
		if (counter == 10) {
			setFlagStates(true);
			broadcastpvp(true);
			pvp = true;
		} else if (counter == 15) {
			counter = 0;
			broadcastpvp(false);
			setFlagStates(false);
			pvp = false;
		}
	}

	private void setFlagStates(boolean allow) {
		Map<String, ProtectedRegion> map = Main.wg.getGlobalRegionManager()
				.get(defaultWorld).getRegions();
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String next = iter.next();
			if (pvpToggleable.contains(next.toLowerCase())) {
				Main.wg.getGlobalRegionManager()
						.get(defaultWorld)
						.getRegion(next)
						.setFlag(DefaultFlag.PVP,
								(allow ? State.ALLOW : State.DENY));
			}
		}
	}
}
