package com.addongaming.hcessentials.worldman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class WorldManagement implements CommandExecutor, SubPlugin {
	JavaPlugin jp;
	private ArrayList<String> onLoadup;

	File storageFile;

	private final HashMap<String, NewWorldStore> tempWorldStore = new HashMap<String, NewWorldStore>();

	ArrayList<String> players = new ArrayList<String>();

	private final String title = ChatColor.GREEN + "[" + ChatColor.AQUA
			+ "HcWorldMan" + ChatColor.GREEN + "] " + ChatColor.GRAY;

	public WorldManagement(JavaPlugin jp) {
		this.jp = jp;
	}

	private String getAllWorlds() {
		String mainFolder = jp.getServer().getWorldContainer().getAbsolutePath();
		StringBuilder sb = new StringBuilder();
		System.out.println("Mainfolder: " + mainFolder);
		for (File folders : new File(mainFolder).listFiles()) {
			if (!folders.getName().contains("."))
				for (File inside : folders.listFiles()) {
					if (inside.getName().equalsIgnoreCase("level.dat"))
						sb.append(folders.getName() + " ");
				}
		}
		return sb.toString();
	}

	private void listCommands(Player p) {
		msg(p, "");
		ms(p, "     Create - Creates a new world");
		ms(p, "     Types - Lists information for the params create can take.");
		ms(p, "     Load - Loads a world");
		ms(p, "     List - Lists all loaded/unloaded worlds");
		ms(p, "     Addonstart - Adds a loaded world to be loaded on startup.");
		ms(p, "     Delonstart - Removes a loaded world from startup.");
		ms(p, "     Unload - Unloads a world");
		msg(p, "");
	}

	private Object loadObj(final String path) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	private void loadWorld(Player p, String string) {
		for (World w : jp.getServer().getWorlds()) {
			if (w.getName().equalsIgnoreCase(string)) {
				if (p != null)
					msg(p, "World " + w.getName() + " is already loaded.");
				return;
			}
		}
		for (String world : getAllWorlds().split(" "))
			if (world.equalsIgnoreCase(string)) {
				if (p != null)
					msg(p, "Loading " + world);
				jp.getServer().createWorld(
						WorldCreator.name(world).seed(new Random().nextLong()));
				if (p != null)
					msg(p, "Loaded world " + world);
				return;
			}
		msg(p,
				"Error loading world. Please check the name is correct /worldman list");
	}

	private void ms(Player p, String msg) {
		p.sendMessage(ChatColor.GRAY + msg);
	}

	private void msg(Player p, String msg) {
		p.sendMessage(title + msg);
	}

	ArrayList<String> temp2 = new ArrayList<String>();

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to access this command.");
			return true;
		}
		// Commands Create, Load, Unload, List
		if (args.length == 0) {
			listCommands((Player) sender);
			return true;
		}
		Player p = (Player) sender;
		switch (args[0].toLowerCase()) {
		case "spawn":
			p.getWorld().setSpawnLocation(p.getLocation().getBlockX(),
					p.getLocation().getBlockY(), p.getLocation().getBlockZ());
			return true;
		case "accept":
			if (!tempWorldStore.containsKey(p.getName())) {
				msg(p, "Please use /worldman create first");
				return true;
			}
			NewWorldStore nws = tempWorldStore.get(p.getName());
			msg(p,
					"Creating " + nws.getWorldName() + " with type "
							+ nws.getWorldTypeStr());
			World worl = jp.getServer().createWorld(
					new WorldCreator(nws.getWorldName()).type(
							nws.getWorldType()).environment(
							Environment.getEnvironment(nws.getType())));
			worl.setAutoSave(true);
			msg(p, "Created " + nws.getWorldName());
			tempWorldStore.remove(p.getName());

			return true;
		case "types":
			msg(p,
					"Types: Normal - regular world, LargeBiomes - large biomes, Flat - flat world.");
			msg(p,
					"Environments: Normal - regular world, Nether - Nether world, End - End(Dragon) World.");
			return true;
		case "create":
			// /worldman create <name> <type>
			if (args.length < 2) {
				msg(p, "Please use /worldman create <name> <type> <environ>");
				msg(p,
						"Type & environ is optional, use /worldman types to see all parameters.");
				return true;
			}
			String worldType = "Normal (default)";
			int type = 0;
			WorldType wt = WorldType.NORMAL;
			if (args.length > 2) {
				String temp = args[2].toLowerCase();
				if (temp.contains("large")) {
					worldType = "Large Biomes";
					wt = WorldType.LARGE_BIOMES;
				} else if (temp.contains("flat")) {
					worldType = "Flat";
					wt = WorldType.FLAT;
				} else if (temp.contains("normal")) {
					worldType = "Normal";
					wt = WorldType.NORMAL;
				}
			}
			if (args.length > 3) {
				String temp = args[3].toLowerCase();
				if (temp.equalsIgnoreCase("normal"))
					type = 0;
				else if (temp.equalsIgnoreCase("nether"))
					type = -1;
				else if (temp.equalsIgnoreCase("end"))
					type = 1;
			}
			msg(p, "World name: " + args[1] + " type: " + worldType
					+ " environment "
					+ Environment.getEnvironment(type).name().toLowerCase());
			msg(p,
					"Please use /worldman accept  if you with to create this world.");
			tempWorldStore.put(p.getName(), new NewWorldStore(args[1],
					worldType, wt, type));
			return true;
		case "load":
			if (args.length < 2) {
				msg(p, "Please use /worldman load <name>  to load the world.");
				msg(p, "Use /worldman list to see all available worlds.");
				return true;
			}
			loadWorld(p, args[1]);
			return true;
		case "list":
			StringBuilder sb = new StringBuilder();
			sb.append("Worlds currently loaded: ");
			for (World w : jp.getServer().getWorlds()) {
				sb.append(w.getName() + ", ");
			}
			msg(p, sb.toString());
			msg(p, "All worlds in folder: " + getAllWorlds());
		case "addonstart":
			if (args.length != 2) {
				msg(p, "Please use /worldman addonstart <worldname>");
				return true;
			}
			String world = args[1];
			for (World w : jp.getServer().getWorlds()) {
				if (w.getName().equalsIgnoreCase(world)) {
					onLoadup.add(w.getName());
					saveLoadup();
					msg(p, "Added " + w.getName() + " to startup list.");
					return true;
				}
			}
			msg(p,
					"Please make sure the world is loaded before adding to startup list.");
		case "unload":
			if (args.length != 2) {
				msg(p, "Please use /worldman unload <worldname>");
			}
			String world2 = args[1];
			for (World w : jp.getServer().getWorlds()) {
				if (w.getName().equalsIgnoreCase(world2)) {
					try {
						for (Player play : w.getPlayers())
							play.teleport(HcEssentials.essentials.getWarps()
									.getWarp("spawn"));
					} catch (WarpNotFoundException e) {
						e.printStackTrace();
					} catch (net.ess3.api.InvalidWorldException e) {
						e.printStackTrace();
					}
					if (Bukkit.unloadWorld(world2, true))
						msg(p, "Unloaded World: " + world2);
					else
						msg(p, "Error unloading world: " + world2);
				}
			}
			return true;
			/*
			 * case "delete": if (args.length != 2) { msg(p,
			 * "Please use /worldman delete <worldname>"); } String world3 =
			 * args[1]; for (World w : jp.getServer().getWorlds()) { if
			 * (w.getName().equalsIgnoreCase(world3) &&
			 * p.hasPermission("worldman.delete")) { World delete =
			 * Bukkit.getServer().getWorld(args[1]); File deleteFolder =
			 * delete.getWorldFolder(); deleteWorld(deleteFolder); return true;
			 * } } return true;
			 */
		case "delonstart":
			if (args.length != 2) {
				msg(p, "Please use /worldman addonstart <worldname>");
				return true;
			}
			String world1 = args[1];
			for (World w : jp.getServer().getWorlds()) {
				if (w.getName().equalsIgnoreCase(world1)) {
					onLoadup.remove(w.getName());
					saveLoadup();
					msg(p, "Removed " + w.getName() + " from startup list.");
					return true;
				}
			}
			msg(p,
					"Please make sure the world is loaded before removing from startup list.");
			return true;
		}
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("worldmanagement.enabled") == false)
			return false;
		if (!new File(jp.getDataFolder() + File.separator + "WorldManagement")
				.exists())
			new File(jp.getDataFolder() + File.separator + "WorldManagement")
					.mkdirs();
		storageFile = new File(jp.getDataFolder() + File.separator
				+ "WorldManagement" + File.separator + "loadup.sav");
		if (!storageFile.exists()) {
			try {
				storageFile.createNewFile();
				onLoadup = new ArrayList<String>();
				saveLoadup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				onLoadup = (ArrayList<String>) loadObj(storageFile
						.getAbsolutePath());
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@Override
							public void run() {
								for (String str : onLoadup) {
									try {
										loadWorld(null, str);
									} catch (NullPointerException npe) {
										npe.printStackTrace();
									}
								}
							}
						}, Config.Ticks.WORLDLOAD);
			} catch (Exception e) {
				onLoadup = new ArrayList<String>();
				e.printStackTrace();
			}
		}
		jp.getCommand("worldman").setExecutor(this);
		return true;
	}

	private void saveLoadup() {
		try {
			saveObj(onLoadup, storageFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveObj(final Object obj, final String path) throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	/*
	 * public boolean deleteWorld(File path) { if (path.exists()) { File files[]
	 * = path.listFiles(); for (int i = 0; i < files.length; i++) { if
	 * (files[i].isDirectory()) { deleteWorld(files[i]); } else {
	 * files[i].delete(); } } } return (path.delete());
	 */
}
