package core.hclinkgetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	File f;
	public Map<Location, String> signs = new HashMap<Location, String>();

	@Override
	public void onDisable() {
		try {
			PrintWriter pw = new PrintWriter(f);
			for (Location loc : signs.keySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(loc.getWorld().getName());
				sb.append(":");
				sb.append(loc.getBlockX());
				sb.append(":");
				sb.append(loc.getBlockY());
				sb.append(":");
				sb.append(loc.getBlockZ());
				sb.append(":");
				sb.append(signs.get(loc));
				pw.println(sb.toString());
				this.getServer().getScheduler()
						.scheduleSyncDelayedTask(this, new Runnable() {
							@Override
							public void run() {
								
							}
						}, 600);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}
		f = new File(this.getDataFolder() + "//locations.andy");
		if (f.exists()) {
			try {
				Scanner scan = new Scanner(f);

				while (scan.hasNextLine()) {
					String next = scan.nextLine();
					String[] parts = next.split(":");
					for (int i = 0; i <= parts.length - 1; i++) {
						System.out.println("Index : " + i + " text: "
								+ parts[i]);
					}
					Location loc = new Location(Bukkit.getWorld(parts[0]),
							Integer.parseInt(parts[1]),
							Integer.parseInt(parts[2]),
							Integer.parseInt(parts[3]));
					signs.put(loc, parts[4]);
				}
				scan.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.getCommand("donate").setExecutor(this);
		this.getCommand("subscribe").setExecutor(this);
		this.getCommand("vote").setExecutor(this);
		this.getCommand("server").setExecutor(this);
		this.getCommand("servers").setExecutor(this);
		this.getCommand("serverlist").setExecutor(this);
	}

	public void msg(CommandSender cs, String message) {
		cs.sendMessage(pos + message);
	}

	String pos = ChatColor.GOLD + "[" + ChatColor.BLUE + "HcLinks"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		switch (command.getName().toLowerCase()) {
		case "donate":
			msg(sender,
					"You can subscribe by going to our website http://www.addongaming.com and creating an account. After purchasing a rank use /rank in-game.");
			break;
		case "subscribe":
			msg(sender,
					"You can subscribe by going to our website http://www.addongaming.com and creating an account. After purchasing a rank use /rank in-game.");
			break;
		case "vote":
			msg(sender,
					"To get the list of places to vote, please go to: http://addongaming.com/topic/8717-rules-of-votingvoting");
			break;
		case "server":
			msg(sender, "If you fancy a hardcore server, why not try HCRaid? "
					+ ChatColor.GOLD + ChatColor.ITALIC + "play.hcraid.com");
			msg(sender,
					"Otherwise, if you fancy a over powered server, try Overkill at"
							+ ChatColor.GOLD + ChatColor.ITALIC
							+ " overkill.hcraid.com");
			msg(sender, "Or if you prefer a factions based sever, try "
					+ ChatColor.GOLD + ChatColor.ITALIC + "factions.hcraid.com");
			break;
		case "serverlist":
			msg(sender, "If you fancy a hardcore server, why not try HCRaid? "
					+ ChatColor.GOLD + ChatColor.ITALIC + "play.hcraid.com");
			msg(sender,
					"Otherwise, if you fancy a over powered server, try Overkill at"
							+ ChatColor.GOLD + ChatColor.ITALIC
							+ " overkill.hcraid.com");
			msg(sender, "Or if you prefer a factions based sever, try "
					+ ChatColor.GOLD + ChatColor.ITALIC + "factions.hcraid.com");
			break;
		case "servers":
			msg(sender, "If you fancy a hardcore server, why not try HCRaid? "
					+ ChatColor.GOLD + ChatColor.ITALIC + "play.hcraid.com");
			msg(sender,
					"Otherwise, if you fancy a over powered server, try Overkill at"
							+ ChatColor.GOLD + ChatColor.ITALIC
							+ " overkill.hcraid.com");
			msg(sender, "Or if you prefer a factions based sever, try "
					+ ChatColor.GOLD + ChatColor.ITALIC + "factions.hcraid.com");
			break;

		}
		return true;
	}
}
