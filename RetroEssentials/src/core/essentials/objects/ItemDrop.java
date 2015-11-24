package core.essentials.objects;

import org.bukkit.inventory.ItemStack;

public class ItemDrop {
	final ItemStack itemStack;
	final float chance;

	public ItemStack getItemStack() {
		return itemStack;
	}

	public float getChance() {
		return chance;
	}

	public ItemDrop(ItemStack itemStack, float chance) {
		this.itemStack = itemStack;
		this.chance = chance;
	}

}
