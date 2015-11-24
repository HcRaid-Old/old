package com.addongaming.hcessentials.minigames.games;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.events.TeamProtectEvent;
import com.addongaming.hcessentials.minigames.Minigame;
import com.addongaming.hcessentials.minigames.dtcmethods.DTCMethods;
import com.addongaming.hcessentials.redeem.SyncInventory;
import com.addongaming.hcessentials.teams.Team;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class DTC implements Minigame, Listener {
	private final JavaPlugin jp;
	private final List<String> list = new ArrayList<String>();
	private Location loc = null;
	private Team team;
	String title = ChatColor.BLUE + "[" + ChatColor.GREEN + "HcDTC"
			+ ChatColor.BLUE + "]" + " " + ChatColor.RESET;
	private boolean dtcready = false;
	private boolean setwarp = false;
	private boolean started = false;
	public List<String> chatToggled = new ArrayList<String>();
	private DTCMethods ins;
	List<Location> chestLocations = new ArrayList<Location>();
	LocationZone re;
	private int timer;

	public DTC(JavaPlugin jp) {
		this.jp = jp;
	}

	File folder;

	SyncInventory armour;
	SyncInventory items;
	SyncInventory defarmour;
	SyncInventory defitems;
	SyncInventory chest;

	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		if (list.contains(event.getEntity().getName())) {
			list.remove(event.getEntity().getName());
		}
	}

	public String getChannel(String name) {
		if (chatToggled.contains(name))
			return "DTC";
		else {
			return null;
		}
	}

	@Override
	public boolean onEnable() {
		load();
		folder = new File(jp.getDataFolder() + File.separator + "DTC");
		if (!folder.exists())
			folder.mkdirs();
		loadConfig();
		if (!jp.getConfig().getBoolean("minigames.dtc.enabled"))
			return false;
		setTimer(jp.getConfig().getInt("minigames.dtc.timer"));
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	private boolean save(final Object obj, final String path) {
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Object load(final String path) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(path));
			final Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private void defload() {
		defarmour = (SyncInventory) load(folder + File.separator
				+ "defarmour.sav");
		defitems = (SyncInventory) load(folder + File.separator
				+ "defitems.sav");
	}

	private void load() {
		armour = (SyncInventory) load(folder + File.separator + "armour.sav");
		items = (SyncInventory) load(folder + File.separator + "items.sav");
	}

	public void cload() {
		chest = (SyncInventory) load(folder + File.separator + "chests.sav");
	}

	private void csave() {
		save(chest, folder + File.separator + "chests.sav");
	}

	private void save() {
		save(armour, folder + File.separator + "armour.sav");
		save(items, folder + File.separator + "items.sav");
	}

	private void defsave() {
		save(defarmour, folder + File.separator + "defarmour.sav");
		save(defitems, folder + File.separator + "defitems.sav");
	}

	private boolean checkTeam(CommandSender cd) {
		if (team == null) {
			if (cd.isOp() || cd.hasPermission("hcraid.minigames"))
				cd.sendMessage(title
						+ "DTC is not ready, please use /dtc setup");
			return true;
		}
		return false;
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerCommand(
			org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
		if (event.isCancelled() && team != null) {
			if (team.containsPlayer(event.getPlayer().getName().toLowerCase())) {
				if (event.getMessage().startsWith("/tp")) {
					event.setCancelled(false);
					System.out.println("Uncancelling "
							+ event.getPlayer().getName() + "'s "
							+ event.getMessage());
				}
			}
		}
	}

	@EventHandler
	public void teamPlayerDamaged(EntityDamageByEntityEvent event) {
		if (team == null)
			return;
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			System.out.println("Both entities are players");
			String dmgr = ((Player) event.getDamager()).getName().toLowerCase();
			String dmge = ((Player) event.getEntity()).getName().toLowerCase();
			System.out.println("Damager: " + dmgr + "  Damagee: " + dmge);
			if (team.containsPlayer(dmgr) && team.containsPlayer(dmge)) {
				System.out.println("Both players are in team team.");
				TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, team);
				Bukkit.getPluginManager().callEvent(ev);
				System.out.println("TeamProtectEvent is cancelled: "
						+ ev.isCancelled());
				if (!ev.isCancelled())
					event.setCancelled(true);
				return;
			}
		} else if (event.getDamager() instanceof Arrow
				&& event.getEntity() instanceof Player) {
			Arrow arr = (Arrow) event.getDamager();
			if (arr.getShooter() instanceof Player) {
				String dmgr = ((Player) arr.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (team.containsPlayer(dmgr) && team.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, team);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;
				}
			}
		} else if (event.getDamager() instanceof org.bukkit.entity.ThrownPotion
				&& event.getEntity() instanceof Player) {
			ThrownPotion tp = (ThrownPotion) event.getDamager();
			if (tp.getShooter() instanceof Player) {
				String dmgr = ((Player) tp.getShooter()).getName()
						.toLowerCase();
				String dmge = ((Player) event.getEntity()).getName()
						.toLowerCase();
				if (team.containsPlayer(dmgr) && team.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, team);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;

				}
			}
		}
	}

	@Override
	public String getMinigameName() {
		return "dtc";
	}

	@Override
	public boolean isInGame(String str) {
		if (started == true) {
			return true;
		}
		return false;
	}

	@Override
	public void commandIssued(CommandSender sender, String[] args) {
		if (args.length == 0) {
			Player p = (Player) sender;
			p.sendMessage(title + " Please choose from the following:");
			p.sendMessage("     " + "/dtc - Lists DTC commands.");
			p.sendMessage("     " + "/dtc join - Joins the DTC.");
			if ((team != null && team.containsPlayer(p.getName().toLowerCase()))
					|| p.isOp()) {
				p.sendMessage("     "
						+ "/dtc defender - Teleports defenders to location and gives kit.");
				p.sendMessage("     " + "/dtc deflist -  Shows all defenders.");
				p.sendMessage("     " + "/dtc c - Toggles DTC chat.");

			}
			if (p.isOp() || sender.hasPermission("hcraid.minigames")) {
				p.sendMessage("     " + "/dtc setup - Sets up the DTC team.");
				p.sendMessage("     " + "/dtc set - Sets the DTC location.");
				p.sendMessage("     " + "/dtc start - Allows /dtc join.");
				p.sendMessage("     " + "/dtc unset - Nulls the DTC location.");
				p.sendMessage("     " + "/dtc load - Loads the player kit.");
				p.sendMessage("     " + "/dtc save - Saves the player kit.");
				p.sendMessage("     "
						+ "/dtc setwarp - Allows/Disallows to set warp.");

				p.sendMessage("     "
						+ "/dtc defload - Loads the defender kit.");
				p.sendMessage("     "
						+ "/dtc defsave - Saves the defender kit.");
				p.sendMessage("     " + "/dtc selregion - Selects region.");
				p.sendMessage("     " + "/dtc setflags - Sets region flags.");
				p.sendMessage("     " + "/dtc chest - Saves chest locations.");
				p.sendMessage("     " + "/dtc chestsave - Saves loot chests.");
				p.sendMessage("     "
						+ "/dtc chestload - Loads loot into chests.");
				p.sendMessage("     " + "/dtc defadd - Adds a defender.");
				p.sendMessage("     " + "/dtc defdel - Removes a defender.");
				p.sendMessage("     " + "/dtc defset - Sets defender location.");
				p.sendMessage("     "
						+ "/dtc defunset - Nulls defender location.");
				p.sendMessage("     "
						+ "/dtc defclear - Clears an itemstack from inventory.");
				p.sendMessage("     "
						+ "/dtc clear - Clears all DTC information.");
				p.sendMessage("     " + "/dtc end - Alias of clear.");

			}
			p.sendMessage(title);
			return;
		}
		switch (args[0]) {

		case "set":
			if (loc == null && args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				loc = p.getLocation();
				sender.sendMessage(title + "Location set.");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "unset":
			if (loc != null && args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				loc = null;
				sender.sendMessage(title + "Location unset.");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "load":
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				p.getInventory().setArmorContents(armour.getContents());
				p.getInventory().setContents(items.getContents());
				p.sendMessage(title + "DTC kit loaded");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "save":
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				armour = new SyncInventory(p.getInventory().getArmorContents());
				items = new SyncInventory(p.getInventory().getContents());
				p.sendMessage(title + "DTC kit saved.");
				save();
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "setwarp":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				if (setwarp == false) {
					setwarp = true;
					sender.sendMessage(title + "/warp dtc allowed.");
				} else {
					setwarp = false;
					sender.sendMessage(title + "/warp dtc not allowed.");
				}
			}
			break;
		case "setup":
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player player = (Player) sender;
				team = new Team(player.getName().toLowerCase(), "DTC", null);
				player.sendMessage(title + "Set-up team");
			}
			break;
		case "end":
		case "clear":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				this.list.clear();
				this.team = null;
				this.loc = null;
				this.started = false;
				this.setwarp = false;
				this.dtcready = false;
				for (Player pl : Bukkit.getOnlinePlayers()) {
					HcEssentials.permission.playerAdd(pl, "hcraid.defender");
				}
				try {
					HcEssentials.essentials.getWarps().removeWarp("dtc");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				sender.sendMessage(title + "Cleared DTC info.");
			} else {
				sender.sendMessage(title + "Sorry, you cannot execute this.");
			}
			break;
		case "deflist":
			if (checkTeam(sender))
				return;
			if (team.containsPlayer(sender.getName().toLowerCase())
					|| sender.isOp()) {
				Player p = (Player) sender;
				String[] text = team.getListedPlayers();
				if (text[0].length() < 3)
					text[0] = title + "There are no defenders online.";
				else
					text[0] = title + "Online Defenders: " + text[0];
				if (text[1].length() < 3)
					text[1] = title + "All defenders are online.";
				else
					text[1] = title + "Offline Defenders: " + text[1];
				p.sendMessage(text);
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "defadd":
			if (checkTeam(sender))
				return;
			if (args.length == 2 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				if (team.containsPlayer(args[1].toLowerCase())) {
					sender.sendMessage(title + "Player is already a defender.");
					return;
				}
				Player p = (Player) sender;
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.equals(args[1])) {
						HcEssentials.permission
								.playerAdd(pl, "hcraid.defender");
					}
				}
				team.addPlayer(args[1].toLowerCase());
				p.sendMessage(title + "Defender added.");
			} else if (args.length != 2 || !sender.isOp()) {
				Player p = (Player) sender;
				p.sendMessage(title + "You cannot run this command.");
			}
			break;
		case "defdel":
			if (checkTeam(sender))
				return;
			if (args.length == 2 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				if (team.containsPlayer(args[1].toLowerCase())) {
					team.removePlayer(args[1].toLowerCase());
					sender.sendMessage(title + "Defender removed.");
				} else if (args.length != 2 || !sender.isOp()) {
					sender.sendMessage(title + "You cannot run this command.");
				}
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.equals(args[1])) {
						HcEssentials.permission.playerRemove(pl,
								"hcraid.defender");
					}
				}
			}
		case "defender":
			if (checkTeam(sender))
				return;
			if (args.length != 0) {
				Player p = (Player) sender;
				if (team == null) {
					sender.sendMessage(title
							+ "DTC isn't ready, please use /dtc setup");
					return;
				}
				if (p.isOp() && !team.containsPlayer(p.getName().toLowerCase()))
					team.addPlayer(p.getName().toLowerCase());
				if (team.containsPlayer(p.getName().toLowerCase())) {
					if (team.getHome() != null) {
						p.getInventory().setArmorContents(
								defarmour.getContents());
						p.getInventory().setContents(defitems.getContents());
						defload();
						p.teleport(team.getHome());
					} else {
						p.sendMessage(title + "Defender spawn is not set.");
					}
				} else {
					p.sendMessage(title + "Sorry, you are not a defender.");
				}
			}
			break;
		case "defset":
			if (checkTeam(sender))
				return;
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				team.setHome(p.getLocation());
				p.sendMessage(title + "Defender location set.");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}

			}
			break;
		case "defunset":
			if (checkTeam(sender))
				return;
			if (team != null && team.getHome() != null && args.length != 0
					&& sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				team.setHome(null);
				sender.sendMessage(title + "Defender location unset.");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "defload":
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				p.getInventory().setArmorContents(defarmour.getContents());
				p.getInventory().setContents(defitems.getContents());
				defload();
				p.sendMessage(title + "Defender kit loaded.");
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "selregion":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Plugin pl = Bukkit.getServer().getPluginManager()
						.getPlugin("WorldEdit");
				if (pl != null) {
					WorldEditPlugin wep = (WorldEditPlugin) pl;
					Selection se = wep.getSelection((Player) sender);
					if (se == null || se.getMinimumPoint() == null
							|| se.getMaximumPoint() == null) {
						sender.sendMessage(title
								+ "WorldEdit region is empty or doesn't contain both points.");
						return;
					}
					re = new LocationZone(se.getMinimumPoint(),
							se.getMaximumPoint());
					sender.sendMessage(title + "Region selected.");
				}
			}
			break;
		case "setflags":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player plp = (Player) sender;
				World w = plp.getWorld();
				RegionManager manager = HcEssentials.worldGuard
						.getRegionManager(w);
				Location minvec = re.getMin();
				Location maxvec = re.getMax();
				Vector mivec = BukkitUtil.toVector(minvec);
				Vector mavec = BukkitUtil.toVector(maxvec);
				BlockVector minbv = new BlockVector(mivec);
				BlockVector maxbv = new BlockVector(mavec);
				ProtectedRegion dpr = new ProtectedCuboidRegion("dtc", minbv,
						maxbv);
				manager.addRegion(dpr);
				StateFlag build = com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD;
				SetFlag<String> cmd = com.sk89q.worldguard.protection.flags.DefaultFlag.ALLOWED_CMDS;
				StateFlag spawn = com.sk89q.worldguard.protection.flags.DefaultFlag.MOB_SPAWNING;
				try {
					dpr.setFlag(build, build.parseInput(
							HcEssentials.worldGuard, sender, "deny"));

				} catch (InvalidFlagFormat e) {
					e.printStackTrace();
				}
				try {
					dpr.setFlag(
							cmd,
							cmd.parseInput(HcEssentials.worldGuard, sender,
									"/home, /spawn, /team, /team c, /team chat, /team invite, /return"));
				} catch (InvalidFlagFormat e) {
					e.printStackTrace();
				}
				try {
					dpr.setFlag(spawn, spawn.parseInput(
							HcEssentials.worldGuard, sender, "deny"));
				} catch (InvalidFlagFormat e) {
					e.printStackTrace();
				}
				sender.sendMessage(title + "Flags set.");
			}
			break;
		case "chestload":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				for (Block block : re.getAllBlocks()) {
					if (block.getState() != null
							&& block.getState() instanceof InventoryHolder) {
						((InventoryHolder) block.getState()).getInventory()
								.setContents(chest.getContents());
						sender.sendMessage(title + "Chests loaded.");
					}
				}
			}
			break;
		case "chestsave":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				chest = new SyncInventory(p.getInventory().getContents());
				csave();
				sender.sendMessage(title + "Chests saved.");
			}
			break;
		case "chest":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				for (Block b : re.getAllBlocks()) {
					if (b.getState() != null
							&& b.getState() instanceof InventoryHolder) {
						chestLocations.add(b.getLocation());
						sender.sendMessage(title + "Locations saved.");
					}
				}
			}
			break;
		case "defsave":
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				defarmour = new SyncInventory(p.getInventory()
						.getArmorContents());
				defitems = new SyncInventory(p.getInventory().getContents());
				p.sendMessage(title + "Defender kit saved.");
				defsave();
			} else {
				Player p = (Player) sender;
				if (!(p.isOp())) {
					p.sendMessage(title + "You cannot run this command.");
				}
			}
			break;
		case "start":
			if (sender.isOp() || sender.hasPermission("hcraid.minigames")) {
				dtcready = true;
				sender.sendMessage(title + "DTC started.");
				Bukkit.broadcastMessage(title + "/dtc join to join the DTC!");
				if (setwarp == true) {
					try {
						HcEssentials.essentials.getWarps().setWarp("dtc", loc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				started = true;
			}
			break;
		case "defclear":
			if (checkTeam(sender))
				return;
			if (args.length != 0 && sender.isOp()
					|| sender.hasPermission("hcraid.minigames")) {
				Player p = (Player) sender;
				if (args.length < 1) {
					p.sendMessage(title + "/dtc defclear <id>");
					break;
				}
				if (!isInteger(args[1])) {
					p.sendMessage(title + "Please use a valid ID.");
					break;
				}
				int id = Integer.parseInt(args[1]);
				@SuppressWarnings("deprecation")
				Material mat = Material.getMaterial(id);
				if (mat == null)
					p.sendMessage(title + "Please use a valid ID.");
				for (String defender : team.getMembers()) {
					@SuppressWarnings("deprecation")
					Player play = Bukkit.getPlayer(defender);
					if (play != null && play.isOnline()) {
						play.getInventory().remove(mat);
					}
				}
				return;
			}
			break;
		case "c":
		case "chat":
			if (args.length != 0 && sender.hasPermission("hcraid.defender")) {
				Player p = (Player) sender;
				if (chatToggled.contains(sender.getName())) {
					sender.sendMessage(title + "DTC chat is now off!");
					chatToggled.remove(sender.getName());
					return;
				} else {
					if (getChannel(p.getName()) != null)
						return;
					sender.sendMessage(title + "DTC chat is now on!");
					chatToggled.add(sender.getName());
					return;
				}
			} else {
				sender.sendMessage(title + "An error has ocurred.");
			}
			break;
		case "join":
			if (dtcready == true) {
				if (!(sender instanceof Player) || loc == null)
					return;
				final Player p = (Player) sender;
				if (!list.contains(sender.getName())) {
					for (ItemStack is : p.getInventory().getContents())
						if (is != null && is.getType() != Material.AIR) {
							p.sendMessage(title
									+ "Please make sure your inventory is empty.");
							return;
						}
					for (ItemStack is : p.getInventory().getArmorContents())
						if (is != null && is.getType() != Material.AIR) {
							p.sendMessage(title
									+ "Please make sure your inventory is empty.");
							return;
						}
					p.getInventory().setArmorContents(armour.getContents());
					p.getInventory().setContents(items.getContents());
					jp.getServer().getScheduler()
							.scheduleSyncDelayedTask(jp, new Runnable() {

								@Override
								public void run() {
									p.sendMessage(title
											+ "Enjoy your kit, Good luck.");
								}
							}, 0l);
					list.add(p.getName());
				}
				p.teleport(loc);
			}
			if (dtcready == false) {
				sender.sendMessage(title + "DTC not set!");
			}
		}

		return;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		if (getChannel(e.getPlayer().getName()) != null) {
			e.setCancelled(true);
			if (chatToggled.contains(e.getPlayer().getName())) {
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.hasPermission("hcraid.defender")) {
						p.sendMessage(title + " <"
								+ e.getPlayer().getDisplayName() + "> "
								+ e.getMessage());
					}
			}
		}
	}

	@Override
	public String getMinigameNotation() {
		return getMinigameName();

	}

	@Override
	public void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("minigames.dtc.enabled", false);
		fc.addDefault("minigames.dtc.timer", 2);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	public boolean hasStarted() {
		return started;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public String getTitle() {
		return title;
	}

	public SyncInventory getChest() {
		return chest;
	}

	public LocationZone getLocationZone() {
		return re;
	}

	public List getLocations() {
		return chestLocations;
	}
}
