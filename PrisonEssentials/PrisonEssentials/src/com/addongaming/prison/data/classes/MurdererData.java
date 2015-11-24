package com.addongaming.prison.data.classes;

import org.bukkit.Material;

public enum MurdererData {
	DEEP_CUT_1("Level 1 Deep Cut", "Deal up to x2 damage", Material.IRON_INGOT,
			0, 0, "prison.assassin.pickpocket1", null), DEEP_CUT_2(
			"Level 2 Deep Cut", "Deal up to x3 damage", Material.GOLD_INGOT,
			10, 2000, "prison.assassin.pickpocket2", MurdererData.DEEP_CUT_1), DEEP_CUT_3(
			"Level 3 Deep Cut", "Deal up to x4 damage", Material.EMERALD, 25,
			4000, "prison.assassin.pickpocket3", MurdererData.DEEP_CUT_2), DEEP_CUT_4(
			"Deep Cut Master", "Deal up to x6 damage", Material.DIAMOND, 60,
			100000, "prison.assassin.pickpocket4", MurdererData.DEEP_CUT_3);
	public static MurdererData getDataByMaterial(Material type) {
		for (MurdererData td : values())
			if (td.getShopId() == type)
				return td;
		return null;
	}
	private int classLevel;
	private int cost;
	private String permission;
	private MurdererData prerequisite;
	private Material shopId;
	private String text;

	private String title;

	MurdererData(String title, String text, Material shopId, int classLevel,
			int cost, String permission, MurdererData pre) {
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

	public MurdererData getPrerequisite() {
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
