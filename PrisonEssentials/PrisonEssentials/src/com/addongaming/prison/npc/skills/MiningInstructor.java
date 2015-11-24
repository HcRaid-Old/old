package com.addongaming.prison.npc.skills;

import java.text.DecimalFormat;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.data.skills.MiningData;
import com.addongaming.prison.data.skills.PickaxeData;
import com.addongaming.prison.npc.InfNPC;
import com.addongaming.prison.npc.NPCData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public class MiningInstructor implements InfNPC {

	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public MiningInstructor(JavaPlugin jp) {
		this.jp = jp;
		setupConfig(jp.getConfig());
	}

	private void createNpc(String str) {
		int value = jp.getConfig().getInt(str + ".maxLevel");
		Location loc = Utils.loadLoc(jp.getConfig()
				.getString(str + ".location"));
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
				"Mining Teacher");
		npc.setProtected(true);
		npc.spawn(loc);
		Entity entity = npc.getEntity();
		npcList.add(new NPCData(npc.getId(), value, loc.getWorld()));
		Villager vill = (Villager) entity;
		vill.setCustomName("Mining Teacher");
		vill.setCustomNameVisible(true);
		vill.setAdult();
		vill.setProfession(Profession.BLACKSMITH);
		vill.setMaxHealth(Double.MAX_VALUE);
	}

	@EventHandler
	public void itemClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getTitle().startsWith("Mining")) {
			Prisoner prisoner = PrisonerManager.getInstance()
					.getPrisonerInfo(event.getWhoClicked().getName());
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				ItemStack is = event.getCurrentItem();
				String name = is.getItemMeta().getDisplayName();
				if (MiningData
						.isMiningRelated(event.getCurrentItem().getType())) {
					MiningData md = MiningData.getMiningDataForMaterial(event
							.getCurrentItem().getType());
					if (name.startsWith(ChatColor.RED + "")) {
						messagePlayer((Player) event.getWhoClicked(),
								"Sorry, I cannot teach you this.");
						event.getWhoClicked().closeInventory();
						return;
					}
					if (name.startsWith(ChatColor.STRIKETHROUGH + "")) {
						messagePlayer((Player) event.getWhoClicked(),
								"Sorry, you need to visit a higher levelled teacher.");
						event.getWhoClicked().closeInventory();
						return;
					}
					if (name.startsWith(ChatColor.GREEN + "")) {
						String strt = (is.getItemMeta().getLore().get(is
								.getItemMeta().getLore().size() - 1));
						int cost = Integer.parseInt(strt.substring(1));
						if (prisoner.hasBalance(cost)) {
							messagePlayer(
									(Player) event.getWhoClicked(),
									"Congratulations " + prisoner.getName()
											+ " you can now mine "
											+ md.toText() + "!");
							prisoner.addPermission(md.getPermission());
							event.getWhoClicked().closeInventory();
							prisoner.removeBalance(cost);
							return;
						} else {
							messagePlayer((Player) event.getWhoClicked(),
									"Sorry, you do not have enough money.");
							event.getWhoClicked().closeInventory();
							return;
						}
					}
					return;
				} else if (PickaxeData.isPickaxeRelated(event.getCurrentItem()
						.getType())) {
					PickaxeData pd = PickaxeData
							.getPickaxeDataForMaterial(event.getCurrentItem()
									.getType());
					if (name.startsWith(ChatColor.RED + "")) {
						messagePlayer((Player) event.getWhoClicked(),
								"Sorry, I cannot teach you this.");
						event.getWhoClicked().closeInventory();
						return;
					}
					if (name.startsWith(ChatColor.STRIKETHROUGH + "")) {
						messagePlayer((Player) event.getWhoClicked(),
								"Sorry, you need to visit a higher levelled teacher.");
						event.getWhoClicked().closeInventory();
						return;
					}
					if (name.startsWith(ChatColor.GREEN + "")) {
						String strt = (is.getItemMeta().getLore().get(is
								.getItemMeta().getLore().size() - 1));
						int cost = Integer.parseInt(strt.substring(1));
						if (prisoner.hasBalance(cost)) {
							messagePlayer(
									(Player) event.getWhoClicked(),
									"Congratulations " + prisoner.getName()
											+ " you can now mine using a "
											+ pd.toText() + " pickaxe!");
							prisoner.addPermission(pd.getPermission());
							event.getWhoClicked().closeInventory();
							prisoner.removeBalance(cost);
							return;
						} else {
							messagePlayer((Player) event.getWhoClicked(),
									"Sorry, you do not have enough money.");
							event.getWhoClicked().closeInventory();
							return;
						}
					}
					return;
				}
			}
		}
	}

	@Override
	public void load() {
		FileConfiguration fc = jp.getConfig();
		for (String s : fc.getConfigurationSection("npcs.instructors.mining")
				.getKeys(false))
			createNpc("npcs.instructors.mining." + s);

	}

	private void messagePlayer(Player p, String message) {
		p.sendMessage(ChatColor.AQUA + "Mining Instructor" + ChatColor.RESET
				+ "> " + message);
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

	private void openInventory(NPCData nd, Player play) {
		Prisoner prisoner = PrisonerManager.getInstance()
				.getPrisonerInfo(play.getName());
		Inventory i = Bukkit.createInventory(null, 9 * 4, "Mining Level: "
				+ Stats.getLevel(prisoner.getStat(Stats.MINING)) + " - "
				+ new DecimalFormat("########.#").format(prisoner.getBalance())
				+ " R");
		List<ItemStack> ores = new ArrayList<ItemStack>();
		for (MiningData md : MiningData.values()) {
			ItemStack ore = new ItemStack(md.getMaterial());
			if (md.getLevelReq() <= nd.getValue()) {
				if (prisoner.hasPermission(md.getPermission()))
					ore = Utils.setLore(
							Utils.setName(ChatColor.RED + md.toText(), ore),
							"You can already mine " + md.toText());
				else if (Stats.getLevel(prisoner.getStat(Stats.MINING)) < md
						.getLevelReq())
					ore = Utils.setLore(
							Utils.setName(ChatColor.RED + md.toText(), ore),
							"You need level " + md.getLevelReq() + " mining.");
				else
					ore = Utils.setLore(
							Utils.setName(ChatColor.GREEN + md.toText(), ore),
							"You can buy the permission",
							"to mine " + md.toText() + " for ",
							"$" + md.getLevelReq() * 150);
			} else {
				ore = Utils.setLore(Utils.setName(
						ChatColor.STRIKETHROUGH + md.toText(), ore),
						"Sorry, you need to", "go to a higher",
						"level teacher.");
			}
			ores.add(ore);
		}
		List<ItemStack> picks = new ArrayList<ItemStack>();
		for (PickaxeData pd : PickaxeData.values()) {
			ItemStack pick = new ItemStack(pd.getMaterial());
			if (pd.getLevelReq() <= nd.getValue()) {
				if (prisoner.hasPermission(pd.getPermission()))
					pick = Utils.setLore(
							Utils.setName(ChatColor.RED + pd.toText(), pick),
							"You can already mine with a ", pd.toText()
									+ " pickaxe.");
				else if (Stats.getLevel(prisoner.getStat(Stats.MINING)) < pd
						.getLevelReq())
					pick = Utils.setLore(
							Utils.setName(ChatColor.RED + pd.toText(), pick),
							"You need level " + pd.getLevelReq() + " mining.");
				else
					pick = Utils.setLore(
							Utils.setName(ChatColor.GREEN + pd.toText(), pick),
							"You can learn how to",
							"to mine with a " + pd.toText(), " pickaxe for ",
							"$" + pd.getLevelReq() * 150);
			} else {
				pick = Utils.setLore(Utils.setName(
						ChatColor.STRIKETHROUGH + pd.toText(), pick),
						"Sorry, you need to", "go to a higher",
						"level teacher.");
			}
			picks.add(pick);
		}
		int counter = 0;
		for (ItemStack is : ores) {
			if (counter == 9)
				counter = 12;
			i.setItem(counter++, is);
		}
		counter = 29;
		for (ItemStack is : picks) {
			i.setItem(counter++, is);
		}
		play.openInventory(i);
	}

	private void setupConfig(final FileConfiguration fc) {
		Location temp = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("npcs.instructors.mining.npc1.location",
				Utils.locationToSaveString(temp));
		fc.addDefault("npcs.instructors.mining.npc1.maxLevel", 20);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void unload() {
		for (NPCData nd : npcList)
			CitizensAPI.getNPCRegistry().getById(nd.getId()).destroy();
	}
}
