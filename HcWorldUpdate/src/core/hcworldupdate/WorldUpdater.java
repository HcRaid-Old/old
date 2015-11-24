package core.hcworldupdate;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldUpdater implements Runnable {
	private JavaPlugin jp;
	private final int maxx, maxz, minx, minz;
	private int currx;
	private int currz;
	private int atTime = 10;
	private List<Point> toSave = new ArrayList<Point>();
	private Location start;
	private String play;

	public WorldUpdater(JavaPlugin jp, Location start, int xrad, int zrad,
			Player play) {
		this.play = play.getName();
		this.start = start;
		this.jp = jp;
		int startx = start.getChunk().getX();
		int startz = start.getChunk().getZ();
		maxx = startx + (xrad / 16);
		minx = startx - (xrad / 16);
		minz = startz - (zrad / 16);
		maxz = startz + (zrad / 16);
		System.out.println(maxx + " | " + maxz + " | " + minx + " | " + minz
				+ " | ");
		currx = minx;
		currz = minz;
	}

	int count = 0;
	int total = 0;

	@Override
	public void run() {
		count++;

		if (count == 10) {
			if (Bukkit.getPlayerExact(play) != null
					&& Bukkit.getPlayerExact(play).isOnline()) {
				Bukkit.getPlayerExact(play).sendMessage(
						"[HcWorldUpdate] Saving and unloading " + toSave.size()
								+ " chunks.");
				Bukkit.getPlayerExact(play).sendMessage(
						"[HcWorldUpdate] Completed " + getPercentageComplete());
			}
			count = 0;
			for (Iterator<Point> iter = toSave.iterator(); iter.hasNext();) {
				Point p = iter.next();
				start.getWorld().getChunkAt(p.x, p.y).unload(true, true);
				iter.remove();
			}
		}

		for (int i = 0; i < atTime; i++) {
			if (currx >= maxx) {
				currx = minx;
				currz++;
			}
			total++;
			start.getWorld().loadChunk(currx++, currz++);
			toSave.add(new Point(currx, currz));
		}
	}

	public String getPercentageComplete() {
		double area = (maxx - minx) * (maxz - minz);
		System.out.println("Area: " + area);

		return new DecimalFormat("##.#").format((total / area) * 100);
	}
}
