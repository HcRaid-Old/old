package com.addongaming.prison.data.classes;

import org.bukkit.Material;

public enum AssassinData {
	SNEAK_ATTACK_1("Level 1 Sneak Attack", "Deal up to x2 damage",
			Material.IRON_INGOT, 0, 0, "prison.assassin.pickpocket1", null), SNEAK_ATTACK_2(
			"Level 2 Sneak Attack", "Deal up to x3 damage",
			Material.GOLD_INGOT, 10, 2000, "prison.assassin.pickpocket2",
			AssassinData.SNEAK_ATTACK_1), SNEAK_ATTACK_3(
			"Level 3 Sneak Attack", "Deal up to x4 damage", Material.EMERALD,
			25, 4000, "prison.assassin.pickpocket3",
			AssassinData.SNEAK_ATTACK_2), SNEAK_ATTACK_4("Sneak Attack Master",
			"Deal up to x6 damage", Material.DIAMOND, 60, 100000,
			"prison.assassin.pickpocket4", AssassinData.SNEAK_ATTACK_3);
	public static AssassinData getDataByMaterial(Material type) {
		for (AssassinData td : values())
			if (td.getShopId() == type)
				return td;
		return null;
	}
	private int classLevel;
	private int cost;
	private String permission;
	private AssassinData prerequisite;
	private Material shopId;
	private String text;

	private String title;

	AssassinData(String title, String text, Material shopId, int classLevel,
			int cost, String permission, AssassinData pre) {
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

	public AssassinData getPrerequisite() {
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
