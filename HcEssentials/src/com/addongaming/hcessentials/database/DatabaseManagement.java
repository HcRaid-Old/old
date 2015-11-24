package com.addongaming.hcessentials.database;

import java.sql.DriverManager;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.utils.AtomicArrayList;

public class DatabaseManagement implements Listener, SubPlugin {
	private final JavaPlugin jp;
	private static DatabaseManagement instance = null;
	private Properties connectionProperties;
	private String sqlurl;
	AtomicArrayList<DatabaseHandler> databaseList = new AtomicArrayList<DatabaseHandler>();

	public DatabaseManagement(JavaPlugin jp) {
		this.jp = jp;
		checkConfig();
		loadConfig();
		if (canConnect())
			instance = this;
	}

	public static boolean hasInstance() {
		return instance != null;
	}

	public static DatabaseManagement getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (canConnect())
			return true;
		return false;
	}

	private boolean canConnect() {
		return canConnect(null);
	}

	private boolean canConnect(String database) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection(sqlurl
					+ (database != null ? "/" + database : ""),
					connectionProperties) != null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		String url = fc.getString("database.url");
		int port = fc.getInt("database.port");
		String user = fc.getString("database.username");
		String pass = fc.getString("database.password");
		sqlurl = "jdbc:mysql://" + url + ":" + port;
		System.out.println(sqlurl);
		connectionProperties = new Properties();
		connectionProperties.put("user", user);
		connectionProperties.put("password", pass);
	}

	public synchronized DatabaseHandler addDatabase(String database) {
		if (!hasDatabase(database)) {
			if (!canConnect(database)) {
				return null;
			} else
				databaseList.add(new DatabaseHandler(database, sqlurl + "/"
						+ database, connectionProperties));
		}
		return getDatabase(database);
	}

	private DatabaseHandler getDatabase(String database) {
		for (DatabaseHandler dh : databaseList)
			if (dh.getName().equalsIgnoreCase(database))
				return dh;
		return null;
	}

	private boolean hasDatabase(String database) {
		for (DatabaseHandler dh : databaseList)
			if (dh.getName().equalsIgnoreCase(database))
				return true;
		return false;
	}

	private void checkConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("database.url", "127.0.0.1");
		fc.addDefault("database.port", 3306);
		fc.addDefault("database.username", "username");
		fc.addDefault("database.password", "password1234");
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

}
