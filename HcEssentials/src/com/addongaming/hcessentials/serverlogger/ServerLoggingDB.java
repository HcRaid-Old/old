package com.addongaming.hcessentials.serverlogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.logging.DataLog;
import com.addongaming.hcessentials.uuid.UUIDSystem;

public class ServerLoggingDB {
	private final DataLog dl;
	private String serverName;
	private DatabaseHandler dh;
	private Connection con = null;

	public ServerLoggingDB(DatabaseHandler dh, String serverName) {
		this.serverName = serverName;
		this.dh = dh;
		dl = HcEssentials.getDataLogger().getLogger("ChatLogging");
	}

	public boolean firstSetup() {
		dl.log("Setting up database connections.");
		con = dh.getConnection();
		if (con != null && createTables(con)) {
			return true;
		}
		return false;
	}

	public boolean createTables(java.sql.Connection con) {
		try {
			dl.log("Creating tables");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS "
									+ serverName
									+ "chat(USERID int, MSG varchar(255), DATE varchar(255))");
			dl.log("Created tables");
			dl.log("Creating tables");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS "
									+ serverName
									+ "command(USERID int, MSG varchar(255), DATE varchar(255))");
			dl.log("Created tables");
			dl.log("Creating tables");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS "
									+ serverName
									+ "tp(USERID int, X varchar(32), Y varchar(32), Z varchar(32), DATE varchar(255))");
			dl.log("Created tables");
			dl.log("Creating tables");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS "
									+ serverName
									+ "loginout(USERID int, MSG varchar(255), DATE varchar(255))");
			dl.log("Created tables");
			return true;
		} catch (SQLException e) {
			dl.log("Error with creating tables: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return false;
	}

	public void insertChatIntoTable(List<PlayerChat> chatList) {
		Connection con = getConnection();
		try {
			String chat = "INSERT INTO " + serverName
					+ "chat(USERID,MSG,DATE) VALUES (?, ?, ?)";
			PreparedStatement ChatStatement = con.prepareStatement(chat);
			for (PlayerChat playerChat : chatList) {
				ChatStatement.setInt(1,
						UUIDSystem.getInstance().getId(playerChat.getUserId()));
				ChatStatement.setString(2, playerChat.getMsg());
				ChatStatement.setString(3, playerChat.getDate() + "");
				ChatStatement.executeUpdate();
			}
			ChatStatement.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertCMDIntoTable(List<PlayerCmd> cmdList) {
		Connection con = getConnection();
		try {
			String cmd = "INSERT INTO " + serverName
					+ "command(USERID,MSG,DATE) VALUES (?, ?, ?)";
			PreparedStatement CmdStatement = con.prepareStatement(cmd);
			for (PlayerCmd playerCmd : cmdList) {
				CmdStatement.setInt(1,
						UUIDSystem.getInstance().getId(playerCmd.getUserId()));
				CmdStatement.setString(2, playerCmd.getMsg());
				CmdStatement.setString(3, playerCmd.getDate() + "");
				CmdStatement.executeUpdate();
			}
			CmdStatement.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTPIntoTable(List<PlayerTp> tpList) {
		Connection con = getConnection();
		try {
			String tp = "INSERT INTO " + serverName
					+ "tp(USERID,X,Y,Z,DATE) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement TPStatement = con.prepareStatement(tp);
			for (PlayerTp playerTp : tpList) {
				TPStatement.setInt(1, playerTp.getUserId());
				TPStatement.setInt(2, playerTp.getLocX());
				TPStatement.setInt(3, playerTp.getLocY());
				TPStatement.setInt(4, playerTp.getLocZ());
				TPStatement.setString(5, playerTp.getDate() + "");
				TPStatement.executeUpdate();
			}
			TPStatement.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertLogIntoTable(List<PlayerLoginout> logList) {
		Connection con = getConnection();
		try {
			String log = "INSERT INTO " + serverName
					+ "loginout(USERID,MSG,DATE) VALUES (?,?,?)";
			PreparedStatement LogStatement = con.prepareStatement(log);
			for (PlayerLoginout playerLog : logList) {
				LogStatement.setInt(1,
						UUIDSystem.getInstance().getId(playerLog.getUserId()));
				LogStatement.setString(2, playerLog.getMsg());
				LogStatement.setString(3, playerLog.getDate() + "");
				LogStatement.executeUpdate();

			}
			LogStatement.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		try {
			if (this.con == null || this.con.isClosed()) {
				this.con = dh.getConnection();
				return con;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return con;
	}
}