package com.addongaming.hcessentials.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.minigames.games.DTC;
import com.addongaming.hcessentials.minigames.games.OneVOne;
import com.addongaming.hcessentials.minigames.games.RedvBlue;

public class MiniGames implements SubPlugin, CommandExecutor {
	private JavaPlugin jp;
	private List<Minigame> minigameList = new ArrayList<Minigame>();
	public static MiniGames mg;

	public MiniGames(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("minigames.enabled", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		mg = this;
	}

	public static MiniGames getMinigameInstance() {
		return mg;
	}

	@Override
	public void onDisable() {

	}

	public boolean isInGame(String str) {
		for (Minigame mg : minigameList)
			if (mg.isInGame(str))
				return true;
		return false;

	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("minigames.enabled"))
			return false;
		minigameList.add(new OneVOne(jp));
		minigameList.add(new RedvBlue(jp));
		minigameList.add(new DTC(jp));
		for (Minigame mg : minigameList)
			if (mg.onEnable()) {
				jp.getCommand(mg.getMinigameNotation()).setExecutor(this);
			}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		for (Minigame mm : minigameList)
			if (mm.getMinigameNotation().equalsIgnoreCase(arg2)) {
				mm.commandIssued(arg0, arg3);
				return true;
			}
		return true;
	}
}
