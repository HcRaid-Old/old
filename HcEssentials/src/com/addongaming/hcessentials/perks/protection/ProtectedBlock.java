package com.addongaming.hcessentials.perks.protection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.HcEssentials;

public class ProtectedBlock {
	private String blockName;
	private String permission;

	public ProtectedBlock(String blockName, String permission) {
		this.blockName = blockName;
		this.permission = permission;
	}

	public boolean isProtected(Block b) {
		String name = HcEssentials.essentials.getItemDb().name(
				new ItemStack(b.getType(), 1, b.getData()));
		if (name.toLowerCase().startsWith(blockName.toLowerCase()))
			return true;
		return false;
	}

	public boolean canUse(Player p, Block b) {
		short data = b.getData();
		if (b.getType() == Material.STEP && data >= 8)
			data -= 8;
		String name = HcEssentials.essentials.getItemDb().name(
				new ItemStack(b.getType(), 1, data));
		if (name.toLowerCase().startsWith(blockName.toLowerCase()))
			if (p.hasPermission(permission))
				return true;
			else
				return false;

		return true;
	}
}
