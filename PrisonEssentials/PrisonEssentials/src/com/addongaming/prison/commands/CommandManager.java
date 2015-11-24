package com.addongaming.prison.commands;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.commands.cmd.Balance;
import com.addongaming.prison.commands.cmd.ClassSkills;
import com.addongaming.prison.commands.cmd.ID;
import com.addongaming.prison.commands.cmd.Pay;
import com.addongaming.prison.commands.cmd.Spawn;
import com.addongaming.prison.commands.cmd.WarpCmd;

public class CommandManager {
	private JavaPlugin jp;

	public CommandManager(JavaPlugin jp) {
		this.jp = jp;
		setupCommands();
	}

	private void setupCommands() {
		jp.getCommand("balance").setExecutor(new Balance());
		jp.getCommand("skill").setExecutor(new ClassSkills());
		jp.getCommand("id").setExecutor(new ID());
		jp.getCommand("pay").setExecutor(new Pay());
		jp.getCommand("spawn").setExecutor(new Spawn(jp));
		jp.getCommand("warp").setExecutor(new WarpCmd(jp));
	}
}
