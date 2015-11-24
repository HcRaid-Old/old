package core.essentials.enums;


import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import core.essentials.objects.ItemDrop;

public enum EEntity {
	zombie(EntityType.ZOMBIE, new ItemDrop[] { new ItemDrop(new ItemStack(
			Material.FEATHER, 1), 0.3f) }), chicken(EntityType.CHICKEN,
			new ItemDrop[] { new ItemDrop(new ItemStack(Material.FEATHER, 1),
					0.3f) }), wolve(EntityType.WOLF,
			new ItemDrop[] { new ItemDrop(new ItemStack(Material.FEATHER, 1),
					0.3f) }), cow(EntityType.COW,
			new ItemDrop[] { new ItemDrop(new ItemStack(Material.LEATHER, 1),
					0.3f) }), spider(EntityType.SPIDER,
			new ItemDrop[] { new ItemDrop(new ItemStack(Material.STRING, 1),
					0.3f) }), skeleton(EntityType.SKELETON, new ItemDrop[] {
			new ItemDrop(new ItemStack(Material.ARROW, 1), 0.32f),
			new ItemDrop(new ItemStack(Material.BONE, 1), 0.4f) }), pigman(
			EntityType.PIG_ZOMBIE, new ItemDrop[] { new ItemDrop(new ItemStack(
					Material.GRILLED_PORK, 1), 0.5f) }), pig(EntityType.PIG,
			new ItemDrop[] { new ItemDrop(new ItemStack(Material.PORK), 0.4f) });
	EntityType entity;

	public EntityType getEntityType() {
		return entity;
	}

	public ItemDrop[] getDrops() {
		return drops;
	}

	ItemDrop[] drops;

	EEntity(EntityType et, ItemDrop[] drops) {
		this.entity = et;
		this.drops = drops;
	}

	public static boolean isEntity(EntityType toCheck) {
		for (EEntity e : EEntity.values()) {
			if (e.getEntityType() == toCheck)
				return true;
		}
		return false;
	}

	public static EEntity getEntity(EntityType toCheck) {
		for (EEntity e : EEntity.values()) {
			if (e.getEntityType() == toCheck)
				return e;
		}
		return null;
	}
}
