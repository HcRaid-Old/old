package com.hamgooof.bedrockbase.worldedit;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;

public class WorldGuardUtils {
	private final File schematicsFolder;
	private final JavaPlugin jp;

	public WorldGuardUtils(JavaPlugin jp) {
		this.jp = jp;
		schematicsFolder = new File(JavaPlugin.getPlugin(WorldEditPlugin.class)
				.getDataFolder() + File.separator + "schematics");
	}

	public void pasteSchematic(World world, int[] coords, String schematic) {
		try {
			loadArea(world,
					new File(schematicsFolder, schematic + ".schematic"),
					new Vector(coords[0], coords[1], coords[2]));
		} catch (MaxChangedBlocksException | DataException | IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void loadArea(World world, File file, Vector origin)
			throws DataException, IOException, MaxChangedBlocksException {
		EditSession es = new EditSession(new BukkitWorld(world), 999999999);
		CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
		cc.paste(es, origin, false);
	}
}
