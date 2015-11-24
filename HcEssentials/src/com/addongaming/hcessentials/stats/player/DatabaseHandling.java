package com.addongaming.hcessentials.stats.player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.logging.DataLog;

public class DatabaseHandling {
	private final String sqlurl;
	private final Properties connectionProperties;
	private final DataLog dl;

	public DatabaseHandling(String url, String database, String username,
			String password, String port) {
		DriverManager.setLoginTimeout(10);
		sqlurl = "jdbc:mysql://" + url + ":" + port + "/" + database;
		connectionProperties = new Properties();
		connectionProperties.put("user", username);
		connectionProperties.put("password", password);
		dl = HcEssentials.getDataLogger().getLogger("Playerstats");
	}

	public boolean firstSetup() {
		dl.log("Setting up database connections.");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dl.log("Gained instance of driver");
			DriverManager.getConnection(sqlurl, connectionProperties).close();
			dl.log("Registered connection with database sucessfully.");
			return true;
		} catch (InstantiationException e) {
			dl.log("Error, please check logs for more information.");
			dl.log(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			dl.log("Error, please check logs for more information.");
			dl.log(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			dl.log("Error, please check logs for more information.");
			dl.log(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			dl.log("Error, please check logs for more information.");
			dl.log(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return false;
	}

	public void executePlayerUpdate(java.sql.Connection c,
			PlayerStatInstance inst) {
		String query = generateQuery(inst);
		try {
			c.createStatement().execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String generateQuery(PlayerStatInstance inst) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE tblPlayerStats SET ");
		sb.append("playTime = playTime + " + inst.getTimeLoggedIn() + ", ");
		for (EPlayerStat eps : EPlayerStat.values())
			if (inst.getStat(eps) > 0) {
				sb.append(eps.getName() + " = " + eps.getName() + " + "
						+ inst.getStat(eps) + ", ");
			}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("WHERE username = '" + inst.getPlayerName() + "'");
		return sb.toString();
	}

	public boolean createTables(java.sql.Connection con) {
		try {
			dl.log("Creating tables");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS tblPlayerStats(userId INT PRIMARY KEY AUTO_INCREMENT, "
									+ "userName VARCHAR( 16 ) NOT NULL ,"
									+ "playTime BIGINT DEFAULT 0,"
									+ "killedPlayers INT DEFAULT 0,"
									+ "killedMobs INT DEFAULT 0,"
									+ "killedSelf INT DEFAULT 0,"
									+ "blocksBroken INT DEFAULT 0,"
									+ "blocksPlaced INT DEFAULT 0 ,"
									+ "itemsEnchanted INT DEFAULT 0,"
									+ "moneyEarnt INT DEFAULT 0,"
									+ "moneySpent INT DEFAULT 0,"
									+ "itemsRecycled INT DEFAULT 0,"
									+ "bountiesIssued INT DEFAULT 0,"
									+ "bountiesClaimed INT DEFAULT 0)");
			dl.log("Created tables");
			return true;
		} catch (SQLException e) {
			dl.log("Error with creating tables: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return false;
	}

	public PlayerStatInstance getStoredDataAsInstance(String playerName,
			Connection con, boolean closeCon) {
		PlayerStatInstance psi = new PlayerStatInstance(playerName);
		try {
			ResultSet rs = con.prepareStatement(
					"SELECT * FROM tblPlayerStats WHERE Username = '"
							+ playerName + "'").executeQuery();
			int timeout = 0;
			while (!rs.next()) {
				if (timeout++ > 500)
					return null;
			}
			psi.setTimeLoggedIn(rs.getLong("playTime"));
			for (EPlayerStat eps : EPlayerStat.values()) {
				psi.setStat(eps, (double) rs.getInt(eps.getName()));
			}
			rs.close();
			if (closeCon)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (closeCon)
					con.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		return psi;
	}

	public java.sql.Connection getConnection() {
		try {
			return DriverManager.getConnection(sqlurl, connectionProperties);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
