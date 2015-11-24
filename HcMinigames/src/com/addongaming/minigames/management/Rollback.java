package com.addongaming.minigames.management;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;

import com.addongaming.hcessentials.serialised.SerInventory;

public class Rollback {
	private final HashMap<Location, SerInventory> preInventory = new HashMap<Location, SerInventory>();
	private final HashMap<Location, Material> originalBlock = new HashMap<Location, Material>();
	private final HashMap<Location, Byte> blockData = new HashMap<Location, Byte>();
	private final HashMap<Location, String[]> signData = new HashMap<Location, String[]>();

	public void blockPlaced(Block block) {
		if (!originalBlock.containsKey(block.getLocation())) {
			originalBlock.put(block.getLocation(), Material.AIR);
		}
	}

	public void blockRemove(Block block) {
		if (!originalBlock.containsKey(block.getLocation())) {
			originalBlock.put(block.getLocation(), block.getType());
			if (block.getState() != null && block.getState() instanceof Sign) {
				signData.put(block.getLocation(),
						((Sign) block.getState()).getLines());
			}
			if (block.getData() != (byte) 0)
				blockData.put(block.getLocation(), block.getData());
			if (block.getState() != null
					&& block.getState() instanceof InventoryHolder) {
				addInventory((InventoryHolder) block.getState());
			}
		}
	}

	public void addInventory(InventoryHolder holder) {
		if (holder instanceof BlockState) {
			BlockState bs = (BlockState) holder;
			if (!preInventory.containsKey(bs.getLocation())) {
				preInventory.put(bs.getLocation(), new SerInventory(holder
						.getInventory().getContents()));
			}
		}
	}

	public void rollBack() {
		for (Location loc : originalBlock.keySet())
			loc.getBlock().setType(originalBlock.get(loc));
		for (Location loc : blockData.keySet())
			loc.getBlock().setData(blockData.get(loc));
		for (Location loc : preInventory.keySet()) {
			Block b = loc.getBlock();
			if (b.getState() != null && b.getState() instanceof InventoryHolder) {
				InventoryHolder ih = (InventoryHolder) b.getState();
				ih.getInventory().setContents(
						preInventory.get(loc).getContents());
				((BlockState) ih).update(true);
			}
		}
		for (Location loc : signData.keySet()) {
			try {
				Sign sign = (Sign) loc.getBlock().getState();
				String[] lines = signData.get(loc);
				for (int i = 0; i < lines.length; i++)
					sign.setLine(i, lines[i]);
				sign.update(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.preInventory.clear();
		this.originalBlock.clear();
		this.blockData.clear();
	}
}
