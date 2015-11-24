package com.addongaming.minigames.management.scheduling;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.uuid.UUIDSystem;

public class MGAsyncDatabaseRunnable implements Runnable {
	private String query;
	private DatabaseHandler databaseHandler;
	private LinkedHashMap<String, Object> objMap = new LinkedHashMap<String, Object>();
	private int timeout = 0;
	public static final String UUID = "USERID";

	public MGAsyncDatabaseRunnable(String query, DatabaseHandler databaseHandler) {
		this.query = query;
		this.databaseHandler = databaseHandler;
	}

	public synchronized void addString(String key, String value) {
		objMap.put(key, value);
	}

	public synchronized void addInt(String key, int value) {
		objMap.put(key, value);
	}

	@Override
	public void run() {
		try {
			PreparedStatement ps = databaseHandler.getConnection()
					.prepareStatement(query);
			int counter = 1;
			for (String key : objMap.keySet()) {
				if (objMap.get(key) instanceof String) {
					if (key.equalsIgnoreCase("USERID"))
						ps.setInt(
								counter,
								UUIDSystem.getInstance().getId(
										(String) objMap.get(key)));
					else
						ps.setString(counter, (String) objMap.get(key));
				} else if (objMap.get(key) instanceof Integer) {
					ps.setInt(counter, (Integer) objMap.get(key));
				}
				counter++;
			}
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			System.err
					.println("Error whilst executing prepared statement. Timeout no: "
							+ timeout);
			System.out.println("Query: " + query);
			if (objMap.isEmpty())
				System.err.println("Extra parameters are empty");
			else {
				for (String key : objMap.keySet()) {
					if (objMap.get(key) instanceof String)
						System.err.println("Key: " + key + " = "
								+ (String) objMap.get(key));
					else if (objMap.get(key) instanceof Integer)
						System.err.println("Key: " + key + " = "
								+ (Integer) objMap.get(key));
				}
			}
			e.printStackTrace();
			if (timeout == 5)
				return;
			Object a = new Object();
			synchronized (a) {
				try {
					a.wait(10000);
					timeout++;
					run();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
