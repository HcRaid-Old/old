package me.hamgooof.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	public SQLite sqlite;

	
	
	@Override
	public void onEnable() {
		//this.getConfig().
		sqlConnection();
		sqlTableCheck();
		this.getServer().getPluginManager()
				.registerEvents(new PlayerChat(sqlite), this);
		
	}

	@Override
	public void onDisable() {
		sqlite.close();
	}

	public void sqlConnection() {
		sqlite = new SQLite(this.getLogger(), "PlayerWatcher", this
				.getDataFolder().getAbsolutePath(), "PlayerWatcher");
		try {
			sqlite.open();
		} catch (Exception e) {
			this.getLogger().info(e.getMessage());
			getPluginLoader().disablePlugin(this);
		}
	}

	public void sqlTableCheck() {

		try {
			java.sql.DatabaseMetaData dbm = sqlite.getConnection()
					.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "playchat", null);
			if (!tables.next()) {
				System.out.println("Keys doesn't exist, making.");
				sqlite.query("CREATE TABLE playchat (chat_id INTEGER PRIMARY KEY AUTOINCREMENT,playername VARCHAR(30) NOT NULL ,message VARCHAR(300), ip VARCHAR(30) , date TIMESTAMP);");
				sqlite.query("CREATE TABLE playcomm ( comm_id INTEGER PRIMARY KEY AUTOINCREMENT, playername VARCHAR(30) NOT NULL , command VARCHAR(300), ip VARCHAR(30) NOT NULL, date TIMESTAMP);");
				tables.close();
			} else {
				this.getLogger().info("Tables have been created");
				tables.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
