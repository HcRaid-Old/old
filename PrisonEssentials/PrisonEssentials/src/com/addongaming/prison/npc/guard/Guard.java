package com.addongaming.prison.npc.guard;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.npc.InfNPC;
import com.addongaming.prison.npc.NPCData;

public class Guard implements InfNPC {
	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public Guard(JavaPlugin jp) {
		this.jp = jp;
		CitizensAPI.getTraitFactory().registerTrait(
				TraitInfo.create(GuardTrait.class).withName("Guard"));
	}

	private void createNpc(String str) {
		List<Location> path = new ArrayList<Location>();
		for (String st : jp.getConfig()
				.getConfigurationSection(str + ".location").getKeys(false)) {
			path.add(Utils.loadLoc(jp.getConfig().getString(
					str + ".location." + st)));
		}
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,
				"Guard");
		npc.spawn(path.get(0));
		GuardTrait gt = new GuardTrait();
		gt.setPath(path.toArray(new Location[path.size()]));
		npc.addTrait(gt);
		npcList.add(new NPCData(npc.getId(), path.get(0).getWorld()));
	}

	@Override
	public void load() {
		FileConfiguration fc = jp.getConfig();
		Location temp = Bukkit.getWorld("world").getBlockAt(0, 0, 0)
				.getLocation();
		fc.addDefault("npcs.guards.guard1.location.loc1",
				Utils.locationToSaveString(temp));
		fc.addDefault("npcs.guards.guard1.location.loc2",
				Utils.locationToSaveString(temp));
		fc.options().copyDefaults(true);
		jp.saveConfig();
		for (String s : fc.getConfigurationSection("npcs.guards")
				.getKeys(false))
			createNpc("npcs.guards." + s);

	}

	@Override
	public void unload() {
		for (NPCData nd : npcList)
			CitizensAPI.getNPCRegistry().getById(nd.getId()).destroy();
	}

}
