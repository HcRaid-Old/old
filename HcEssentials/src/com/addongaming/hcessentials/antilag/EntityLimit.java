package com.addongaming.hcessentials.antilag;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class EntityLimit implements Listener {

	private final int bedrockLimit = 75;
	private final int limit = 200;

	@EventHandler(priority = EventPriority.NORMAL)
	public void mobSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled() || event.getSpawnReason() == SpawnReason.EGG
				|| !shouldBeCounted(event.getEntityType()))
			return;
		List<Entity> entityList = event.getEntity().getNearbyEntities(24, 50,
				24);
		entityList = sanatiseList(entityList);
		if (entityList.size() > bedrockLimit
				&& event.getEntity().getWorld().getName().contains("bedrock"))
			event.setCancelled(true);
		if (entityList.size() > limit)
			event.setCancelled(true);
	}

	private List<Entity> sanatiseList(List<Entity> entityList) {
		for (Iterator<Entity> iter = entityList.iterator(); iter.hasNext();) {
			Entity e = iter.next();
			if (!shouldBeCounted(e.getType()))
				iter.remove();
		}
		return entityList;
	}

	private boolean shouldBeCounted(EntityType type) {
		EntityType toClear[] = { EntityType.ARROW, EntityType.DROPPED_ITEM,
				EntityType.EXPERIENCE_ORB, EntityType.ITEM_FRAME,
				EntityType.PAINTING, EntityType.MINECART,
				EntityType.MINECART_CHEST };
		for (EntityType check : toClear)
			if (type == check)
				return false;
		return true;
	}
}
