package core.essentials.perks.farm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.Disableable;

public class FarmMonitor implements Listener, Disableable {
	HashMap<String, FarmInstance> hm;
	private JavaPlugin jp;

	@SuppressWarnings("unchecked")
	public FarmMonitor(JavaPlugin jp) {
		this.jp = jp;
		File farmDir = new File(jp.getDataFolder() + "\\Farming");
		if (!farmDir.exists())
			farmDir.mkdirs();
		File farmFile = new File(jp.getDataFolder().getAbsolutePath()
				+ "\\Farming\\farmsaves.farmsav");
		if (farmFile.exists()) {
			try {
				hm = (HashMap<String, FarmInstance>) load(jp.getDataFolder()
						.getAbsolutePath() + "\\Farming\\farmsaves.farmsav");
			} catch (Exception e) {
				hm = new HashMap<String, FarmInstance>();
				e.printStackTrace();
			}
		} else {
			hm = new HashMap<String, FarmInstance>();
		}
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						for (Iterator<FarmInstance> it = hm.values().iterator(); it
								.hasNext();) {
							FarmInstance fi = it.next();
							if (fi.isEmpty())
								it.remove();
							fi.checkCrops();
						}

					}
				}, 50, 500);
	}

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.CROPS) {
			if (hm.containsKey(event.getPlayer().getName())) {
				hm.get(event.getPlayer().getName()).addCrop(
						event.getBlockPlaced(), event.getPlayer());
			} else {
				hm.put(event.getPlayer().getName(), new FarmInstance(event
						.getPlayer().getName()));
				hm.get(event.getPlayer().getName()).addCrop(
						event.getBlockPlaced(), event.getPlayer());
			}
		}
	}

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

	@Override
	public void onDisable() {
		try {
			save(hm, jp.getDataFolder().getAbsolutePath()
					+ "\\Farming\\farmsaves.farmsav");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
