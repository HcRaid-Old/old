package com.addongaming.minigames.hub.npc;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;

public class Banker implements InfNPC {
	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public Banker(JavaPlugin jp) {
		this.jp = jp;
		setupConfig(jp.getConfig());
	}

	private void createNpc(String str) {
		Location loc = Utils.loadLoc(jp.getConfig()
				.getString(str + ".location"));
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
				"Banker");
		npc.setProtected(true);
		npc.spawn(loc);
		Entity entity = npc.getEntity();
		npcList.add(new NPCData(npc.getId(), loc.getWorld()));
		Villager vill = (Villager) entity;
		vill.setCustomName("Banker");
		vill.setCustomNameVisible(true);
		vill.setAdult();
		vill.setProfession(Profession.LIBRARIAN);
		vill.setMaxHealth(Double.MAX_VALUE);
	}

	@EventHandler
	public void itemClick(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().getTitle()
				.startsWith(ChatColor.GREEN + "Banker")) {
			MinigameUser user = HcMinigames.getInstance().getHub()
					.getMinigameUser(event.getPlayer().getName());
			if (user == null) {
				return;
			}
		}
	}

	@Override
	public void load() {
		createNpc("npcs.bank");

	}

	private void messagePlayer(Player p, String message) {
		p.sendMessage(ChatColor.GOLD + "Banker" + ChatColor.RESET + "> "
				+ message);
	}

	@EventHandler
	public void npcClick(NPCRightClickEvent event) {
		for (NPCData nd : npcList)
			if (nd.getId() == event.getNPC().getId()) {
				openInventory(nd, event.getClicker());
			}
	}

	@EventHandler
	public void npcDespawn(NPCDespawnEvent event) {
		for (NPCData nd : this.npcList)
			if (event.getNPC().getId() == nd.getId()) {
				event.setCancelled(true);
				return;
			}
	}

	private void openInventory(NPCData nd, Player play) {
		MinigameUser user = HcMinigames.getInstance().getHub()
				.getMinigameUser(play.getName());
		if (user == null) {
			return;
		}

		int bal = user.getBankPoints();
		bal = bal - Utils.count(play, Material.GOLD_INGOT);
		int rows = 1;
		while (rows * 9 <= (bal / 64) + 1) {
			rows++;
		}
		Inventory i = Bukkit.createInventory(null, rows * 9, ChatColor.GREEN
				+ "Banker");
		for (int counter = bal; counter > 0;) {
			if (counter >= 64) {
				ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 64);
				ItemMeta goldMeta = goldIngot.getItemMeta();
				goldMeta.setDisplayName(ChatColor.GOLD + "Gold Ingot");
				goldMeta.setLore(new ArrayList<String>() {
					{
						add(ChatColor.AQUA + "This is the main currency");
						add(ChatColor.AQUA + "    on HcMinigames");
						add("");
						add(ChatColor.AQUA + "   Right click to bank");
						add(ChatColor.AQUA + "Or spend it straight away");
					}
				});
				goldIngot.setItemMeta(goldMeta);
				i.addItem(goldIngot);
				counter = counter - 64;
			} else {
				ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT,
						counter);
				ItemMeta goldMeta = goldIngot.getItemMeta();
				goldMeta.setDisplayName(ChatColor.GOLD + "Gold Ingot");
				goldMeta.setLore(new ArrayList<String>() {
					{
						add(ChatColor.AQUA + "This is the main currency");
						add(ChatColor.AQUA + "    on HcMinigames");
						add("");
						add(ChatColor.AQUA + "   Right click to bank");
						add(ChatColor.AQUA + "Or spend it straight away");
					}
				});
				goldIngot.setItemMeta(goldMeta);
				i.addItem(goldIngot);
				i.addItem(goldIngot);
				counter = 0;
			}
		}
		play.openInventory(i);
	}

	private void setupConfig(final FileConfiguration fc) {
		Location temp = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("npcs.bank.location", Utils.locationToSaveString(temp));
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void unload() {
		for (NPCData nd : npcList)
			CitizensAPI.getNPCRegistry().getById(nd.getId()).destroy();
	}

}
