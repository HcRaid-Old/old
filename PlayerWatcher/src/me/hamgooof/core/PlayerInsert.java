package me.hamgooof.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import lib.PatPeter.SQLibrary.SQLite;

public class PlayerInsert {
	// sqlite.query("CREATE TABLE playchat (chat_id int NOT NULL
	// AUTO_INCREMENT,playername VARCHAR(30) NOT NULL ,message VARCHAR(300),
	// ip VARCHAR(30) NOT NULL, date DATETIME, PRIMARY KEY (chat_id));");

	// sqlite.query("INSERT INTO playchat(playername, message) VALUES('testplayer', 'test string');");
	public boolean addPlayerChat(SQLite sql, String playerName, String message,
			String ip) {
		try {
			PreparedStatement ps = sql
					.getConnection()
					.prepareStatement(
							"INSERT INTO playchat(playername,message,ip,date) VALUES(?,?,?,?);");
			ps.setString(1, playerName);
			ps.setString(2, message);
			ps.setString(3, ip);
			ps.setObject(4, new java.sql.Timestamp(new Date().getTime()));
			sql.query(ps);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}

		return true;
	}// sqlite.query("CREATE TABLE playcomm ( comm_id INTEGER PRIMARY KEY
		// AUTOINCREMENT, playername VARCHAR(30) NOT NULL
		// , command VARCHAR(300), ip VARCHAR(30) NOT NULL, date DATETIME");

	public boolean addPlayerCommand(SQLite sql, String playerName,
			String command, String ip) {
		try {
			PreparedStatement ps = sql
					.getConnection()
					.prepareStatement(
							"INSERT INTO playcomm(playername,command,ip,date) VALUES(?,?,?,?);");
			ps.setString(1, playerName);
			ps.setString(2, command);
			ps.setString(3, ip);
			ps.setObject(4, new java.sql.Timestamp(new Date().getTime()));
			sql.query(ps);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}

		return true;
	}

}
