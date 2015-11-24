package core.syncitems;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

public class SyncInventory implements Serializable {

	private static final long serialVersionUID = -3124433113705919741L;

	private final SyncItemStack[] inventory = new SyncItemStack[54];

	/**
	 * A serializable implementation of Bukkit's ItemStack[] (Inventory) class.
	 * 
	 * @param inventory
	 */
	public SyncInventory(final ItemStack[] inventory) {
		for (int pos = 0; pos < inventory.length; pos++) {
			if (inventory[pos] != null && inventory[pos].getAmount() != 0 && inventory[pos].getTypeId() != 0) {
				this.inventory[pos] = new SyncItemStack(inventory[pos]);
			} else {
				continue;
			}
		}
	}

	/**
	 * Converts a SyncInventory object into a Bukkit ItemStack[] (Inventory) object.
	 * 
	 * @param syncInventory
	 * @return ItemStack[] (Inventory)
	 */
	public ItemStack[] getContents() {
		final ItemStack[] stack = new ItemStack[inventory.length];
		for (int pos = 0; pos < inventory.length; pos++) {
			if (inventory[pos] != null && inventory[pos].getAmount() != 0 && inventory[pos].getId() != 0) {
				stack[pos] = inventory[pos].getBukkitItemStack();
			} else {
				continue;
			}
		}
		return stack;
	}

}