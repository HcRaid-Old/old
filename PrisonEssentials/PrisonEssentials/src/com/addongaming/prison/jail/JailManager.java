package com.addongaming.prison.jail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.core.Main;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.prison.PrisonManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class JailManager implements Listener {

	private static JailManager instance;

	public static JailManager getInstance() {
		return instance;
	}

	public static ItemStack getPrisonStick() {
		ItemStack is = new ItemStack(Material.BLAZE_ROD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("Guard Baton");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + "Right click inmates");
		lore.add(ChatColor.RESET + "to apprehend them.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public static boolean isPrisonStick(ItemStack itemInHand) {
		ItemStack is = getPrisonStick();
		if (is.getType() != itemInHand.getType())
			return false;
		if (!is.getItemMeta().getDisplayName()
				.equalsIgnoreCase(itemInHand.getItemMeta().getDisplayName()))
			return false;
		return true;
	}

	private List<Jail> jailList = new ArrayList<Jail>();

	private final String jailPrefix = ChatColor.GOLD + "[" + ChatColor.DARK_RED
			+ "Jail" + ChatColor.GOLD + "] " + ChatColor.RESET;

	HashMap<String, Date> jailTime = new HashMap<String, Date>();

	private JavaPlugin jp;

	HashMap<String, Date> suppliesTime = new HashMap<String, Date>();

	public JailManager(JavaPlugin jp) {
		this.jp = jp;
		initConfig();
		loadConfig();
		jp.getServer().getPluginManager().registerEvents(this, jp);
		instance = this;
	}

	public boolean isInJail(Player player) {
		ApplicableRegionSet ars = Main.wg.getRegionManager(player.getWorld())
				.getApplicableRegions(player.getLocation());
		for (Iterator<ProtectedRegion> iter = ars.iterator(); iter.hasNext();) {
			ProtectedRegion pr = iter.next();
			for (Jail jail : jailList)
				if (!player.getWorld().getName()
						.equalsIgnoreCase(jail.getWorld()))
					continue;
				else if (jail.getRegion().equalsIgnoreCase(pr.getId()))
					return true;
		}
		return false;
	}

	private boolean canGetSupplies(Player p) {
		if (!suppliesTime.containsKey(p.getName())
				|| suppliesTime.get(p.getName()).before(new Date()))
			return true;
		return false;
	}

	private DataReturn canImprison(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (prisoner.getPlayerClass() != PlayerClasses.GUARD)
			return DataReturn.NOPERM;
		if (!jailTime.containsKey(p.getName())
				|| jailTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	private void checkJailLeave(Jail jail, Player player) {
		int currentGravel = Utils.count(player, Material.GRAVEL);
		int currentSponge = Utils.count(player, Material.SPONGE);
		int gravNeeded = 0, spongNeeded = 0;
		if (currentGravel < jail.getNeededGravel()) {
			gravNeeded = jail.getNeededGravel() - currentGravel;
		}
		if (currentSponge < jail.getNeededSponge()) {
			spongNeeded = jail.getNeededSponge() - currentSponge;
		}
		if (gravNeeded > 0 && spongNeeded > 0)
			player.sendMessage(jailPrefix + "You need " + gravNeeded
					+ " more gravel and " + spongNeeded + " more sponge.");
		else if (gravNeeded > 0)
			player.sendMessage(jailPrefix + "You need " + gravNeeded
					+ " more gravel.");
		else if (spongNeeded > 0)
			player.sendMessage(jailPrefix + "You need " + spongNeeded
					+ " more sponge");
		else {
			player.sendMessage(jailPrefix
					+ "You have paid your debt to the prison.");
			Utils.removeFromInventory(player, new ItemStack(Material.GRAVEL,
					jail.getNeededGravel()), new ItemStack(Material.SPONGE,
					jail.getNeededSponge()));
			player.teleport(jail.getSafeLoc());

		}
	}

	@SuppressWarnings("deprecation")
	private void checkSuppliesGive(Player player) {
		if (!canGetSupplies(player)) {
			player.sendMessage(jailPrefix
					+ "You cannot currently get another set of supplies.");
			return;
		}
		player.getInventory().addItem(new ItemStack(Material.STONE_SPADE),
				new ItemStack(Material.STONE_PICKAXE));
		player.sendMessage(jailPrefix + "Make good use of these supplies.");
		suppliesTime.put(player.getName(), new Date(new Date().getTime()
				+ (60000 * 4)));
		player.updateInventory();
	}

	private void initConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.jail.jail1.name", "Starter Jail");
		fc.addDefault("prison.jail.jail1.world", "world");
		fc.addDefault("prison.jail.jail1.region", "jail1");
		fc.addDefault("prison.jail.jail1.sponge", 20);
		fc.addDefault("prison.jail.jail1.prison", "Beginner");
		fc.addDefault("prison.jail.jail1.gravel", 64);
		fc.addDefault("prison.jail.jail1.safelocation", "world|0.0|0.0|0.0");
		fc.addDefault("prison.jail.jail1.jaillocation", "world|0.0|0.0|0.0");
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@EventHandler
	public void interact(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Player) {
			Player guard = event.getPlayer();
			Player rightClicked = (Player) event.getRightClicked();
			if (guard.getItemInHand() == null
					|| !isPrisonStick(guard.getItemInHand()))
				return;
			if (canImprison(guard) == DataReturn.NOPERM
					|| PrisonerManager.getInstance()
							.getPrisonerInfo(rightClicked.getName())
							.getPlayerClass() == PlayerClasses.GUARD)
				return;
			DataReturn dr = canImprison(guard);
			switch (dr) {
			case FAILURE:
				guard.sendMessage(jailPrefix
						+ "You cannot jail another inmate just yet.");
				return;
			case NOLEVEL:
				return;
			case NOPERM:
				return;
			case SUCCESS:
				guard.sendMessage(jailPrefix + "You jailed "
						+ rightClicked.getName() + ".");
				break;
			default:
				return;
			}
			jailPlayer(rightClicked);
		}
	}

	public void jailPlayer(Player player) {
		Jail closest = getJailFor(player);
		player.teleport(closest.getJailLoc());
		player.sendMessage(jailPrefix
				+ "You are now in jail. To get out work off your time by harvesting "
				+ closest.getNeededGravel() + " gravel and "
				+ closest.getNeededSponge() + " sponge.");
		List<ItemStack> banned = new ArrayList<ItemStack>();
		for (ItemStack is : player.getInventory().getContents())
			if (is != null
					&& Enchantable.getItemType(is.getType()) == ItemType.SWORD)
				banned.add(is);
		if (!banned.isEmpty()) {
			player.getInventory().removeItem(
					banned.toArray(new ItemStack[banned.size()]));
			player.sendMessage(jailPrefix
					+ "All your illegal items have been removed.");
		}
		player.setHealth(20.0);
		player.setFoodLevel(20);
	}

	public Jail getJailFor(Player player) {
		for (Jail jail : jailList)
			if (PrisonManager.getInstance().getPrison(player).getName()
					.equalsIgnoreCase(jail.getPrison()))
				return jail;
		return null;
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		for (String part : fc.getConfigurationSection("prison.jail").getKeys(
				false)) {
			String name = fc.getString("prison.jail." + part + ".name");
			String world = fc.getString("prison.jail." + part + ".world");
			String region = fc.getString("prison.jail." + part + ".region");
			int sponge = fc.getInt("prison.jail." + part + ".sponge");
			int gravel = fc.getInt("prison.jail." + part + ".gravel");
			String prison = fc.getString("prison.jail." + part + ".prison");
			Location safeloc = Utils.loadLoc(fc.getString("prison.jail." + part
					+ ".safelocation"));
			Location jailLoc = Utils.loadLoc(fc.getString("prison.jail." + part
					+ ".jaillocation"));
			jailList.add(new Jail(name, world, region, sponge, gravel, jailLoc,
					safeloc, prison));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void signClicked(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (event.getClickedBlock().getState() != null
					&& event.getClickedBlock().getState() instanceof Sign) {
				ApplicableRegionSet ars = Main.wg.getRegionManager(
						event.getClickedBlock().getWorld())
						.getApplicableRegions(
								event.getClickedBlock().getLocation());
				for (Iterator<ProtectedRegion> iter = ars.iterator(); iter
						.hasNext();) {
					ProtectedRegion pr = iter.next();
					for (Jail jail : jailList)
						if (jail.getWorld().equalsIgnoreCase(
								event.getClickedBlock().getWorld().getName())) {
							if (pr.getId().equalsIgnoreCase(jail.getRegion())) {
								Sign sign = (Sign) event.getClickedBlock()
										.getState();
								if (sign.getLine(0).equalsIgnoreCase("[Jail]")) {
									checkJailLeave(jail, event.getPlayer());
									event.setCancelled(true);
									return;
								} else if (sign.getLine(0).equalsIgnoreCase(
										"[Supplies]")) {
									checkSuppliesGive(event.getPlayer());
									event.setCancelled(true);
									return;
								}
							}
						}
				}
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase("[Baton]")
						&& PrisonerManager.getInstance()
								.getPrisonerInfo(event.getPlayer().getName())
								.getPlayerClass() == PlayerClasses.GUARD) {
					event.getPlayer().getInventory().addItem(getPrisonStick());
				}
			}
		}
	}
}
