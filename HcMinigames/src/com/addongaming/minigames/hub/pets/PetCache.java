package com.addongaming.minigames.hub.pets;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.hub.npc.InfNPC;
import com.addongaming.minigames.hub.npc.NPCData;

public class PetCache implements InfNPC, CommandExecutor {
	private JavaPlugin jp;
	List<NPCData> npcList = new ArrayList<NPCData>();
	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "Pets"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	public PetCache(JavaPlugin jp) {
		this.jp = jp;
		CitizensAPI.getTraitFactory().registerTrait(
				TraitInfo.create(PetTrait.class).withName("Pet"));
		jp.getCommand("pet").setExecutor(this);
	}

	public void createPet(MinigameUser mu, Pets pets) {
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(pets.getEntityType(),
				mu.getName());
		npc.spawn(mu.getLocation());
		PetTrait gt = new PetTrait();
		gt.setUser(mu);
		npc.addTrait(gt);
		mu.setPet(npc);
	}

	@Override
	public void load() {
	}

	@Override
	public void unload() {
		for (NPCData nd : npcList)
			if (CitizensAPI.getNPCRegistry().getById(nd.getId()) != null)
				CitizensAPI.getNPCRegistry().getById(nd.getId()).destroy();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player))
			return false;
		Player player = (Player) arg0;
		MinigameUser user = HcMinigames.getInstance().getHub()
				.getMinigameUser(player.getName());
		if (arg3.length == 0) {
			listCommands(player);
			return true;
		}
		switch (arg3[0]) {
		case "list": {
			Pets[] allowedPets = Pets.getAllowedPets(player);
			if (allowedPets.length == 0) {
				arg0.sendMessage(prefix
						+ "You need to purchase some pets from the pet seller!");
				return true;
			}
			List<String> allowed = new ArrayList<String>();
			for (Pets pet : allowedPets)
				allowed.add(pet.getName());
			arg0.sendMessage(prefix + "You can spawn "
					+ StringUtils.join(allowed, ", "));
			return true;
		}
		case "spawn":
			if (arg3.length == 1) {
				arg0.sendMessage(prefix + "Please use /pet spawn <name>");
				return true;
			}
			Pets pet = Pets.getByName(arg3[1]);
			if (pet == null) {
				arg0.sendMessage(prefix + "That pet doesn't exist.");
				return true;
			} else if (!player.hasPermission(pet.getPermNode())) {
				arg0.sendMessage(prefix
						+ "You need to purchase that pet from the shop");
				return true;
			} else if (HcMinigames.getInstance().getManagement()
					.getQueueManagement().getLobby(user) != null) {
				arg0.sendMessage(prefix
						+ "Sorry, you cannot spawn in pets here.");
				return true;
			}
			createPet(user, pet);
			arg0.sendMessage(prefix + "Spawned " + pet.getName());
			return true;
		default:
			listCommands(player);
			return true;
		}
	}

	private void listCommands(Player player) {
		player.sendMessage(prefix);
		player.sendMessage(ChatColor.AQUA
				+ "/pet list - Lists your available pets");
		player.sendMessage(ChatColor.AQUA
				+ "/pet spawn <name> - Spawns your pet");
		player.sendMessage(prefix);
	}

}
