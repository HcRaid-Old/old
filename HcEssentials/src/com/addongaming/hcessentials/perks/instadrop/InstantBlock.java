package com.addongaming.hcessentials.perks.instadrop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class InstantBlock {
	private Material mat;
	private String permission;

	public InstantBlock(String blockName, String permission) {
		this.mat = Material.getMaterial(blockName.toUpperCase());
		this.permission = permission;
	}

	public boolean canUse(Player p, Block b) {
		if (b.getType() == mat)
			if (p.hasPermission(permission))
				return true;
			else
				return false;

		return false;
	}
}
