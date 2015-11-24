package com.addongaming.minigames.management.scheduling;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;

public class SchedEntityRemover implements Runnable {
	private Entity entity;

	public SchedEntityRemover(Entity entity) {
		this.entity = entity;
	}

	public SchedEntityRemover(Projectile entity) {
		this.entity = (Entity) entity;
	}

	@Override
	public void run() {
		if (!entity.isDead() && entity.isValid())
			entity.remove();
	}

}
