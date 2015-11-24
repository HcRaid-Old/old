package com.addongaming.prison.npc.scenes;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.npc.InfNPC;
import com.addongaming.prison.npc.NPCData;

public class Judge implements InfNPC {
	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public Judge(JavaPlugin jp) {
		this.jp = jp;
		setupConfig(jp.getConfig());
	}

	private void createNpc(String string) {
		Location loc = Utils.loadLoc(jp.getConfig().getString(
				string + ".location"));
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
				"Judge");
		npc.setProtected(true);
		npc.spawn(loc);
		Entity entity = npc.getEntity();
		npcList.add(new NPCData(npc.getId(), loc.getWorld()));
		Villager vill = (Villager) entity;
		vill.setCustomName("Judge");
		vill.setCustomNameVisible(true);
		vill.setAdult();
		vill.setProfession(Profession.PRIEST);
		vill.setMaxHealth(Double.MAX_VALUE);
	}

	@Override
	public void load() {
		FileConfiguration fc = jp.getConfig();
		for (String s : fc.getConfigurationSection("npcs.scene.start.judge")
				.getKeys(false))
			createNpc("npcs.scene.start.judge." + s);

	}

	@EventHandler
	public void npcDespawn(NPCDespawnEvent event) {
		for (NPCData nd : this.npcList)
			if (event.getNPC().getId() == nd.getId()) {
				event.setCancelled(true);
				return;
			}
	}

	private void setupConfig(final FileConfiguration fc) {
		Location temp = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("npcs.scene.start.judge.judge1.location",
				Utils.locationToSaveString(temp));
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void unload() {
		for (NPCData nd : npcList)
			CitizensAPI.getNPCRegistry().getById(nd.getId()).destroy();
	}

}
