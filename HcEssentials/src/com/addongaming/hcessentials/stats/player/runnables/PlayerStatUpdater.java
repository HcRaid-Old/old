package com.addongaming.hcessentials.stats.player.runnables;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.stats.player.DatabaseHandling;
import com.addongaming.hcessentials.stats.player.PlayerStatInstance;

public class PlayerStatUpdater implements Runnable {
	private List<PlayerStatInstance> list;
	private DatabaseHandling dbh;

	public PlayerStatUpdater(List<PlayerStatInstance> list, DatabaseHandling dbh) {
		this.list = list;
		this.dbh = dbh;
	}

	@Override
	public void run() {
		Connection c = dbh.getConnection();
		if (c == null) {
			HcEssentials.getDataLogger().getLogger("Playerstats")
					.log("ERROR IN UPDATING. Connection == null");
			return;
		}
		for (PlayerStatInstance psi : list)
			dbh.executePlayerUpdate(c, psi);
		try {
			c.close();
		} catch (SQLException e) {
		}
	}
}
