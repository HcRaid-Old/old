package com.addongaming.hcessentials.limits.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MultiHealthBlock {
	private final Material block;
	private final int maxHealth;
	private final String message;

	public MultiHealthBlock(Material block, int maxHealth, String message) {
		this.block = block;
		this.maxHealth = maxHealth;
		this.message = message;
	}

	public Material getBlock() {
		return block;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	@SuppressWarnings("deprecation")
	public String getMessage(Block health) {
		return ChatColor.translateAlternateColorCodes(
				'&',
				message.replaceAll("(<health>)",
						"" + (maxHealth - health.getData())));
	}

	@SuppressWarnings("deprecation")
	public int getHealthLeft(Block health) {
		return (maxHealth - health.getData());
	}
}
