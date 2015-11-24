package hcmodtools.core.raidingreport;

import hcmodtools.core.Main;
import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RaidReports extends Tools implements Listener, ModTool,
		CommandExecutor {
	HashSet<Report> reportMap = new HashSet<Report>();
	private int counter = 0;
	private JavaPlugin jp;
	private final String serverName;

	public RaidReports(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.BLUE + "RaidReport"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_GRAY
				+ "[" + ChatColor.RED + "RaidReport" + ChatColor.DARK_GRAY
				+ "] " + ChatColor.RESET);
		this.jp = jp;
		jp.getCommand("raidproof").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		serverName = jp.getConfig().getString("server.name");
		try {
			loadCounter();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void saveCounter() {
		try {
			File f = Main.getFile("raidcounter.txt");
			if (!f.exists())
				f.createNewFile();
			PrintWriter pw = new PrintWriter(f);
			pw.println(counter);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadCounter() throws FileNotFoundException {
		if (Main.fileExists("raidcounter.txt")) {
			Scanner scan = new Scanner(Main.getFile("raidcounter.txt"));
			String str = scan.nextLine();
			scan.close();
			if (isInteger(str)) {
				counter = Integer.parseInt(str);
			} else {
				counter = 0;
			}
		} else {
			counter = 0;
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {

		if (!arg0.hasPermission("HcRaid.Mod")) {
			lookedUpreports((Player) arg0);
		} else {
			Player p = (Player) arg0;
			if (arg3.length == 0) {
				msg(p, "Please use:");
				msg(p, "/raidproof list");
				msg(p, "/raidproof check <reportid>");
				msg(p, "/raidproof delete <reportid>");
			} else {
				switch (arg3[0].toLowerCase()) {
				case "create":
					createReport(p, arg3);
					break;
				case "list":
					listReports(p);
					break;
				case "check":
					if (arg3.length != 2) {
						msg(p, "Please use /raidproof check <reportid>");
						break;
					}
					checkReport(p, arg3);
					break;
				case "delete":
					if (arg3.length != 2) {
						msg(p, "Please use /raidproof delete <reportid>");
						break;
					}
					deleteReport(p, arg3);
					break;
				default:
					msg(p, "Please use:");
					msg(p, "/raidproof create <username> <message>");
					msg(p, "/raidproof list");
					msg(p, "/raidproof check <reportid>");
					msg(p, "/raidproof delete <reportid>");
				}
			}
		}
		return true;

	}

	private void createReport(Player p, String[] arg3) {
		// Arg0 = create
		// Arg 1 = Playername
		// Arg 2+ = Message
		if (arg3.length < 4) {
			msg(p, "Please use /raidproof create <playername> <message>");
			msg(p, "With a small message detailing the time of the raid.");
			return;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(arg3[1]);
		if (player == null || !player.hasPlayedBefore()) {
			msg(p, "Player " + player.getName()
					+ " has not played on the server before.");
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 2; i != arg3.length; i++) {
			sb.append(arg3[i] + " ");
		}
		reportMap.add(new Report(p.getName(), counter, sb.toString(),
				new Date(), player.getName()));
		super.msg(p, "Created report, ID: " + counter++);
		saveCounter();
	}

	private void deleteReport(Player p, String[] arg3) {
		if (!isInteger(arg3[1])) {
			msg(p, arg3[1] + " is not a valid number.");
			return;
		}
		int id = Integer.parseInt(arg3[1]);
		Report toDelete = null;
		for (Report r : reportMap) {
			if (r.getId() == id) {
				if (!p.getName().equalsIgnoreCase(r.getModWhoOpened())
						&& !p.hasPermission("Hcraid.ADMIN")) {
					msg(p,
							"Sorry, you do not have permission to delete this report.");
					return;
				} else {
					toDelete = r;
					break;
				}

			}
		}
		if (toDelete == null)
			msg(p, "Sorry no report with ID " + id + " was found.");
		else {
			reportMap.remove(toDelete);
			msg(p, "Report " + toDelete.getId() + " was deleted.");
		}

	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	private void checkReport(Player p, String[] arg3) {
		if (!isInteger(arg3[1])) {
			msg(p, arg3[1] + " is not a valid number.");
			return;
		}
		int id = Integer.parseInt(arg3[1]);
		for (Report r : reportMap) {
			if (r.getId() == id) {
				if (!p.getName().equalsIgnoreCase(r.getModWhoOpened())
						&& !p.hasPermission("Hcraid.ADMIN")) {
					msg(p,
							"Sorry, you do not have permission to see this report.");
					return;
				} else {
					msg(p, "-----------------------------------");
					msg(p, "Report ID: " + r.getId() + " Server: " + serverName);
					msg(p, "Mod requested: " + r.getModWhoOpened());
					msg(p, "Message: " + r.getMessage());
					msg(p, "Time left to respond: " + r.getTimeLeft());
					msg(p, "-----------------------------------");
					return;
				}

			}
		}
		msg(p, "Sorry no report with ID " + id + " was found.");
	}

	private void listReports(Player p) {

		msg(p, "Report ID's you are requesting proof of:");
		for (Report r : reportMap) {
			if (r.getModWhoOpened().equalsIgnoreCase(p.getName())) {
				msg(p, "Report ID: " + r.getId() + " Player: " + r.getPlayer());
			}
		}
		List<Integer> list = new ArrayList<Integer>();
		if (p.hasPermission("HcRaid.admin")) {
			for (Report r : reportMap) {
				if (!r.getModWhoOpened().equals(p.getName()))
					list.add(r.getId());
			}
			if (list.size() > 0) {
				msg(p, "Other mods have request proof ID's:");
				msg(p, Arrays.toString(list.toArray(new Integer[list.size()])));
			} else {
				msg(p, "No other mods have open reports.");
			}
		}

	}

	private void lookedUpreports(Player p) {
		List<Report> reportList = new ArrayList<Report>();
		for (Report report : reportMap)
			if (report.getPlayer().equalsIgnoreCase(p.getName())) {
				reportList.add(report);
			}
		if (reportList.size() == 0) {
			super.msg(p, "You have no raiding reports open.");
			return;
		}
		for (Report r : reportList) {
			msg(p, "-----------------------------------");
			msg(p, "Report ID: " + r.getId() + " Server: " + serverName);
			msg(p, "Mod requested: " + r.getModWhoOpened());
			msg(p, "Message: " + r.getMessage());
			msg(p, "Time left to respond: " + r.getTimeLeft());
			msg(p, "-----------------------------------");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onStart() {
		if (Main.fileExists("raidreport.sav")) {
			try {
				reportMap = (HashSet<Report>) Main.load("raidreport.sav");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStop() {
		try {
			Main.save(reportMap, "raidreport.sav");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void playerJoin(final PlayerJoinEvent event) {
		boolean bool = false;
		for (Report report : reportMap)
			if (report.getPlayer()
					.equalsIgnoreCase(event.getPlayer().getName())) {
				bool = true;
				if (report.hasExpired()) {
					event.getPlayer()
							.kickPlayer(
									"You need to provide evidence \n of how you found a base on forum.addongaming.com\n ID: "
											+ report.getId()
											+ " Server: "
											+ serverName
											+ " \nMod: "
											+ report.getModWhoOpened()
											+ "\n Message: "
											+ report.getMessage());
					return;
				}
			}
		if (bool) {
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							final Player p = event.getPlayer();
							if (p.isOnline() && p != null) {
								p.sendMessage(ChatColor.RED
										+ "------------------------------------------");
								p.sendMessage(ChatColor.DARK_RED
										+ "You have recently raided a player.");
								p.sendMessage(ChatColor.DARK_RED
										+ "A member of staff has requested your proof on how you found the base.");
								p.sendMessage(ChatColor.DARK_RED
										+ "/raidproof   for more information.");
								p.sendMessage(ChatColor.RED
										+ "------------------------------------------");
							}
						}
					}, 10L);
		}
	}
}
