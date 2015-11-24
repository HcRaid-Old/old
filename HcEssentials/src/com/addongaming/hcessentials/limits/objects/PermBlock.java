package com.addongaming.hcessentials.limits.objects;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PermBlock {
	private final int id;
	private final byte data;
	private final String permission;

	public PermBlock(String line) {
		String[] split1 = line.split("(|)");
		permission = split1[1];
		if (split1[0].contains(":")) {
			String[] split2 = split1[0].split("(:)");
			id = Integer.parseInt(split2[0]);
			data = Byte.parseByte(split2[1]);
		} else {
			id = Integer.parseInt(split1[0]);
			data = -1;
		}
	}

	public boolean isValid(Block block) {
		return block.getTypeId() == id
				&& (data > -1 && block.getData() == data);
	}

	public boolean isValid(ItemStack item) {
		return item.getTypeId() == id
				&& (data > -1 && item.getDurability() == data);
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission(Player player) {
		return player.hasPermission(permission);
	}

}
