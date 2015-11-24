package com.addongaming.hcessentials.special;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class SpecialDrops implements SubPlugin, Listener {
	private JavaPlugin jp;

	public SpecialDrops(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("specialdrops.enabled", Boolean.FALSE);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("specialdrops.enabled"))
			return false;
		System.out.println("Enabled drops");
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void breakblock(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.STONE) {
			int rand = new Random().nextInt(100);
			if (rand == 1) {
				e.getBlock()
						.getWorld()
						.dropItem(e.getBlock().getLocation(),
								new ItemStack(Material.DIAMOND));
			}

		} else if (e.getBlock().getType() == Material.LEAVES
				|| e.getBlock().getType() == Material.LEAVES_2) {
			if ((e.getPlayer().getItemInHand() != null)
					&& (e.getPlayer().getItemInHand().getType() == Material.SHEARS)) {
				return;
			}
			ItemStack is;
			int randomNumber = new Random().nextInt(9999) + 1;
			if (randomNumber == 1) {
				is = new ItemStack(Material.DIAMOND_CHESTPLATE);
				is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				is = setName(is, ChatColor.GOLD + "Awesome Chestplate");
				dropItem(is, e.getBlock().getLocation());
				is = new ItemStack(Material.DIAMOND_HELMET);
				is = setName(is, ChatColor.GOLD + "Awesome Helmet");
				is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				dropItem(is, e.getBlock().getLocation());
				is = new ItemStack(Material.DIAMOND_LEGGINGS);
				is = setName(is, ChatColor.GOLD + "Awesome Leggings");
				is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				dropItem(is, e.getBlock().getLocation());
				is = new ItemStack(Material.DIAMOND_BOOTS);
				is = setName(is, ChatColor.GOLD + "Awesome Boots");
				is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
				dropItem(is, e.getBlock().getLocation());
			} else if (randomNumber <= 600) {
				is = new ItemStack(Material.GOLDEN_APPLE);
				dropItem(is, e.getBlock().getLocation());
			} else if (randomNumber <= 3000) {
				is = new ItemStack(Material.APPLE);
				dropItem(is, e.getBlock().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void breakCactus(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.CACTUS)) {
			int rand = getRandom(500);
			int rand2 = getRandom(4);
			ItemStack is = null;
			if (rand == 1) {
				switch (rand2) {
				case 1:
					is = new ItemStack(Material.DIAMOND_HELMET);
					break;
				case 2:
					is = new ItemStack(Material.DIAMOND_CHESTPLATE);
					break;
				case 3:
					is = new ItemStack(Material.DIAMOND_LEGGINGS);
					break;
				case 4:
					is = new ItemStack(Material.DIAMOND_BOOTS);
					break;
				default:
					is = new ItemStack(Material.DIAMOND_HELMET);
				}
				is.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				is.addEnchantment(Enchantment.THORNS, 2);
				is.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
				is.addEnchantment(Enchantment.PROTECTION_FALL, 2);
				is.addEnchantment(Enchantment.PROTECTION_FIRE, 2);
				is.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
				is.addEnchantment(Enchantment.DURABILITY, 2);
				setName(is, ChatColor.RED + "Cactus Power!");
				dropItem(is, e.getBlock().getLocation());
			} else if (rand <= 5) {
				rand2 = getRandom(4);
				switch (rand2) {
				case 1:
					is = new ItemStack(Material.IRON_HELMET);
					break;
				case 2:
					is = new ItemStack(Material.IRON_CHESTPLATE);
					break;
				case 3:
					is = new ItemStack(Material.IRON_LEGGINGS);
					break;
				case 4:
					is = new ItemStack(Material.IRON_BOOTS);
					break;
				default:
					is = new ItemStack(Material.IRON_HELMET);
				}
				is.addEnchantment(Enchantment.THORNS, 2);
				setName(is, ChatColor.BLUE + "Cactus Power!");
				dropItem(is, e.getBlock().getLocation());
			} else if (rand <= 1) {
				is = new ItemStack(Material.DIAMOND_SWORD);
				is.addEnchantment(Enchantment.DAMAGE_ALL, 4);
				setName(is, ChatColor.RED + "**** Legend.");
				dropItem(is, e.getBlock().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void breakReed(BlockBreakEvent e) {

		if (e.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)) {
			int rand = getRandom(100);
			int rand2 = getRandom(10);
			ItemStack is = null;
			if (rand <= 5) {
				switch (rand2) {
				case 1:
					is = new ItemStack(Material.REDSTONE_WIRE);
					break;
				case 2:
					is = new ItemStack(Material.SULPHUR);
					break;
				case 3:
					is = new ItemStack(Material.SUGAR);
					break;
				case 4:
					is = new ItemStack(Material.GLOWSTONE_DUST);
					break;
				case 5:
					is = new ItemStack(Material.NETHER_WARTS);
					break;
				case 6:
					is = new ItemStack(Material.GLOWSTONE_DUST);
					break;
				case 7:
					is = new ItemStack(Material.NETHER_WARTS);
					break;
				case 8:
					is = new ItemStack(Material.GHAST_TEAR);
					break;
				case 9:
					is = new ItemStack(Material.BLAZE_POWDER);
					break;
				case 10:
					is = new ItemStack(Material.MAGMA_CREAM);
					break;
				case 11:
					is = new ItemStack(Material.FERMENTED_SPIDER_EYE);
					break;
				case 12:
					is = new ItemStack(Material.SPIDER_EYE);
					break;
				case 13:
					is = new ItemStack(Material.SPECKLED_MELON);
				}
			}
			if (is != null)
				dropItem(is, e.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void breakPumpkin(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.PUMPKIN)) {
			int rand = getRandom(100);
			int rand2 = getRandom(10);
			ItemStack is = null;
			if (rand <= 5) {
				switch (rand2) {
				case 1:
					is = new ItemStack(Material.IRON_INGOT);
					break;
				case 2:
					is = new ItemStack(Material.ARROW);
					break;
				case 3:
					is = new ItemStack(Material.EGG);
					break;
				case 4:
					is = new ItemStack(Material.WOOD_BUTTON);
					break;
				case 5:
					is = new ItemStack(Material.STEP);
					break;
				case 6:
					is = new ItemStack(Material.BAKED_POTATO);
					break;
				case 7:
					is = new ItemStack(Material.YELLOW_FLOWER);
					break;
				case 8:
					is = new ItemStack(Material.BONE);
					break;
				case 9:
					is = new ItemStack(Material.TORCH);
					break;
				case 10:
					is = new ItemStack(Material.DAYLIGHT_DETECTOR);
					break;
				case 11:
					is = new ItemStack(Material.PORK);
					break;
				case 12:
					is = new ItemStack(Material.FLINT);
					break;
				case 13:
					is = new ItemStack(Material.VINE);
				}
			}

			if (is != null)
				dropItem(is, e.getBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void breakMelon(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.MELON_BLOCK)) {
			int rand = getRandom(100);
			int rand2 = getRandom(10);
			ItemStack is = null;
			if (rand <= 5) {
				switch (rand2) {
				case 1:
					is = new ItemStack(Material.IRON_INGOT);
					break;
				case 2:
					is = new ItemStack(Material.ARROW);
					break;
				case 3:
					is = new ItemStack(Material.EGG);
					break;
				case 4:
					is = new ItemStack(Material.WOOD_BUTTON);
					break;
				case 5:
					is = new ItemStack(Material.STEP);
					break;
				case 6:
					is = new ItemStack(Material.BAKED_POTATO);
					break;
				case 7:
					is = new ItemStack(Material.YELLOW_FLOWER);
					break;
				case 8:
					is = new ItemStack(Material.BONE);
					break;
				case 9:
					is = new ItemStack(Material.TORCH);
					break;
				case 10:
					is = new ItemStack(Material.DAYLIGHT_DETECTOR);
					break;
				case 11:
					is = new ItemStack(Material.PORK);
					break;
				case 12:
					is = new ItemStack(Material.FLINT);
					break;
				case 13:
					is = new ItemStack(Material.VINE);
				}
			}
			if (is != null)
				dropItem(is, e.getBlock().getLocation());
		}
	}

	private int getRandom(int i) {
		return new Random().nextInt(i) + 1;
	}

	private ItemStack setName(ItemStack is, String string) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(string);
		is.setItemMeta(im);
		return is;
	}

	private void dropItem(ItemStack is, Location lov) {
		Bukkit.getWorld(lov.getWorld().getName()).dropItemNaturally(lov, is);
	}
}
