package com.addongaming.hcessentials.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseHandler {
	private final String name;
	private final String sqlUrl;
	private final Properties connectionProperties;

	public DatabaseHandler(String name, String sqlUrl,
			Properties connectionProperties) {
		this.name = name;
		this.sqlUrl = sqlUrl;
		this.connectionProperties = connectionProperties;
	}

	public synchronized Connection getConnection() {
		try {
			return DriverManager.getConnection(sqlUrl, connectionProperties);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getName() {
		return name;
	}
}
