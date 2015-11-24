package com.addongaming.hcessentials.teams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.teams.listeners.PlayerInteractions;
import com.addongaming.hcessentials.teams.listeners.TeamChatHandler;

public class TeamCore implements SubPlugin {
	public static final boolean debug = false;
	public static final String pluginName = "[HcTeams] ";
	public static HashSet<Team> teamSet = new HashSet<Team>();
	private final JavaPlugin jp;
	private final String pluginDebug = "[HcTeams debug] ";
	private SQLite sqlite;
	private TeamChatHandler tch;

	private final double version = 0.1;

	public TeamCore(JavaPlugin jp) {
		this.jp = jp;
	}

	private void checkConfig() {
		TeamChatHandler.canTeam = jp.getConfig().getBoolean(
				"teams.homes.enabled");
	}

	private void loadTeams() {
		System.out.println("Loading teams.");
		try {

			ResultSet rs = sqlite
					.query("SELECT * FROM  team INNER JOIN members ON team.t_id=members.t_id ORDER BY team.t_id, m_status DESC");
			String teamName = "";
			Team t = null;
			java.sql.ResultSetMetaData meta = rs.getMetaData();
			boolean homes = false;
			for (int i = 1; i < meta.getColumnCount() + 1; i++) {
				if (meta.getColumnName(i).equalsIgnoreCase("t_home")) {
					System.out.println("Database supports team homes.");
					homes = true;
					break;
				}
			}
			while (rs.next()) {
				if (!rs.getString("t_name").equalsIgnoreCase(teamName)) {
					if (t != null)
						teamSet.add(t);
					teamName = rs.getString("t_name");
					Location home = null;
					if (homes) {
						String hom = rs.getString("t_home");
						if (hom != null && hom.length() > 4) {
							String[] split = hom.split("[|]");
							World w = jp.getServer().getWorld(split[0]);
							if (w != null) {
								home = w.getBlockAt(Integer.parseInt(split[1]),
										Integer.parseInt(split[2]),
										Integer.parseInt(split[3]))
										.getLocation();
							}
						}
					}
					t = new Team(rs.getString("m_name"), teamName, home);
				} else {
					t.addPlayer(rs.getString("m_name"), rs.getInt("m_status"));
				}
			}
			if (t != null)
				teamSet.add(t);
			rs.close();
			System.out.println("Teams loaded fine.");
		} catch (SQLException e) {
			System.out.println("Problem loading teams.");
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		System.out.println(pluginName + "Stopping.");
		sqlite.close();
		tch.closeConnection();
		if (debug) {
			printDebug();
		}
		teamSet.clear();
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("teams.enabled")) {
			System.out.println("Teams is disabled.");
			return false;
		}
		System.out.println(pluginName + "has started. Version: " + version);
		startSqlConnection();
		sqlTableCheck();
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						loadTeams();
					}
				}, Config.Ticks.POSTWORLD);
		jp.getServer().getPluginManager()
				.registerEvents(new PlayerInteractions(), jp);
		tch = new TeamChatHandler(jp, sqlite);
		jp.getServer().getPluginCommand("team").setExecutor(tch);
		checkConfig();
		return true;

	}

	private void printDebug() {
		System.out.println(pluginDebug + "There were " + teamSet.size()
				+ " teams in the hashset.");
	}

	public void sqlTableCheck() {

		try {
			java.sql.DatabaseMetaData dbm = sqlite.getConnection()
					.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "team", null);
			if (!tables.next()) {
				System.out.println("Tables doesn't exist, making.");
				sqlite.query("CREATE TABLE team (t_id INTEGER PRIMARY KEY AUTOINCREMENT,t_name VARCHAR(20) NOT NULL, t_home VARCHAR(40));");
				sqlite.query("CREATE TABLE members (t_id INTEGER, m_name VARCHAR(18) NOT NULL , m_status INTEGER);");
				tables.close();
			} else {
				jp.getLogger().info("Tables have been created");
				tables.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void startSqlConnection() {
		sqlite = new SQLite(jp.getLogger(), "HCTeams", jp.getDataFolder()
				.getAbsolutePath(), "HCTeams");
		try {
			sqlite.open();
		} catch (Exception e) {
			jp.getLogger().info(e.getMessage());
		}
	}

}
