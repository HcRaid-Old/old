package core.start;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import core.bank.BankInstance;

public class Main extends JavaPlugin implements CommandExecutor {
	public static HashMap<String, BankInstance> playerMap = new HashMap<String, BankInstance>();
	public static File dataFolder;
	public static File dupeFolder;
	public static JavaPlugin jp;

	public Main() {
		Main.jp = this;
	}

	@Override
	public void onEnable() {
		dataFolder = this.getDataFolder();
		if (!dataFolder.exists())
			dataFolder.mkdirs();
		dupeFolder = new File(dataFolder + "\\dupers");
		if (!dupeFolder.exists())
			dupeFolder.mkdirs();
		// checkFields();
		this.getServer().getPluginManager()
				.registerEvents(new BankEventListener(), this);
		System.out.println("[HCBank] Bank plugin loaded");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player))
			return false;
		if (command.getName().equalsIgnoreCase("setbankchest")) {
			if (!sender.isOp()) {
				sender.sendMessage(title + ChatColor.RED
						+ " Operator permissions required.");
				return true;
			}
			setBankChest((Player) sender);
			return true;
		}
		return false;

	}

	public final static String title = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "HcBank" + ChatColor.GOLD + "] ";

	private static void setBankChest(Player p) {
		if (p.getTargetBlock(null, 20).getType().equals(Material.CHEST)) {
			p.getTargetBlock(null, 20).setData((byte) 10);
			p.sendMessage(title + ChatColor.RED + "Made a bank chest.");
		} else {
			p.sendMessage(title + ChatColor.RED + "Target block is not chest.");
		}
	}

	public void onDisable() {
		for (String s : playerMap.keySet()) {
			Player p = Bukkit.getPlayerExact(s);
			if (p == null)
				continue;
			else
				BankEventListener.playerLeft(p);
			playerMap.remove(s);
		}
	}
}
