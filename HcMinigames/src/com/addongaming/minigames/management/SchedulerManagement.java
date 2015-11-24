package com.addongaming.minigames.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.scheduling.HcRepeat;
import com.addongaming.minigames.management.scheduling.SchedulerCleanser;
import com.addongaming.minigames.minigames.ArenaGame;

public class SchedulerManagement {
	private HcMinigames minigames;
	private final static HashMap<ArenaGame, ArrayList<HcRepeat>> schedulerMap = new HashMap<ArenaGame, ArrayList<HcRepeat>>();

	public SchedulerManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(minigames,
						new SchedulerCleanser(minigames), 10l, 10l);
	}

	public void runScheduler(ArenaGame ag, final HcRepeat hcr, long delay,
			long repeatTicks) {
		int id = minigames.getServer().getScheduler()
				.scheduleSyncRepeatingTask(minigames, hcr, delay, repeatTicks);
		hcr.setId(id);
		ArrayList<HcRepeat> repeatMap = schedulerMap.containsKey(ag) ? schedulerMap
				.get(ag) : new ArrayList<HcRepeat>();
		repeatMap.add(hcr);
		schedulerMap.put(ag, repeatMap);
	}

	public void cleanse() {
		for (ArenaGame ag : schedulerMap.keySet())
			for (Iterator<HcRepeat> iter = schedulerMap.get(ag).iterator(); iter
					.hasNext();) {
				HcRepeat hr = (HcRepeat) iter.next();
				if (hr.isFinished()) {
					minigames.getServer().getScheduler().cancelTask(hr.getId());
					iter.remove();
				}
			}
	}
}
