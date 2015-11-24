package com.addongaming.hcessentials.items;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface InfCustomItem extends Listener {
	/**
	 * Gets the name for the custom item
	 * 
	 * @return Name of the custom item
	 */
	public String getName();

	/**
	 * Gets an ItemStack with all the names/lore for the custom item
	 * 
	 * @return ItemStack representation of the custom item.
	 */
	public ItemStack getItem();

	/**
	 * Checks to see whether or not a player can buy this custom item - it's
	 * down to the custom item on how it handles this.
	 * 
	 * @return String of message to send to player on fail, null if successful
	 */
	public String canBuy(Player player);

	/**
	 * Checks to see if a given ItemStack represents that of the custom item
	 * 
	 * @param is
	 *            ItemStack to check
	 * @return @true if is matches @false if is doesn't match
	 */
	public boolean isItem(ItemStack is);

	/**
	 * Checks to see whether the custom item is enabled or not
	 * 
	 * @return @true if the custom item is enabled @false if the custom item
	 *         isn't enabled
	 */
	public boolean isEnabled();

	/**
	 * Gets a description of the custom item, useful for thing like signs.
	 * 
	 * @return Description of the custom item
	 */
	public String getDescription();
}
