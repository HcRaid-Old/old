package com.addongaming.hcessentials.items.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.hooks.logging.BlockLoggingHook;
import com.addongaming.hcessentials.items.InfCustomItem;

public class MiningLaser implements InfCustomItem {
	private final boolean enabled;
	private int duraDamage;

	public MiningLaser(JavaPlugin jp) {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("customitems.mininglaser.enabled", false);
		fc.addDefault("customitems.mininglaser.duraDamage", 5);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		enabled = fc.getBoolean("customitems.mininglaser.enabled");
		duraDamage = fc.getInt("customitems.mininglaser.duraDamage");
	}

	@Override
	public ItemStack getItem() {
		ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Mining Laser");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.AQUA + "Mode 1: " + ChatColor.GREEN
				+ "Right click a block");
		lore.add(ChatColor.GREEN + "and dig four blocks");
		lore.add(ChatColor.AQUA + "Mode 2: " + ChatColor.GREEN
				+ "Right click a block");
		lore.add(ChatColor.GREEN + "and dig in a 3x3 radius");
		lore.add(ChatColor.DARK_AQUA + "Shift right click to toggle modes");
		lore.add(ChatColor.BOLD + "NO ITEMS WILL BE DROPPED");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	@Override
	public String getName() {
		return "Mining Laser";
	}

	@Override
	public boolean isItem(ItemStack is) {
		if (is == null
				|| is.getType() != Material.DIAMOND_PICKAXE
				|| !is.hasItemMeta()
				|| is.getItemMeta().getDisplayName() == null
				|| !is.getItemMeta().getDisplayName()
						.equalsIgnoreCase(ChatColor.GOLD + "Mining Laser"))
			return false;
		return true;
	}

	List<String> playerMode = new ArrayList<String>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.hasItem()) {
			if (isItem(event.getItem())) {
				if (!event.getPlayer().isSneaking()) {
					if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
						return;
					if (Combat.getCombatInstance() != null
							&& Combat.getCombatInstance().isInCombat(
									event.getPlayer().getName())) {
						event.getPlayer().sendMessage(
								message + "You cannot use this in combat.");
					} else
						mine(event);
					return;
				} else {
					toggleMode(event.getPlayer());
				}
			}
		}
	}

	private void toggleMode(Player player) {
		String msg;
		if (playerMode.contains(player.getName())) {
			playerMode.remove(player.getName());
			msg = "Mode 1 toggled on.";
		} else {
			playerMode.add(player.getName());
			msg = "Mode 2 toggled on.";
		}
		player.sendMessage(message + msg);
	}

	private void mine(PlayerInteractEvent event) {
		Block[] block;
		if (playerMode.contains(event.getPlayer().getName())) {
			// Mode 2
			block = getBlocks(event.getClickedBlock(), 2);
		} else {
			// Mode 1
			block = getBlocks(event.getClickedBlock(), event.getBlockFace(), 3);
		}
		List<Block> finalList = new ArrayList<Block>();
		Material[] ignored = { Material.AIR, Material.BEDROCK,
				Material.MOB_SPAWNER, Material.CHEST, Material.TRAPPED_CHEST };
		for (Block bl : block) {
			boolean ignore = false;
			if (HcEssentials.worldGuard.canBuild(event.getPlayer(), bl)) {
				for (Material mat : ignored) {
					if (mat == bl.getType()) {
						ignore = true;
						break;
					}
				}
				if (!ignore)
					finalList.add(bl);
			}
		}
		for (Block bl : finalList) {
			if (BlockLoggingHook.hasInstance())
				BlockLoggingHook
						.getInstance()
						.getApi()
						.logRemoval(event.getPlayer().getName(),
								bl.getLocation(), bl.getTypeId(), bl.getData());
			bl.setType(Material.AIR);
		}
		Player player = event.getPlayer();
		if (player.getItemInHand().getDurability()
				+ (duraDamage * finalList.size()) > player.getItemInHand()
				.getType().getMaxDurability()) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else
			player.getItemInHand()
					.setDurability(
							(short) (player.getItemInHand().getDurability() + (duraDamage * finalList
									.size())));
	}

	public Block[] getBlocks(Block start, BlockFace clicked, int depth) {
		List<Block> bl = new ArrayList<Block>();
		bl.add(start);
		for (int i = 1; i <= depth; i++) {
			start = start.getRelative(clicked.getOppositeFace());
			bl.add(start);
		}
		return bl.toArray(new Block[bl.size()]);
	}

	public Block[] getBlocks(Block start, int radius) {
		List<Block> bl = new ArrayList<Block>();
		for (int x = start.getX() - radius; x <= start.getX() + radius; x++) {
			for (int y = start.getY() - radius; y <= start.getY() + radius; y++) {
				for (int z = start.getZ() - radius; z <= start.getZ() + radius; z++) {
					bl.add(start.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return bl.toArray(new Block[bl.size()]);
	}

	private final String message = ChatColor.GOLD + "[" + ChatColor.AQUA
			+ "Mining Laser" + ChatColor.GOLD + "] " + ChatColor.GREEN;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public String getDescription() {
		return "Allows you to easily mine out an area.";
	}

	List<String> ips = new ArrayList<String>();
	List<String> userName = new ArrayList<String>();

	@Override
	public String canBuy(Player player) {
		if (userName.contains(player.getName().toLowerCase())) {
			if (!ips.contains(player.getAddress().getAddress().getHostAddress()))
				ips.add(player.getAddress().getAddress().getHostAddress());
			return message + "You may only purchase one " + getName()
					+ " a day.";
		} else if (ips.contains(player.getAddress().getAddress()
				.getHostAddress())) {
			if (!userName.contains(player.getName().toLowerCase()))
				userName.add(player.getName().toLowerCase());
			return message
					+ "Only one mining laser may be purchased per IP per day.";
		}
		ips.add(player.getAddress().getAddress().getHostAddress());
		userName.add(player.getName().toLowerCase());
		return null;
	}

}
