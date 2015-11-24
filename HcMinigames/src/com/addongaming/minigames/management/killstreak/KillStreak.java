package com.addongaming.minigames.management.killstreak;

import java.io.Serializable;

public interface KillStreak extends Serializable {
	public enum Type {
		ITEM, PET
	};

	/**
	 * Gets the killstreaks type, either Type.ITEM or Type.PET <br/>
	 * Item you will be able to cast to ItemKillStreak and obtain the
	 * SyncInventory <br/>
	 * Pet you will need to spawn in AI/Entity in the child class.
	 * 
	 * @return The
	 */
	public Type getType();

	public int getNeededKills();
}
