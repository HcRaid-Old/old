package com.addongaming.hcessentials.world.gen;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class WorldRegen implements Listener, SubPlugin {
	public static List<Block> toChange = new CopyOnWriteArrayList<Block>();
	List<Point> li = new ArrayList<Point>();
	public static boolean altering = false;
	public static boolean checking = false;
	public static List<Chunk> al = new CopyOnWriteArrayList<Chunk>();

	public static void save(final Object obj, final String path)
			throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public static Object load(final String path) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	File saveFile;
	int blockChange;
	int chunkCheck;
	private JavaPlugin jp;

	@SuppressWarnings({ "serial" })
	public WorldRegen(JavaPlugin jp, File saveFile) {
		this.jp = jp;
		this.saveFile = saveFile;
		if (!saveFile.getParentFile().exists())
			saveFile.getParentFile().mkdirs();
		jp.getConfig().addDefault("worldgen.enabled", false);
		jp.getConfig().addDefault("worldgen.chunkscan", 20);
		jp.getConfig().addDefault("worldgen.blockchange", 600);
		jp.getConfig().addDefault("worldgen.blocklist",
				new ArrayList<String>() {
					{
						add("bookshelf");
					}
				});
	}

	@EventHandler
	public void chunkLoad(ChunkLoadEvent event) {
		Point p = new Point(event.getChunk().getX(), event.getChunk().getZ());
		if (li.contains(p)
				|| !event.getChunk().getWorld().getName()
						.equalsIgnoreCase("world")) {
			return;
		} else {
			al.add(event.getChunk());
			li.add(p);
		}
	}

	@EventHandler
	public void chunkLoad(org.bukkit.event.world.ChunkPopulateEvent event) {
		Point p = new Point(event.getChunk().getX(), event.getChunk().getZ());
		if (li.contains(p)
				|| !event.getChunk().getWorld().getName()
						.equalsIgnoreCase("world"))
			return;
		else {
			al.add(event.getChunk());
			li.add(p);
		}
	}

	@SuppressWarnings("serial")
	public static final List<Material> disallowed = new ArrayList<Material>();

	@Override
	public void onDisable() {
		try {
			save(li, saveFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		jp.getServer().getScheduler().cancelTasks(jp);
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("worldgen.enabled"))
			return false;

		try {
			if (!saveFile.exists()) {
				li.add(new Point(-50000, -50000));
				save(li, saveFile.getAbsolutePath());
			} else {
				li = (List<Point>) load(saveFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String str : (List<String>) jp.getConfig().getList(
				"worldgen.blocklist"))
			disallowed.add(Material.getMaterial(str.toUpperCase()));
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer()
				.getScheduler()
				.runTaskTimerAsynchronously(
						jp,
						new ChunkChecker(jp.getConfig().getInt(
								"worldgen.chunkscan")), 200, 20);
		jp.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(
						jp,
						new BlockChanger(jp.getConfig().getInt(
								"worldgen.blockchange")), 200, 15);
		return true;
	}
}
