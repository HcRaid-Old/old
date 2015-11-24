package com.addongaming.hcessentials.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Utils {
	public static int count(Inventory inventory, Material material) {
		int counter = 0;
		for (ItemStack is : inventory.getContents())
			if (is != null && is.getType() != null) {
				if (material == is.getType())
					counter += is.getAmount();
			}
		return counter;
	}

	public static int count(Player player, Material material) {
		return count(player.getInventory(), material);
	}

	public static void createRecipe(ItemStack finish,
			HashMap<Character, Material> materialMap, String... shape) {
		ShapedRecipe recipe = new ShapedRecipe(finish);
		for (char c : materialMap.keySet()) {
			recipe.setIngredient(c, materialMap.get(c));
		}
		recipe.shape(shape);
		Bukkit.getServer().addRecipe(recipe);
	}

	public static float getAngleBetweenVectors(Vector v1, Vector v2) {
		return Math.abs((float) Math.toDegrees(v1.angle(v2)));
	}

	public static Location getLocationBetweenTwoLocations(Location start,
			Location end, float distance) {
		if (end.getWorld() != start.getWorld())
			return null;
		double deltaX = end.getX() - start.getX();
		double deltaZ = end.getZ() - start.getZ();
		return start
				.getWorld()
				.getBlockAt((int) (start.getX() + distance * deltaX), 0,
						(int) (start.getZ() + distance * deltaZ)).getLocation();
	}

	public static Player[] getPlayersInsideCone(List<Entity> entities,
			Vector location, float radius, float degrees, Vector direction) {
		List<Player> newEntities = new ArrayList<Player>();
		float squaredRadius = radius * radius;
		for (Entity e : entities) {
			if (!(e instanceof Player)) {
				continue;
			}
			Vector relativePosition = e.getLocation().toVector();
			relativePosition.subtract(location);
			if (relativePosition.lengthSquared() > squaredRadius) {
				continue;
			}
			if (getAngleBetweenVectors(direction, relativePosition) > degrees) {
				continue;
			}
			newEntities.add((Player) e);
		}
		return newEntities.toArray(new Player[newEntities.size()]);
	}

	/**
	 * @param startPos
	 *            starting position
	 * @param radius
	 *            distance cone travels
	 * @param degrees
	 *            angle of cone
	 * @param direction
	 *            direction of the cone
	 * @return All block positions inside the cone
	 */
	public static List<Vector> getPositionsInCone(Vector startPos,
			float radius, float degrees, Vector direction) {

		List<Vector> positions = new ArrayList<Vector>();
		float squaredRadius = radius * radius;

		for (float x = startPos.getBlockX() - radius; x < startPos.getBlockX()
				+ radius; x++)
			for (float y = startPos.getBlockY() - radius; y < startPos
					.getBlockY() + radius; y++)
				for (float z = startPos.getBlockZ() - radius; z < startPos
						.getBlockZ() + radius; z++) {
					Vector relative = new Vector(x, y, z);
					relative.subtract(startPos);
					if (relative.lengthSquared() > squaredRadius)
						continue;
					if (getAngleBetweenVectors(direction, relative) > degrees)
						continue;
					positions.add(new Vector(x, y, z));
				}
		return positions;
	}

	/**
	 * @param player
	 *            List of nearby entities
	 * @param location
	 *            starting position
	 * @param Radius
	 *            distance cone travels
	 * @param Degrees
	 *            angle of cone
	 * @param direction
	 *            direction of the cone
	 * @return All entities inside the cone
	 */
	public static boolean isEntityInCone(Player e, Vector location,
			float radius, float degrees, Vector direction) {
		float squaredRadius = radius * radius;
		Vector relativePosition = e.getLocation().toVector();
		relativePosition.subtract(location);
		if (relativePosition.lengthSquared() > squaredRadius) {
			return false;
		}
		if (getAngleBetweenVectors(direction, relativePosition) > degrees) {
			return false;
		}
		return true;
	}

	public static Location loadLoc(String string) {
		String[] split = string.split("[|]");
		double x, y, z;
		float yaw = -1, pitch = -1;
		World world = Bukkit.getWorld(split[0]);
		x = Double.parseDouble(split[1]);
		y = Double.parseDouble(split[2]);
		z = Double.parseDouble(split[3]);
		if (split.length == 4)
			return new Location(world, x, y, z);
		else if (split.length == 6)
			return new Location(world, x, y, z, Float.parseFloat(split[4]),
					Float.parseFloat(split[5]));
		else
			return null;
	}

	public static String locationToSaveString(Location loc) {
		if (loc == null)
			return null;
		return loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY()
				+ "|" + loc.getZ() + "|" + loc.getYaw() + "|" + loc.getPitch();
	}

	public static String locationToString(Location loc) {
		if (loc == null)
			return null;
		return ("World: " + loc.getWorld().getName() + " X: " + loc.getBlockX()
				+ " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
	}

	@SuppressWarnings("deprecation")
	public static void removeFromInventory(Player player, ItemStack... items) {
		player.getInventory().removeItem(items);
		player.updateInventory();
	}

	public static List<ItemStack> reverseList(ItemStack[] toReverse) {
		List<ItemStack> reversed = new ArrayList<>();
		for (int i = toReverse.length - 1; i >= 0; i--) {
			reversed.add(toReverse[i]);
		}
		return reversed;
	}

	public static ItemStack setLore(ItemStack is, String... lore) {
		ItemMeta im = is.getItemMeta();
		List<String> itemLore = new ArrayList<String>();
		for (String str : lore)
			itemLore.add(str);
		im.setLore(itemLore);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack setName(String name, ItemStack is) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return is;
	}

	public static int getPlayerPing(Player player) {
		try {
			int ping = -1;
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit."
					+ getServerVersion() + "entity.CraftPlayer");
			Object converted = craftPlayer.cast(player);
			Method handle = converted.getClass().getMethod("getHandle");
			Object entityPlayer = handle.invoke(converted);
			Field pingField = entityPlayer.getClass().getField("ping");
			ping = pingField.getInt(entityPlayer);
			return ping;
		} catch (Exception e) {
			return -1;
		}
	}

	public static String getServerVersion() {
		Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
		String version = null;
		String pkg = Bukkit.getServer().getClass().getPackage().getName();
		String version0 = pkg.substring(pkg.lastIndexOf('.') + 1);
		if (!brand.matcher(version0).matches()) {
			version0 = "";
		}
		version = version0;
		return !"".equals(version) ? version + "." : "";
	}

	public static String itemToDebug(ItemStack is) {
		StringBuilder sb = new StringBuilder();
		sb.append(is.getType().name().toLowerCase() + " #" + is.getAmount()+" ");
		if(is.hasItemMeta()){
			ItemMeta im  = is.getItemMeta();
			if(im.hasDisplayName())
				sb.append(im.getDisplayName() + " ");
			if(im.hasEnchants())
				for(Enchantment e:im.getEnchants().keySet())
					sb.append(e.getName() + " lvl " + im.getEnchantLevel(e)+" | ");
		}
		return sb.toString();
	}
}
