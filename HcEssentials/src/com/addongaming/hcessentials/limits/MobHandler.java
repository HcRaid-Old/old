package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

public class MobHandler implements Listener {
	private final List<EntityType> etList;
	private List<LivingEntity> id = new ArrayList<LivingEntity>();

	private final JavaPlugin jp;

	private int taskId = -1;

	public MobHandler(JavaPlugin jp, List<EntityType> disAllowed) {
		this.etList = disAllowed;
		this.jp = jp;
		taskId = jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {
					@Override
					public void run() {
						if (id.isEmpty())
							return;
						for (Iterator<LivingEntity> in = id.iterator(); in
								.hasNext();) {
							LivingEntity i = in.next();
							if (i != null && i.isValid() && !i.isDead()) {
								i.remove();
							}
							i=null;
							in.remove();
						}
					}
				}, 20l, 5l);
	}

	public void disable() {
		if (taskId > -1)
			jp.getServer().getScheduler().cancelTask(taskId);
	}

	@EventHandler
	public void entitySpawned(CreatureSpawnEvent event) {
		if (event.isCancelled()
				|| event.getSpawnReason() == SpawnReason.SPAWNER_EGG)
			return;
		for (EntityType et : etList) {
			if (event.getEntityType() == et) {
				id.add(event.getEntity());
				return;
			}
		}
	}

}
