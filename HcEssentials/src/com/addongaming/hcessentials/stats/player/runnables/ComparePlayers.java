package com.addongaming.hcessentials.stats.player.runnables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.stats.player.DatabaseHandling;
import com.addongaming.hcessentials.stats.player.EPlayerStat;
import com.addongaming.hcessentials.stats.player.PlayerStatInstance;

public class ComparePlayers implements Runnable {
	private Player playerName1;
	private String playerName2;
	private PlayerStatInstance playerInstance1, playerInstance2;
	private Connection con;
	private DatabaseHandling dh;

	public ComparePlayers(Player playerName1, PlayerStatInstance p1,
			String playerName2, PlayerStatInstance p2, Connection con,
			DatabaseHandling dh) {
		this.playerName1 = playerName1;
		this.playerName2 = playerName2;
		playerInstance1 = p1;
		playerInstance2 = p2;
		this.con = con;
		this.dh = dh;

	}

	@Override
	public void run() {
		try {
			ResultSet rs = con.createStatement().executeQuery(
					"SELECT username FROM tblPlayerStats WHERE username = '"
							+ playerName2 + "'");
			if (!rs.next()) {
				rs.close();
				playerName1
						.sendMessage("Player " + playerName2 + " not found.");
				return;
			} else {
				rs.close();
			}
			PlayerStatInstance psi1 = dh.getStoredDataAsInstance(
					playerName1.getName(), con, false);
			PlayerStatInstance psi2 = dh.getStoredDataAsInstance(playerName2,
					con, true);
			psi1.pauseTimer();
			psi2.pauseTimer();
			psi1.merge(playerInstance1);
			if (playerInstance2 != null)
				psi2.merge(playerInstance2);
			List<String> messageList = new ArrayList<String>();
			messageList.add(ChatColor.GREEN + "-----" + ChatColor.BLUE
					+ "[ALPHA] Player Stats" + ChatColor.GREEN + "-----");
			messageList.add(ChatColor.GREEN + "   Your stats " + ChatColor.BLUE
					+ "| " + ChatColor.GREEN + playerName2 + " stats");
			messageList.add(ChatColor.DARK_GREEN + "Your time online: "
					+ ChatColor.GREEN + psi1.getLoggedInString());
			messageList.add(ChatColor.DARK_GREEN + playerName2
					+ " time online: " + ChatColor.GREEN
					+ psi2.getLoggedInString());
			for (EPlayerStat eps : EPlayerStat.values())
				if (psi1.getStat(eps) > 0) {
					if (psi2.getStat(eps) > 0)
						messageList.add(ChatColor.DARK_GREEN
								+ eps.getReadableName() + ": "
								+ ChatColor.GREEN + psi1.getStat(eps) + " | "
								+ psi2.getStat(eps));
					else
						messageList.add(ChatColor.DARK_GREEN
								+ eps.getReadableName() + ": "
								+ ChatColor.GREEN + psi1.getStat(eps) + " | 0");
				} else if (psi2.getStat(eps) > 0) {
					messageList.add(ChatColor.DARK_GREEN
							+ eps.getReadableName() + ": " + ChatColor.GREEN
							+ "0 | " + psi2.getStat(eps));
				}
			messageList.add(ChatColor.GREEN + "-----" + ChatColor.BLUE
					+ "[ALPHA] Player Stats" + ChatColor.GREEN + "-----");
			if (playerName1 == null || !playerName1.isOnline())
				return;
			playerName1.sendMessage(messageList.toArray(new String[messageList
					.size()]));
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
