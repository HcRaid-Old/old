package com.addongaming.hcessentials.data;

import org.bukkit.entity.EntityType;

public enum EntityList {
	creeper("Creeper", EntityType.CREEPER, EntityStatus.AGGRESIVE), skeleton(
			"Skeleton", EntityType.SKELETON, EntityStatus.AGGRESIVE), spider(
			"Spider", EntityType.SPIDER, EntityStatus.AGGRESIVE), giant(
			"Giant", EntityType.GIANT, EntityStatus.AGGRESIVE), zombie(
			"Zombie", EntityType.ZOMBIE, EntityStatus.AGGRESIVE), slime(
			"Slime", EntityType.SLIME, EntityStatus.AGGRESIVE), ghast("Ghast",
			EntityType.GHAST, EntityStatus.AGGRESIVE), pigZombie("Pig Zombie",
			EntityType.PIG_ZOMBIE, EntityStatus.AGGRESIVE), enderman(
			"Enderman", EntityType.ENDERMAN, EntityStatus.PASSIVE), caveSpider(
			"Cave Spider", EntityType.CAVE_SPIDER, EntityStatus.AGGRESIVE), silverFish(
			"Silver fish", EntityType.SILVERFISH, EntityStatus.AGGRESIVE), blaze(
			"Blaze", EntityType.BLAZE, EntityStatus.AGGRESIVE), magmaCube(
			"Magma Cube", EntityType.MAGMA_CUBE, EntityStatus.AGGRESIVE), enderDragon(
			"Enderdragon", EntityType.ENDER_DRAGON, EntityStatus.AGGRESIVE), wither(
			"Wither", EntityType.WITHER, EntityStatus.AGGRESIVE), bat("Bat",
			EntityType.BAT, EntityStatus.AGGRESIVE), witch("Witch",
			EntityType.WITCH, EntityStatus.AGGRESIVE), pig("Pig",
			EntityType.PIG, EntityStatus.FRIENDLY), sheep("Sheep",
			EntityType.SHEEP, EntityStatus.FRIENDLY), cow("Cow",
			EntityType.COW, EntityStatus.FRIENDLY), chicken("Chicken",
			EntityType.CHICKEN, EntityStatus.FRIENDLY), squid("Squid",
			EntityType.SQUID, EntityStatus.FRIENDLY), wolf("Wolf",
			EntityType.WOLF, EntityStatus.PASSIVE), mooshroom("Mushroom cow",
			EntityType.MUSHROOM_COW, EntityStatus.FRIENDLY), snowman("Snowman",
			EntityType.SNOWMAN, EntityStatus.PASSIVE), ocelot("Ocelot",
			EntityType.OCELOT, EntityStatus.FRIENDLY), ironGolem("Iron golem",
			EntityType.IRON_GOLEM, EntityStatus.FRIENDLY), horse("Horse",
			EntityType.HORSE, EntityStatus.FRIENDLY), villager("Villager",
			EntityType.VILLAGER, EntityStatus.FRIENDLY), itemFrame(
			"Item Frame", EntityType.ITEM_FRAME, EntityStatus.FRIENDLY);

	private final String name;
	private final EntityType entityType;
	private final EntityStatus status;

	EntityList(String name, EntityType entityType, EntityStatus status) {
		this.name = name;
		this.entityType = entityType;
		this.status = status;
	}

	public EntityType getEntityType() {
		return this.entityType;
	}

	public EntityStatus getEntityStatus() {
		return this.status;
	}

	public static boolean isAggressive(EntityType et) {
		for (EntityList el : EntityList.values())
			if (el.getEntityType() == et)
				if (el.getEntityStatus() == EntityStatus.AGGRESIVE)
					return true;
				else
					return false;
		return false;
	}
}
