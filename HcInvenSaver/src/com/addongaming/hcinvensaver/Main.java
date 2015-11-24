package com.addongaming.hcinvensaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	File folder;
	SyncInventory armour;
	SyncInventory inventory;

	@Override
	public void onEnable() {
		folder = new File(getDataFolder() + File.separator + "Saves");
		if (!folder.exists())
			folder.mkdirs();
		loadInventories();
	}

	private void loadInventories() {
		armour = (SyncInventory) load(folder.getAbsolutePath() + File.separator
				+ "armour.sav");
		inventory = (SyncInventory) load(folder.getAbsolutePath()
				+ File.separator + "inven.sav");
	}

	private void saveInventories() {
		save(armour, folder.getAbsolutePath() + File.separator + "armour.sav");
		save(inventory, folder.getAbsolutePath() + File.separator + "inven.sav");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("Please use /crapsorter <load|save>");
			return true;
		}
		Player player = (Player) sender;
		switch (args[0].toLowerCase()) {
		case "load":
			player.getInventory().setArmorContents(armour.getContents());
			player.getInventory().setContents(inventory.getContents());
			break;
		case "save":
			armour = new SyncInventory(player.getInventory().getArmorContents());
			inventory = new SyncInventory(player.getInventory().getContents());
			saveInventories();
			break;
		}
		return true;
	}

	private boolean save(final Object obj, final String path) {
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Object load(final String path) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(path));
			final Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
