package com.addongaming.hcessentials.rankup;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class Utils {
	private final int timeout = 2000;

	public JSONObject getJSON(URL url) {
		try {
			URLConnection con = url.openConnection();
			con.setConnectTimeout(timeout);
			Scanner s = new Scanner(con.getInputStream());
			String str = s.nextLine();
			s.close();
			JSONObject js = new JSONObject(str);
			return js;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String title = ChatColor.GOLD + "[" + ChatColor.BLUE + "HcRankup"
			+ ChatColor.GOLD + "] " + ChatColor.GREEN;

	public void msg(Player p, String message) {
		p.sendMessage(title + message);
	}
}
