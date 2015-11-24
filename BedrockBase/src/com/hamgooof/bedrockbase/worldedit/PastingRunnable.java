package com.hamgooof.bedrockbase.worldedit;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.primesoft.asyncworldedit.PluginMain;
import org.primesoft.asyncworldedit.worldedit.AsyncCuboidClipboard;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

import com.hamgooof.bedrockbase.core.BBPlugin;
import com.hamgooof.bedrockbase.objects.BedrockSchematic;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

public class PastingRunnable implements Runnable {
	private final BedrockSchematic schematic;
	private final int[] coords;
	private String name;

	public PastingRunnable(String name, BedrockSchematic schematic, int[] coords) {
		this.name = name;
		this.schematic = schematic;
		this.coords = coords;
	}

	@Override
	public void run() {
		try {
			loadArea(Bukkit.getWorld(BBPlugin.world), schematic.getSchematic(),
					new Vector(coords[0], coords[1], coords[2]));
		} catch (MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void loadArea(World world, File file, Vector origin)
			throws DataException, IOException, MaxChangedBlocksException {
		AsyncEditSession es = new AsyncEditSession(new AsyncEditSessionFactory(
				PluginMain.getInstance()), PluginMain.getInstance(), name + "|"
				+ schematic.getName(), new BukkitWorld(world), 999999999);
		AsyncCuboidClipboard cc = new AsyncCuboidClipboard(name + "|"
				+ schematic.getName(), AsyncCuboidClipboard.loadSchematic(file));
		cc.paste(es, origin, false);
	}
}
