package hcmodtools.core.alt;

import hcmodtools.core.ModTool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AltChecking implements ModTool, CommandExecutor {
	private JavaPlugin jp;
	private String sqlURL;
	private String user;
	private String pass;

	public AltChecking(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
		jp.getServer().getPluginCommand("ac").setExecutor(this);
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("bandb.ip", "localhost");
		fc.addDefault("bandb.name", "hcglobalban");
		fc.addDefault("bandb.user", "root");
		fc.addDefault("bandb.pass", "rootpass");
		fc.options().copyDefaults(true);
		jp.saveConfig();
		sqlURL = "jdbc:mysql://" + fc.getString("bandb.ip") + ":3306/"
				+ fc.getString("bandb.name");
		this.user = fc.getString("bandb.user");
		this.pass = fc.getString("bandb.pass");

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	private Connection createConnection() {
		try {
			return DriverManager.getConnection(sqlURL, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	private final String accheck = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "AcCheck" + ChatColor.GOLD + "] " + ChatColor.RESET;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("hcraid.mod")) {
			arg0.sendMessage(ChatColor.RED
					+ "Sorry you do not have permission for this command.");
			return true;
		} else {
			if (arg3.length != 2) {
				arg0.sendMessage(accheck
						+ "Please use /ac check <username> - Lists all alts & amount of ip's");
				arg0.sendMessage(accheck
						+ "Please use /ac ipcheck <username> - Lists all currently associated accounts & ban status.");
				return true;
			}
			switch (arg3[0].toLowerCase()) {
			case "check":
				altCheck(arg0, arg3[1]);
				return true;
			case "ipcheck":
				ipCheck(arg0, arg3[1]);
				return true;
			default:
				arg0.sendMessage(accheck + "Sorry, " + arg3[0]
						+ " is not currently recognised.");
				return true;
			}
		}
	}

	private void ipCheck(CommandSender arg0, String string) {
		Connection con = createConnection();
		if (con == null) {
			arg0.sendMessage(accheck
					+ "A connection could not be established. Please try again in a little while.");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(jp,
				new IpBanRunnable((Player) arg0, string, con));
		arg0.sendMessage(accheck + "Initiated ip check for " + string
				+ ". Please be patient.");
	}

	private void altCheck(CommandSender arg0, String string) {
		Connection con = createConnection();
		if (con == null) {
			arg0.sendMessage(accheck
					+ "A connection could not be established. Please try again in a little while.");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(jp,
				new AltCheckingRunnable((Player) arg0, string, con));
		arg0.sendMessage(accheck + "Initiated alt check for " + string
				+ ". Please be patient.");
	}

}
