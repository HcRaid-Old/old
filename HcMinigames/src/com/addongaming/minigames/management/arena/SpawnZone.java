package com.addongaming.minigames.management.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.utils.Utils;

public class SpawnZone extends LocationZone {
	private float yaw, pitch;

	public SpawnZone(Location min, Location max, float yaw, float pitch) {
		super(min, min.getBlockY() == max.getBlockY() ? max.getBlock()
				.getRelative(BlockFace.UP).getLocation() : max);
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Location getRandomFreeLocation() {
		Location toReturn = null;
		Random r = new Random();
		int differX = getMax().getBlockX() - getMin().getBlockX(), differY = getMax()
				.getBlockY() - getMin().getBlockY(), differZ = getMax()
				.getBlockZ() - getMin().getBlockZ();
		if (differX == 0)
			differX = 1;
		if (differY == 0)
			differY = 1;
		if (differZ == 0)
			differZ = 1;
		while (toReturn == null) {
			int x = (int) (getMin().getX() + (r.nextInt(differX))), y = (int) (getMin()
					.getY() + (r.nextInt(differY))), z = (int) (getMin().getZ() + (r
					.nextInt(differZ)));
			if (getMin().getX() == x && getMax().getX() != x + 1)
				x = x + 1;
			if (getMax().getX() == x && getMin().getX() != x - 1)
				x = x - 1;
			if (getMin().getZ() == z && getMax().getZ() != z + 1)
				z = z + 1;
			if (getMax().getZ() == z && getMin().getZ() != z - 1)
				z = z - 1;
			if (new Random().nextInt(10) % 2 == 0)
				y = y + 1;
			Block base = getMin().getWorld().getBlockAt(x, y, z);
			if (base.getType() == Material.AIR
					&& base.getRelative(BlockFace.UP).getType() == Material.AIR) {
				System.out.println("Found air");
				while (base.getType() == Material.AIR) {
					base = base.getRelative(BlockFace.DOWN);
					System.out.println(base.getType().name());
				}
				toReturn = base.getRelative(BlockFace.UP).getLocation();
				toReturn.setPitch(getPitch());
				toReturn.setYaw(getYaw());
				toReturn.setX(toReturn.getX() + 0.5);
				toReturn.setZ(toReturn.getZ() + 0.5);
				System.out.println("Location teleporting to " + x + ", " + y
						+ ", " + z);
				return toReturn;
			}

		}
		return null;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> myMap = new HashMap<String, Object>();
		Map<String, Object> superMap = super.serialize();
		for (String str : superMap.keySet())
			myMap.put(str, superMap.get(str));
		myMap.put("yaw", yaw);
		myMap.put("pitch", pitch);
		return myMap;
	}

	public static SpawnZone valueOf(Map<String, Object> myMap) {
		return new SpawnZone(Utils.loadLoc(String.valueOf(myMap.get("min"))),
				Utils.loadLoc(String.valueOf(myMap.get("max"))),
				Float.valueOf((double) (myMap.get("yaw")) + ""),
				Float.valueOf((double) (myMap.get("pitch")) + ""));
	}

	public String asString() {
		return "Min X:" + getMin().getBlockX() + " Y: " + getMin().getBlockY()
				+ " Z: " + getMin().getBlockZ() + " Max X:"
				+ getMax().getBlockX() + " Y: " + getMax().getBlockY() + " Z: "
				+ getMax().getBlockZ();
	}
}
