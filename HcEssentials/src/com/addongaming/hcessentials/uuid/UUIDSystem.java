package com.addongaming.hcessentials.uuid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.database.DatabaseHandler;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.logging.DataLog;

public class UUIDSystem implements SubPlugin, Listener {
	private final JavaPlugin jp;
	private String database;
	private DatabaseHandler handler;
	private static UUIDSystem uuidSystem = null;
	private final ConcurrentMap<String, Integer> playerIdMap = new ConcurrentHashMap<String, Integer>();
	private final ConcurrentMap<String, Integer> playerUserMap = new ConcurrentHashMap<String, Integer>();
	private DataLog dl;

	public UUIDSystem(JavaPlugin jp) {
		this.jp = jp;
		dl = HcEssentials.getDataLogger().addLogger("UUID System");
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	/**
	 * Acquires the players ID in tblUUID
	 * 
	 * @param playerUUID
	 *            The players UUID in which to get the value of.
	 * @return The ID in the database the UUID relates to. -1 if not stored
	 *         locally -2 if not stored in the database
	 */
	public int getId(UUID playerUUID) {
		synchronized (playerIdMap) {
			if (playerIdMap.containsKey(playerUUID.toString()))
				return playerIdMap.get(playerUUID.toString());
		}
		loadId(playerUUID);
		return playerIdMap.get(playerUUID.toString());
	}

	/**
	 * Acquires the players ID in tblUUID Uses Mojangs AccountClient API to
	 * convert username->UUID
	 * 
	 * @param playerName
	 *            The players name to lookup
	 * @return The ID in the database the UUID relates to. -1 if not stored
	 *         locally -2 if not stored in the database
	 * 
	 */
	public int getId(String playerName) {
		if (playerUserMap.containsKey(playerName))
			return playerUserMap.get(playerName);
		for (String str : playerUserMap.keySet())
			if (str.equalsIgnoreCase(playerName)) {
				return playerUserMap.get(str);
			}
		UUID playerUUID = getUUID(playerName);
		System.out.println("Name : " + playerName);
		System.out.println("[UUIDSystem] User: " + playerName + " UUID: "
				+ playerUUID.toString());
		synchronized (playerIdMap) {
			synchronized (playerUserMap) {
				if (!playerIdMap.containsKey(playerUUID.toString())) {
					loadId(playerUUID);
				}
				if (playerIdMap.containsKey(playerUUID.toString())) {
					playerUserMap.put(playerName,
							playerIdMap.get(playerUUID.toString()));
					return playerIdMap.get(playerUUID.toString());
				}
			}
		}
		return playerIdMap.get(playerUUID.toString());
	}

	public UUID getUUID(String playerName) {
		System.out.println("getUUID0");
		if (Bukkit.getOfflinePlayer(playerName) != null
				&& Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
			return Bukkit.getOfflinePlayer(playerName).getUniqueId();
		}
		System.out.println("getUUID1");
		for (String str : playerUserMap.keySet())
			if (str.equalsIgnoreCase(playerName)) {
				int id = playerUserMap.get(str);
				if (playerIdMap.containsValue(id)) {
					for (String uid : playerIdMap.keySet())
						if (playerIdMap.get(uid) == id)
							return UUID.fromString(uid);
				}
			}
		System.out.println("getUUID2");
		loadUUID(playerName);
		for (String str : playerUserMap.keySet())
			if (str.equalsIgnoreCase(playerName)) {
				int id = playerUserMap.get(str);
				if (playerIdMap.containsValue(id)) {
					for (String uid : playerIdMap.keySet())
						if (playerIdMap.get(uid) == id)
							return UUID.fromString(uid);
				}
			}
		System.out.println("getUUID3");
		System.out.println("Get UUID try{ met");
		try {
			UUID uuid = null;
			UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(playerName));
			Map<String, UUID> response = null;
			response = fetcher.call();
			for (String str : response.keySet())
				if (str.equalsIgnoreCase(playerName)) {
					uuid = response.get(str);
					break;
				}
			if (uuid != null)
				return uuid;
			return null;
		} catch (Exception e) {

			return null;
		}
	}

	public static UUIDSystem getInstance() {
		return uuidSystem;
	}

	private void loadId(final UUID playerUUID) {
		final Connection con = handler.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT userId,userName FROM tblUUID WHERE uuid = ?");
			ps.setString(1, playerUUID.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				playerIdMap.put(playerUUID.toString(), rs.getInt("userId"));
				playerUserMap
						.put(rs.getString("userName"), rs.getInt("userId"));
				System.out.println("Loaded " + rs.getInt("userId") + " UUID "
						+ playerUUID.toString());
			} else {
				System.out.println("ID not loaded");
			}
			rs.close();
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

	private void loadUUID(final String playerName) {
		final Connection con = handler.getConnection();
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT userId,UUID FROM tblUUID WHERE userName = ?");
			ps.setString(1, playerName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				playerIdMap.put(rs.getString("UUID"), rs.getInt("userId"));
				playerUserMap.put(playerName, rs.getInt("userId"));
				System.out.println("Loaded " + rs.getInt("userId") + " UUID "
						+ rs.getString("UUID"));
			} else {
				System.out.println("ID not loaded");
			}
			rs.close();
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

	public String getUsername(int userId) {
		synchronized (playerUserMap) {
			if (this.playerUserMap.containsValue(userId)) {
				for (Iterator<String> iter = playerUserMap.keySet().iterator(); iter
						.hasNext();) {
					String str = iter.next();
					if (playerUserMap.get(str) == userId)
						return str;
				}
			}
		}

		final Connection con = handler.getConnection();
		String str = null;
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT userName,UUID FROM tblUUID WHERE userId = ?");
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				playerUserMap.put(rs.getString("userName"), userId);
				playerIdMap.put(rs.getString("UUID"), userId);
				str = rs.getString("userName");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	public String getUsername(UUID playerUUID) {
		if (this.playerIdMap.containsValue(playerUUID.toString())) {
			return getUsername(playerIdMap.get(playerUUID.toString()));
		}
		final Connection con = handler.getConnection();
		String str = null;
		try {
			PreparedStatement ps = con
					.prepareStatement("SELECT userName,userId FROM tblUUID WHERE UUID = ?");
			ps.setString(1, playerUUID.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				playerIdMap.put(playerUUID.toString(), rs.getInt("userId"));
				playerUserMap
						.put(rs.getString("userName"), rs.getInt("userId"));
				str = rs.getString("userName");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		if (playerIdMap.containsKey(event.getPlayer().getUniqueId().toString()))
			return;
		final Connection con = handler.getConnection();
		final String uuid = event.getPlayer().getUniqueId().toString();
		final String userName = event.getPlayer().getName();
		if (con == null)
			return;
		else {
			synchronized (playerIdMap) {
				try {
					PreparedStatement statement = con.prepareStatement(
							"INSERT INTO tblUUID (UUID, userName) VALUES ('"
									+ uuid + "', '" + userName
									+ "') ON DUPLICATE KEY UPDATE userName='"
									+ userName + "';",
							Statement.RETURN_GENERATED_KEYS);
					statement.executeUpdate();
					ResultSet rs = statement.getGeneratedKeys();
					if (rs.next()) {
						playerIdMap.put(uuid, rs.getInt(1));
						playerUserMap.put(event.getPlayer().getName(),
								rs.getInt(1));
						System.out.println("Loaded1 " + rs.getInt(1) + " UUID "
								+ uuid);
					}
					rs.close();
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
		}
	}

	@Override
	public boolean onEnable() {
		dl.log("Checking config");
		checkConfig();
		dl.log("Loading config");
		loadConfig();
		if (DatabaseManagement.hasInstance()
				&& (handler = DatabaseManagement.getInstance().addDatabase(
						database)) != null) {
			dl.log("Checking tables");
			checkTables();
			uuidSystem = this;
			dl.log("Registering listeners");
			jp.getServer().getPluginManager().registerEvents(this, jp);
			for (Player player : Bukkit.getOnlinePlayers())
				getId(player.getUniqueId());
			return true;
		}
		dl.log("Failed to load");
		return false;
	}

	private void checkTables() {
		Connection con = handler.getConnection();
		try {
			con.createStatement()
					.execute(
							"CREATE TABLE IF NOT EXISTS tblUUID (userId INT PRIMARY KEY AUTO_INCREMENT, "
									+ "UUID VARCHAR( 36 ) UNIQUE NOT NULL , userName VARCHAR( 16 ) NOT NULL); ");
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

	private void loadConfig() {
		database = jp.getConfig().getString("db.database.uuid");
	}

	private void checkConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("db.database.uuid", "globalBan");
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

}
