package hcmodtools.core.alt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AltCheckingRunnable implements Runnable {
	private String nameToCheck;
	private Player issuer;
	private Connection con;

	public AltCheckingRunnable(Player issuer, String nameToCheck, Connection con) {
		this.issuer = issuer;
		this.nameToCheck = nameToCheck;
		this.con = con;
	}

	private final String accheck = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "AcCheck" + ChatColor.GOLD + "] " + ChatColor.RESET;

	@Override
	public void run() {
		HashMap<String, AltInstance> altMap = new HashMap<String, AltInstance>();
		try {
			ArrayList<String> ipAddresses = new ArrayList<String>();
			ResultSet rs = con.createStatement().executeQuery(
					"select ipaddress from tblLOGIN where username = \""
							+ nameToCheck + "\"");
			while (rs.next()) {
				String ip = rs.getNString("ipaddress");
				if (!ipAddresses.contains(ip)
						&& (!ip.equalsIgnoreCase("JAVAPIPE") && ip
								.contains(".")))
					ipAddresses.add(ip);
			}
			if (ipAddresses.size() == 0) {
				messageIssuer(accheck
						+ "No registered IP's, or player not found. ERR 1");
				con.close();
				return;
			}
			rs.close();
			int counter = 0;
			StringBuilder query = new StringBuilder();
			query.append("SELECT username,ipaddress FROM tblLOGIN WHERE ");
			for (String s : ipAddresses) {
				if (s.equalsIgnoreCase("JAVAPIPE")
						|| s.equalsIgnoreCase("37.220.21.58"))
					continue;
				query.append("ipaddress = \"" + s + "\" OR ");
			}
			query.deleteCharAt(query.length() - 1);
			query.deleteCharAt(query.length() - 1);
			query.deleteCharAt(query.length() - 1);
			System.out.println("Query 1: " + query.toString());

			if (query.toString().endsWith("WHE")) {
				messageIssuer(accheck
						+ "No registered IP's, or player not found. ERR 2");
				con.close();
				return;
			}
			System.out.println(query.toString());
			ResultSet rss;
			rss = con.createStatement().executeQuery(query.toString());

			while (rss.next()) {
				String user = rss.getString("username");
				String ip = rss.getString("ipaddress");
				if (!user.equalsIgnoreCase(nameToCheck))
					if (!altMap.containsKey(user)) {
						altMap.put(user, new AltInstance(user, ip));
					} else {
						AltInstance ai = altMap.get(user);
						if (!ai.containsIp(ip)) {
							ai.addIp(ip);
							altMap.put(user, ai);
						}
					}
			}
			rss.close();
			con.close();
			if (altMap.isEmpty()) {
				messageIssuer(accheck + "No alts found on "
						+ ipAddresses.size() + " different IPs.");

				return;
			}
			if (issuer.isOnline()) {
				messageIssuer(accheck + "Alts scanned on " + ipAddresses.size()
						+ " different IPs.");
				for (AltInstance ai : altMap.values())
					issuer.sendMessage(ChatColor.GREEN + "Name: "
							+ ChatColor.AQUA + ai.getName() + ChatColor.GREEN
							+ " IP's Associated: " + ChatColor.AQUA
							+ ai.getIpList().size());
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
