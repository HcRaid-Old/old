package com.addongaming.hcessentials.perks.safedrop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;

public class SafeDropBlock {
	private Material mat;
	private ItemType itemType;

	public SafeDropBlock(String blockName, String toolName) {
		this.mat = Material.getMaterial(blockName.toUpperCase());
		this.itemType = ItemType.valueOf(toolName);
	}

	public boolean canUse(Player p, Block b) {
		if (b.getType() == mat)
			if (Enchantable
					.getItemType(p.getPlayer().getItemInHand().getType()) == itemType)
				return true;
			else
				return false;

		return false;
	}
}
