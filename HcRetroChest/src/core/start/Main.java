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
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import core.bank.BankFileHandler;
import core.bank.BankInstance;
import core.syncitems.SyncInventory;

public class Main extends JavaPlugin implements CommandExecutor {
	public static HashMap<String, BankInstance> playerMap = new HashMap<String, BankInstance>();
	public static File dataFolder;
	public static JavaPlugin jp;
	public static File dupeFolder;

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
		} else if (command.getName().equalsIgnoreCase("checkbankchest")) {
			if (!sender.isOp()) {
				sender.sendMessage(title + ChatColor.RED
						+ " Operator permissions required.");
				return true;
			}
			if (args.length < 1 || 3 > args.length) {
				sender.sendMessage(title
						+ ChatColor.RED
						+ " Not enough parameters. /checkbankchest <name> [grade]");

			} else {
				String s = args[0];
				int i = Integer.parseInt(args[1]);
				SyncInventory si = BankFileHandler.loadInventory(s, i);
				if (si == null) {
					sender.sendMessage(title + ChatColor.RED
							+ "Inventory not found");
					return true;
				}
				Inventory inv = Bukkit.createInventory(null, 56);
				inv.setContents(si.getContents());
				((Player) (sender)).openInventory(inv);
			}

		}
		return false;

	}

	private static void setBankChest(Player p) {
		if (p.getTargetBlock(null, 20).getType().equals(Material.CHEST)) {
			p.getTargetBlock(null, 20).setData((byte) 10);
			p.sendMessage(title + ChatColor.RED + "Made a bank chest.");
		} else {
			p.sendMessage(title + ChatColor.RED + "Target block is not chest.");
		}
	}

	public final static String title = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "HcBank" + ChatColor.GOLD + "] ";

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
