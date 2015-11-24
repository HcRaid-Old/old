package com.addongaming.minigames.management.arena;

import org.bukkit.entity.Player;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.minigames.core.HcMinigames;
import com.sk89q.worldedit.bukkit.selections.Selection;

public enum ArenaProperty {
	ARENA(LocationZone.class, null), BLOCK_BREAK(Boolean.class, false), BLOCK_PLACE(
			Boolean.class, false), SCORE_PER_KILL(Integer.class, 50), MAX_LIVES(
			Integer.class, 5), INVENTORY_OPEN(Boolean.class, false), INVENTORY_CLICK(
			Boolean.class, false), ARMOUR_REMOVABLE(Boolean.class, false), HUNGER_REGEN_HEALTH(
			Boolean.class, false), HUNGER_19(Boolean.class, true), PORK_CHOP_HEALTH(
			Boolean.class, true), PORK_CHOP_INCREASE(Integer.class, 6), FRIENDLY_FIRE_ENABLED(
			Boolean.class, false), ITEM_DROP(Boolean.class, false), PARTITION_WALL_RED(
			LocationZone.class, null), PARTITION_WALL_BLUE(LocationZone.class,
			null), PARTITION_TIMER(Integer.class, 60 * 10 * 20), RED_TEAM_CHOOSE(
			SpawnZone.class, null), BLUE_TEAM_CHOOSE(SpawnZone.class, null), BLUE_TEAM_SIDE(
			LocationZone.class, null), RED_TEAM_SIDE(LocationZone.class, null), INSTANT_KILL(
			Boolean.class, false), THE_SHIP_WIN(Integer.class, 25000), PUNCH_DAMAGE(
			Boolean.class, true);
	private Class<?> propertyClass;
	private Object defaultValue;
	private static HcMinigames minigames;

	public static void setMinigames(HcMinigames minigames) {
		ArenaProperty.minigames = minigames;
	}

	ArenaProperty(Class<?> propertyClass, Object defaultValue) {
		this.propertyClass = propertyClass;
		this.defaultValue = defaultValue;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	public static boolean isValid(ArenaProperty ap, Player player, String value) {
		try {
			if (ap.getPropertyClass() == Boolean.class)
				Boolean.valueOf(value);
			else if (ap.getPropertyClass() == String.class)
				String.valueOf(value);
			else if (ap.getPropertyClass() == Integer.class)
				Integer.valueOf(value);
			else if (ap.getPropertyClass() == Double.class)
				Double.valueOf(value);
			else if (ap.getPropertyClass() == SpawnZone.class
					|| ap.getPropertyClass() == LocationZone.class) {
				Selection sel = minigames.getWorldEditHook().getSelection(
						player);
				if (sel == null || sel.getMinimumPoint() == null
						|| sel.getMaximumPoint() == null) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static Object getObject(ArenaProperty ap, Player player, String value) {
		try {
			if (ap.getPropertyClass() == Boolean.class)
				return Boolean.valueOf(value);
			else if (ap.getPropertyClass() == String.class)
				return String.valueOf(value);
			else if (ap.getPropertyClass() == Integer.class)
				return Integer.valueOf(value);
			else if (ap.getPropertyClass() == Double.class)
				return Double.valueOf(value);
			else if (ap.getPropertyClass() == SpawnZone.class) {
				Selection sel = minigames.getWorldEditHook().getSelection(
						player);
				if (sel == null || sel.getMinimumPoint() == null
						|| sel.getMaximumPoint() == null) {
					return null;
				}
				return new SpawnZone(sel.getMinimumPoint(),
						sel.getMaximumPoint(),
						player.getEyeLocation().getYaw(), player
								.getEyeLocation().getPitch());
			} else if (ap.getPropertyClass() == LocationZone.class) {
				Selection sel = minigames.getWorldEditHook().getSelection(
						player);
				if (sel == null || sel.getMinimumPoint() == null
						|| sel.getMaximumPoint() == null) {
					return null;
				}
				return new LocationZone(sel.getMinimumPoint(),
						sel.getMaximumPoint());
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static ArenaProperty getByName(String name) {
		for (ArenaProperty ap : values())
			if (ap.name().equalsIgnoreCase(name))
				return ap;
		return null;
	}
}
