package core.essentials.gen;

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
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.Disableable;

public class NetherGen implements Listener, Disableable {
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

	@SuppressWarnings("unchecked")
	public NetherGen(File saveFile, JavaPlugin jp) {
		this.jp = jp;
		this.saveFile = saveFile;
		try {
			if (!saveFile.exists()) {
				System.out.println(saveFile.getAbsolutePath());
				li.add(new Point(-50000, -50000));
				save(li, saveFile.getAbsolutePath());
			} else {
				li = (List<Point>) load(saveFile.getAbsolutePath());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jp.getServer().getScheduler()
				.runTaskTimerAsynchronously(jp, new ChunkChecker(), 200, 20);
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new BlockChanger(), 200, 15);
	}

	@EventHandler
	public void chunkLoad(ChunkLoadEvent event) {
		Point p = new Point(event.getChunk().getX(), event.getChunk().getZ());
		if (li.contains(p)
				|| !event.getChunk().getWorld().getName().contains("nether"))
			return;
		else {
			al.add(event.getChunk());
			li.add(p);
		}
	}

	@EventHandler
	public void chunkLoad(org.bukkit.event.world.ChunkPopulateEvent event) {
		Point p = new Point(event.getChunk().getX(), event.getChunk().getZ());
		if (li.contains(p)
				|| !event.getChunk().getWorld().getName().contains("nether"))
			return;
		else {
			al.add(event.getChunk());
			li.add(p);
		}
	}

	@SuppressWarnings("serial")
	public static final List<Material> disallowed = new ArrayList<Material>() {
		{
			this.add(Material.NETHER_BRICK);
			this.add(Material.QUARTZ_ORE);
			this.add(Material.NETHER_FENCE);
			this.add(Material.NETHER_STALK);
			this.add(Material.NETHER_BRICK_STAIRS);
		}
	};

	@Override
	public void onDisable() {
		try {
			save(li, saveFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		jp.getServer().getScheduler().cancelTasks(jp);
	}

	@EventHandler
	public void blockDamage(BlockDamageEvent bde) {
		for (Material m : disallowed)
			if (bde.getBlock().getType() == m) {
				bde.getBlock().setType(Material.NETHERRACK);
				return;
			}
	}
}
