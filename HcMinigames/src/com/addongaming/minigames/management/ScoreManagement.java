package com.addongaming.minigames.management;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.utils.AtomicArrayList;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MGConfig;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.scheduling.MGAsyncDatabaseRunnable;
import com.addongaming.minigames.management.scheduling.MGAsyncUserUpdater;
import com.addongaming.minigames.management.scheduling.ScoreTicker;
import com.addongaming.minigames.management.score.ODeathLog;
import com.addongaming.minigames.minigames.ArenaGame;

public class ScoreManagement {
	private HcMinigames minigames;
	private DatabaseHandler dbHandler;

	public ScoreManagement(HcMinigames minigames) {
		this.minigames = minigames;
		if (!DatabaseManagement.hasInstance()) {
			System.err
					.println("HcEssentials doesn't have DB Management set-up.");
			minigames.getServer().getPluginManager().disablePlugin(minigames);
			return;
		} else if (DatabaseManagement.getInstance() == null) {
			System.err
					.println("HcEssentials doesn't have DB Management set-up.");
			minigames.getServer().getPluginManager().disablePlugin(minigames);
			return;
		} else {
			DatabaseHandler dbHandler = DatabaseManagement.getInstance()
					.addDatabase(MGConfig.database);
			if (dbHandler == null) {
				System.err
						.println("DB Handler is null, scoremanagement constructor");
				minigames.getServer().getPluginManager()
						.disablePlugin(minigames);
				return;
			}
			this.dbHandler = dbHandler;
			initDatabases();
			minigames
					.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(minigames,
							new ScoreTicker(this), 5l, 5l);
		}
	}

	private void initDatabases() {
		Connection con = dbHandler.getConnection();
		try {
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS MGPlayerData (userId INT PRIMARY KEY AUTO_INCREMENT, "
									+ "playername VARCHAR( 16 ) NOT NULL , wins INT DEFAULT 0, losses INT DEFAULT 0, kills INT DEFAULT 0, deaths INT DEFAULT 0, small_lootbox INT DEFAULT 0, medium_lootbox INT DEFAULT 0, large_lootbox INT DEFAULT 0, games_played INT DEFAULT 0, bank_currency INT DEFAULT 0, overall_points_earnt INT DEFAULT 0);");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS MGDeathLog (username VARCHAR(16),killer VARCHAR(16), weapon VARCHAR(30), points INT, arena VARCHAR(30), gamemode VARCHAR(30), time INT);");
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS MGArenaData(gamemode VARCHAR(30), times_played INT default 0, arenatype VARCHAR(30), upvotes INT default 0, downvotes INT default 0,primary key (gamemode,arenatype));");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void playerDeath(String killer, String death, ItemStack weapon,
			int points, ArenaGame ag) {
		WeaponManagement wm = minigames.getManagement().getWeaponManagement();
		String weaponName;
		if (weapon == null) {
			weaponName = "fist";
		} else if (wm.isWeapon(weapon)) {
			weaponName = wm.getWeapon(weapon).getWeapons().toReadableText();
		} else {
			if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName())
				weaponName = weapon.getItemMeta().getDisplayName();
			else
				weaponName = weapon.getType().name();
		}
		ODeathLog odl = new ODeathLog(death, killer, weaponName, ag.getArena()
				.getArenaType(), ag.getLobby().getGameMode().name(), points,
				System.currentTimeMillis());
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(
				odl.getQuery(), dbHandler);
		odl.format(runnable);
		runTaskAsync(runnable);
	}

	public void registerArena(GameMode gameMode, String arenaType) {
		String query = "INSERT INTO MGArenaData (gamemode, arenatype) VALUES('"
				+ gameMode.name() + "','" + arenaType
				+ "') ON DUPLICATE KEY UPDATE gamemode='" + gameMode.name()
				+ "';";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		scheduleRunnable(runnable);
	}

	private AtomicArrayList<MGAsyncDatabaseRunnable> runnables = new AtomicArrayList<MGAsyncDatabaseRunnable>();

	private void scheduleRunnable(MGAsyncDatabaseRunnable runnable) {
		runnables.add(runnable);
	}

	public void runSchedulers() {
		for (Iterator<MGAsyncDatabaseRunnable> iterator = runnables.iterator(); iterator
				.hasNext();) {
			MGAsyncDatabaseRunnable next = iterator.next();
			minigames.getServer().getScheduler()
					.runTaskAsynchronously(minigames, next);
			iterator.remove();
		}
	}

	public void runTaskAsync(MGAsyncDatabaseRunnable next) {
		minigames.getServer().getScheduler()
				.runTaskAsynchronously(minigames, next);
	}

	public void incrementArena(GameMode gameMode, String arenaType) {
		String query = "UPDATE MGArenaData SET times_played = times_played + 1 WHERE gamemode = '"
				+ gameMode.name() + "' AND arenatype = '" + arenaType + "'";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		scheduleRunnable(runnable);

	}

	public void upvoteArena(GameMode gameMode, String arenaType) {
		String query = "UPDATE MGArenaData SET upvotes = upvotes+1 WHERE gamemode = '"
				+ gameMode.name() + "' AND arenatype = '" + arenaType + "'";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		scheduleRunnable(runnable);
	}

	public void downvoteArena(GameMode gameMode, String arenaType) {
		String query = "UPDATE MGArenaData SET downvotes = downvotes+1 WHERE gamemode = '"
				+ gameMode.name() + "' AND arenatype = '" + arenaType + "'";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerWin(String player) {
		String query = "UPDATE MGPlayerData SET wins = wins + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerSmallLoot(String player) {
		String query = "UPDATE MGPlayerData SET small_lootbox = small_lootbox + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void decrementPlayerSmallLoot(String player) {
		String query = "UPDATE MGPlayerData SET small_lootbox = small_lootbox - 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void incrementPlayerMedLoot(String player) {
		String query = "UPDATE MGPlayerData SET medium_lootbox = medium_lootbox + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void decrementPlayerMedLoot(String player) {
		String query = "UPDATE MGPlayerData SET medium_lootbox = medium_lootbox - 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void incrementPlayerLargeLoot(String player) {
		String query = "UPDATE MGPlayerData SET large_lootbox = large_lootbox + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void decrementPlayerLargeLoot(String player) {
		String query = "UPDATE MGPlayerData SET large_lootbox = large_lootbox - 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		runTaskAsync(runnable);
	}

	public void incrementPlayerLoss(String player) {
		String query = "UPDATE MGPlayerData SET losses = losses + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerKills(String player) {
		String query = "UPDATE MGPlayerData SET kills = kills + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerDeaths(String player) {
		String query = "UPDATE MGPlayerData SET deaths = deaths + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerGamesPlayed(String player) {
		String query = "UPDATE MGPlayerData SET games_played = games_played + 1 WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementPlayerBankCurrency(String player, int amount) {
		String query = "UPDATE MGPlayerData SET bank_currency = bank_currency + ? WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addInt("Bank_Increment", amount);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void decrementPlayerBankCurrency(String player, int amount) {
		String query = "UPDATE MGPlayerData SET bank_currency = bank_currency - ? WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addInt("Bank_Decrement", amount);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void incrementOverallPointsEarnt(String player, int amount) {
		String query = "UPDATE MGPlayerData SET overall_points_earnt = overall_points_earnt + ? WHERE userId = ?;";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addInt("Points_Earnt", amount);
		runnable.addString("USERID", player);
		scheduleRunnable(runnable);
	}

	public void updatePlayer(MinigameUser minigameUser) {
		minigames
				.getServer()
				.getScheduler()
				.runTaskAsynchronously(minigames,
						new MGAsyncUserUpdater(minigameUser, dbHandler));
	}

	public void registerPlayer(Player player) {
		String query = "INSERT INTO MGPlayerData (userId, playername) VALUES (?,?) ON DUPLICATE KEY UPDATE playername='"
				+ player.getName() + "';";
		MGAsyncDatabaseRunnable runnable = new MGAsyncDatabaseRunnable(query,
				dbHandler);
		runnable.addString(MGAsyncDatabaseRunnable.UUID, player.getName());
		runnable.addString("username", player.getName());
		runTaskAsync(runnable);
	}
}
