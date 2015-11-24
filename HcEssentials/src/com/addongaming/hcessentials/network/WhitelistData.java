package com.addongaming.hcessentials.network;

import org.bukkit.inventory.ItemStack;

public class WhitelistData {
	private boolean whitelisted = false;
	private String[] whitelistedUUIDs;
	private final ItemStack is;
	private String bungeeId;
	private int priority;

	public WhitelistData(String bungeeId, int priority, ItemStack is,
			boolean whitelisted, String[] whitelistedUUIDs) {
		this.is = is;
		this.bungeeId = bungeeId;
		this.priority = priority;
		this.whitelisted = whitelisted;
		this.whitelistedUUIDs = whitelistedUUIDs;
	}

	public int getPriority() {
		return priority;
	}

	public ItemStack getIs() {
		return is;
	}

	public String[] getWhitelistedUUIDs() {
		return whitelistedUUIDs;
	}

	public void setWhitelisted(boolean whitelisted) {
		this.whitelisted = whitelisted;
	}

	public boolean isWhitedlisted(String uuid) {
		if (!whitelisted)
			return true;
		for (String str : whitelistedUUIDs)
			if (str.equals(uuid))
				return true;
		return false;

	}

	public void setWhitelistedUUIDs(String[] whitelistedUUIDs) {
		this.whitelistedUUIDs = whitelistedUUIDs;
	}

	public boolean isWhitelisted() {
		return whitelisted;
	}

	public String getBungeeId() {
		return bungeeId;
	}
}
