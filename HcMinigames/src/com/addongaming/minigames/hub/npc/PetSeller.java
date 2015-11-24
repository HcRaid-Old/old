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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.hub.pets.Pets;

public class PetSeller implements InfNPC {
	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();

	public PetSeller(JavaPlugin jp) {
		this.jp = jp;
		setupConfig(jp.getConfig());
	}

	private void createNpc(String str) {
		Location loc = Utils.loadLoc(jp.getConfig()
				.getString(str + ".location"));
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER,
				"Pet Seller");
		npc.setProtected(true);
		npc.spawn(loc);
		Entity entity = npc.getEntity();
		npcList.add(new NPCData(npc.getId(), loc.getWorld()));
		Villager vill = (Villager) entity;
		vill.setCustomName("Pet Seller");
		vill.setCustomNameVisible(true);
		vill.setAdult();
		vill.setProfession(Profession.FARMER);
		vill.setMaxHealth(Double.MAX_VALUE);
	}

	@EventHandler
	public void itemClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getTitle()
				.startsWith(ChatColor.GREEN + "Pet Seller")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null
					&& event.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = event.getCurrentItem();
				String name = is.getItemMeta().getDisplayName();
				MinigameUser user = HcMinigames.getInstance().getHub()
						.getMinigameUser(event.getWhoClicked().getName());
				Pets pet = Pets.getByName(ChatColor.stripColor(name));
				if (user == null) {
					event.setCancelled(true);
					return;
				}
				if (name.startsWith(ChatColor.RED + "")) {
					messagePlayer((Player) event.getWhoClicked(),
							"Sorry, you do not have the funds to purchase this pet.");
					event.getWhoClicked().closeInventory();
					return;
				}
				if (name.startsWith(ChatColor.DARK_RED + "")) {
					messagePlayer((Player) event.getWhoClicked(),
							"You already own this pet.");
					event.getWhoClicked().closeInventory();
					return;
				}
				if (name.startsWith(ChatColor.DARK_BLUE + "")) {
					messagePlayer((Player) event.getWhoClicked(),
							"This pet is premium only.");
					event.getWhoClicked().closeInventory();
					return;
				}
				if (name.startsWith(ChatColor.GREEN + "")) {
					String strt = (is.getItemMeta().getLore().get(is
							.getItemMeta().getLore().size() - 1));
					int cost = Integer.parseInt(strt.substring(1));
					if (user.getBankPoints() >= cost) {
						int ingots = Utils.count(
								(Player) event.getWhoClicked(),
								Material.GOLD_INGOT);
						if (ingots >= cost) {
							ingots = cost;
						}
						ItemStack gold = new ItemStack(Material.GOLD_INGOT,
								ingots);
						ItemMeta goldMeta = gold.getItemMeta();
						goldMeta.setDisplayName(ChatColor.GOLD + "Gold Ingot");
						goldMeta.setLore(new ArrayList<String>() {
							{
								add(ChatColor.AQUA
										+ "This is the main currency");
								add(ChatColor.AQUA + "    on HcMinigames");
								add("");
								add(ChatColor.AQUA + "   Right click to bank");
								add(ChatColor.AQUA
										+ "Or spend it straight away");
							}
						});
						gold.setItemMeta(goldMeta);
						event.getWhoClicked().getInventory().removeItem(gold);
						messagePlayer((Player) event.getWhoClicked(),
								"Congratulations "
										+ event.getWhoClicked().getName()
										+ " you can now spawn your pet, /pet.");
						HcEssentials.permission.playerAdd(
								(Player) event.getWhoClicked(),
								pet.getPermNode());
						HcMinigames
								.getInstance()
								.getManagement()
								.getScoreManagement()
								.decrementPlayerBankCurrency(
										event.getWhoClicked().getName(), cost);

						user.setBankPoints(user.getBankPoints() - cost);
						event.getWhoClicked().closeInventory();
						return;
					} else {
						messagePlayer((Player) event.getWhoClicked(),
								"Sorry, you do not have enough gold.");
						event.getWhoClicked().closeInventory();
						return;
					}
				}
				return;

			}
		}
	}

	@Override
	public void load() {
		createNpc("npcs.petseller");

	}

	private void messagePlayer(Player p, String message) {
		p.sendMessage(ChatColor.GOLD + "Pet Seller" + ChatColor.RESET + "> "
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
		Inventory i = Bukkit.createInventory(null, 9 * 1, ChatColor.GREEN
				+ "Pet Seller - $" + user.getBankPoints());
		List<ItemStack> cmds = new ArrayList<ItemStack>();
		for (Pets md : Pets.values()) {
			ItemStack cmd = new ItemStack(Material.MONSTER_EGG);
			cmd.setDurability(md.getEggDura());
			if (play.hasPermission(md.getPermNode()))
				cmd = Utils.setLore(
						Utils.setName(ChatColor.DARK_RED + "" + ChatColor.RED
								+ md.getName(), cmd),
						"You can already spawn this");
			else if (md.getCost() > user.getBankPoints())
				cmd = Utils.setLore(
						Utils.setName(ChatColor.RED + md.getName(), cmd),
						"You cannot afford this.");
			else if (md.isPremium()
					&& !play.hasPermission("hcraid.premium.pet"))
				cmd = Utils.setLore(
						Utils.setName(ChatColor.DARK_BLUE + "" + ChatColor.RED
								+ md.getName(), cmd),
						"This is a premium only pet");
			else
				cmd = Utils.setLore(
						Utils.setName(ChatColor.GREEN + md.getName(), cmd),
						"You can buy a " + md.getName() + " for",
						"$" + md.getCost());
			cmds.add(cmd);
		}
		int counter = 0;
		for (ItemStack is : cmds) {
			i.setItem(counter++, is);
		}
		play.openInventory(i);
	}

	private void setupConfig(final FileConfiguration fc) {
		Location temp = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		fc.addDefault("npcs.petseller.location",
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
