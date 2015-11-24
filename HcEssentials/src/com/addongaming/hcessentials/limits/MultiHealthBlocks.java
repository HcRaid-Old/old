package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.hooks.logging.BlockLoggingHook;
import com.addongaming.hcessentials.limits.objects.MultiHealthBlock;
import com.addongaming.hcessentials.logging.DataLog;

public class MultiHealthBlocks implements Listener {
	private List<MultiHealthBlock> blockList = new ArrayList<MultiHealthBlock>();
	private Material inspector;
	DataLog dl;
	private int searchRadius;

	public MultiHealthBlocks(List<String> configList, String inspector,
			int searchRadius) {
		dl = HcEssentials.getDataLogger().addLogger("MultiHealthBlocks");
		this.searchRadius = searchRadius;
		dl.log("The search radius for blocks when TnT is exploded is "
				+ searchRadius);
		for (String str : configList) {
			dl.log("Loading " + str);
			String[] split = str.split("[|]");
			blockList.add(new MultiHealthBlock(Material.valueOf(split[0]
					.toUpperCase()), Integer.parseInt(split[1]), split[2]));
		}
		this.inspector = Material.valueOf(inspector.toUpperCase());
		for (MultiHealthBlock mhb : blockList)
			dl.log("Block type: " + mhb.getBlock().name() + " Health : "
					+ mhb.getMaxHealth());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void explode(final EntityExplodeEvent event) {
		List<Block> searchBlocks = new ArrayList<Block>();
		Location start = event.getEntity().getLocation();
		MultiHealthBlocks mhb = Limiter.getInstance().getMultiHealthBlocks();
		for (int x = start.getBlockX() - this.searchRadius; x < start
				.getBlockX() + searchRadius; x++) {
			for (int y = start.getBlockY() - this.searchRadius; y < start
					.getBlockY() + searchRadius; y++) {
				for (int z = start.getBlockZ() - this.searchRadius; z < start
						.getBlockZ() + searchRadius; z++) {
					Block b = start.getWorld().getBlockAt(x, y, z);
					if (hasMultiHealth(b)) {
						if (hasWaterBetween(event.getEntity().getLocation(),
								b.getLocation()))
							continue;
						searchBlocks.add(b);
						if (mhb.getHealthLeft(b) == 1) {
							b.setType(Material.AIR);
							if (BlockLoggingHook.hasInstance())
								BlockLoggingHook
										.getInstance()
										.getApi()
										.logRemoval("#tnt", b.getLocation(),
												Material.AIR.getId(), (byte) 0);
						} else {
							b.setData((byte) (b.getData() + 1));
						}
					}
				}
			}
		}
		for (Iterator<Block> iter = event.blockList().iterator(); iter
				.hasNext();) {
			Block block = iter.next();
			if (mhb.hasMultiHealth(block) && !searchBlocks.contains(block)) {
				if (hasWaterBetween(event.getEntity().getLocation(),
						block.getLocation()))
					continue;
				iter.remove();
				if (mhb.getHealthLeft(block) == 1) {
					block.setType(Material.AIR);
					if (BlockLoggingHook.hasInstance())
						BlockLoggingHook
								.getInstance()
								.getApi()
								.logRemoval("#tnt", block.getLocation(),
										Material.AIR.getId(), (byte) 0);
				} else {
					block.setData((byte) (block.getData() + 1));
				}
			}
		}
	}

	private boolean hasWaterBetween(Location aLoc, Location bLoc) {
		if (aLoc.getBlock().isLiquid() || bLoc.getBlock().isLiquid())
			return true;
		double a = aLoc.getX() - bLoc.getX();
		double b = aLoc.getZ() - bLoc.getZ();
		double yawdegree = Math.atan(a / b);
		double c = Math.sqrt((a * a) + (b * b));
		double d = aLoc.getY() - bLoc.getY();
		double pitchdegree = Math.atan(c / d);
		Location loc = aLoc;
		loc.setYaw((float) yawdegree);
		loc.setPitch((float) pitchdegree);
		int distance = (int) bLoc.distance(aLoc);
		if (distance < 1)
			distance = 1;
		BlockIterator bi = new BlockIterator(loc, 0, distance);
		int counter = 0;
		while (bi.hasNext()) {
			counter += 1;
			if (counter > 60)
				return true;
			Block block = bi.next();
			if (block.isLiquid())
				return true;
		}
		return false;
	}

	@EventHandler
	public void blockInteract(PlayerInteractEvent event) {
		if (event.hasBlock() && event.hasItem()
				&& hasMultiHealth(event.getClickedBlock())
				&& event.getItem().getType() == inspector) {
			event.getPlayer().sendMessage(
					getMultiHealthBlock(event.getClickedBlock()).getMessage(
							event.getClickedBlock()));
			final HashMap<String, Integer> swordEnchs = new HashMap<String, Integer>();
			swordEnchs.put("DAMAGE_ALL", 3);
			final String swordName = "DIAMOND_SWORD";
			ItemStack sword = new ItemStack(Material.getMaterial(swordName)) {
				{
					for (String str : swordEnchs.keySet())
						addEnchantment(Enchantment.getByName(str),
								swordEnchs.get(str));
				}
			};
		}
	}

	public boolean hasMultiHealth(Block b) {
		for (MultiHealthBlock mhb : blockList)
			if (mhb.getBlock() == b.getType())
				return true;
		return false;
	}

	public int getHealthLeft(Block b) {
		for (MultiHealthBlock mhb : blockList)
			if (mhb.getBlock() == b.getType())
				return mhb.getHealthLeft(b);
		return 0;
	}

	public MultiHealthBlock getMultiHealthBlock(Block b) {
		for (MultiHealthBlock mhb : blockList)
			if (mhb.getBlock() == b.getType())
				return mhb;
		return null;
	}
}
