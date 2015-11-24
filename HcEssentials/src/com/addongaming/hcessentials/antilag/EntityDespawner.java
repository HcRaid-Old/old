package com.addongaming.hcessentials.antilag;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.data.EntityList;

public class EntityDespawner implements Runnable {
	private JavaPlugin jp;
	private Player p = null;
	private List<String> entities = null;

	public EntityDespawner(JavaPlugin jp, List<String> entities) {
		this.jp = jp;
		this.entities = entities;
	}

	public EntityDespawner(JavaPlugin jp, Player p, List<String> entities) {
		this.jp = jp;
		this.p = p;
		this.entities = entities;
	}

	@Override
	public void run() {
		int arrow = 0;
		int entit = 0;
		for (World w : jp.getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (!canDespawn(e))
					continue;
				if (e.getType() == EntityType.ARROW) {
					if (e.getTicksLived() >= 20 * 30) {
						e.remove();
						arrow++;
					}
				} else if (e instanceof LivingEntity
						&& e.getType() != EntityType.PLAYER) {
					LivingEntity le = (LivingEntity) e;
					if (le.isLeashed()
							|| !EntityList.isAggressive(le.getType()))
						continue;
					if (le instanceof Ageable && !((Ageable) (le)).isAdult())
						continue;
					if (le.getHealth() == le.getMaxHealth()
							&& le.getTicksLived() > 20 * 60 * 5) {
						le.remove();
						entit++;
					} else if (le.getTicksLived() > 20 * 60 * 20) {
						le.remove();
						entit++;
					}
				}
			}
		}
		if (p != null)
			p.sendMessage(arrow + " arrows despawned as well as " + entit
					+ " entities.");
	}

	private boolean canDespawn(Entity e) {
		if (entities == null)
			return true;
		for (String str : entities) {
			if (e.getClass().getName().endsWith(str))
				return false;
		}
		return true;
	}
}
