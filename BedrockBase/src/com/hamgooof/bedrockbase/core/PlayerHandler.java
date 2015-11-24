package com.hamgooof.bedrockbase.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.hamgooof.bedrockbase.BBHandler;

public class PlayerHandler {
	private final File playerFolder;
	private final BBHandler bbHandler;
	private final int posDistance = 50;

	public PlayerHandler(File dataFolder, BBHandler bbHandler) {
		playerFolder = new File(dataFolder, "PlayerData");
		this.bbHandler = bbHandler;
		if (!playerFolder.exists())
			playerFolder.mkdirs();
	}

	public boolean hasFile(UUID uuid) {
		return new File(playerFolder, uuid.toString() + ".sav").exists();
	}

	public void createFile(UUID uuid) {
		File playerFile = new File(playerFolder, uuid.toString() + ".sav");
		try {
			PrintWriter pw = new PrintWriter(playerFile);
			pw.println(bbHandler.getX() + ":" + bbHandler.getInitialheight()
					+ ":" + bbHandler.getZ());
			if (bbHandler.getX() < posDistance * 20) {
				bbHandler.setZ(bbHandler.getZ() + posDistance);
			} else {
				bbHandler.setZ(0);
				bbHandler.setX(bbHandler.getX() + posDistance);
			}
			bbHandler.updateConfig();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Location getPlayersInitialLocation(UUID uuid) {
		File playerFile = new File(playerFolder, uuid.toString() + ".sav");
		if (!playerFile.exists())
			return null;
		try {
			Scanner scan = new Scanner(playerFile);
			Location loc = loadLoc(scan.nextLine(), BBPlugin.world);
			scan.close();
			return loc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasUsed(String base, UUID uuid) {
		File playerFile = new File(playerFolder, uuid.toString() + ".sav");
		try {
			Scanner scan = new Scanner(playerFile);
			scan.nextLine();
			boolean used = false;
			while (scan.hasNextLine())
				if (scan.nextLine().equalsIgnoreCase(base)) {
					used = true;
					break;
				}
			scan.close();
			return used;
		} catch (Exception e) {
		}
		return false;
	}

	public void setUsed(String base, UUID uuid) {
		File playerFile = new File(playerFolder, uuid.toString() + ".sav");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(playerFile, true));
			pw.println(base);
			pw.close();
		} catch (Exception e) {
		}
	}

	public void setPasting(String base, UUID uuid) {
		File playerFile = new File(playerFolder, uuid.toString() + ".sav");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(playerFile, true));
			pw.println(base + " pasting");
			pw.close();
		} catch (Exception e) {
		}
	}

	private Location loadLoc(String string, String world) {
		String[] split = string.split("[:]");
		int x, y, z;
		x = Integer.parseInt(split[0]);
		y = Integer.parseInt(split[1]);
		z = Integer.parseInt(split[2]);
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public List<String> getUsedSchematics(UUID uniqueId) {
		List<String> stringList = new ArrayList<String>();
		File playerFile = new File(playerFolder, uniqueId.toString() + ".sav");
		try {
			Scanner scan = new Scanner(playerFile);
			scan.nextLine();
			while (scan.hasNextLine())
				stringList.add(scan.nextLine());
			scan.close();
			for (Iterator<String> iter = stringList.iterator(); iter.hasNext();)
				if (iter.next().endsWith(" pasting"))
					iter.remove();
			return stringList;
		} catch (Exception e) {
		}
		return new ArrayList<String>();
	}

	public void deleteFile(UUID uniqueId) {
		File playerFile = new File(playerFolder, uniqueId.toString() + ".sav");
		if (playerFile.exists())
			playerFile.delete();
	}
}
