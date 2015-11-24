package hcmodtools.core.alt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class IpBanRunnable implements Runnable {
	private String nameToCheck;
	private Player issuer;
	private Connection con;

	public IpBanRunnable(Player issuer, String nameToCheck, Connection con) {
		this.issuer = issuer;
		this.nameToCheck = nameToCheck;
		this.con = con;
	}

	private final String accheck = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "AcCheck" + ChatColor.GOLD + "] " + ChatColor.RESET;

	@Override
	public void run() {
		try {
			String ip = null;
			ResultSet rs = con.createStatement().executeQuery(
					"select ip from tblUSER where username = \"" + nameToCheck
							+ "\"");
			while (rs.next()) {
				ip = rs.getNString("ip");
			}
			rs.close();
			if (ip == null || ip.equalsIgnoreCase("JAVAPIPE")
					|| !ip.contains(".")) {
				messageIssuer(accheck + "No recent valid IP found. ERR 1");
				con.close();
				return;
			}
			StringBuilder query = new StringBuilder();
			query.append("SELECT username,banned FROM tblUSER WHERE ip = '"
					+ ip + "'");
			System.out.println("Query 1: " + query.toString());
			System.out.println(query.toString());
			ResultSet rss;
			rss = con.createStatement().executeQuery(query.toString());
			List<String> alts = new ArrayList<String>();
			while (rss.next()) {
				String user = rss.getString("username");
				String banned = rss.getString("banned");
				System.out.println("Banned: = '" + banned + "'");
				if (!banned.equalsIgnoreCase("0")) {
					System.out.println("red");
					alts.add(ChatColor.RED + user);
				} else {
					System.out.println("green");
					alts.add(ChatColor.GREEN + user);
				}
			}
			rss.close();
			con.close();

			if (issuer.isOnline()) {
				messageIssuer(accheck + alts.size() + " accounts found.");
				messageIssuer(ChatColor.RED + " Red is banned,"
						+ ChatColor.GREEN + " green is not.");
				for (String str : alts)
					issuer.sendMessage("     - " + str);
				messageIssuer(accheck + " End of alt list.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void messageIssuer(String msg) {
		if (issuer != null && issuer.isOnline())
			issuer.sendMessage(msg);
	}
}
