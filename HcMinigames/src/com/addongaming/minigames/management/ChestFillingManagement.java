package com.addongaming.minigames.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.chest.ChestFiller;

public class ChestFillingManagement implements CommandExecutor {
	private final HcMinigames minigames;
	private final static HashMap<GameMode, ChestFiller> fillingMap = new HashMap<GameMode, ChestFiller>();
	private final static HashMap<String, ChestFiller> modificationMap = new HashMap<String, ChestFiller>();
	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "ChestMan"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	public ChestFillingManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getCommand("cm").setExecutor(this);
		for (GameMode gm : GameMode.values())
			if (gm.isChestPopulating())
				fillingMap.put(gm,
						new ChestFiller(new File(minigames.getDataFolder()
								+ File.separator + "ChestFilling", gm.name()),
								gm));
	}

	public ChestFiller getChestFiller(GameMode gameMode) {
		return fillingMap.get(gameMode);
	}

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
		switch (arg3[0].toLowerCase()) {
		case "sel": {
			if (arg3.length == 1) {
				arg0.sendMessage(prefix + "Please provide a gamemode to edit.");
				return true;
			}
			String gameModeStr = arg3[1];
			GameMode gm = GameMode.getByName(gameModeStr);
			if (gm == null) {
				arg0.sendMessage(prefix + gameModeStr + " not found.");
				return true;
			} else if (!gm.isChestPopulating()) {
				arg0.sendMessage(prefix
						+ "That gamemode does not support chest population");
				return true;
			}
			modificationMap.put(arg0.getName(), fillingMap.get(gm));
			arg0.sendMessage(prefix + "Selected " + gameModeStr);
			return true;
		}
		case "listgm": {
			List<String> gms = new ArrayList<String>();
			for (GameMode gm : GameMode.values())
				if (gm.isChestPopulating())
					gms.add(gm.name().toLowerCase());
			arg0.sendMessage(prefix + "Available gamemodes: "
					+ StringUtils.join(gms, ", "));
			return true;
		}
		case "save": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			modificationMap.get(arg0.getName()).save();
			arg0.sendMessage(prefix + "Saved.");
			return true;
		}
		case "listid": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			ChestFiller cf = modificationMap.get(arg0.getName());
			if (cf.getIds().size() == 0)
				arg0.sendMessage(prefix
						+ "There are no available tiers yet. Use /cm add <tier>");
			else
				arg0.sendMessage(prefix + "Available tiers: " + cf.getAllIds());
			return true;
		}
		case "del": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			if (arg3.length == 2) {
				arg0.sendMessage(prefix + "Please provide a tier to delete.");
				return true;
			}
			if (!isInteger(arg3[2])) {
				arg0.sendMessage(prefix + "Please provide a valid tier ID");
				return true;
			}
			int id = Integer.parseInt(arg3[2]);
			ChestFiller cf = modificationMap.get(arg0.getName());
			if (!cf.hasInventory(id)) {
				arg0.sendMessage(prefix + "There isn't a tier with that id.");
				return true;
			}
			cf.removeId(id);
			arg0.sendMessage(prefix + "Deleted " + id + ".");
			return true;
		}
		case "add": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			if (arg3.length == 1) {
				arg0.sendMessage(prefix + "Please provide a tier to add.");
				return true;
			}
			if (!isInteger(arg3[1])) {
				arg0.sendMessage(prefix + "Please provide a valid tier ID");
				return true;
			}
			int id = Integer.parseInt(arg3[1]);
			ChestFiller cf = modificationMap.get(arg0.getName());
			cf.addInventory(id);
			arg0.sendMessage(prefix + "Created " + id + ".");
			return true;
		}
		case "load": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			if (arg3.length == 1) {
				arg0.sendMessage(prefix + "Please provide a tier to load.");
				return true;
			}
			if (!isInteger(arg3[1])) {
				arg0.sendMessage(prefix + "Please provide a valid tier ID");
				return true;
			}
			int id = Integer.parseInt(arg3[1]);
			ChestFiller cf = modificationMap.get(arg0.getName());
			if (!cf.hasInventory(id))
				arg0.sendMessage(prefix + "No inventory for " + id + ".");
			else {
				for (ItemStack is : cf.getItems(id))
					((Player) arg0).getInventory().addItem(is);
				((Player) arg0).updateInventory();
				arg0.sendMessage(prefix + "Loaded " + id + ".");
			}
			return true;
		}
		case "reload": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			ChestFiller cf = modificationMap.get(arg0.getName());
			cf.reload();
			arg0.sendMessage(prefix + "Reloaded.");
			return true;
		}
		case "quit": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			modificationMap.get(arg0.getName()).reload();
			modificationMap.remove(arg0.getName());
			arg0.sendMessage(prefix + "Exited editing.");
			return true;
		}
		case "set": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You aren't modifying any chest fillers");
				return true;
			}
			if (arg3.length == 1) {
				listAdminCommands(arg0);
				return true;
			}
			ChestFiller cf = modificationMap.get(arg0.getName());
			switch (arg3[1]) {
			case "inv": {
				if (arg3.length == 2) {
					arg0.sendMessage(prefix
							+ "Please provide a tier to set the items of.");
					return true;
				}
				if (!isInteger(arg3[2])) {
					arg0.sendMessage(prefix + "Please provide a valid tier ID");
					return true;
				}
				int id = Integer.parseInt(arg3[2]);
				if (!cf.hasInventory(id)) {
					arg0.sendMessage(prefix + "That tier doesn't exist.");
					return true;
				}
				modificationMap.get(arg0.getName()).setInventory(id,
						((Player) arg0).getInventory());
				arg0.sendMessage(prefix + "Updated inventory");
				return true;
			}
			case "gen":
				if (arg3.length <= 4) {
					arg0.sendMessage(prefix
							+ "/cm set gen min|max <tier> <itemamount>");
					return true;
				}

				if (!isInteger(arg3[3]) || !isInteger(arg3[4])) {
					arg0.sendMessage(prefix
							+ "/cm set gen min|max <tier> <itemamount> - Tier and item amount are numbers");
					return true;
				}
				int tier = Integer.parseInt(arg3[3]),
				amnt = Integer.parseInt(arg3[4]);
				if (!cf.hasInventory(tier)) {
					arg0.sendMessage(prefix + "That tier doesn't exist.");
					return true;
				}
				if (arg3[2].equalsIgnoreCase("min")) {
					cf.setMin(tier, amnt);
					arg0.sendMessage(prefix + "Set the minimum item gen");
				} else if (arg3[2].equalsIgnoreCase("max")) {
					cf.setMax(tier, amnt);
					arg0.sendMessage(prefix + "Set the maximum item gen");
				} else {
					arg0.sendMessage(prefix
							+ "/cm set gen min|max <tier> <itemamount> - Tier and item amount are numbers");
				}
				return true;
			}

		}
		}
		return true;
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void listAdminCommands(CommandSender cs) {
		cs.sendMessage(prefix + "Commands");
		if (!modificationMap.containsKey(cs.getName())) {
			cs.sendMessage(ChatColor.AQUA
					+ "/cm sel <gamemode> - Selects a game modes chest man");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm listgm - Lists all gamemodes which work with chest man");
		} else {
			cs.sendMessage(ChatColor.AQUA
					+ "/cm listid - Lists all current inventory ID's");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm add <tier> - Creates a new tier of chest man.");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm del <tier> - Deletes a tier of chest man.");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm set inv <tier> - Sets your current inventory as possible items");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm set gen min <tier> <amnt> - Sets the minimum items spawned for that tier");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm set gen max <tier> <amnt> - Sets the maximum items spawned for that tier");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm reload - Reloads saved information");
			cs.sendMessage(ChatColor.AQUA + "/cm save - Saves changes to file");
			cs.sendMessage(ChatColor.AQUA
					+ "/cm quit - Exits out of modification");
		}
		cs.sendMessage(prefix);
	}
}
