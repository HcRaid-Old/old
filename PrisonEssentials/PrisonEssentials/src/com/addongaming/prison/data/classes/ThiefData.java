package com.addongaming.prison.data.classes;

import org.bukkit.Material;

public enum ThiefData {
	PICKPOCKET_1("Level 1 Pickpocketing", "Pickpocket 1 item",
			Material.IRON_INGOT, 0, 0, "prison.thief.pickpocket1", null), PICKPOCKET_2(
			"Level 2 Pickpocketing", "Pickpocket up to a stack",
			Material.GOLD_INGOT, 10, 2000, "prison.thief.pickpocket2",
			ThiefData.PICKPOCKET_1), PICKPOCKET_3("Level 3 Pickpocketing",
			"Choose what you steal", Material.EMERALD, 25, 4000,
			"prison.thief.pickpocket3", ThiefData.PICKPOCKET_2), PICKPOCKET_4(
			"Pickpocketing Master", "Always steal a stack", Material.DIAMOND,
			60, 100000, "prison.thief.pickpocket4", ThiefData.PICKPOCKET_3);
	public static ThiefData getDataByMaterial(Material type) {
		for (ThiefData td : values())
			if (td.getShopId() == type)
				return td;
		return null;
	}
	private int classLevel;
	private int cost;
	private String permission;
	private ThiefData prerequisite;
	private Material shopId;
	private String text;

	private String title;

	ThiefData(String title, String text, Material shopId, int classLevel,
			int cost, String permission, ThiefData pre) {
		this.title = title;
		this.text = text;
		this.shopId = shopId;
		this.classLevel = classLevel;
		this.cost = cost;
		this.permission = permission;
		this.prerequisite = pre;
	}

	public int getClassLevel() {
		return classLevel;
	}

	public int getCost() {
		return cost;
	}

	public String getPermission() {
		return permission;
	}

	public ThiefData getPrerequisite() {
		return prerequisite;
	}

	public Material getShopId() {
		return shopId;
	}

	public String getText() {
		return text;
	}

	public String getTitle() {
		return title;
	}

	public boolean hasPreRequisite() {
		return prerequisite != null;
	}

}
