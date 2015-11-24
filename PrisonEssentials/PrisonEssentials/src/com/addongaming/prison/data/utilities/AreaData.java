package com.addongaming.prison.data.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public enum AreaData {
	BREEDFARM1("breedfarmentrance", "Beginners Reed Farm", "Beginner",
			Material.SUGAR_CANE, 500, 5, "prison.access.breedfarm"), BREEDFARM2(
			"breedfarmentrance2", "Beginners Reed Farm", "Beginner", null, 10,
			10, "prison.access.breedfarm"), BWHEATFARM("bwheatfarmentrance",
			"Beginners Wheat Farm", "Beginner", Material.WHEAT, 750, 9,
			"prison.access.bwheatfarm"), BPUMPKINFARM("bpumpkinfarmentrance",
			"Beginners Pumpkin Farm", "Beginner", Material.PUMPKIN, 500, 5,
			"prison.access.bpumpkinfarm"), DANGEROUS_MINE(
			"dangerousmineentrance", "Dangerous Mine", "Beginner",
			Material.IRON_PICKAXE, 500, 7, "prison.access.dangerousmine"), HAZARD_MINE(
			"harzardmineentrance", "Hazard Mine", "Beginner",
			Material.GOLD_PICKAXE, 2500, 15, "prison.access.hazardmine"), LOST_CAUSE_MINE(
			"lostcausemineentrance", "Lost Cause Mine", "Beginner",
			Material.DIAMOND_PICKAXE, 4000, 25, "prison.access.lostcausemine"), BANK1(
			"beginnerbankentrance", "Beginners Bank", "Beginner",
			Material.GOLD_INGOT, 750, 10, "prison.access.beginnerbank"), SURFACEMINE1(
			"surfacemineentrance1", "Surface Mine", "Kolguyev",
			Material.GLOWSTONE, 5000, 40, "prison.access.surfacemine"), SURFACEMINE2(
			"surfacemineentrance2", "Surface Mine", "Kolguyev", null, 5000, 40,
			"prison.access.surfacemine"), SMALLWHEAT("smallwheatfarmentrance",
			"Small Wheat Farm", "Kolguyev", Material.WHEAT, 2000, 25,
			"prison.access.smallwheat"), MEDWHEAT("medwheatentrance",
			"Medium Wheat Farm", "Kolguyev", Material.WHEAT, 3000, 35,
			"prison.access.medwheat"), MELONFARM("melonfarmentrance",
			"Melon Farm", "Kolguyev", Material.MELON_BLOCK, 6000, 45,
			"prison.access.melon"), MEDCACTUS("medcactusfarmentrance",
			"Medium Cactus Farm", "Kolguyev", Material.CACTUS, 5000, 35,
			"prison.access.medcactus"), MEDTREEFARM("medtreefarmentrance",
			"Medium Tree Farm", "Kolguyev", Material.LOG_2, 8000, 45,
			"prison.access.medtreefarm"), MREEDFARM("mediumreedfarmentrance",
			"Medium Reed Farm", "Kolguyev", Material.SUGAR_CANE, 4000, 30,
			"prison.access.medreedfarm");
	public static AreaData getAreaByMaterial(Material type) {
		for (AreaData cd : values())
			if (cd.getShopid() == type)
				return cd;
		return null;
	}

	public static AreaData getAreaByName(String name) {
		for (AreaData cd : values())
			if (cd.getRegion().equalsIgnoreCase(name))
				return cd;
		return null;
	}

	public static AreaData getAreaByShopName(String name) {
		for (AreaData cd : values())
			if (cd.getName() == name)
				return cd;
		return null;
	}

	public static DataReturn hasPermission(Player p, String id) {
		AreaData ad = getAreaByName(id);
		if (ad == null)
			return DataReturn.SUCCESS;
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (PlayerClasses.getLevel(prisoner.getCharacterExp()) < ad
				.getCharLevel())
			return DataReturn.NOLEVEL;
		else if (!prisoner.hasPermission(ad.getPermission()))
			return DataReturn.NOPERM;
		else
			return DataReturn.SUCCESS;
	}

	public static void setupLocations(JavaPlugin jp) {
		for (AreaData ad : AreaData.values()) {
			if (jp.getConfig().contains("prison.area." + ad.getRegion())) {
				ad.setLocation(Utils.loadLoc(jp.getConfig().getString(
						"prison.area." + ad.getRegion())));
			} else {
				Location temp = new Location(Bukkit.getWorld("world"), 0.0,
						0.0, 0.0);
				ad.setLocation(temp);
				jp.getConfig().set("prison.area." + ad.getRegion(),
						Utils.locationToSaveString(temp));

			}
		}
		jp.saveConfig();
	}

	private int charLevel;
	private int cost;
	private String island;
	private String name;

	private String permission;

	private String region;

	private Location safeTeleport;

	private Material shopid;

	AreaData(String region, String name, String island, Material shopid,
			int cost, int charLevel, String permission) {
		this.island = island;
		this.region = region;
		this.name = name;
		this.cost = cost;
		this.charLevel = charLevel;
		this.shopid = shopid;
		this.permission = permission;
	}

	public int getCharLevel() {
		return charLevel;
	}

	public int getCost() {
		return cost;
	}

	public String getPrison() {
		return island;
	}

	public String getName() {
		return name;
	}

	public String getPermission() {
		return permission;
	}

	public String getRegion() {
		return region;
	}

	public Location getSafeLocation() {
		return safeTeleport;
	}

	public Material getShopid() {
		return shopid;
	}

	private void setLocation(Location loadLoc) {
		this.safeTeleport = loadLoc;
	}

}
