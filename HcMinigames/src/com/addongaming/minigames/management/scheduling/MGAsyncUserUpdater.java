package com.addongaming.minigames.management.scheduling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.uuid.UUIDSystem;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;

public class MGAsyncUserUpdater implements Runnable {

	private MinigameUser mg;
	private DatabaseHandler dbHandler;
	private int timeout = 0;

	public MGAsyncUserUpdater(MinigameUser minigameUser,
			DatabaseHandler dbHandler) {
		this.mg = minigameUser;
		this.dbHandler = dbHandler;
	}

	@Override
	public void run() {
		String query = "SELECT small_lootbox,medium_lootbox,large_lootbox,bank_currency,overall_points_earnt FROM MGPlayerData WHERE userId = ?";
		try {
			PreparedStatement preparedStatement = dbHandler.getConnection()
					.prepareStatement(query);
			preparedStatement.setInt(1,
					UUIDSystem.getInstance().getId(mg.getName()));
			ResultSet rs = preparedStatement.executeQuery();
			if (!rs.next()) {
				timeout++;
				if (timeout > 5) {
					return;
				} else {
					sleep(1000);
					run();
					return;
				}
			} else {
				int small = rs.getInt("small_lootbox"), med = rs
						.getInt("medium_lootbox"), large = rs
						.getInt("large_lootbox"), bank = rs
						.getInt("bank_currency"), overall_points = rs
						.getInt("overall_points_earnt");
				mg.setSmallLoot(small);
				mg.setMedLoot(med);
				mg.setLargeLoot(large);
				mg.setBankPoints(bank);
				mg.setOverallScore(overall_points);
				mg.setFetchingInfo(false);
				HcMinigames
						.getInstance()
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(HcMinigames.getInstance(),
								new Runnable() {

									@Override
									public void run() {
										mg.refresh();
									}
								});
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			timeout++;
			if (timeout > 5) {
				return;
			} else {
				sleep(1000);
				run();
				return;
			}
		}
	}

	private void sleep(int i) {
		Object a = new Object();
		synchronized (a) {
			try {
				a.wait(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
