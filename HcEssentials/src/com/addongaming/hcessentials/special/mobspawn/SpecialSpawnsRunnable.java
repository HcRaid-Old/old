package com.addongaming.hcessentials.special.mobspawn;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;

import com.addongaming.hcessentials.data.SpecialItems.SpecialTypes;

public class SpecialSpawnsRunnable implements Runnable {
	private int counter = 0;
	private List<Location> locs;
	private SpecialSpawns sp;

	public SpecialSpawnsRunnable(List<Location> locs, SpecialSpawns sp) {
		this.locs = locs;
		this.sp = sp;
		shuffleLocations();
	}

	private void shuffleLocations() {
		Collections.shuffle(locs);
	}

	@Override
	public void run() {
		Location loc = locs.get(counter);
		SpecialTypes st = SpecialTypes.SPECIAL;
		int ran = new Random().nextInt(100) + 1;
		if (ran > 95)
			st = SpecialTypes.UBER;
		else if (ran > 80)
			st = SpecialTypes.LEGENDARY;
		sp.spawnSpecial(st, loc);
		counter++;
		if (counter + 1 == locs.size()) {
			shuffleLocations();
			counter = 0;
		}
	}

}
