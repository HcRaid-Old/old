package com.addongaming.prison.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.npc.bank.ItemBanker;
import com.addongaming.prison.npc.classes.MasterAssassin;
import com.addongaming.prison.npc.classes.MasterMurderer;
import com.addongaming.prison.npc.classes.MasterThief;
import com.addongaming.prison.npc.guard.Guard;
import com.addongaming.prison.npc.scenes.Judge;
import com.addongaming.prison.npc.scenes.Jury;
import com.addongaming.prison.npc.skills.AreaInstructor;
import com.addongaming.prison.npc.skills.BMCombatInstructor;
import com.addongaming.prison.npc.skills.BMSmitherInstructor;
import com.addongaming.prison.npc.skills.CommandInstructor;
import com.addongaming.prison.npc.skills.CraftingInstructor;
import com.addongaming.prison.npc.skills.FarmingInstructor;
import com.addongaming.prison.npc.skills.InventoryInstructor;
import com.addongaming.prison.npc.skills.MiningInstructor;
import com.addongaming.prison.npc.skills.WoodcuttingInstructor;

public class NPCManager {
	private JavaPlugin jp;
	List<InfNPC> npcs = new ArrayList<InfNPC>();

	public NPCManager(final JavaPlugin jp) {
		// Skill Teachers
		npcs.add(new MiningInstructor(jp));
		npcs.add(new WoodcuttingInstructor(jp));
		npcs.add(new CommandInstructor(jp));
		npcs.add(new CraftingInstructor(jp));
		npcs.add(new InventoryInstructor(jp));
		npcs.add(new AreaInstructor(jp));
		npcs.add(new FarmingInstructor(jp));
		npcs.add(new BMCombatInstructor(jp));
		npcs.add(new BMSmitherInstructor(jp));
		// Scenes
		npcs.add(new Judge(jp));
		npcs.add(new Jury(jp));
		// Class Teachers
		npcs.add(new MasterThief(jp));
		npcs.add(new MasterAssassin(jp));
		npcs.add(new MasterMurderer(jp));
		npcs.add(new Guard(jp));
		// Misc
		npcs.add(new ItemBanker(jp));
		for (final InfNPC npc : npcs) {
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							npc.load();
							jp.getServer().getPluginManager()
									.registerEvents(npc, jp);
						}
					}, 10L);

		}
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
