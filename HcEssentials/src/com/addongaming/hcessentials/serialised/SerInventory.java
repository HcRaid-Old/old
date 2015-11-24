package com.addongaming.hcessentials.serialised;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

public class SerInventory implements Serializable {

	private static final long serialVersionUID = -3124433113705919741L;

	private final SerItemStack[] inventory;

	/**
	 * A serializable implementation of Bukkit's ItemStack[] (Inventory) class.
	 * 
	 * @param inventory
	 */
	public SerInventory(final ItemStack[] inventory) {
		this.inventory = new SerItemStack[inventory.length];
		for (int pos = 0; pos < inventory.length; pos++) {
			if (inventory[pos] != null && inventory[pos].getAmount() != 0
					&& inventory[pos].getTypeId() != 0) {
				this.inventory[pos] = new SerItemStack(inventory[pos]);
			} else {
				continue;
			}
		}
	}

	/**
	 * Converts a SyncInventory object into a Bukkit ItemStack[] (Inventory)
	 * object.
	 * 
	 * @param syncInventory
	 * @return ItemStack[] (Inventory)
	 */
	public ItemStack[] getContents() {
		final ItemStack[] stack = new ItemStack[inventory.length];
		for (int pos = 0; pos < inventory.length; pos++) {
			if (inventory[pos] != null && inventory[pos].getAmount() != 0
					&& inventory[pos].getId() != 0) {
				stack[pos] = inventory[pos].getBukkitItemStack();
			} else {
				continue;
			}
		}
		return stack;
	}

	public static Object loadObject(final String path) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(path));
			final Object result = ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean saveObj(final Object obj, final String path) {
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}