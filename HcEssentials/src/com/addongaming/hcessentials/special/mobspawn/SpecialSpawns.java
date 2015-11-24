package com.addongaming.hcessentials.special.mobspawn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.data.SpecialItems;
import com.addongaming.hcessentials.data.SpecialItems.SpecialTypes;
import com.addongaming.hcessentials.utils.Utils;

public class SpecialSpawns implements SubPlugin, Listener, CommandExecutor {

	private final JavaPlugin jp;
	private int taskId;
	List<Integer> mobIds = new ArrayList<Integer>();

	@Override
	public void onDisable() {
		for (World w : jp.getServer().getWorlds())
			for (Entity e : w.getEntities())
				if (e instanceof LivingEntity)
					for (Iterator<Integer> iter = mobIds.iterator(); iter
							.hasNext();)
						if (e.getEntityId() == iter.next()) {
							((LivingEntity) (e)).setHealth(0.0d);
							iter.remove();
							break;
						}
	}

	public SpecialSpawns(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("specialspawns.enabled", Boolean.TRUE);
		fc.addDefault("specialspawns.minMins", 30);
		fc.addDefault("specialspawns.maxMins", 120);
		fc.addDefault("specialspawns.locations", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("world|0.0|0.0|0.0");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	List<Location> locs;

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("specialspawns.enabled"))
			return false;
		jp.getCommand("specialkill").setExecutor(this);
		jp.getServer().getPluginManager().registerEvents(this, jp);
		int min = jp.getConfig().getInt("specialspawns.minMins");
		int max = jp.getConfig().getInt("specialspawns.maxMins");
		final int ran = (new Random().nextInt(max - min)) + min;
		final List<String> tempLocs = jp.getConfig().getStringList(
				"specialspawns.locations");
		locs = new ArrayList<Location>();
		final SpecialSpawns ss = this;
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						for (String str : tempLocs)
							locs.add(Utils.loadLoc(str));
						int mins = ran * 20 * 60;
						taskId = jp
								.getServer()
								.getScheduler()
								.scheduleSyncRepeatingTask(jp,
										new SpecialSpawnsRunnable(locs, ss),
										mins, mins);
					}
				}, Config.Ticks.POSTWORLD);

		return true;
	}

	private void saveLocations() {
		List<String> list = new ArrayList<String>();
		for (Location loc : locs)
			list.add(Utils.locationToSaveString(loc));
		jp.getConfig().set("specialspawns.locations", list);
		jp.saveConfig();
		jp.reloadConfig();
	}

	private void resetScheduler() {
		if (taskId > 0)
			jp.getServer().getScheduler().cancelTask(taskId);
		int min = jp.getConfig().getInt("specialspawns.minMins");
		int max = jp.getConfig().getInt("specialspawns.maxMins");
		int ran = (new Random().nextInt(max - min)) + min;
		List<String> tempLocs = jp.getConfig().getStringList(
				"specialspawns.locations");
		locs = new ArrayList<Location>();
		for (String str : tempLocs)
			locs.add(Utils.loadLoc(str));
		int mins = ran * 20 * 60;
		taskId = jp
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(jp,
						new SpecialSpawnsRunnable(locs, this), mins, mins);
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		Player pl = (Player) arg0;
		if (arg0.isOp()) {
			if (arg3.length == 0) {
				arg0.sendMessage("Please use /specialkills <set|list|restart> or /specialkills <del> [id]");
			} else {
				switch (arg3[0].toLowerCase()) {
				case "set":
					Player player = (Player) arg0;
					locs.add(player.getLocation());
					saveLocations();
					break;
				case "list":
					int counter = 0;
					for (Location loc : locs)
						arg0.sendMessage(counter++ + ": "
								+ Utils.locationToString(loc));
					break;
				case "restart":
					resetScheduler();
					break;
				case "del":
					if (arg3.length == 2 || !isNumber(arg3[1]))
						arg0.sendMessage("Please use /specialkills <set|list> or /specialkills <del> [id]");
					else if (Integer.parseInt(arg3[1]) > locs.size() + 1
							|| Integer.parseInt(arg3[1]) < 0)
						arg0.sendMessage("Please use /specialkills list to get an appropiate id.");
					else {
						locs.remove(Integer.parseInt(arg3[1]));
						saveLocations();
					}
					break;
				default:
					arg0.sendMessage("Please use /specialkills <set|list> or /specialkills <del> [id]");
					break;
				}
			}
		}
		if (!pl.getName().equalsIgnoreCase("hamgooof")) {
			return true;
		}
		spawnSpecial(SpecialTypes.valueOf(arg3[0]), pl.getLocation());
		return true;
	}

	private boolean isNumber(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private void spawnSpecial() {
		Random r = new Random();
		int x = r.nextInt(7000) + 500;
		int z = r.nextInt(7000) + 500;
		if (r.nextInt(10) + 1 > 5)
			x = -x;
		if (r.nextInt(10) + 1 > 5)
			z = -z;
		Block loc = Bukkit.getWorld("world").getBlockAt(x, 255, z)
				.getLocation().getBlock();
		loc.getChunk().load();
		while (loc.getType() == Material.AIR || loc.isEmpty()) {
			loc = loc.getRelative(BlockFace.DOWN);
		}
		SpecialTypes st = SpecialTypes.SPECIAL;
		int ran = new Random().nextInt(100) + 1;
		if (ran > 95)
			st = SpecialTypes.UBER;
		else if (ran > 80)
			st = SpecialTypes.LEGENDARY;
		spawnSpecial(st, loc.getRelative(BlockFace.UP).getLocation());
	}

	public void spawnSpecial(SpecialTypes st, Location pl) {
		LivingEntity z = (LivingEntity) pl.getWorld().spawnEntity(pl,
				EntityType.ZOMBIE);
		mobIds.add(z.getEntityId());
		String name = getRandomName();
		z.setCustomName(name);
		Bukkit.broadcastMessage(name + " Has spawned at X: "
				+ z.getLocation().getBlockX() + " Z: "
				+ z.getLocation().getBlockZ());

		String firstNamMain = SpecialItems.getRandomArmourFirstName(st) + " ";
		z.getEquipment().setHelmet(
				SpecialItems.generateArmourPiece(Material.DIAMOND_HELMET, st,
						firstNamMain));
		z.getEquipment().setChestplate(
				SpecialItems.generateArmourPiece(Material.DIAMOND_CHESTPLATE,
						st, firstNamMain));
		z.getEquipment().setLeggings(
				SpecialItems.generateArmourPiece(Material.DIAMOND_LEGGINGS, st,
						firstNamMain));
		z.getEquipment().setBoots(
				SpecialItems.generateArmourPiece(Material.DIAMOND_BOOTS, st,
						firstNamMain));
		z.getEquipment().setItemInHand(
				SpecialItems.generateSword(Material.DIAMOND_SWORD, st));
		z.setCustomNameVisible(true);
		String fw = SpecialItems.getRandomArmourFirstName(SpecialTypes.COMMON)
				+ " ";
		SpecialTypes ss = SpecialTypes.COMMON;
		if (st == SpecialTypes.LEGENDARY)
			ss = SpecialTypes.SPECIAL;
		else if (st == SpecialTypes.UBER)
			ss = SpecialTypes.LEGENDARY;
		for (int i = 0; i < new Random().nextInt(5) + 4; i++) {
			LivingEntity ent = (LivingEntity) pl.getWorld().spawnEntity(pl,
					EntityType.ZOMBIE);
			ent.setCustomName(ChatColor.stripColor(name + "'s minion"));
			ent.getEquipment().setHelmet(
					SpecialItems.generateArmourPiece(Material.GOLD_HELMET,
							SpecialTypes.COMMON, fw));
			ent.getEquipment().setChestplate(
					SpecialItems.generateArmourPiece(Material.GOLD_CHESTPLATE,
							SpecialTypes.COMMON, fw));
			ent.getEquipment().setLeggings(
					SpecialItems.generateArmourPiece(Material.GOLD_LEGGINGS,
							SpecialTypes.COMMON, fw));
			ent.getEquipment().setBoots(
					SpecialItems.generateArmourPiece(Material.GOLD_BOOTS,
							SpecialTypes.COMMON, fw));
			ent.getEquipment().setItemInHand(
					SpecialItems.generateSword(Material.GOLD_SWORD,
							SpecialTypes.COMMON));
			mobIds.add(ent.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void entitySpawn(CreatureSpawnEvent event) {
		if (mobIds.contains(event.getEntity().getEntityId())
				&& event.isCancelled())
			event.setCancelled(false);
	}

	private String getRandomName() {
		ChatColor[] cc = { ChatColor.RED, ChatColor.DARK_GREEN,
				ChatColor.DARK_PURPLE };
		String[] names = { "John", "James the Wicked", "Yoghurt the Nasty",
				"Bread the Mouldy" };
		return cc[new Random().nextInt(cc.length)]
				+ names[new Random().nextInt(names.length)];
	}

	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
		if (mobIds.contains(event.getDamager().getEntityId())) {
			event.setDamage(event.getDamage() * (new Random().nextFloat() + 3));
			if (event.getEntity() instanceof Player) {
				int random = new Random().nextInt(20);
				Player atackee = (Player) event.getEntity();
				if (random == 15) {

				} else if (random == 10)
					atackee.addPotionEffect(new PotionEffect(
							PotionEffectType.POISON, 20 * 8, 2));
				else if (random == 5)
					atackee.addPotionEffect(new PotionEffect(
							PotionEffectType.CONFUSION, 20 * 8, 1));
				else if (random == 2)
					atackee.addPotionEffect(new PotionEffect(
							PotionEffectType.SLOW, 20 * 8, 1));
				else if (random == 1)
					atackee.addPotionEffect(new PotionEffect(
							PotionEffectType.BLINDNESS, 20 * 8, 1));
			}
		} else if (mobIds.contains(event.getEntity().getEntityId())) {
			event.setDamage(event.getDamage() * new Random().nextFloat());
		}
	}

	@EventHandler
	public void entityDeath(EntityDeathEvent event) {
		if (mobIds.contains(event.getEntity().getEntityId())) {
			LivingEntity le = event.getEntity();
			EntityEquipment ee = le.getEquipment();
			ItemStack boots = ee.getBoots();
			ItemStack helm = ee.getHelmet();
			ItemStack chest = ee.getChestplate();
			ItemStack legs = ee.getLeggings();
			ItemStack sword = ee.getItemInHand();

			if (boots != null && boots.getType() != Material.AIR) {
				boots.setDurability((short) 0);
				// ee.setBoots(boots);
				event.getEntity()
						.getWorld()
						.dropItemNaturally(event.getEntity().getLocation(),
								boots);
			}
			if (sword != null && sword.getType() != Material.AIR) {
				sword.setDurability((short) 0);
				// ee.setItemInHand(sword);
				event.getEntity()
						.getWorld()
						.dropItemNaturally(event.getEntity().getLocation(),
								sword);
			}
			if (helm != null && helm.getType() != Material.AIR) {
				helm.setDurability((short) 0);
				// ee.setHelmet(helm);
				event.getEntity()
						.getWorld()
						.dropItemNaturally(event.getEntity().getLocation(),
								helm);
			}
			if (chest != null && chest.getType() != Material.AIR) {
				chest.setDurability((short) 0);
				// ee.setChestplate(chest);
				event.getEntity()
						.getWorld()
						.dropItemNaturally(event.getEntity().getLocation(),
								chest);
			}
			if (legs != null && legs.getType() != Material.AIR) {
				legs.setDurability((short) 0);
				// ee.setLeggings(legs);
				event.getEntity()
						.getWorld()
						.dropItemNaturally(event.getEntity().getLocation(),
								legs);
			}
			ee.clear();
		}
	}
}
