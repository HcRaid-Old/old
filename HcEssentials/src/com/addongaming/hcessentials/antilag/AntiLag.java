package com.addongaming.hcessentials.antilag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class AntiLag implements Listener, SubPlugin, CommandExecutor {
	private JavaPlugin jp;
	private Integer[] tasks = { -1 };
	List<String> list = new ArrayList<String>();

	public AntiLag(JavaPlugin jp) {
		this.jp = jp;
		jp.getServer().getPluginCommand("save").setExecutor(this);
		jp.getServer().getPluginCommand("antilag").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.isOp()) {
			Player p = (Player) sender;
			if (command.getName().equalsIgnoreCase("save")) {
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new AutoSaver(jp, p), 1l);
			} else if (command.getName().equalsIgnoreCase("antilag")) {
				jp.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(jp, new ChunkUnloader(jp, p),
								1l);
				jp.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(jp,
								new EntityDespawner(jp, p, list), 2l);
			}
		}
		return true;
	}

	@Override
	public void onDisable() {
		for (int i : tasks)
			if (i > -1)
				jp.getServer().getScheduler().cancelTask(i);
	}

	@Override
	public boolean onEnable() {
		list = jp.getConfig()
				.getStringList("antilag.entitydespawner.blacklist");
		jp.getServer().getPluginManager().registerEvents(new EntityLimit(), jp);
		List<Integer> idList = new ArrayList<Integer>();
		/*
		 * idList.add(jp .getServer() .getScheduler()
		 * .scheduleSyncRepeatingTask(jp, new ChunkUnloader(jp), 80l, 20 * 60 *
		 * 5));
		 */
			idList.add(jp
					.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(jp, new AutoSaver(jp),
							20 * 60 * 29, 20 * 60 * 30));
		idList.add(jp
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(jp, new EntityDespawner(jp, list),
						10l, 20 * 60 * 5));
		tasks = idList.toArray(new Integer[idList.size()]);
		if (jp.getConfig().getBoolean("antilag.rainstopper.enabled"))
			jp.getServer().getPluginManager()
					.registerEvents(new WeatherChange(), jp);
		if (jp.getConfig().getBoolean("antilag.alwaysday.enabled"))
			idList.add(jp
					.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(
							jp,
							new AlwaysDay(jp.getConfig().getStringList(
									"antilag.alwaysday.worlds")), 10l, 20 * 30));
		return true;
	}
}
