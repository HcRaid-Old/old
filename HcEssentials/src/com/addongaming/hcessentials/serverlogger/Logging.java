package com.addongaming.hcessentials.serverlogger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.logging.DataLog;

public class Logging implements SubPlugin {

	private JavaPlugin jp;
	private DataLog dl;
	private ServerLoggingDB dh;
	private DatabaseHandler dbHandler;

	public Logging(JavaPlugin jp) {
		this.jp = jp;
		dl = HcEssentials.getDataLogger().addLogger("ChatLogging");
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		setupConfig();
		if (!jp.getConfig().getBoolean("chatlog.enabled"))
			return false;
		dl.log("Loading Chat Logging...");
		if (!DatabaseManagement.hasInstance()) {
			dl.log("Database management has no instance.");
			return false;
		}
		String servername = jp.getConfig().getString("chatlog.server");
		String database = jp.getConfig().getString("chatlog.db.database");
		dbHandler = DatabaseManagement.getInstance().addDatabase(database);
		if (dbHandler == null) {
			dl.log("DBHandler is null onEnable()");
			return false;
		}
		dh = new ServerLoggingDB(dbHandler, servername);
		if (!dh.firstSetup())
			return false;
		jp.getServer().getPluginManager()
				.registerEvents(new LogListenerDB(jp, dh), jp);
		return true;
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("chatlog.enabled", false);
		fc.addDefault("chatlog.server", "servername");
		fc.addDefault("chatlog.db.database", "serverlog");
		fc.options().copyDefaults(true);
		jp.saveConfig();

	}

}
