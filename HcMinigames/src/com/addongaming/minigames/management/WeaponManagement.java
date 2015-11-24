package com.addongaming.minigames.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.management.scheduling.ArrowClearer;
import com.addongaming.minigames.management.scheduling.FullyAuto;
import com.addongaming.minigames.management.scheduling.ReloadScheduler;
import com.addongaming.minigames.management.scheduling.SemiAuto;
import com.addongaming.minigames.management.scheduling.WeaponCheck;
import com.addongaming.minigames.management.scheduling.WeaponManagementCleanser;
import com.addongaming.minigames.management.weapon.Gun;
import com.addongaming.minigames.management.weapon.GunFireType;
import com.addongaming.minigames.management.weapon.PhysicalWeapon;
import com.addongaming.minigames.management.weapon.Weapon;
import com.addongaming.minigames.management.weapon.WeaponFire;
import com.addongaming.minigames.management.weapon.Weapons;
import com.addongaming.minigames.minigames.ArenaGame;

public class WeaponManagement implements CommandExecutor {
	private final HcMinigames minigames;
	private final static List<Weapon> weaponList = new ArrayList<Weapon>();
	private String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "WeaponMan" + ChatColor.GOLD + "] " + ChatColor.AQUA;
	private final static HashMap<String, WeaponFire> fireMap = new HashMap<String, WeaponFire>();

	public WeaponManagement(HcMinigames minigames) {
		this.minigames = minigames;
		loadWeapons();
		minigames.getCommand("wm").setExecutor(this);
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(minigames,
						new WeaponManagementCleanser(this), 6l, 2l);
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(minigames, new ArrowClearer(), 60l,
						20l);
	}

	public boolean hasWeaponFire(String name) {
		return fireMap.containsKey(name);
	}

	public WeaponFire getWeaponFire(String name) {
		return fireMap.get(name);
	}

	public void removeWeaponFire(String name) {
		fireMap.remove(name);
	}

	private void loadWeapons() {
		File dir = new File(minigames.getDataFolder(), "Weapons");
		for (Weapons weapons : Weapons.values()) {
			switch (weapons.getWeaponType()) {
			case PHYSICAL:
				weaponList.add(new PhysicalWeapon(minigames, new File(dir,
						weapons.name() + ".yml"), weapons));
				break;
			case GRENADE:
				// TODO
				break;
			case GUN:
				weaponList.add(new Gun(minigames, new File(dir, weapons.name()
						+ ".yml"), weapons));
				break;
			case THROWING:
				// TODO
				break;
			default:
				break;

			}
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(prefix + "This can only be executed in-game.");
			return true;
		} else if (!arg0.isOp()) {
			arg0.sendMessage(ChatColor.RED + "This is only available for ops.");
			return true;
		}
		if (arg3.length == 0) {
			listWeaponCommands(arg0);
			return true;
		}
		weaponCommand(arg0, arg3);
		return true;
	}

	private void weaponCommand(CommandSender arg0, String[] arg3) {
		switch (arg3[0].toLowerCase()) {
		case "list":
			arg0.sendMessage(prefix + " Loaded weapons");
			for (Weapon weapon : weaponList)
				arg0.sendMessage(ChatColor.GOLD + "Weapon: " + ChatColor.AQUA
						+ weapon.getWeapons().name() + ChatColor.GOLD
						+ " Type: " + ChatColor.AQUA
						+ weapon.getWeaponType().name());
			arg0.sendMessage(prefix);
			return;
		case "sel":
			if (arg3.length == 1) {
				arg0.sendMessage(prefix
						+ "/wm sel <name> - Please get the name from /wm wep list");
				return;
			}
			String name = arg3[1];
			Weapon weapon = null;
			for (Weapon wep : weaponList)
				if (wep.getWeapons().name().equalsIgnoreCase(name)) {
					weapon = wep;
					break;
				}
			if (weapon == null) {
				arg0.sendMessage(prefix + name + " weapon was not found.");
				return;
			}
			weaponModification.put(arg0.getName(), weapon);
			arg0.sendMessage(prefix + "Selected "
					+ weapon.getWeapons().toReadableText()
					+ " use /wm - to get all commands");
			return;
		case "save":
			if (!weaponModification.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix + "You don't have a weapon selected.");
				return;
			}
			weaponModification.get(arg0.getName()).save();
			arg0.sendMessage("Saved"
					+ weaponModification.get(arg0.getName()).getWeapons()
							.toReadableText());
			return;
		case "quit":
			if (!weaponModification.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix + "You don't have a weapon selected.");
				return;
			}
			arg0.sendMessage(prefix
					+ "No longer modifying "
					+ weaponModification.get(arg0.getName()).getWeapons()
							.toReadableText());
			weaponModification.remove(arg0.getName());
			return;
		case "load":
			if (!weaponModification.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix + "You don't have a weapon selected.");
				return;
			}
			Weapon wep = weaponModification.get(arg0.getName());
			Player player = (Player) arg0;
			player.setItemInHand(wep.getWeapon());
			return;
		case "set":
			if (!weaponModification.containsKey(arg0.getName())) {
				arg0.sendMessage(prefix + "You don't have a weapon selected.");
				return;
			}
			if (arg3.length == 1) {
				listWeaponCommands(arg0);
				return;
			}
			switch (weaponModification.get(arg0.getName()).getWeaponType()) {
			case PHYSICAL: {
				PhysicalWeapon pw = (PhysicalWeapon) weaponModification
						.get(arg0.getName());
				switch (arg3[1]) {
				case "type": {
					Player pl = (Player) arg0;
					if (pl.getItemInHand() == null
							|| pl.getItemInHand().getType() == Material.AIR)
						pl.sendMessage(prefix
								+ "You need to have a valid item in your hand.");
					else {
						weaponModification.get(pl.getName()).setType(
								pl.getItemInHand().getType());
						pl.sendMessage(prefix + "Changed weapon type.");
					}
				}
					break;
				case "damage":
				case "dmg":
					if (arg3.length < 3) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					pw.setDamage(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set damage");
					break;
				}
			}
				break;
			case GRENADE:
				break;
			case GUN: {
				Gun gun = (Gun) weaponModification.get(arg0.getName());
				switch (arg3[1]) {
				case "clip":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setClipSize(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set clip size");
					break;
				case "damage":
				case "dmg":
					if (arg3.length < 3) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setDamage(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set damage");
					break;
				case "rof":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setRof(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set rate of fire");
					break;
				case "reload":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setReload(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set reload time");
					break;
				case "accuracy":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setAccuracy(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set gun accuracy");
					break;
				case "totammo":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setOverallammo(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set total ammo");
					break;
				case "recoil":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					if (!isInteger(arg3[2], arg0))
						return;
					gun.setRecoil(Integer.parseInt(arg3[2]));
					arg0.sendMessage(prefix + "Set recoil amount");
					break;
				case "firemode":
					if (arg3.length == 2) {
						listWeaponCommands(arg0);
						return;
					}
					GunFireType gft = GunFireType.getType(arg3[2]);
					if (gft == null) {
						arg0.sendMessage(prefix + "Available firemode types: "
								+ GunFireType.asString());
						return;
					}
					gun.setFiremode(gft);
					arg0.sendMessage(prefix + "Set gun fire type.");
					return;
				case "type": {
					Player pl = (Player) arg0;
					if (pl.getItemInHand() == null
							|| pl.getItemInHand().getType() == Material.AIR)
						pl.sendMessage(prefix
								+ "You need to have a valid item in your hand.");
					else {
						weaponModification.get(pl.getName()).setType(
								pl.getItemInHand().getType());
						pl.sendMessage(prefix + "Changed weapon type.");
					}
				}
					return;
				default:
					listWeaponCommands(arg0);
					break;
				}
			}
				break;
			case THROWING:
				break;
			default:
				break;

			}
		}
	}

	private boolean isInteger(String text, CommandSender cs) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception e) {
			cs.sendMessage(prefix + text + " is not a viable number.");
			return false;
		}
	}

	private final static HashMap<String, Weapon> weaponModification = new HashMap<String, Weapon>();

	private void listWeaponCommands(CommandSender arg0) {
		arg0.sendMessage(prefix);
		arg0.sendMessage(ChatColor.AQUA + "/wm list - Lists all weapons");
		arg0.sendMessage(ChatColor.AQUA
				+ "/wm sel <name> - Selects a weapon for modification");
		if (weaponModification.containsKey(arg0.getName())) {
			arg0.sendMessage(ChatColor.AQUA
					+ "/wm save - Saves the weapon to file");
			arg0.sendMessage(ChatColor.AQUA + "/wm quit - Quits the session");
			arg0.sendMessage(ChatColor.AQUA + "/wm wep load - Loads the weapon");
			arg0.sendMessage(ChatColor.AQUA
					+ "/wm set type - Makes material of weapon the item in your hand");
			Weapon weapon = weaponModification.get(arg0.getName());
			switch (weapon.getWeaponType()) {
			case PHYSICAL:
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set damage <dmg> - Sets damage in half-hearts");
				break;
			case GRENADE:
				// TODO
				break;
			case GUN:
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set firemode <firemode> - Sets the fire mode");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set clip <amnt> - Sets clip-size");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set damage <dmg> - Sets damage in half-hearts");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set rof <rof> - Sets fire in ms");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set reload <speed> - Duration in ms");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set accuracy <acc> - 10 avg, 0 bad, 20 good");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set recoil <rec> - 10 avg, 0 good, 20 bad");
				arg0.sendMessage(ChatColor.AQUA
						+ "/wm set totammo <amnt> - Total ammo");
				break;
			case THROWING:
				// TODO
				break;
			default:
				break;
			}
		}
		arg0.sendMessage(prefix);
	}

	public boolean isWeapon(ItemStack item) {
		for (Weapon weapon : weaponList)
			if (weapon.isWeapon(item))
				return true;
		return false;
	}

	public Weapon getWeapon(ItemStack item) {
		for (Weapon weapon : weaponList)
			if (weapon.isWeapon(item))
				return weapon;
		return null;
	}

	public HcMinigames getMinigames() {
		return minigames;
	}

	public void useWeapon(ItemStack item, ArenaGame ag, Player player) {
		Weapon weapon = getWeapon(item);
		if (fireMap.containsKey(player.getName())) {
			WeaponFire wea = fireMap.get(player.getName());
			if (wea.getWeapon().isWeapon(item)) {
				wea.shoot();
			} else
				removeWeaponFire(player.getName());
			return;
		}
		switch (weapon.getWeaponType()) {
		case PHYSICAL:
			break;
		case GRENADE:
			break;
		case GUN: {
			Gun gun = (Gun) weapon;
			if (gun.isReloading(item)) {
				return;
			}
			switch (gun.getGft()) {
			case FULLY_AUTOMATIC:
				minigames
						.getManagement()
						.getSchedulerManagement()
						.runScheduler(ag, new FullyAuto(gun, ag, player, this),
								0l, 1);
				fireMap.put(player.getName(), new WeaponFire(player.getName(),
						gun, System.currentTimeMillis()));
				break;
			case SEMI_AUTOMATIC:
				minigames
						.getManagement()
						.getSchedulerManagement()
						.runScheduler(ag, new SemiAuto(gun, ag, player, this),
								0l, 1);
				fireMap.put(player.getName(), new WeaponFire(player.getName(),
						gun, System.currentTimeMillis()));
				break;
			case SCATTER_SHOT:
			case SINGLE_SHOT:
				if (gun.getClipAmmo(player.getItemInHand()) == 0) {
					gun.reload(player);
					minigames
							.getManagement()
							.getSchedulerManagement()
							.runScheduler(ag,
									new ReloadScheduler(gun, player, this), 2l,
									2l);
					removeWeaponFire(player.getName());
				} else {
					gun.fire(player);
					if (gun.getClipAmmo(player.getItemInHand()) == 0) {
						gun.reload(player);
						minigames
								.getManagement()
								.getSchedulerManagement()
								.runScheduler(ag,
										new ReloadScheduler(gun, player, this),
										2l, 2l);
					} else {
						fireMap.put(
								player.getName(),
								new WeaponFire(player.getName(), gun, System
										.currentTimeMillis()));
					}
				}
				break;
			default:
				break;

			}
		}
			break;
		case THROWING:
			break;
		default:
			break;

		}
	}

	public void switchWeapon(Player player, int oldSlot, int newSlot) {
		if (fireMap.containsKey(player.getName()))
			fireMap.get(player.getName()).setTimer(0);
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(minigames,
						new WeaponCheck(player, this), 2l);
	}

	public void reset(Player player) {
		Weapon weapon = getWeapon(player.getItemInHand());
		switch (weapon.getWeaponType()) {
		case PHYSICAL:
			break;
		case GRENADE:
			break;
		case GUN: {
			Gun gun = (Gun) weapon;
			if (gun.isReloading(player.getItemInHand()))
				gun.cancelReload(player);
		}
			break;
		case THROWING:
			break;
		default:
			break;

		}
	}

	public Weapon getWeapon(Weapons weapons) {
		for (Weapon weapon : weaponList)
			if (weapon.getWeapons() == weapons)
				return weapon;
		return null;
	}

	public void onTick() {
		List<String> toRemove = new ArrayList<String>();
		for (String string : fireMap.keySet())
			if (fireMap.get(string).shouldRemove())
				toRemove.add(string);
		for (String string : toRemove)
			fireMap.remove(string);
	}
}
