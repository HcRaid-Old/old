package com.addongaming.hcessentials.logging;

import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

public class DataLogging {
	private HashMap<String, DataLog> dataMap = new HashMap<String, DataLog>();
	private JavaPlugin jp;

	public DataLogging(JavaPlugin jp) {
		this.jp = jp;
	}

	public DataLog addLogger(String name) {
		File folder = new File(jp.getDataFolder() + File.separator + "Logs"
				+ File.separator + name);
		if (!folder.exists())
			folder.mkdirs();
		DataLog dl = new DataLog(name, folder);
		dataMap.put(name, dl);
		return dl;
	}

	public DataLog getLogger(String name) {
		return dataMap.get(name);
	}

}
