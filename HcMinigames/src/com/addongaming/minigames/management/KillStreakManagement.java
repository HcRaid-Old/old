package com.addongaming.minigames.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.killstreak.GameStreak;
import com.addongaming.minigames.management.killstreak.ItemKillStreak;
import com.addongaming.minigames.management.killstreak.KillStreak;

public class KillStreakManagement implements CommandExecutor {
	private HcMinigames minigames;
	private final static List<GameStreak> gameStreaks = new ArrayList<GameStreak>();

	public KillStreakManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getCommand("ks").setExecutor(this);
		File folder = new File(minigames.getDataFolder() + File.separator
				+ "KillStreaks");
		if (!folder.exists())
			folder.mkdirs();
		for (GameMode gm : GameMode.values()) {
			if (!new File(folder, gm.name()).exists())
				new File(folder, gm.name()).mkdirs();
			gameStreaks.add(new GameStreak(gm, new File(folder, gm.name())));
		}
	}

	public GameStreak getGameStreak(GameMode gm) {
		for (GameStreak gs : gameStreaks)
			if (gs.getGameMode() == gm)
				return gs;
		return null;
	}

	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "KillsMan"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(prefix + "This can only be executed in-game.");
			return true;
		} else if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED + "This is only available for ops.");
			return true;
		}
		if (arg3.length == 0) {
			listAdminCommands(arg0);
			return true;
		}
		Player p = (Player) arg0;
		switch (arg3[0].toLowerCase()) {
		case "list": {
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You have no selection, use /ks sel <gamemode>");
				return true;
			}
			GameStreak gs = manipulatingMap.get(arg0.getName());
			if (!gs.getKillStreaks().isEmpty()) {
				arg0.sendMessage(ChatColor.BLACK + "-----------  "
						+ ChatColor.DARK_RED
						+ gs.getGameMode().name().toLowerCase() + " mode"
						+ ChatColor.BLACK + "  -----------");
				for (KillStreak ks : gs.getKillStreaks()) {
					arg0.sendMessage("     " + ChatColor.GOLD
							+ "Required kills: " + ChatColor.AQUA
							+ ks.getNeededKills());
					arg0.sendMessage("     " + ChatColor.GOLD + "Type: "
							+ ChatColor.AQUA
							+ ks.getType().name().toLowerCase());
					arg0.sendMessage(ChatColor.BLACK
							+ "---------------------------------");
				}
			} else {
				arg0.sendMessage(prefix + "There are no killstreaks for "
						+ gs.getGameMode().name().toLowerCase());
			}
			return true;
		}
		case "load": {
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You have no selection, use /ks sel <gamemode>");
				return true;
			} else if (arg3.length == 1 || !isInteger(arg3[1])) {
				arg0.sendMessage(prefix
						+ "Please use /ks load <kills> - To list use /ks list");
				return true;
			}
			int kills = Integer.parseInt(arg3[1]);
			GameStreak gs = getSelection(arg0.getName());
			List<KillStreak> killStreaks = gs.getKillStreaks();
			for (KillStreak ks : killStreaks)
				if (ks.getNeededKills() == kills)
					if (ks instanceof ItemKillStreak) {
						ItemKillStreak iks = (ItemKillStreak) ks;
						p.getInventory().setItemInHand(
								iks.getItemStack().getBukkitItemStack());
						arg0.sendMessage(prefix
								+ "Set item in your hand to the killstreak.");
						return true;
					}
			arg0.sendMessage(prefix + "Nothing found for kill amount " + kills);
			return true;
		}
		case "gamemodes": {
			arg0.sendMessage(prefix + "Gamemodes are " + GameMode.asString());
			return true;
		}
		case "del": {
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You have no selection, use /ks sel <gamemode>");
				return true;
			} else if (arg3.length == 1 || !isInteger(arg3[1])) {
				arg0.sendMessage(prefix
						+ "Please use /ks del <kills> - To list use /ks list");
				return true;
			}
			int kills = Integer.parseInt(arg3[1]);
			GameStreak gs = getSelection(arg0.getName());
			if (gs.deleteKillStreak(kills))
				arg0.sendMessage(prefix + "Deleted killstreak for " + kills
						+ " kills.");
			else
				arg0.sendMessage(prefix + "No killstreak with id " + kills
						+ " use /ks list");
			return true;
		}
		case "add": {
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You have no selection, use /ks sel <gamemode>");
				return true;
			} else if (arg3.length <= 2 || !isInteger(arg3[1])) {
				arg0.sendMessage(prefix
						+ "Please use /ks add <kills> <item|pet>");
				return true;
			}
			int kills = Integer.parseInt(arg3[1]);
			GameStreak gs = getSelection(arg0.getName());
			switch (arg3[2].toLowerCase()) {
			case "pet":
				arg0.sendMessage(prefix + "Not yet implemented");
				return true;
			case "item":
				ItemStack is = p.getItemInHand();
				if (is == null || is.getType() == Material.AIR) {
					arg0.sendMessage(prefix
							+ "Please have an item in your hand and try again.");
					return true;
				}
				if (gs.addItem(p.getItemInHand(), kills)) {
					arg0.sendMessage(prefix
							+ "Sucessfully added item streak for " + kills
							+ " kills.");
				} else {
					arg0.sendMessage(prefix
							+ "That killstreak slot is currently being used ("
							+ kills + ")");
				}
				return true;
			}
		}
		case "sel": {
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "Please use /ks sel <gamemode>, list of gamemodes are "
						+ GameMode.asString());
				return true;
			}
			if (isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "Please use /ks save if you wish to save then /ks quit");
				return true;
			}
			String kit = arg3[1];
			GameMode gameMode = GameMode.getByName(kit);
			if (gameMode == null) {
				arg0.sendMessage(prefix + kit
						+ " gamemode was not found, available gamemodes are "
						+ GameMode.asString());
				return true;
			}
			for (GameStreak gs : gameStreaks)
				if (gs.getGameMode() == gameMode) {
					manipulatingMap.put(arg0.getName(), gs);
					arg0.sendMessage(prefix + "Selected " + gameMode.name()
							+ "'s killstreak");
					return true;
				}
			arg0.sendMessage(prefix + "Something went wrong selecting " + kit);
			return true;
		}
		case "save": {
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You need to have selected a gamestreak first");
				return true;
			}
			getSelection(arg0.getName()).save();
			arg0.sendMessage(prefix + "Saved killstreak "
					+ getSelection(arg0.getName()).getGameMode().name());
			return true;
		}
		case "quit":
			if (!isInSelection(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You do not currently have a killstreak selection.");
				return true;
			}
			manipulatingMap.get(arg0.getName()).loadKillStreaks();
			manipulatingMap.remove(arg0.getName());
			arg0.sendMessage(prefix + "Exited from selection.");
			return true;
		default:
			listAdminCommands(arg0);
			return true;
		}
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isInSelection(String name) {
		return manipulatingMap.containsKey(name);
	}

	private GameStreak getSelection(String name) {
		return manipulatingMap.get(name);
	}

	private HashMap<String, GameStreak> manipulatingMap = new HashMap<String, GameStreak>();

	private void listAdminCommands(CommandSender arg0) {
		arg0.sendMessage(prefix + "Admin commands");
		arg0.sendMessage(ChatColor.AQUA
				+ "  /ks gamemodes - Lists all gamemodes");
		if (manipulatingMap.containsKey(arg0.getName())) {
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks list - Lists all the current killstreaks and their kill values");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks add <kills> <pet|item> - Adds a killsteak which needs <kills> - <item> saves item in your hand, <pet> - Placeholder");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks del <kills> - Deletes the killstreak which requires <kills>");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks load <kills> - Loads the killstreak via min kills");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks save - Saves the selected GameStreak to file");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks quit - Exits the current selection, keeps settings but doesn't save to file. ");
		} else {
			arg0.sendMessage(ChatColor.AQUA
					+ "  /ks sel <gamemode> - Selects the gamemodes killstreak");
		}
		arg0.sendMessage(prefix);
	}
}
