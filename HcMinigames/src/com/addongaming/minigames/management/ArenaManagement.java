package com.addongaming.minigames.management;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.data.Position;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.SpawnZone;
import com.addongaming.minigames.management.arena.SurvivalGameArena;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.arena.TeamSpawns;
import com.addongaming.minigames.management.arena.TheShipArena;
import com.addongaming.minigames.management.flag.Flag;
import com.addongaming.minigames.minigames.ArenaGame;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ArenaManagement implements CommandExecutor {
	private final HcMinigames minigames;
	// List of all arenas for the actual minigame system
	private final static List<Arena> arenaMap = new ArrayList<Arena>();
	// Map of editing users and the arena
	private final static HashMap<String, Arena> arenaModification = new HashMap<String, Arena>();
	// List of arena types i.e. Camelot, Bioshock etc (schematics)
	private List<String> arenaTypes = new ArrayList<String>();
	// Last id of arena, auto increments
	private int latestArena = 0;
	// Folder represented as plugins/HcMinigames/Minigames
	File minigamesFolder;

	public ArenaManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getCommand("arenaman").setExecutor(this);
		checkMinigameStructure();
		loadArenas();
	}

	private void loadArenas() {
		for (File file : minigamesFolder.listFiles()) {
			String arena = file.getName();
			arenaTypes.add(arena);
			for (File roots : file.listFiles()) {
				if (roots.getName().endsWith(".yml")) {
					Integer i = Integer.parseInt(roots.getName().substring(0,
							roots.getName().indexOf('.')));
					if (latestArena < i)
						latestArena = i;
					loadArena(arena, roots);
				}
			}
		}

	}

	// TODO Loading of arenas fully
	private void loadArena(String arenaType, File roots) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(roots);
		Arena ar;
		GameMode gameMode = GameMode.getByName(config.getString("gamemode"));
		if (gameMode == GameMode.SURVIVAL_GAMES) {
			ar = new SurvivalGameArena(Integer.parseInt(roots.getName()
					.substring(0, roots.getName().indexOf('.'))),
					GameMode.getByName(config.getString("gamemode")),
					arenaType, config);
		} else if (gameMode == GameMode.THE_SHIP) {
			ar = new TheShipArena(Integer.parseInt(roots.getName().substring(0,
					roots.getName().indexOf('.'))), GameMode.getByName(config
					.getString("gamemode")), arenaType, config);
		} else {
			ar = new Arena(Integer.parseInt(roots.getName().substring(0,
					roots.getName().indexOf('.'))), GameMode.getByName(config
					.getString("gamemode")), arenaType, config);
		}
		arenaMap.add(ar);
	}

	// TODO Saving of arena
	private void saveArena(Arena arena) {
		YamlConfiguration config = arena.saveToFile();
		arena.setConfig(config);
		try {
			config.save(new File(minigamesFolder + File.separator
					+ arena.getArenaType(), arena.getId() + ".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addArenaType(String name) {
		if (!new File(minigamesFolder, name).exists())
			new File(minigamesFolder, name).mkdirs();
	}

	// Checks the bare-bone folders have been created
	private void checkMinigameStructure() {
		minigamesFolder = new File(minigames.getDataFolder().getAbsolutePath()
				+ File.separator + "Arenas");
		if (!minigamesFolder.exists())
			minigamesFolder.mkdirs();
	}

	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "ArenaMan"
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
		switch (arg3[0].toLowerCase()) {
		// TODO Temp code
		case "convert":
			arg0.sendMessage(prefix + "Adding all arenas to database.");
			for (Arena arena : arenaMap)
				minigames
						.getManagement()
						.getScoreManagement()
						.registerArena(arena.getGameMode(),
								arena.getArenaType());
			arg0.sendMessage(prefix + "Scheduled all arenas to be added.");
			return true;
		case "arenatype":
		case "at":
			arenaTypeCmd(arg0, arg3);
			return true;
		case "arena":
			arenaCmd(arg0, arg3);
			return true;
		case "gm":
		case "gamemodes":
			arg0.sendMessage(prefix + "Available gamemodes: "
					+ GameMode.asString());
			return true;
		case "selection":
		case "sel":
			selectionCmd(arg0, arg3);
			return true;
		case "team":
		case "teams":
			arg0.sendMessage(prefix + "Currently available teams");
			for (Team team : Team.values()) {
				arg0.sendMessage("  " + ChatColor.GOLD + "Team: "
						+ ChatColor.AQUA + team.name().toLowerCase()
						+ ChatColor.GOLD + " ID: " + ChatColor.AQUA
						+ team.getTeamId());
			}
			return true;
		case "prop":
		case "property":
			arg0.sendMessage(prefix + "Currently available properties");
			for (ArenaProperty ap : ArenaProperty.values()) {
				arg0.sendMessage("  " + ChatColor.GOLD + "Name: "
						+ ChatColor.AQUA + ap.name().toLowerCase()
						+ ChatColor.GOLD + " Type: " + ChatColor.AQUA
						+ ap.getPropertyClass().getSimpleName());
			}
			arg0.sendMessage(prefix);
			return true;
		default:
			listAdminCommands(arg0);
			return true;
		}
	}

	private void selectionCmd(CommandSender arg0, String[] arg3) {
		if (!arenaModification.containsKey(arg0.getName())) {
			arg0.sendMessage(prefix
					+ "Please use /am arena - for selecting an arena");
			return;
		}
		if (arg3.length == 1) {
			listSelectionCommands(arg0);
			return;
		}
		Arena arena = arenaModification.get(arg0.getName());
		switch (arg3[1].toLowerCase()) {
		// TODO Minigames special things
		case "sg":
			if (!(arena instanceof SurvivalGameArena)) {
				arg0.sendMessage(prefix
						+ "This arena is not of type SurvivalGameArena");
				return;
			}
			survivalGames(arg0, arg3);
			return;
		case "ship":
			if (!(arena instanceof TheShipArena)) {
				arg0.sendMessage(prefix
						+ "This arena is not of type TheShipArena");
				return;
			}
			theShip(arg0, arg3);
			return;
		case "setproperty":
		case "addproperty": {
			if (arg3.length == 2) {
				arg0.sendMessage(prefix
						+ "Please use /am addproperty <prop> [value] - use /am property for all");
				return;
			}
			String name = arg3[2].toUpperCase();
			ArenaProperty ap = ArenaProperty.getByName(name);
			if (ap == null) {
				arg0.sendMessage(prefix
						+ name.toLowerCase()
						+ " not found, use /am property for a list of all properties");
				return;
			}
			String str = null;
			if (arg3.length == 4) {
				str = arg3[3];
			}
			if (!ArenaProperty.isValid(ap, (Player) arg0, str)) {
				if (ap.getPropertyClass() == SpawnZone.class
						|| ap.getPropertyClass() == LocationZone.class) {
					arg0.sendMessage(prefix + "You need an area selected.");
				} else
					arg0.sendMessage(prefix + ap.name() + " needs a value of "
							+ ap.getPropertyClass().getSimpleName());
				return;
			}
			Object property = ArenaProperty.getObject(ap, (Player) arg0, str);
			if (property == null) {
				arg0.sendMessage(prefix
						+ "Something went wrong. Property value was null on return.");
				return;
			}
			arena.putProperty(ap, property);
			arg0.sendMessage(prefix + "Added property " + ap.name());
			return;
		}
		case "delproperty": {
			if (arg3.length == 2) {
				arg0.sendMessage(prefix
						+ "Please use /am sel delproperty <name> - use /am sel listproperty for all currently set");
				return;
			}
			String name = arg3[2].toUpperCase();
			ArenaProperty ap = ArenaProperty.getByName(name);
			if (ap == null) {
				arg0.sendMessage(prefix
						+ name.toLowerCase()
						+ " not found, please use /am sel delproperty <name> - use /am sel listproperty for all currently set");
				return;
			}
			if (!arena.getArenaProperties().containsKey(ap)) {
				arg0.sendMessage(prefix
						+ "This arena doesn't have that property set.");
				return;
			}
			arena.removeProperty(ap);
			arg0.sendMessage(prefix + "Deleted property " + name.toLowerCase());
			return;
		}
		case "listproperty": {
			Set<ArenaProperty> prop = arena.getArenaProperties().keySet();
			if (prop.isEmpty()) {
				arg0.sendMessage(prefix + "No properties have been set yet.");
				return;
			}
			arg0.sendMessage(prefix + "Properties for arena " + arena.getId());
			for (ArenaProperty ap : prop) {
				Class<?> def = ap.getPropertyClass();
				String str = "";
				if (def == Integer.class)
					str = arena.getInt(ap) + "";
				else if (def == Boolean.class)
					str = arena.getString(ap);
				else if (def == String.class)
					str = arena.getString(ap);
				else if (def == SpawnZone.class)
					str = arena.getSpawnZone(ap).asString();
				else if (def == LocationZone.class)
					str = arena.getLocationZone(ap).asString();
				arg0.sendMessage(ChatColor.GOLD + "   Prop: " + ChatColor.AQUA
						+ ap.name().toLowerCase() + ChatColor.GOLD + " Value: "
						+ ChatColor.AQUA + str);
			}
			arg0.sendMessage(prefix);
			return;
		}
		case "info": {
			arg0.sendMessage(prefix);
			arg0.sendMessage(ChatColor.AQUA + "Information for"
					+ ChatColor.GOLD + " #" + arena.getId());
			arg0.sendMessage(ChatColor.AQUA + "    GameMode: "
					+ arena.getGameMode().name());
			int counter = 0;
			for (TeamSpawns ts : arena.getTeamSpawnMap().values())
				counter += ts.getZones().size();
			arg0.sendMessage(ChatColor.AQUA + "    Spawns Set: " + counter);
			arg0.sendMessage(ChatColor.AQUA
					+ "    Flags Set: Not Yet Implemented");
			arg0.sendMessage(prefix);
			return;
		}
		case "setlobby": {
			Selection sel = minigames.getWorldEditHook().getSelection(
					(Player) arg0);
			if (sel == null || sel.getMinimumPoint() == null
					|| sel.getMaximumPoint() == null) {
				arg0.sendMessage(prefix
						+ "Your WorldEdit region is currently empty or doesn't contain both points.");
				return;
			}
			Player p = (Player) arg0;
			SpawnZone lz = new SpawnZone(sel.getMinimumPoint(),
					sel.getMaximumPoint(), p.getEyeLocation().getYaw(), p
							.getEyeLocation().getPitch());
			arena.setLobbyLocation(lz);
			arg0.sendMessage(prefix + "Set the lobby location");
			return;
		}
		case "addspawn": {
			Selection sel = minigames.getWorldEditHook().getSelection(
					(Player) arg0);
			if (arg3.length != 3) {
				arg0.sendMessage(prefix
						+ "Please use /am sel addspawn <teamid>");
				return;
			}
			if (!isInteger(arg3[2])) {
				arg0.sendMessage(prefix
						+ arg3[2]
						+ " is not a valid team ID. Please use a number i.e. -1, 0, 1");
				return;
			}
			int id = Integer.parseInt(arg3[2]);
			if (sel == null || sel.getMinimumPoint() == null
					|| sel.getMaximumPoint() == null) {
				arg0.sendMessage(prefix
						+ "Your WorldEdit region is currently empty or doesn't contain both points.");
				return;
			}
			Player p = (Player) arg0;
			SpawnZone lz = new SpawnZone(sel.getMinimumPoint(),
					sel.getMaximumPoint(), p.getEyeLocation().getYaw(), p
							.getEyeLocation().getPitch());
			arena.addSpawnZone(id, lz);
			arg0.sendMessage(prefix + "Added spawn to team " + id + " ("
					+ Team.getById(id) + ")");
			return;
		}
		case "listspawn": {
			arg0.sendMessage(prefix + "All spawns for arena " + arena.getId());
			HashMap<Integer, TeamSpawns> spawnMap = arena.getTeamSpawnMap();
			for (Integer i : spawnMap.keySet()) {
				arg0.sendMessage(ChatColor.GOLD + "     Team " + i + " ("
						+ Team.getById(i) + ")");
				int counter = 0;
				for (SpawnZone lz : spawnMap.get(i).getZones()) {
					arg0.sendMessage("#" + (counter++) + " Min X:"
							+ lz.getMin().getBlockX() + " Y: "
							+ lz.getMin().getBlockY() + " Z: "
							+ lz.getMin().getBlockZ() + " Max X:"
							+ lz.getMax().getBlockX() + " Y: "
							+ lz.getMax().getBlockY() + " Z: "
							+ lz.getMax().getBlockZ());
				}
			}
			arg0.sendMessage(prefix);
			return;
		}
		case "delspawn": {
			if (arg3.length != 4) {
				arg0.sendMessage(prefix
						+ "Please use /am sel delspawn <teamid> <spawnid>");
				return;
			}
			if (!isInteger(arg3[2]) || !isInteger(arg3[3])) {
				arg0.sendMessage(prefix
						+ "Please use /am sel delspawn <teamid> <spawnid>");
				return;
			}
			int teamId = Integer.parseInt(arg3[2]), spawnId = Integer
					.parseInt(arg3[3]);
			if (arena.removeSpawnZone(teamId, spawnId))
				arg0.sendMessage(prefix + "Successfully deleted spawn.");
			else
				arg0.sendMessage(prefix
						+ "Something went wrong deleting the spawn.");
			return;
		}
		case "flag":
			if (arg3.length <= 2) {
				arg0.sendMessage(prefix
						+ "Please use /am sel flag <add|list|remove> [id]");
				return;
			}
			switch (arg3[2]) {
			case "add":
				arenaModification.get(arg0.getName()).addFlag(
						((Player) arg0).getLocation());
				arg0.sendMessage(prefix + "Added flag.");
				break;
			case "list":
				Flag[] flags = arena.getFlags();
				if (flags.length == 0) {
					arg0.sendMessage(prefix + "No flags have been setup.");
					return;
				}
				arg0.sendMessage(prefix + "   Flags");
				for (int i = 0; i < flags.length; i++) {
					arg0.sendMessage(ChatColor.GOLD + "Id: " + ChatColor.AQUA
							+ i + ChatColor.GOLD + " Location: "
							+ ChatColor.AQUA
							+ Utils.locationToString(flags[i].getOrigLoc()));
				}
				arg0.sendMessage(prefix);
				return;
			case "del":
				if (arg3.length <= 3 || !isInteger(arg3[3])) {
					arg0.sendMessage(prefix
							+ "Please use /am sel flag del <id>. For all id's do /am sel list");
					return;
				}
				int id = Integer.parseInt(arg3[3]);
				if (arena.removeFlag(id)) {
					arg0.sendMessage(prefix + "Removed the flag!");
				} else
					arg0.sendMessage(prefix + "No flag found at id " + id);
				return;
			}
			return;
		case "save":
			if (arg3.length == 3) {
				if (arg3[2].equalsIgnoreCase("true"))
					arena.setEnabled(true);
				else if (arg3[2].equalsIgnoreCase("false"))
					arena.setEnabled(false);
			}
			saveArena(arena);
			arg0.sendMessage(prefix + "Saved arena");
			return;
		case "quit":
			arena.reload();
			arenaModification.remove(arg0.getName());
			arg0.sendMessage(prefix
					+ "Quit out of arena and reloaded it from file.");
			return;
		default:
			listSelectionCommands(arg0);
		}
	}

	private void survivalGames(CommandSender arg0, String[] arg3) {
		if (arg3.length == 2) {
			listSelectionCommands(arg0);
			return;
		}
		// /am sel sg spawns
		// addspawn
		// delspawn
		SurvivalGameArena sga = (SurvivalGameArena) arenaModification.get(arg0
				.getName());
		switch (arg3[2]) {
		case "spawns": {
			if (sga.getPositions().length == 0) {
				arg0.sendMessage(prefix
						+ "There are currently no spawn points set.");
				return;
			} else {
				int counter = 0;
				arg0.sendMessage(prefix + "Available starting points");
				for (Position pos : sga.getPositions()) {
					arg0.sendMessage(ChatColor.GOLD + "Id: " + ChatColor.AQUA
							+ counter + " "
							+ Utils.locationToString(pos.getLoc()));
				}
				arg0.sendMessage(prefix);
				return;
			}
		}
		case "addspawn": {
			Player p = (Player) arg0;
			sga.addPosition(p.getLocation());
			arg0.sendMessage(prefix + "Added location");
			return;
		}
		case "delspawn": {
			if (arg3.length == 3) {
				arg0.sendMessage(prefix
						+ "Please give an ID of the spawn to delete");
				return;
			} else if (!isInteger(arg3[3])) {
				arg0.sendMessage(prefix
						+ "Please give a valid numerical ID of the spawn to delete.");
				return;
			}
			int i = Integer.parseInt(arg3[3]);
			if (sga.removePosition(i)) {
				arg0.sendMessage(prefix + "Deleted the spawn position.");
			} else {
				arg0.sendMessage(prefix
						+ "Error deleting the spawn. Are you sure the ID is correct?");
			}
			return;
		}

		case "scan": {
			if (!sga.hasLocationZone(ArenaProperty.ARENA)) {
				arg0.sendMessage(prefix
						+ "Please set the ArenaProperty ARENA first.");
				return;
			}
			arg0.sendMessage(prefix + "Scanning the arena. This may cause lag.");
			LocationZone zone = sga.getLocationZone(ArenaProperty.ARENA);
			HashMap<Position, Integer> chestMap = new HashMap<Position, Integer>();
			for (Block block : zone.getAllBlocks()) {
				if (block.getType() == Material.CHEST) {
					boolean signFound = false;
					for (BlockFace bf : BlockFace.values())
						if (block.getRelative(bf).getState() != null
								&& block.getRelative(bf).getState() instanceof Sign) {
							if (!signFound) {
								Sign sign = (Sign) block.getRelative(bf)
										.getState();
								for (String str : sign.getLines())
									if (str.length() > 0 && isInteger(str)) {
										chestMap.put(
												new Position(block
														.getLocation()),
												Integer.parseInt(str));
										signFound = true;
									}
							}
						}
					if (!signFound)
						chestMap.put(new Position(block.getLocation()), 1);
				}
			}
			sga.setChests(chestMap);
			arg0.sendMessage(prefix + "Finished scanning. Found "
					+ ChatColor.GOLD + chestMap.keySet().size()
					+ ChatColor.AQUA + " chests.");
			return;
		}
		}
	}

	private void theShip(CommandSender arg0, String[] arg3) {
		if (arg3.length == 2) {
			listSelectionCommands(arg0);
			return;
		}
		// /am sel sg spawns
		// addspawn
		// delspawn
		TheShipArena sga = (TheShipArena) arenaModification.get(arg0.getName());
		switch (arg3[2]) {
		case "scan": {
			if (!sga.hasLocationZone(ArenaProperty.ARENA)) {
				arg0.sendMessage(prefix
						+ "Please set the ArenaProperty ARENA first.");
				return;
			}
			arg0.sendMessage(prefix + "Scanning the arena. This may cause lag.");
			LocationZone zone = sga.getLocationZone(ArenaProperty.ARENA);
			HashMap<Position, Integer> chestMap = new HashMap<Position, Integer>();
			for (Block block : zone.getAllBlocks()) {
				if (block.getType() == Material.CHEST) {
					boolean signFound = false;
					for (BlockFace bf : BlockFace.values())
						if (block.getRelative(bf).getState() != null
								&& block.getRelative(bf).getState() instanceof Sign) {
							if (!signFound) {
								Sign sign = (Sign) block.getRelative(bf)
										.getState();
								for (String str : sign.getLines())
									if (str.length() > 0 && isInteger(str)) {
										chestMap.put(
												new Position(block
														.getLocation()),
												Integer.parseInt(str));
										signFound = true;
									}
							}
						}
					if (!signFound)
						chestMap.put(new Position(block.getLocation()), 1);
				}
			}
			sga.setChests(chestMap);
			arg0.sendMessage(prefix + "Finished scanning. Found "
					+ ChatColor.GOLD + chestMap.keySet().size()
					+ ChatColor.AQUA + " chests.");
			return;
		}
		}
	}

	private void arenaCmd(CommandSender arg0, String[] arg3) {
		if (arg3.length == 1) {
			listArenasCommands(arg0);
			return;
		}
		switch (arg3[1].toLowerCase()) {
		case "add": {
			if (arg3.length != 4) {
				arg0.sendMessage(prefix
						+ " Please use /am add <arenatype> <gamemode>");
				arg0.sendMessage(prefix
						+ " /am at list - for all arena types. Or /am gamemodes - for gamemodes");
				return;
			}
			String type = arg3[2];
			GameMode gm = GameMode.getByName(arg3[3]);
			if (gm == null) {
				arg0.sendMessage(prefix + "Gamemode: " + arg3[3]
						+ " not found. Please use /am gamemodes");
				return;
			}
			for (String types : arenaTypes)
				if (type.equalsIgnoreCase(types)) {
					latestArena++;
					Arena arena;
					// TODO Setup extra games here
					if (gm == GameMode.SURVIVAL_GAMES)
						arena = new SurvivalGameArena(latestArena, gm, types);
					else if (gm == GameMode.THE_SHIP)
						arena = new TheShipArena(latestArena, gm, types);
					else
						arena = new Arena(latestArena, gm, types);
					arena.setEnabled(false);
					arenaMap.add(arena);
					arenaModification.put(arg0.getName(), arena);
					arg0.sendMessage(prefix + "Created arena with map type "
							+ types + " and gamemode "
							+ gm.name().toLowerCase());
					arg0.sendMessage(ChatColor.AQUA + "Arena #" + arena.getId()
							+ " is now your selection. /am sel - to modify");
					return;
				}
			arg0.sendMessage(prefix
					+ type
					+ " map type not found. Use /am at list - for all arena types");
			return;
		}
		case "list": {
			arg0.sendMessage(prefix + " Current loaded arenas:");
			for (Arena a : arenaMap) {
				arg0.sendMessage(ChatColor.GOLD
						+ "ID: "
						+ ChatColor.AQUA
						+ a.getId()
						+ ChatColor.GOLD
						+ " Type: "
						+ ChatColor.AQUA
						+ a.getArenaType()
						+ "/"
						+ a.getGameMode().name().toLowerCase()
						+ (a.isEnabled() ? "" : "/" + ChatColor.RED
								+ "Disabled"));
			}
			arg0.sendMessage(prefix + arenaMap.size() + " loaded arenas.");
		}
			return;
		case "del": {
			if (arg3.length != 3) {
				arg0.sendMessage(prefix + "Please use /am arena del <id>");
				return;
			}
			if (!isInteger(arg3[2])) {
				arg0.sendMessage(prefix + arg3[2]
						+ " is not a valid ID. Please use /am arena list");
				return;
			}
			int id = Integer.parseInt(arg3[2]);
			for (Iterator<Arena> iter = arenaMap.iterator(); iter.hasNext();) {
				Arena arena = iter.next();
				if (arena.getId() == id) {
					if (arena.hasCurrentGame()) {
						arg0.sendMessage(prefix
								+ "That arena is currently being used!");
						return;
					}
					deleteArena(arena);
					iter.remove();
					arg0.sendMessage(prefix + "Deleted arena");
					return;
				}
			}
			arg0.sendMessage(prefix + "Arena with ID: " + id
					+ " not found. Please use /am arena list");
			return;
		}
		case "select": {
			if (arenaModification.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You already have an arena selected. Please safely save and exit using /am sel");
				return;
			}
			if (!isInteger(arg3[2])) {
				arg0.sendMessage(prefix + arg3[2]
						+ " is not a valid ID. Please use /am arena list");
				return;
			}
			int id = Integer.parseInt(arg3[2]);
			for (Arena a : arenaMap) {
				if (a.getId() == id) {
					if (a.isEnabled()) {
						a.setEnabled(false);
						arg0.sendMessage(prefix
								+ ChatColor.BOLD
								+ "Obtained lock on arena. Remember to re-enable after editing.");
					}
					arenaModification.put(arg0.getName(), a);
					arg0.sendMessage(prefix + "Selected arena #" + a.getId()
							+ " use /am sel - to alter the arena");
					return;
				}
			}
			arg0.sendMessage(prefix + "Arena with ID: " + id
					+ " not found. Please use /am arena list");
			return;
		}
		default:
			listArenasCommands(arg0);
			return;
		}
	}

	private void deleteArena(Arena arena) {
		File file = new File(minigamesFolder + File.separator
				+ arena.getArenaType(), arena.getId() + ".yml");
		if (file.exists())
			file.delete();
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void arenaTypeCmd(CommandSender arg0, String[] arg3) {
		if (arg3.length == 1) {
			listArenaTypeCommands(arg0);
			return;
		}
		switch (arg3[1].toLowerCase()) {
		case "add": {
			if (arg3.length == 2) {
				arg0.sendMessage(prefix + "Please use /am at add <arenaname>");
				return;
			}
			String str = arg3[2];
			for (String string : arenaTypes)
				if (string.equalsIgnoreCase(str)) {
					arg0.sendMessage(prefix + string
							+ " arena type already exists.");
					return;
				}
			addArenaType(str);
			arenaTypes.add(str);
			arg0.sendMessage(prefix + "Added arena type " + str);
			return;
		}
		case "list": {
			if (arenaTypes.isEmpty())
				arg0.sendMessage(prefix
						+ "There are no arena types. Want to create one? /am at add <arenaname>");
			else
				arg0.sendMessage(prefix + "Currently loaded arena types: "
						+ StringUtils.join(arenaTypes, ", "));
		}
			return;
		case "del": {
			if (arg3.length == 2) {
				arg0.sendMessage(prefix + "Please use /am at del <arenaname>");
				return;
			}
			String str = arg3[2];
			for (String string : arenaTypes)
				if (string.equalsIgnoreCase(str)) {
					if (deleteArenaType(string)) {
						arg0.sendMessage(prefix
								+ "Successfully removed arena type " + string);
					} else {
						arg0.sendMessage(prefix
								+ "Error deleting arenatype. Make sure there are no arenas of that type.");
					}
					return;
				}
			return;
		}
		default:
			listArenaTypeCommands(arg0);
		}
	}

	private boolean deleteArenaType(String string) {
		return new File(minigamesFolder, string).delete();
	}

	private void listAdminCommands(CommandSender cs) {
		cs.sendMessage(prefix + "Commands");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arenatype - Arena type commands");
		cs.sendMessage(ChatColor.AQUA + "    /am arena - All arena commands");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel - All arena modification commands (selection)");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am gamemodes - Lists all gamemodes");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am property - Lists all arena properties");
		cs.sendMessage(ChatColor.AQUA + "    /am teams - Lists all teams");
		cs.sendMessage(prefix);
	}

	private void listArenaTypeCommands(CommandSender cs) {
		cs.sendMessage(prefix + "ArenaType - Commands");
		cs.sendMessage(ChatColor.DARK_AQUA
				+ "ArenaType allows for arenas to be organise by map. For example if you have two camelot maps, the arenatype would be Camelot.");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arenatype add <name> - Adds a new arenatype");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arenatype list - Lists all arena types");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arenatype del <name> - Deletes an arenatype. There must be no arenas with that type.");
		cs.sendMessage(prefix);
	}

	private void listArenasCommands(CommandSender cs) {
		cs.sendMessage(prefix + "Arenas - Commands");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arena add <arenatype> <gamemode>- Adds a new arena");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arena list - Lists all arenas with their ID");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arena del <id> - Deletes an arenas information");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am arena select [id] - Selects arena by id, if no id is given it will find the one you are currently standing in.");
		cs.sendMessage(ChatColor.AQUA
				+ "    After selecting an arena to modify, use /am sel");
		cs.sendMessage(prefix);
	}

	private void listSelectionCommands(CommandSender cs) {
		cs.sendMessage(prefix + "Arena Selection - Commands");
		cs.sendMessage(ChatColor.DARK_BLUE
				+ "   NOTE: Spawns & lobbies use worldguard selection");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel info - Gives information on the currently selected arena");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel setlobby - Sets the pre-lobby area.");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel addspawn <teamid> - Sets the area for a team spawn.");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel listspawn [teamid] - Lists all team spawns as well as id's.");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel delspawn <teamid> <spawnid> - Deletes the spawn id");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel flag add - Adds a flag at your current location");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel flag list - Lists all flags for the arena ");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel flag remove <id>- Removes a flag with given ID");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel setproperty <property> <value> - Set arena specific properties - zones use WorldGuard");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel delproperty <property> - Deletes arena specific properties");
		if (arenaModification.containsKey(cs.getName())) {
			Arena arena = arenaModification.get(cs.getName());
			// Special commands here
			if (arena instanceof SurvivalGameArena) {
				cs.sendMessage(ChatColor.AQUA
						+ "    /am sel sg spawns - Lists all the locations to spawn");
				cs.sendMessage(ChatColor.AQUA
						+ "    /am sel sg addspawn - Adds a spawn to survival games");
				cs.sendMessage(ChatColor.AQUA
						+ "    /am sel sg delspawn <id> - Deletes a spawn location");
			}
		}
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel save [enabled] - Saves all alterations");
		cs.sendMessage(ChatColor.AQUA
				+ "    /am sel quit - Exits without save warning");
		cs.sendMessage(prefix);
	}

	public Arena getFreeArena(GameMode gameMode) {
		List<Arena> toShuffle = new ArrayList<Arena>();
		for (Arena arena : arenaMap)
			if (arena.isEnabled() && arena.getCurrentGame() == null
					&& arena.getGameMode() == gameMode)
				toShuffle.add(arena);
		Collections.shuffle(toShuffle);
		if (toShuffle.isEmpty())
			return null;
		return toShuffle.get(new Random().nextInt(toShuffle.size()));
	}

	public Arena[] getAllArenas() {
		return arenaMap.toArray(new Arena[arenaMap.size()]);
	}

	public Arena[] getAllArenas(GameMode gm) {
		List<Arena> tempList = new ArrayList<Arena>();
		for (Arena arena : arenaMap)
			if (gm == arena.getGameMode())
				tempList.add(arena);
		return tempList.toArray(new Arena[tempList.size()]);
	}

	public Arena[] getAllFreeArenas(GameMode gm) {
		List<Arena> tempList = new ArrayList<Arena>();
		for (Arena arena : arenaMap)
			if (gm == arena.getGameMode() && !arena.hasCurrentGame()
					&& arena.isEnabled())
				tempList.add(arena);
		return tempList.toArray(new Arena[tempList.size()]);
	}

	public ArenaGame getGame(MinigameUser mg) {
		return getGame(mg.getName());
	}

	public ArenaGame getGame(String mg) {
		for (Arena arena : arenaMap)
			if (arena.hasCurrentGame() && arena.getCurrentGame().hasPlayer(mg)) {
				return arena.getCurrentGame();
			}
		return null;
	}
}
