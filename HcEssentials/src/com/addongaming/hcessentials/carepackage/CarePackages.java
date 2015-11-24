package com.addongaming.hcessentials.carepackage;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.data.SpecialItems;
import com.addongaming.hcessentials.data.SpecialItems.SpecialTypes;
import com.addongaming.hcessentials.redeem.SyncInventory;
import com.addongaming.hcessentials.utils.Utils;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class CarePackages implements CommandExecutor, Listener, SubPlugin {
	private JavaPlugin jp;
	HashMap<Location, Date> cpLocs = new HashMap<Location, Date>();
	HashMap<String, Date> qlist = new HashMap<String, Date>();
	private int radius, offset, despawnTime, expDrop;
	private boolean specialWeapons, specialArmour = false;
	private String world;
	private boolean protfive;

	public CarePackages(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("carepackages.enabled", Boolean.valueOf(false));
		fc.addDefault("carepackages.minMins", 30);
		fc.addDefault("carepackages.maxMins", 40);
		fc.addDefault("carepackages.world", "world");
		fc.addDefault("carepackages.radius", 4000);
		fc.addDefault("carepackages.offset", 1000);
		fc.addDefault("carepackages.despawnmins", 120);
		fc.addDefault("carepackages.expdrop", 0);
		fc.addDefault("carepackages.protfive", Boolean.valueOf(false));
		fc.addDefault("carepackages.specialweapons", Boolean.valueOf(false));
		fc.options().copyDefaults(true);
		jp.saveConfig();
		this.jp = jp;
	}

	@EventHandler
	public void creeperExplode(EntityExplodeEvent event) {
		for (Iterator<Block> iter = event.blockList().iterator(); iter
				.hasNext();) {
			Block locOrig = iter.next();
			if (locOrig.getType() == Material.AIR)
				continue;
			for (Location loc : cpLocs.keySet()) {
				if (loc.equals(locOrig.getLocation()))
					iter.remove();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		for (Iterator<Location> iter = cpLocs.keySet().iterator(); iter
				.hasNext();) {
			Location loc = iter.next();
			loc.setY(255.0);
			Block b = loc.getBlock();
			while (((b = b.getRelative(BlockFace.DOWN))).getY() > 1) {
				if (b.getType() == Material.PISTON_BASE
						&& b.getData() == (byte) 6) {
					b.setType(Material.AIR);
					break;
				}
			}
			iter.remove();
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b.getType() == Material.PISTON_BASE && b.getData() == (byte) 6) {
			b.setType(Material.AIR);
			for (Iterator<Location> iter = cpLocs.keySet().iterator(); iter
					.hasNext();)
				if (iter.next().equals(b.getLocation())) {
					iter.remove();
					Bukkit.broadcastMessage("" + ChatColor.AQUA
							+ ChatColor.BOLD + event.getPlayer().getName()
							+ " claimed a carepackage!");
					event.setExpToDrop(new Random().nextInt(200));
					for (ItemStack is : randomise()) {
						b.getWorld().dropItemNaturally(b.getLocation(), is);
					}
					if (expDrop > 0)
						event.getPlayer().setLevel(
								event.getPlayer().getLevel() + expDrop);
				}
		}
	}

	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("carepackages.enabled")) {
			jp.getServer().getPluginManager().registerEvents(this, jp);
			jp.getCommand("cp").setExecutor(this);
			final int min = jp.getConfig().getInt("carepackages.minMins");
			final int max = jp.getConfig().getInt("carepackages.maxMins");
			radius = jp.getConfig().getInt("carepackages.radius");
			offset = jp.getConfig().getInt("carepackages.offset");
			world = jp.getConfig().getString("carepackages.world");
			protfive = jp.getConfig().getBoolean("carepackages.protfive");
			despawnTime = jp.getConfig().getInt("carepackages.despawnmins");
			specialWeapons = jp.getConfig().getBoolean(
					"carepackages.specialweapons");
			expDrop = jp.getConfig().getInt("carepackages.expdrop");
			si = loadInventory();
			jp.getServer().getScheduler()
					.scheduleSyncRepeatingTask(jp, new Runnable() {
						private long lastrun = 0;
						int ran = (new Random().nextInt(max - min)) + min;

						@Override
						public void run() {
							if (lastrun + (60000 * ran) < new Date().getTime()) {
								spawnCarepackage();
								lastrun = new Date().getTime();
								ran = (new Random().nextInt(max - min)) + min;
							}
						}
					}, 20 * 60, 20 * 60 * 1);
			if (despawnTime > 0) {
				jp.getServer().getScheduler()
						.scheduleSyncRepeatingTask(jp, new Runnable() {

							@Override
							public void run() {
								Iterator<Location> iter = cpLocs.keySet()
										.iterator();
								while (iter.hasNext()) {
									Location loc = iter.next();
									if (cpLocs.get(loc).before(new Date())) {
										loc.getBlock().setType(Material.AIR);
										iter.remove();
									}
								}

							}
						}, 20 * 60 * 20, 5 * 20 * 60);
			}
			return true;
		}
		return false;
	}

	private final void spawnCarepackage(final Location l) {
		l.getBlock().setType(Material.PISTON_BASE);
		l.getBlock().setData((byte) 6);
		cpLocs.put(l, new Date(new Date().getTime() + (despawnTime * 60000)));
		Location drop = l;
		// drop.setY(180);
		Firework fw = (Firework) l.getWorld().spawnEntity(l,
				EntityType.FIREWORK);
		FireworkMeta fmd = fw.getFireworkMeta();
		fmd.addEffects(new FireworkEffect[] { FireworkEffect.builder()
				.withColor(Color.ORANGE).with(FireworkEffect.Type.STAR).build() });
		fw.setFireworkMeta(fmd);
		Bukkit.broadcastMessage(""
				+ ChatColor.AQUA
				+ ChatColor.BOLD
				+ "Dropping carepackage at: X: "
				+ l.getBlockX()
				+ " Z: "
				+ l.getBlockZ()
				+ (this.world.equalsIgnoreCase("world") ? "" : " in world "
						+ this.world));

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg3.length == 0 || !arg0.isOp()) {
			if (cpLocs.isEmpty()) {
				arg0.sendMessage(ChatColor.RED
						+ "No carepackages are on the map.");
			} else {
				arg0.sendMessage(ChatColor.GREEN
						+ "List of carepackages"
						+ (this.world.equalsIgnoreCase("world") ? ""
								: " in world \"" + this.world + "\""));
				int counter = 1;
				for (Location l : cpLocs.keySet()) {
					arg0.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD
							+ "Carepackage " + counter++ + ":" + ChatColor.AQUA
							+ " X: " + l.getBlockX() + " Z: " + l.getBlockZ());
				}
			}
		} else if (arg0.isOp() && arg3.length > 0) {
			if (arg3[0].equalsIgnoreCase("spawn")) {
				if (arg3.length > 1 && arg3[1].equalsIgnoreCase("here")) {
					Player p = (Player) arg0;
					spawnCarepackage(p.getLocation());
				} else {
					spawnCarepackage();
				}

			} else if (arg3[0].equalsIgnoreCase("clear")) {
				onDisable();
			} else if (arg3[0].equalsIgnoreCase("setcpstuff")) {
				saveInventory(((Player) arg0).getInventory());
				arg0.sendMessage(ChatColor.GREEN
						+ "Saved carepackage inventory!");
			} else {
				for (SpecialTypes st : SpecialTypes.values())
					if (st.name().equalsIgnoreCase(arg3[0])) {
						ItemStack is = generateRandomWeapon(st);
						((Player) (arg0)).getInventory().addItem(is);
					}
			}
		}

		return true;
	}

	private void spawnCarepackage() {
		Point locP = getRandomPoint();
		Block loc = Bukkit.getWorld(world).getBlockAt(locP.x, 255, locP.y)
				.getLocation().getBlock();
		loc.getChunk().load();
		while (loc.getType() == Material.AIR || loc.isEmpty()) {
			loc = loc.getRelative(BlockFace.DOWN);
		}

		spawnCarepackage(loc.getRelative(BlockFace.UP).getLocation());
	}

	private Point getRandomPoint() {
		Random r = new Random();
		int x = r.nextInt(radius), y = r.nextInt(radius);
		if (x < offset && y < offset)
			if (r.nextInt(2) == 1)
				x += offset;
			else
				y += offset;
		if (r.nextInt(2) == 1) {
			x = -x;
		}
		if (r.nextInt(2) == 1) {
			y = -y;
		}
		return new Point(x, y);
	}

	SyncInventory si;

	private ItemStack[] randomise() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (ItemStack is : si.getContents())
			if (is != null && is.getType() != null
					&& is.getType() != Material.AIR && is.getAmount() > 1) {
				list.add(new ItemStack(is.getType(), new Random().nextInt(is
						.getAmount() - 1) + 1));
			}
		if (protfive) {
			ItemStack i;
			i = new ItemStack(Material.DIAMOND_HELMET);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
			list.add(i);
			i = new ItemStack(Material.DIAMOND_CHESTPLATE);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
			list.add(i);
			i = new ItemStack(Material.DIAMOND_LEGGINGS);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
			list.add(i);
			i = new ItemStack(Material.DIAMOND_BOOTS);
			i.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
			list.add(i);
		}
		if (specialWeapons) {
			int random = new Random().nextInt(10000) + 1;
			if (random >= 9900)
				list.add(generateRandomWeapon(SpecialTypes.UBER));
			else if (random >= 9250)
				list.add(generateRandomWeapon(SpecialTypes.LEGENDARY));
			else if (random >= 8000)
				list.add(generateRandomWeapon(SpecialTypes.SPECIAL));
			else if (random >= 5000)
				list.add(generateRandomWeapon(SpecialTypes.COMMON));
		}
		if (specialArmour) {
			int random = new Random().nextInt(10000) + 1;
		}
		return list.toArray(new ItemStack[list.size()]);
	}

	private SyncInventory loadInventory() {
		File file = new File(jp.getDataFolder() + File.separator + "SaveFiles"
				+ File.separator + "carepackages" + ".sav");
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		if (!file.exists()) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			ItemStack i = new ItemStack(Material.DIAMOND);
			int rand = new Random().nextInt(63);
			if (rand == 0) {
				rand = 1;
			}
			i.setAmount(rand);
			list.add(i);
			i = new ItemStack(Material.GOLDEN_APPLE);
			rand = new Random().nextInt(20);
			i.setAmount(rand);
			list.add(i);
			i = new ItemStack(Material.DIAMOND_BLOCK);
			rand = new Random().nextInt(10);
			i.setAmount(rand);
			list.add(i);
			i = new ItemStack(Material.GOLD_BLOCK);
			rand = new Random().nextInt(20);
			i.setAmount(rand);
			list.add(i);
			SyncInventory si = new SyncInventory(
					list.toArray(new ItemStack[list.size()]));
			Inventory inv = Bukkit.createInventory(null, 9 * 4);
			inv.setContents(si.getContents());
			saveInventory(inv);
			return si;
		}
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

	private void saveInventory(Inventory inv) {
		File file = new File(jp.getDataFolder() + File.separator + "SaveFiles"
				+ File.separator + "carepackages" + ".sav");
		SyncInventory si = new SyncInventory(inv.getContents());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(si);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to generate random weapon depending on the SpecialTypes enum given
	 * 
	 * @param st
	 *            SpecialType, the rarity of the item to drop.
	 * @return The item (weapon) that should be dropped
	 */
	private ItemStack generateRandomWeapon(SpecialTypes st) {
		ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(SpecialItems.getRandomWeaponName(st));
		HashMap<Enchantment, Integer> map = SpecialItems
				.getRandomWeaponEnchantments(st);
		for (Enchantment e : map.keySet())
			im.addEnchant(e, map.get(e), true);
		is.setItemMeta(im);
		return is;

	}

	private ItemStack generateRandomArmourPiece(SpecialTypes st) {
		Material[] mat = { Material.DIAMOND_HELMET,
				Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
				Material.DIAMOND_BOOTS };
		ItemStack is = SpecialItems.generateArmourPiece(
				mat[new Random().nextInt(mat.length)], st,
				SpecialItems.getRandomArmourFirstName(st));
		return is;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if (e.getPlayer().getWorld() != Bukkit.getWorld("world")) {
			qlist.put(e.getPlayer().getName(), new Date(new Date().getTime()
					+ (60000 * 5)));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (this.world.equalsIgnoreCase("world")
				|| !e.getPlayer().getWorld().getName()
						.equalsIgnoreCase(this.world))
			return;
		if (this.world.equalsIgnoreCase(e.getPlayer().getWorld().getName())) {
			if (tpToSpawn(e.getPlayer()) == true) {
				try {
					e.getPlayer()
							.teleport(
									HcEssentials.essentials.getWarps().getWarp(
											"spawn"));
				} catch (WarpNotFoundException | InvalidWorldException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (qlist.containsKey(e.getPlayer().getName()))
			qlist.remove(e.getPlayer().getName());
	}

	private boolean tpToSpawn(Player p) {
		if (!qlist.containsKey(p.getName())
				|| qlist.get(p.getName()).before(new Date()))
			return true;
		return false;

	}
}
