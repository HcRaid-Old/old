package com.addongaming.prison.npc.bank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.redeem.SyncInventory;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.npc.InfNPC;
import com.addongaming.prison.npc.NPCData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class ItemBanker implements InfNPC {

	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public ItemBanker(JavaPlugin jp) {
		this.jp = jp;
		new File(jp.getDataFolder() + File.separator + "Banks").mkdirs();
		setupConfig(jp.getConfig());
	}

	private void createNpc(String str) {
		Location loc = Utils.loadLoc(jp.getConfig()
				.getString(str + ".location"));
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
				"Item Banker");
		npc.setProtected(true);
		npc.spawn(loc);
		Entity entity = npc.getEntity();
		npcList.add(new NPCData(npc.getId(), loc.getWorld()));
		Villager vill = (Villager) entity;
		vill.setCustomName("Item Banker");
		vill.setCustomNameVisible(true);
		vill.setAdult();
		vill.setProfession(Profession.PRIEST);
		vill.setMaxHealth(Double.MAX_VALUE);
	}

	@Override
	public void load() {
		FileConfiguration fc = jp.getConfig();
		for (String s : fc.getConfigurationSection("npcs.instructors.itembank")
				.getKeys(false))
			createNpc("npcs.instructors.itembank." + s);

	}

	private void messagePlayer(Player p, String message) {
		p.sendMessage(ChatColor.AQUA + "Item Banker" + ChatColor.RESET + "> "
				+ message);
	}

	@EventHandler
	public void npcClick(NPCRightClickEvent event) {
		for (NPCData nd : npcList)
			if (nd.getId() == event.getNPC().getId())
				openInventory(nd, event.getClicker());
	}

	@EventHandler
	public void npcDespawn(NPCDespawnEvent event) {
		for (NPCData nd : this.npcList)
			if (event.getNPC().getId() == nd.getId()) {
				event.setCancelled(true);
				return;
			}
	}

	@EventHandler
	public void inventoryClose(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().getName()
				.equalsIgnoreCase(ChatColor.GOLD + "Item Banker")) {
			saveInventory(event.getPlayer().getUniqueId().toString(), event
					.getView().getTopInventory());
		}
	}

	private void openInventory(NPCData nd, Player play) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				play.getName());
		Inventory i = Bukkit.createInventory(null, 9 * 6, ChatColor.GOLD
				+ "Item Banker");
		SyncInventory si = loadInventory(play.getUniqueId().toString());
		i.setContents(si.getContents());
		play.openInventory(i);
	}

	private SyncInventory loadInventory(String string) {
		File file = new File(jp.getDataFolder() + File.separator + "Banks"
				+ File.separator + string + ".sav");
		if (!file.exists())
			return new SyncInventory(new ItemStack[9 * 6]);
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			SyncInventory toReturn = (SyncInventory) ois.readObject();
			ois.close();
			return toReturn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveInventory(String string, Inventory inv) {
		File file = new File(jp.getDataFolder() + File.separator + "Banks"
				+ File.separator + string + ".sav");
		SyncInventory si = new SyncInventory(inv.getContents());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(si);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupConfig(final FileConfiguration fc) {
		Location temp = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("npcs.instructors.itembank.npc1.location",
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
