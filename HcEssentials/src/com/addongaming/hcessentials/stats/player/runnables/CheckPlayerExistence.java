package com.addongaming.hcessentials.stats.player.runnables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckPlayerExistence implements Runnable {
	private String name;
	private Connection con;

	public CheckPlayerExistence(String playerName, Connection con) {
		this.name = playerName;
		this.con = con;

	}

	@Override
	public void run() {
		try {
			ResultSet rs = con.createStatement().executeQuery(
					"SELECT username FROM tblPlayerStats WHERE username = '"
							+ name + "'");
			if (!rs.next()) {
				rs.close();
				con.createStatement().execute(
						"INSERT INTO tblPlayerStats(userName) VALUES('" + name
								+ "')");
			} else {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
