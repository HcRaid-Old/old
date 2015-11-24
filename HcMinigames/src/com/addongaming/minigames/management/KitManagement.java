package com.addongaming.minigames.management;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.kits.Kit;
import com.addongaming.minigames.minigames.ArenaGame;

public class KitManagement implements CommandExecutor {
	private HcMinigames minigames;
	private final static HashMap<EKit, Kit> kitMap = new HashMap<EKit, Kit>();
	private final static HashMap<String, Kit> modificationMap = new HashMap<String, Kit>();

	public KitManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames.getCommand("kitman").setExecutor(this);
		minigames.getCommand("kit").setExecutor(this);
		File folder = new File(minigames.getDataFolder() + File.separator
				+ "Kits");
		if (!folder.exists())
			folder.mkdirs();
		for (EKit ekit : EKit.values()) {
			kitMap.put(ekit, new Kit(folder, ekit.name()));
		}
	}

	public ItemStack[] getArmourContents(EKit kit) {
		return kitMap.get(kit).getArmour();
	}

	public ItemStack[] getInvenContents(EKit kit) {
		return kitMap.get(kit).getInven();
	}

	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "KitMan"
			+ ChatColor.GOLD + "] " + ChatColor.AQUA;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg1.getName().equalsIgnoreCase("kit")) {
			if (!(arg0 instanceof Player))
				return true;
			ArenaGame ag = minigames.getManagement().getArenaManagement()
					.getGame(arg0.getName());
			if (ag == null) {
				arg0.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN
						+ "Kits" + ChatColor.GOLD + "] " + ChatColor.AQUA
						+ "You need to be in a game to do this command.");
				return true;
			}
			ag.openKits((Player) arg0);
			return true;
		}
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(prefix + "This can only be executed in-game.");
			return true;
		} else if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED + "This is only available for ops.");
			return true;
		}
		if (arg3.length == 0) {
			listAdminCommands(arg0);
			return true;
		}
		Player p = (Player) arg0;
		switch (arg3[0].toLowerCase()) {
		case "kits":
			arg0.sendMessage(prefix + "Available kits: " + EKit.asString());
			return true;
		case "load": {
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "Please use /kitman load <kit>, list of kits are "
						+ EKit.asString());
				return true;
			}
			String kit = arg3[1];
			EKit eKit = EKit.getByName(kit);
			if (eKit == null) {
				arg0.sendMessage(prefix + kit
						+ " kit was not found, list of kits are "
						+ EKit.asString());
				return true;
			}
			p.getInventory().setArmorContents(getArmourContents(eKit));
			p.getInventory().setContents(getInvenContents(eKit));
			arg0.sendMessage(prefix + "Armour and inventory to kit " + kit);
			return true;
		}
		case "save": {
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "Please use /kitman save <kit>, list of kits are "
						+ EKit.asString());
				return true;
			}
			String kitText = arg3[1];
			EKit eKit = EKit.getByName(kitText);
			if (eKit == null) {
				arg0.sendMessage(prefix + kitText
						+ " kit was not found, list of kits are "
						+ EKit.asString());
				return true;
			}
			Kit kit = kitMap.get(eKit);
			kit.saveInven(p.getInventory().getContents());
			kit.saveArmour(p.getInventory().getArmorContents());
			arg0.sendMessage(prefix + "Saved kit " + kitText);
			return true;
		}
		case "select":
		case "sel":
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "Please use /km sel <kit>, list of kits are "
						+ EKit.asString());
				return true;
			}
			String kitText = arg3[1];
			EKit eKit = EKit.getByName(kitText);
			if (eKit == null) {
				arg0.sendMessage(prefix + kitText
						+ " kit was not found, list of kits are "
						+ EKit.asString());
				return true;
			}
			Kit kit = kitMap.get(eKit);
			modificationMap.put(arg0.getName(), kit);
			arg0.sendMessage(prefix + "You are now modifying " + eKit.name());
			return true;
		case "set": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You do not have a kit selected, please use /km sel <kit>");
				return true;
			}
			if (arg3.length == 2) {
				listAdminCommands(arg0);
				return true;
			}
			switch (arg3[1].toLowerCase()) {
			case "type":
				Player player = (Player) arg0;
				if (player.getItemInHand() == null
						|| player.getItemInHand().getType() == Material.AIR) {
					arg0.sendMessage(prefix
							+ "Please hold a validitem in your hand.");
					return true;
				}
				modificationMap.get(player.getName()).setDisplayItem(
						player.getItemInHand().getType());
				arg0.sendMessage(prefix + "Set the display item.");
				return true;
			}
		}
		case "effect": {
			if (!modificationMap.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix
						+ "You do not have a kit selected, please use /km sel <kit>");
				return true;
			}
			if (arg3.length == 2) {
				listAdminCommands(arg0);
				return true;
			}
			switch (arg3[1].toLowerCase()) {
			case "add": {
				if (arg3.length != 5) {
					arg0.sendMessage(ChatColor.AQUA
							+ "  /km effect add <potionid> <duration> <amplifier> - Adds a potion effect");
					return true;
				}
				if (!isInteger(arg3[2]) || !isInteger(arg3[3])
						|| !isInteger(arg3[4])) {
					arg0.sendMessage(prefix + "Parameters must be an integer");
					arg0.sendMessage(ChatColor.AQUA
							+ "  /km effect add <potionid> <duration> <amplifier> - Adds a potion effect");
					return true;
				}
				int potionId = Integer.parseInt(arg3[2]), duration = Integer
						.parseInt(arg3[3]), amplifier = Integer
						.parseInt(arg3[4]);
				PotionEffectType pet = PotionEffectType.getById(potionId);
				if (pet == null)
					arg0.sendMessage(prefix + "That is not a valid potion ID.");
				else {
					modificationMap.get(arg0.getName()).addPotionEffect(pet,
							duration, amplifier);
					arg0.sendMessage(prefix + "Added potion effect");
				}
				return true;
			}
			case "remove":
			case "del":
				if (arg3.length != 3) {
					arg0.sendMessage(prefix
							+ "Please use /km effect del <index> ");
					return true;
				} else if (!isInteger(arg3[2])) {
					arg0.sendMessage(prefix + "Index needs to be an integer.");
					return true;
				}
				boolean removed = modificationMap.get(arg0.getName())
						.removePotionEffect(Integer.parseInt(arg3[2]));
				if (removed) {
					arg0.sendMessage(prefix
							+ "Successfully removed potion effect");
				} else {
					arg0.sendMessage(prefix + arg3[2]
							+ " is not a valid index.");
				}
				return true;
			case "list": {
				Kit kkit = modificationMap.get(arg0.getName());
				int index = 0;
				arg0.sendMessage(prefix + "Available potion effects: "
						+ kkit.getPotionEffects().length);
				for (PotionEffect pe : kkit.getPotionEffects()) {
					arg0.sendMessage(ChatColor.GOLD + "  Index "
							+ ChatColor.AQUA + index + "" + ChatColor.GOLD
							+ " Potion id " + ChatColor.AQUA
							+ pe.getType().getId() + " ("
							+ pe.getType().getName() + ")" + ChatColor.GOLD
							+ " Duration " + ChatColor.AQUA + pe.getDuration()
							+ ChatColor.GOLD + " Amplifier " + ChatColor.AQUA
							+ pe.getAmplifier());
				}
				if (kkit.getPotionEffects().length != 0)
					arg0.sendMessage(prefix);
				return true;
			}
			default:
				arg0.sendMessage(prefix);
				arg0.sendMessage(ChatColor.AQUA
						+ "  /km effect add <potionid> <duration> <amplifier> - Adds a potion effect");
				arg0.sendMessage(ChatColor.AQUA
						+ "  /km effect remove <id> - Removes a potion effect from selected kit");
				arg0.sendMessage(ChatColor.AQUA
						+ "  /km effect list - Lists all potion effects");
				arg0.sendMessage(prefix);
				return true;

			}
		}
		case "quit":
			if (modificationMap.containsKey(arg0.getName())) {
				modificationMap.remove(arg0.getName());
				arg0.sendMessage(prefix + "You are no longer modifying a kit.");
			} else {
				arg0.sendMessage(prefix + "You are not modifying a kit.");
			}
			return true;
		default:
			listAdminCommands(arg0);
			return true;
		}
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void listAdminCommands(CommandSender arg0) {
		arg0.sendMessage(prefix + "Admin commands");
		arg0.sendMessage(ChatColor.AQUA + "  /km kits - Lists all kit names");
		arg0.sendMessage(ChatColor.AQUA
				+ "  /km load <kit> - Loads the kit items onto you ");
		arg0.sendMessage(ChatColor.AQUA
				+ "  /km save <kit> - Saves your current inventory&armour");
		if (modificationMap.containsKey(arg0.getName())) {
			arg0.sendMessage(ChatColor.AQUA
					+ "  /km effect add <potionid> <duration> <amplifier> - Adds a potion effect");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /km effect remove <id> - Removes a potion effect from selected kit");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /km effect list - Lists all potion effects");
			arg0.sendMessage(ChatColor.AQUA
					+ "  /km set type - Sets the display item to be that of the item in your hand");
		} else
			arg0.sendMessage(ChatColor.AQUA
					+ "  /km sel <kit> - Selects kit for full-modification");
		arg0.sendMessage(prefix);
	}

	public PotionEffect[] getPotionEffects(EKit ek) {
		return kitMap.get(ek).getPotionEffects();
	}

	public Kit getKit(EKit ek) {
		return kitMap.get(ek);
	}
}
