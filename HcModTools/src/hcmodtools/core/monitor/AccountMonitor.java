package hcmodtools.core.monitor;

import hcmodtools.core.Main;
import hcmodtools.core.ModTool;
import hcmodtools.core.monitor.objects.StaffInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AccountMonitor implements Listener, ModTool, CommandExecutor {
	CaseInsensitiveStaffMap staffInstances = new CaseInsensitiveStaffMap();
	List<String> toMonitor = new ArrayList<String>();
	File dataFolder;
	String title = ChatColor.BLACK + "[" + ChatColor.DARK_RED
			+ "HcAccountMonitor" + ChatColor.BLACK + "]" + " "
			+ ChatColor.RESET;
	Player staff;

	public AccountMonitor(File dataFolder, JavaPlugin jp) {
		checkForExploitMonitor(jp);
		this.dataFolder = dataFolder;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("monitor").setExecutor(this);
		try {
			if (Main.fileExists("accmonitor.sav")) {
				toMonitor = (ArrayList<String>) Main.load("accmonitor.sav");
			} else
				toMonitor = new ArrayList<String>();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void checkForExploitMonitor(final JavaPlugin jp) {
		for (final Plugin plugin : jp.getServer().getPluginManager()
				.getPlugins()) {
			System.out.println(plugin.getName());
			if (plugin.getName().equalsIgnoreCase("HcExploitMonitor")) {

				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@Override
							public void run() {
								jp.getServer().getPluginManager()
										.disablePlugin(plugin);
							}
						}, 5l);
				return;
			}
		}
	}

	public File getDataFolder() {
		return this.dataFolder;
	}

	public void onStart() {
		this.getDataFolder().mkdirs();
	}

	@EventHandler
	public void playerLeft(PlayerQuitEvent event) {
		if (toMonitor.contains(event.getPlayer().getName())) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("hcraid.mod")) {
					staff = p;
					staff.sendMessage(title + event.getPlayer().getName()
							+ " left the game.");
				}
			}
		}
		if (toMonitor.contains(event.getPlayer().getName())
				&& event.getPlayer().hasPermission("hcraid.mod")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("hcraid.admin")) {
					staff = p;
					staff.sendMessage(title + event.getPlayer().getName()
							+ " left the game.");
				}
			}
		}
	}

	@EventHandler
	public void playerJoined(PlayerJoinEvent event) {
		if (toMonitor.contains(event.getPlayer().getName())) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("hcraid.mod")) {
					staff = p;
					staff.sendMessage(title + event.getPlayer().getName()
							+ " joined the game.");
				}
			}
		}
		if (toMonitor.contains(event.getPlayer().getName())
				&& event.getPlayer().hasPermission("hcraid.mod")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("hcraid.admin")) {
					staff = p;
					staff.sendMessage(title + event.getPlayer().getName()
							+ " joined the game.");
				}
			}
		}
		if (event.getPlayer().hasPermission("hcraid.mod")
				|| toMonitor.contains(event.getPlayer().getName())) {// permission
																		// node
			File f = new File(this.getDataFolder() + "" + File.separator
					+ "Monitor/" + event.getPlayer().getName());
			if (!f.exists())
				f.mkdirs();
			File main = new File(this.getDataFolder() + "" + File.separator
					+ "Monitor/" + event.getPlayer().getName() + ""
					+ File.separator + "main.txt");
			File time = new File(this.getDataFolder() + "" + File.separator
					+ "Monitor/" + event.getPlayer().getName() + ""
					+ File.separator + "time.txt");
			File pubchat = new File(this.getDataFolder() + "" + File.separator
					+ "Monitor/" + event.getPlayer().getName() + ""
					+ File.separator + "pubch.txt");
			File privchat = new File(this.getDataFolder() + "" + File.separator
					+ "Monitor/" + event.getPlayer().getName() + ""
					+ File.separator + "privch.txt");
			StaffInstance si = new StaffInstance(event.getPlayer().getName(),
					main, time, pubchat, privchat);
			staffInstances.put(event.getPlayer().getName(), si);
		}
	}

	@Override
	public void onStop() {
		for (StaffInstance si : staffInstances.values())
			si.disable();
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		if (staffInstances.containsKey(event.getPlayer().getName())) {
			staffInstances.get(event.getPlayer().getName()).checkSwear(
					event.getMessage());
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		playerLog(event.getPlayer());
	}

	@EventHandler
	public void playerKick(PlayerKickEvent event) {
		playerLog(event.getPlayer());
	}

	private void playerLog(Player p) {
		if (staffInstances.containsKey(p.getName())) {
			staffInstances.get(p.getName()).disable();
			staffInstances.remove(p.getName());
		}
	}

	@EventHandler
	public void command(PlayerCommandPreprocessEvent event) {
		if (staffInstances.containsKey(event.getPlayer().getName())) {
			staffInstances.get(event.getPlayer().getName()).checkCommand(
					event.getMessage());
		}

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player p = (Player) arg0;
			if (!p.isOp())
				return true;
			if (arg3.length == 0) {
				p.sendMessage(title
						+ "Please use /monitor <playername> to toggle on/off");
				return true;
			}
			String playerName = arg3[0];
			if (toMonitor.contains(playerName)) {
				toMonitor.remove(playerName);
				p.sendMessage(title + playerName + " removed from monitor");
			} else {
				toMonitor.add(playerName);
				p.sendMessage(title + playerName + " added to monitor");
			}
			save();
		}
		return true;
	}

	private void save() {
		try {
			Main.save(toMonitor, "accmonitor.sav");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
