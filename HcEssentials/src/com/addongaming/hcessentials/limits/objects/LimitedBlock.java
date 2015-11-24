package com.addongaming.hcessentials.limits.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class LimitedBlock {
	private Material material;
	private final String message;
	private int dura = -1;
	private int id;

	public LimitedBlock(String line) {
		if (line.contains(":")) {
			String[] colonSplit = line.split("[:]");
			String material = colonSplit[0];
			String[] lineThingSplit = colonSplit[1].split("[|]");
			dura = Integer.parseInt(lineThingSplit[0]);
			this.message = lineThingSplit[1];
			if (isInteger(material)) {
				id = Integer.parseInt(material);
				material = null;
			} else
				for (Material mat : Material.values())
					if (mat.name().equalsIgnoreCase(material)) {
						this.material = mat;
						id = mat.getId();
						break;
					}
		} else {
			String[] split = line.split("[|]");
			if (isInteger(split[0])) {
				id = Integer.parseInt(split[0]);
				material = null;
			} else
				for (Material mat : Material.values())
					if (mat.name().equalsIgnoreCase(split[0])) {
						this.material = mat;
						id = mat.getId();
						break;
					}
			this.message = split[1];
		}
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Returns the material if specified. If ID is given in the config then you
	 * can only obtain the ID
	 * 
	 * @return Material of the limited block.
	 */
	public Material getMaterial() {
		return material;
	}

	public boolean matches(ItemStack is) {
		if (material != null) {
			if (dura == -1) {
				return is.getType() == material;
			} else {
				return is.getType() == material && is.getDurability() == dura;
			}
		} else {
			if (dura == -1) {
				return is.getTypeId() == id;
			} else {
				return is.getTypeId() == id && is.getDurability() == dura;
			}
		}
	}

	public boolean matches(Block block) {
		if (material != null) {
			if (dura == -1) {
				return block.getType() == material;
			} else {
				return block.getType() == material && block.getData() == dura;
			}
		} else {
			if (dura == -1) {
				return block.getTypeId() == id;
			} else {
				return block.getTypeId() == id && block.getData() == dura;
			}
		}
	}

	public String getMessage() {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
