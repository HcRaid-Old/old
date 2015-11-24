package com.addongaming.hcessentials.redeem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.logging.DataLog;

public class Redeem implements Listener, CommandExecutor, SubPlugin {
	public static Economy economy = null;
	public static String errorString = "";
	public static Permission permission = null;
	public static boolean working;
	private final JavaPlugin jp;
	private DataLog dl;

	public Redeem(JavaPlugin jp) {
		this.jp = jp;
	}

	/**
	 * claimedMap String - The users name List<String> - Timestamps of the
	 * player
	 */
	private HashMap<String, List<SyncRedeemTimestamp>> claimedMap = new HashMap<String, List<SyncRedeemTimestamp>>();
	/**
	 * expMap String - Group name Integer - amount of exp
	 */
	private HashMap<String, Integer> expMap = new HashMap<String, Integer>();
	/**
	 * groups List of all the groups that can be redeemed
	 */
	private List<String> groups = new ArrayList<String>();
	/**
	 * itemMap
	 * 
	 * @key The key is the group name
	 * @value The value is the inventory (stacked items) the user will get
	 */
	private HashMap<String, SyncInventory> itemMap = new HashMap<String, SyncInventory>();
	File itemMapFile, claimedMapFile, moneyMapFile, groupsFile,
			itemRedeemMapFile, expMapFile;
	private HashMap<String, SyncItemStack> itemRedeemMap = new HashMap<String, SyncItemStack>();
	/**
	 * moneyMap String - Group name Integer - Amount of money
	 */
	private HashMap<String, Integer> moneyMap = new HashMap<String, Integer>();

	private final String negTitle = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "StrafeRedeem" + ChatColor.DARK_RED + "] " + ChatColor.RED;

	private final String perm = "HcRaid.";

	private final String posTitle = ChatColor.GOLD + "[" + ChatColor.BLUE
			+ "StrafeRedeem" + ChatColor.GOLD + "] " + ChatColor.BLUE;

	private PrintWriter pw;

	@SuppressWarnings("deprecation")
	private void addItemToMap(String group, int id) {
		ItemStack is = new ItemStack(id, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + group + " redeem for ");
		List<String> al = new ArrayList<String>();
		al.add("Please right click to get your kit.");
		al.add("Make sure you have enough inventory spaces!");
		im.setLore(al);
		is.setItemMeta(im);
		itemRedeemMap.put(group, new SyncItemStack(is));
	}

	@SuppressWarnings("deprecation")
	private boolean checkItemRemoval(ItemStack istack) {
		for (String currentGroup : itemRedeemMap.keySet()) {
			if (istack == null || istack.getItemMeta() == null
					|| istack.getItemMeta().getDisplayName() == null)
				return false;
			SyncItemStack stackList = itemRedeemMap.get(currentGroup);
			ItemStack temp = stackList.getBukkitItemStack();
			if (temp.getTypeId() == istack.getTypeId()
					&& istack.getItemMeta().getDisplayName()
							.contains(temp.getItemMeta().getDisplayName())) {

				String[] pl = istack.getItemMeta().getDisplayName().split(" ");
				String p = pl[pl.length - 1];
				Player player = Bukkit.getPlayer(UUID.fromString(p));
				List<SyncRedeemTimestamp> tempList = claimedMap.get(player
						.getUniqueId().toString());
				List<SyncRedeemTimestamp> newList = new ArrayList<SyncRedeemTimestamp>();
				for (SyncRedeemTimestamp srt : tempList) {
					if (!srt.getGroup().equalsIgnoreCase(currentGroup)) {
						System.out.println("Adding group: " + srt.getGroup()
								+ "  currentgroup: " + currentGroup);
						newList.add(srt);
					} else if (srt.getDateUsed() == null) {
						if (player != null)
							sendMessage(player, "Looks like you lost your "
									+ currentGroup
									+ " kit! Use /redeem to get it again!",
									false);
					} else {
						newList.add(srt);
						sendMessage(player, "You have already redeemed your "
								+ currentGroup + " kit!", false);
					}
				}
				claimedMap.put(p, newList);
				saveInfo();
				// The end!
				return true;
			}
		}
		return false;
	}

	private boolean checkRedeem(Player sender, String[] args) {
		if (args.length != 1) {
			sendMessage(sender, "Please use /checkredeem <name>", false);
			return true;
		}
		SimpleDateFormat SDF = new SimpleDateFormat("MM-dd hh:mm:ss");
		String str = args[0];
		OfflinePlayer op = Bukkit.getOfflinePlayer(str);
		if (!claimedMap.containsKey(op.getUniqueId().toString())) {
			sendMessage(
					sender,
					"Name not found in item claim. Did they lose it? Use /redeem again",
					false);
			return true;
		}
		sendMessage(sender, "Player " + str + "'s redeem info.", true);
		sendMessage(sender, "UUID: " + op.getUniqueId().toString(), true);
		for (SyncRedeemTimestamp srt : claimedMap.get(op.getUniqueId()
				.toString())) {
			boolean used = srt.getDateUsed() != null;
			if (used) {
				sendMessage(
						sender,
						"Group: " + srt.getGroup() + " Recieved item at: "
								+ SDF.format(srt.getDateClaimed())
								+ " and used the item at: "
								+ SDF.format(srt.getDateUsed()), true);
			} else {
				sendMessage(sender,
						"Group: " + srt.getGroup() + " Recieved item at: "
								+ SDF.format(srt.getDateClaimed())
								+ " and hasn't used their item", true);
			}
		}

		return true;
	}

	private boolean delRedeem(Player sender, String[] args) {
		if (args.length != 1 && args.length != 2) {
			sendMessage(sender, "Please use /delredeem <name> [group]", false);
			return true;
		}
		String str = args[0];
		OfflinePlayer op = Bukkit.getOfflinePlayer(str);
		if (claimedMap.containsKey(op.getUniqueId().toString())) {
			if (args.length > 1) {
				for (Iterator<SyncRedeemTimestamp> it = claimedMap.get(
						op.getUniqueId().toString()).iterator(); it.hasNext();)
					if (it.next().getGroup().equalsIgnoreCase(args[1])) {
						it.remove();
						sendMessage(sender, args[0] + " has had their "
								+ args[1] + " redeem reset", true);
					}
			} else {
				claimedMap.remove(op.getUniqueId().toString());
				sendMessage(sender, args[0] + " has had their redeem reset",
						true);
			}
			saveInfo();
			return true;
		} else {
			sendMessage(sender, args[0]
					+ " player not found, try /checkredeem <name> ", false);
			return true;
		}
	}

	private void giveMoney(Player p, int money) {
		dl.logPlayer(
				p,
				"Ending balance: "
						+ HcEssentials.economy.getBalance(p.getName()));
		HcEssentials.economy.depositPlayer(p.getName(), money);
		dl.logPlayer(p,
				"End balance: " + HcEssentials.economy.getBalance(p.getName()));
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getType() != InventoryType.PLAYER
				&& event.getCurrentItem() != null)
			if (checkItemRemoval(event.getCurrentItem())) {
				pw.println(event.getWhoClicked().getName()
						+ " player tried to put the item "
						+ event.getCurrentItem().getItemMeta().getDisplayName()
						+ " in a chest");
				event.setCurrentItem(new ItemStack(Material.AIR));
			}
	}

	private Object load(final String path) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	private boolean loadRedeem(Player sender, String[] args) {
		if (args.length != 1) {
			sendMessage(sender, "Please use /loadredeem <group>", false);
			return true;
		}
		for (String group : groups) {
			String gr = args[0];
			if (group.equalsIgnoreCase(gr)) {
				SyncInventory si = itemMap.get(group);
				sender.getInventory().setContents(si.getContents());
				sendMessage(sender, "Loaded kit: " + group, true);
				return true;
			}
		}
		sendMessage(sender, "Group not found: " + args[0], false);
		return true;
	}

	/**
	 * Commands:
	 * 
	 * @param command
	 *            redeem loadreward savereward <group> <id> <money> <exp>
	 *            checkredeems
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		if (cmd.equalsIgnoreCase("delredeemkit")) {
			if (sender.isOp()) {
				if (args.length == 0) {
					sendMessage((Player) sender,
							"Please use /delredeemkit <kitname>", false);
					return true;
				}
				String str = args[0];
				itemMap.remove(str);
				expMap.remove(str);
				moneyMap.remove(str);
				String toRemove = null;
				for (String stri : itemRedeemMap.keySet())
					if (ChatColor.stripColor(stri.split(" ")[0])
							.equalsIgnoreCase(stri))
						toRemove = stri;
				itemRedeemMap.remove(toRemove);
				saveInfo();
				System.out.println("Removed " + str);

			} else
				return true;
		} else if (cmd.equalsIgnoreCase("redeem")) {
			if (!working) {
				sendMessage((Player) sender, errorString, false);
				return true;
			}
			return redeem((Player) sender);
		} else if (cmd.equalsIgnoreCase("loadredeem")) {
			if (sender.isOp())
				return loadRedeem((Player) sender, args);
			else {
				sendMessage((Player) sender,
						"You don't have permission to do this.", false);
				return true;
			}
		} else if (cmd.equalsIgnoreCase("delredeem")) {
			if (sender.isOp()) {
				if (args[0].equalsIgnoreCase("full")) {
					return delRedeemFromSystem(sender, args[1]);
				} else
					return delRedeem((Player) sender, args);
			} else {
				sendMessage((Player) sender,
						"You don't have permission to do this.", false);
				return true;
			}
		} else if (cmd.equalsIgnoreCase("saveredeem")) {
			System.out.println("Save reward triggered");
			if (sender.isOp())
				return saveReward((Player) sender, args);
			else {
				sendMessage((Player) sender,
						"You don't have permission to do this.", false);
				return true;
			}

		} else if (cmd.equalsIgnoreCase("checkredeem")) {
			if (sender.isOp())
				return checkRedeem((Player) sender, args);
			else {
				sendMessage((Player) sender,
						"You don't have permission to do this.", false);
				return true;
			}
		}
		return true;
	}

	private boolean delRedeemFromSystem(CommandSender sender, String string) {
		if (!groups.contains(string)) {
			StringBuilder sb = new StringBuilder();
			for (String str : groups)
				sb.append(str + ", ");
			sender.sendMessage(negTitle + "Group " + string
					+ " not found. Groups are: " + sb.toString());
			return false;
		}
		groups.remove(string);
		moneyMap.remove(string);
		expMap.remove(string);
		saveInfo();
		sender.sendMessage(posTitle + "Sucessfully deleted group: " + string);
		return true;
	}

	@Override
	public void onDisable() {
		this.pw.close();

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onEnable() {
		dl = HcEssentials.getDataLogger().addLogger("Redeem");
		try {
			if (!setupConfig()) {
				dl.log("Not loading.");
				return false;
			}
			setupEconomy();
			if (!setupPermissions())
				return false;
			jp.getCommand("redeem").setExecutor(this);
			jp.getCommand("saveredeem").setExecutor(this);
			jp.getCommand("loadredeem").setExecutor(this);
			jp.getCommand("delredeem").setExecutor(this);
			jp.getCommand("checkredeem").setExecutor(this);
			jp.getCommand("delredeemkit").setExecutor(this);

			File folder = new File(jp.getDataFolder() + File.separator
					+ "redeem");
			if (!folder.exists())
				folder.mkdirs();
			folder = jp.getDataFolder();
			itemMapFile = new File(folder + File.separator + "redeem"
					+ File.separator + "itemMapFile.hcrdm");
			claimedMapFile = new File(folder + File.separator + "redeem"
					+ File.separator + "claimedMapFile.hcrdm");
			moneyMapFile = new File(folder + File.separator + "redeem"
					+ File.separator + "moneyMapFile.hcrdm");
			groupsFile = new File(folder + File.separator + "redeem"
					+ File.separator + "groupsFile.hcrdm");
			itemRedeemMapFile = new File(folder + File.separator + "redeem"
					+ File.separator + "itemRedeemMapFile.hcrdm");
			expMapFile = new File(folder + File.separator + "redeem"
					+ File.separator + "expMapFile.hcrdm");
			File log = new File(folder + File.separator + "redeem"
					+ File.separator + "log.txt");
			boolean flag = false;
			if (itemMapFile.exists()) {
				itemMap = (HashMap<String, SyncInventory>) load(itemMapFile
						.getAbsolutePath());
			} else {
				flag = true;
			}
			if (expMapFile.exists()) {
				expMap = (HashMap<String, Integer>) load(expMapFile
						.getAbsolutePath());
			} else {
				flag = true;
			}
			if (claimedMapFile.exists()) {
				claimedMap = (HashMap<String, List<SyncRedeemTimestamp>>) load(claimedMapFile
						.getAbsolutePath());
			} else {
				flag = true;
			}
			if (moneyMapFile.exists()) {
				moneyMap = (HashMap<String, Integer>) load(moneyMapFile
						.getAbsolutePath());
			} else {
				flag = true;
			}
			if (groupsFile.exists()) {
				groups = (List<String>) load(groupsFile.getAbsolutePath());
			} else {
				flag = true;
			}
			if (itemRedeemMapFile.exists()) {
				itemRedeemMap = (HashMap<String, SyncItemStack>) load(itemRedeemMapFile
						.getAbsolutePath());
			} else {
				flag = true;
			}
			if (!log.exists())
				log.createNewFile();
			if (flag)
				saveInfo();
			pw = new PrintWriter(new BufferedWriter(new FileWriter(log, true)));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler
	public void playerDropEvent(ItemSpawnEvent event) {
		boolean b = checkItemRemoval(event.getEntity().getItemStack());
		if (b) {
			pw.println("Tried to drop the item "
					+ event.getEntity().getItemStack().getItemMeta()
							.getDisplayName());

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			if (checkItemRemoval(event.getPlayer().getItemInHand())) {
				event.setCancelled(true);
				event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				event.getPlayer().updateInventory();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = event.getPlayer();
			ItemStack is = p.getItemInHand();
			if (is == null || is.getItemMeta() == null
					|| is.getItemMeta().getDisplayName() == null)
				return;
			for (String currentGroup : itemRedeemMap.keySet()) {
				SyncItemStack stackList = itemRedeemMap.get(currentGroup);
				ItemStack temp = stackList.getBukkitItemStack();
				if (temp.getTypeId() == is.getTypeId()
						&& is.getItemMeta().getDisplayName()
								.contains(temp.getItemMeta().getDisplayName())) {
					String pl[] = is.getItemMeta().getDisplayName().split(" ");
					String playersUUID = pl[pl.length - 1];
					if (p.getUniqueId().toString()
							.equalsIgnoreCase(playersUUID)) {
						pw.println("Player: " + p.getName()
								+ " tried to redeem " + playersUUID
								+ "'s redeem kit: "
								+ is.getItemMeta().getDisplayName());
					}
					event.setCancelled(true);
					SyncInventory si = itemMap.get(currentGroup);
					int amount = 0;
					dl.log("-----------------------", p.getName() + "inventory");
					dl.log("Start items", p.getName() + "inventory");
					for (ItemStack tempStack2 : si.getContents())
						if (tempStack2 != null) {
							amount++;
							dl.log(tempStack2.getType().name() + " #"
									+ tempStack2.getAmount(), p.getName()
									+ "inventory");
						}
					dl.log("-----------------------", p.getName() + "inventory");
					PlayerInventory pi = p.getInventory();
					int freeSlots = 0;
					for (ItemStack tempStack1 : pi.getContents())
						if (tempStack1 == null)
							freeSlots++;
						else if (tempStack1.getType() == Material.AIR)
							freeSlots++;
					if (amount > freeSlots) {
						sendMessage(p,
								"You do not have enough space in your inventory. You need "
										+ (amount - freeSlots)
										+ " more empty slots", false);
						final Player play = p;
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(jp, new Runnable() {

									@Override
									public void run() {
										play.updateInventory();
									}
								}, 1);
						return;
					}
					SimpleDateFormat sdf = new SimpleDateFormat(
							"MM-dd hh:mm:ss");
					List<SyncRedeemTimestamp> tempList = claimedMap.get(p
							.getUniqueId().toString());
					List<SyncRedeemTimestamp> newList = new ArrayList<SyncRedeemTimestamp>();
					boolean addedRight = false;
					for (SyncRedeemTimestamp srt : tempList) {
						if (srt.getGroup().equalsIgnoreCase(currentGroup)) {
							if (srt.getDateUsed() != null) {
								sendMessage(
										p,
										"It seems you have used this kit before. If this is an error please report.",
										false);
								pw.println("Player: "
										+ p.getName()
										+ " tried to get their kit at: "
										+ sdf.format(new Date())
										+ " but seems to have already redeemed at : "
										+ sdf.format(srt.getDateUsed()));
								p.setItemInHand(new ItemStack(Material.AIR));
								return;
							}
							srt.setDateUsed(new Date().getTime());
							newList.add(srt);
							addedRight = true;
						} else
							newList.add(srt);
					}
					if (!addedRight) {
						sendMessage(
								p,
								"Sorry your name isn't in the item claim. Please do /redeem again.",
								false);
						return;
					}
					pw.println("Player: "
							+ p.getName()
							+ " is redeeming their items, they are as follows: ");
					dl.logPlayer(event.getPlayer(), "Is redeeming "
							+ currentGroup + " redeems.");
					for (ItemStack newItems : si.getContents())
						if (newItems != null
								&& newItems.getType() != Material.AIR) {
							p.getInventory().addItem(newItems);
							StringBuilder sb = new StringBuilder();
							sb.append(newItems.getType().name() + " AMNT "
									+ newItems.getAmount() + " DURA "
									+ newItems.getDurability() + " ---- ");
							if (!newItems.getEnchantments().isEmpty()) {
								for (Enchantment e : newItems.getEnchantments()
										.keySet())
									sb.append(" " + e.getName() + " lvl "
											+ newItems.getEnchantmentLevel(e)
											+ " ");
							}
						}
					dl.log("-----------------------", p.getName() + "inventory");
					dl.log("New items", p.getName() + "inventory");
					for (ItemStack newInve : p.getInventory().getContents())
						if (newInve != null)
							dl.log(newInve.getType().name() + " #"
									+ newInve.getAmount(), p.getName()
									+ "inventory");
					dl.log("-----------------------", p.getName() + "inventory");

					pw.println(Character.LINE_SEPARATOR);
					pw.println("Player: " + p.getName()
							+ " has finished redeeming their kit.");
					p.setItemInHand(new ItemStack(Material.AIR));
					claimedMap.put(p.getUniqueId().toString(), newList);
					sendMessage(p, "Here's your stuff!", true);
					saveInfo();
					p.updateInventory();
					int xp = expMap.get(currentGroup);
					int money = moneyMap.get(currentGroup);
					if (xp > 0) {
						dl.logPlayer(p, "Beginning level: " + p.getLevel());
						p.setLevel(p.getLevel() + xp);
						dl.logPlayer(p, "Ending level: " + p.getLevel());
					}
					if (money > 0)
						giveMoney(p, money);
					dl.logPlayer(p, "Player: " + p.getName()
							+ " has redeemed their " + currentGroup
							+ " kit at " + sdf.format(new Date()));
					dl.logPlayer(p,
							"-----------------------------------------------");
					pw.println("Player: " + p.getName()
							+ " has redeemed their " + currentGroup
							+ " kit at " + sdf.format(new Date()));
				}
			}
		}

	}

	@EventHandler
	public void playerJoin(final PlayerJoinEvent event) {
		jp.getServer().getScheduler().runTaskLater(jp, new Runnable() {
			public void run() {
				Player p = event.getPlayer();
				if (!working)
					return;
				for (String group : groups) {
					if (p.hasPermission(perm + group)) {
						if (claimedMap.containsKey(p.getName())) {
							List<SyncRedeemTimestamp> lsrt = claimedMap.get(p
									.getUniqueId().toString());
							boolean flag = true;
							for (SyncRedeemTimestamp srt : lsrt) {
								if (srt.getGroup().equalsIgnoreCase(group)) {
									flag = false;
									break;
								}
							}
							if (!flag)
								continue;
							sendMessage(p, "You can redeem! /redeem", true);
							return;
						} else {
							sendMessage(p, "You can redeem! /redeem", true);
							return;
						}
					}
				}
			}
		}, 20);
	}

	private boolean redeem(Player sender) {
		boolean saveFlag = false;
		for (String group : groups) {
			if (sender.getInventory().firstEmpty() == -1) {
				sendMessage(sender, "You need an empty slot to do this!", false);
				if (saveFlag)
					saveInfo();
				return true;
			} else if (sender.hasPermission(perm + group)) {
				List<SyncRedeemTimestamp> list = claimedMap.get(sender
						.getUniqueId().toString());
				boolean flag = false;
				if (list == null) {
					flag = false;
				} else {
					for (SyncRedeemTimestamp srt : list) {
						if (srt.getGroup().equalsIgnoreCase(group)) {
							if (srt.getDateClaimed() != null
									&& srt.getDateUsed() == null) {
								System.out
										.println("It has been claimed but not used");
								PlayerInventory pi = sender.getInventory();
								List<ItemStack> items = new ArrayList<ItemStack>();
								for (ItemStack is : pi.getContents())
									items.add(is);
								for (ItemStack is : pi.getArmorContents())
									items.add(is);
								for (ItemStack is : items) {
									if (is != null
											&& !(is.getType()
													.equals(Material.AIR))) {
										if (is.getItemMeta() != null
												&& is.getItemMeta()
														.getDisplayName() != null) {
											String name = is.getItemMeta()
													.getDisplayName();
											if (name.equalsIgnoreCase(ChatColor.AQUA
													+ itemRedeemMap
															.get(group)
															.getBukkitItemStack()
															.getItemMeta()
															.getDisplayName()
													+ " redeem for "
													+ sender.getUniqueId()
															.toString())) {
												System.out
														.println("Item "
																+ name
																+ " found in inventory");
												flag = true;
												break;
											}
										}
									}
								}
							} else {
								System.out.println("flag=true for " + group);
								flag = true;
							}

						}
					}

					if (flag) {
						flag = false;
						continue;
					}
				}
				// TODO GO BACK TO
				ItemStack is = itemRedeemMap.get(group).getBukkitItemStack();
				ItemMeta im = is.getItemMeta();
				System.out.println("Giving " + sender.getName() + "s "
						+ im.getDisplayName());
				im.setDisplayName(ChatColor.AQUA
						+ itemRedeemMap.get(group).getBukkitItemStack()
								.getItemMeta().getDisplayName()
						+ " redeem for " + sender.getUniqueId().toString());
				is.setItemMeta(im);
				sender.getInventory().addItem(is);
				if (list == null) {
					list = new ArrayList<SyncRedeemTimestamp>();
				}
				list.add(new SyncRedeemTimestamp(group, im.getDisplayName(),
						new Date().getTime(), -1));
				claimedMap.put(sender.getUniqueId().toString(), list);
				saveFlag = true;

			}
		}
		if (saveFlag) {
			sendMessage(sender, "There ya go! Right click to get your items!",
					true);
			saveInfo();
		} else {
			sendMessage(sender, "You have no redeems left.", false);
		}
		return true;
	}

	private void save(final Object obj, final String path) throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	private void saveInfo() {
		try {
			save(itemMap, itemMapFile.getAbsolutePath());
			save(claimedMap, claimedMapFile.getAbsolutePath());
			save(moneyMap, moneyMapFile.getAbsolutePath());
			save(groups, groupsFile.getAbsolutePath());
			save(itemRedeemMap, itemRedeemMapFile.getAbsolutePath());
			save(expMap, expMapFile.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Something went wrong! Oh noes!");
		}
	}

	private boolean saveReward(Player sender, String[] args) {
		if (args.length != 4) {
			sendMessage(sender,
					"Please use /savereward <group> <boxid> <money> <exp>",
					false);
			return true;
		}
		String group = null;
		int boxid = -1;
		int money = -1;
		int exp = -1;

		try {
			group = args[0];
			boxid = Integer.parseInt(args[1]);
			money = Integer.parseInt(args[2]);
			exp = Integer.parseInt(args[3]);
			sender.sendMessage("Group " + group + "  boxid  " + boxid
					+ "  money " + money + "  exp " + exp);
		} catch (Exception e) {
			sendMessage(sender,
					"Please use /savereward <group> <boxid> <money> <exp>",
					false);
			return true;
		}
		if (group == null || boxid == -1 || money == -1 || exp == -1) {
			sendMessage(sender,
					"Please use /savereward <group> <boxid> <money> <exp>",
					false);
			return true;
		}
		groups.add(group);
		addItemToMap(group, boxid);
		moneyMap.put(group, money);
		expMap.put(group, exp);
		SyncInventory si = new SyncInventory(sender.getInventory()
				.getContents());
		itemMap.put(group, si);
		pw.println("Player: " + sender.getName() + " Created redeem: " + group
				+ " with " + money + " cash and " + exp + " EXP");
		saveInfo();
		return true;
	}

	private void sendMessage(Player player, String message, boolean positive) {
		if (player.isOnline()) {
			if (positive)
				player.sendMessage(posTitle + message);
			else
				player.sendMessage(negTitle + message);
		}
	}

	private boolean setupConfig() {
		jp.getConfig().addDefault("redeem.enabled", false);
		jp.getConfig().addDefault("redeem.disabledstring",
				"Redeems will be enabled on the ___");
		jp.getConfig().addDefault("redeem.playerenabled", true);
		jp.getConfig().options().copyDefaults(true);
		jp.saveConfig();
		working = jp.getConfig().getBoolean("redeem.playerenabled");
		errorString = jp.getConfig().getString("redeem.disabledstring");
		return jp.getConfig().getBoolean("redeem.enabled");
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = jp.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = jp
				.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
}
