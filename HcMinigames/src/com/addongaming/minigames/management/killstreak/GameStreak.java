package com.addongaming.minigames.management.killstreak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.addongaming.minigames.management.arena.GameMode;

public class GameStreak {
	private List<KillStreak> killStreak = new ArrayList<KillStreak>();
	private GameMode gameMode;
	private File directory;

	public GameStreak(GameMode gameMode, File directory) {
		this.gameMode = gameMode;
		this.directory = directory;
		loadKillStreaks();
	}

	public void loadKillStreaks() {
		killStreak.clear();
		for (File file : directory.listFiles())
			addKillStreak((KillStreak) loadObj(file));
		organiseStreaks();

	}

	private void organiseStreaks() {
		Collections.sort(killStreak, new Comparator<KillStreak>() {
			public int compare(KillStreak o1, KillStreak o2) {
				return o1.getNeededKills() - o2.getNeededKills();
			};
		});
	}

	private void addKillStreak(KillStreak ks) {
		if (ks instanceof ItemKillStreak)
			killStreak.add((ItemKillStreak) (ks));
	}

	public boolean addItem(ItemStack is, int id) {
		for (KillStreak ks : killStreak)
			if (ks instanceof ItemKillStreak
					&& ((ItemKillStreak) ks).getNeededKills() == id)
				return false;
		killStreak.add(new ItemKillStreak(is, id));
		return true;
	}

	public void save() {
		for (File file : directory.listFiles())
			file.delete();
		int counter = 0;
		for (KillStreak ks : killStreak) {
			saveObj(ks,
					new File(directory, (counter++) + ".sav").getAbsolutePath());
		}
	}

	private boolean saveObj(final Object obj, final String path) {
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

	private Object loadObj(File file) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(file.getAbsolutePath()));
			final Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public List<KillStreak> getKillStreaks() {
		return killStreak;
	}

	public KillStreak getKillStreak(int kills) {
		for (KillStreak ks : killStreak)
			if (ks.getNeededKills() == kills)
				return ks;
		return null;
	}

	public boolean deleteKillStreak(int kills) {
		for (Iterator<KillStreak> iter = killStreak.iterator(); iter.hasNext();)
			if (iter.next().getNeededKills() == kills) {
				iter.remove();
				return true;
			}
		return false;
	}
}
