package com.addongaming.minigames.hub.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.config.Config;
import com.addongaming.minigames.hub.pets.PetCache;

public class NPCManager {
	private JavaPlugin jp;
	final List<InfNPC> npcs = new ArrayList<InfNPC>();

	public NPCManager(final JavaPlugin jp) {
		// Skill Teachers
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						npcs.add(new PetSeller(jp));
						npcs.add(new PetCache(jp));
						npcs.add(new Banker(jp));
						for (final InfNPC npc : npcs) {
							jp.getServer()
									.getScheduler()
									.scheduleSyncDelayedTask(jp,
											new Runnable() {

												@Override
												public void run() {
													npc.load();
													jp.getServer()
															.getPluginManager()
															.registerEvents(
																	npc, jp);
												}
											}, 10L);
						}

					}
				}, Config.Ticks.POSTWORLD);
	}

	public void reload() {
		for (InfNPC in : npcs) {
			in.unload();
			in.load();
		}
	}

	public void stop() {
		for (Iterator<InfNPC> iter = npcs.iterator(); iter.hasNext();) {
			iter.next().unload();
			iter.remove();
		}
	}
}
