package com.addongaming.prison.farm;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.farm.tree.TreeManager;

public class FarmManager {
	final JavaPlugin jp;

	public FarmManager(JavaPlugin jp) {
		this.jp = jp;
		new CactusManager(jp);
		new TreeManager(jp);
		new SugarCaneManager(jp);
		new WheatManager(jp);
		new MelonManager(jp);
		new PumpkinManager(jp);
	}

}
