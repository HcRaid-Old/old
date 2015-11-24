package com.addongaming.minigames.hub.pets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public enum Pets {
	chicken(EntityType.CHICKEN, "hcraid.pet.chicken", 750, (short) 93, false), ocelot(
			EntityType.OCELOT, "hcraid.pet.ocelot", 1500, (short) 98, false), pig(
			EntityType.PIG, "hcraid.pet.pig", 1400, (short) 90, true), slime(
			EntityType.SLIME, "hcraid.pet.slime", 500, (short) 55, true), blaze(
			EntityType.BLAZE, "hcraid.pet.blaze", 8000, (short) 61, true), mooshroom(
			EntityType.MUSHROOM_COW, "hcraid.pet.mooshroom", 6000, (short) 96,
			true), enderdragon(EntityType.ENDER_DRAGON,
			"hcraid.pet.enderdragon", 1, (short) 92, false), wither(
			EntityType.WITHER, "hcraid.pet.wither", 600, (short) 91, false);

	private EntityType entityType;
	private String permNode;
	private int cost;
	private short dura;
	private boolean premium;

	Pets(EntityType entityType, String permNode, int cost, short dura,
			boolean premium) {
		this.entityType = entityType;
		this.permNode = permNode;
		this.cost = cost;
		this.dura = dura;
		this.premium = premium;
	}

	public boolean isPremium() {
		return premium;
	}

	public int getCost() {
		return cost;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public String getPermNode() {
		return permNode;
	}

	public static Pets getByName(String name) {
		for (Pets pet : Pets.values())
			if (pet.getName().equalsIgnoreCase(name))
				return pet;
		return null;
	}

	public String getName() {
		StringBuilder sb = new StringBuilder();
		sb.append(name());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	public short getEggDura() {
		return dura;
	}

	public static Pets[] getAllowedPets(Player player) {
		List<Pets> pets = new ArrayList<Pets>();
		for (Pets pet : Pets.values())
			if (player.hasPermission(pet.getPermNode()))
				pets.add(pet);
		return pets.toArray(new Pets[pets.size()]);
	}
}
