package core.essentials.limit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.enums.EFood;
import core.essentials.objects.Config;

public class CraftingHandler implements Listener {
	private final JavaPlugin jp;

	public CraftingHandler(JavaPlugin jp) {
		this.jp = jp;
	}

	private final String negTitle = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "RetroPvP" + ChatColor.DARK_RED + "] " + ChatColor.RESET;
	@SuppressWarnings("unused")
	private final String postTitle = ChatColor.AQUA + "[" + ChatColor.GOLD
			+ "RetroPvP" + ChatColor.AQUA + "] " + ChatColor.RESET;

	Material[] allowedItems = { Material.WOOD_STEP, Material.AIR,
			Material.STONE, Material.GRASS, Material.DIRT,
			Material.COBBLESTONE, Material.WOOD, Material.SAPLING,
			Material.BEDROCK, Material.WATER, Material.STATIONARY_WATER,
			Material.LAVA, Material.STATIONARY_LAVA, Material.SAND,
			Material.GRAVEL, Material.GOLD_ORE, Material.IRON_ORE,
			Material.COAL_ORE, Material.LOG, Material.LEAVES, Material.SPONGE,
			Material.GLASS, Material.LAPIS_ORE, Material.LAPIS_BLOCK,
			Material.DISPENSER, Material.SANDSTONE, Material.NOTE_BLOCK,
			Material.BED_BLOCK, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
			Material.PISTON_STICKY_BASE, Material.LONG_GRASS,
			Material.DEAD_BUSH, Material.PISTON_BASE,
			Material.PISTON_EXTENSION, Material.WOOL,
			Material.PISTON_MOVING_PIECE, Material.YELLOW_FLOWER,
			Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
			Material.GOLD_BLOCK, Material.IRON_BLOCK, Material.DOUBLE_STEP,
			Material.STEP, Material.BRICK, Material.TNT, Material.BOOKSHELF,
			Material.MOSSY_COBBLESTONE, Material.OBSIDIAN, Material.TORCH,
			Material.FIRE, Material.MOB_SPAWNER, Material.WOOD_STAIRS,
			Material.CHEST, Material.REDSTONE_WIRE, Material.DIAMOND_ORE,
			Material.DIAMOND_BLOCK, Material.WORKBENCH, Material.CROPS,
			Material.SOIL, Material.FURNACE, Material.BURNING_FURNACE,
			Material.SIGN_POST, Material.WOODEN_DOOR, Material.LADDER,
			Material.RAILS, Material.COBBLESTONE_STAIRS, Material.WALL_SIGN,
			Material.LEVER, Material.STONE_PLATE, Material.IRON_DOOR_BLOCK,
			Material.WOOD_PLATE, Material.REDSTONE_ORE,
			Material.GLOWING_REDSTONE_ORE, Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW,
			Material.ICE, Material.SNOW_BLOCK, Material.CACTUS, Material.CLAY,
			Material.SUGAR_CANE_BLOCK, Material.JUKEBOX, Material.FENCE,
			Material.PUMPKIN, Material.NETHERRACK, Material.SOUL_SAND,
			Material.GLOWSTONE, Material.PORTAL, Material.JACK_O_LANTERN,
			Material.CAKE_BLOCK, Material.DIODE_BLOCK_OFF,
			Material.DIODE_BLOCK_ON, Material.TRAP_DOOR, Material.BRICK_STAIRS,
			Material.SMOOTH_STAIRS, Material.IRON_SPADE, Material.IRON_PICKAXE,
			Material.IRON_AXE, Material.FLINT_AND_STEEL, Material.APPLE,
			Material.BOW, Material.ARROW, Material.COAL, Material.DIAMOND,
			Material.IRON_INGOT, Material.GOLD_INGOT, Material.IRON_SWORD,
			Material.WOOD_SWORD, Material.WOOD_SPADE, Material.WOOD_PICKAXE,
			Material.WOOD_AXE, Material.STONE_SWORD, Material.STONE_SPADE,
			Material.STONE_PICKAXE, Material.STONE_AXE, Material.DIAMOND_SWORD,
			Material.DIAMOND_SPADE, Material.DIAMOND_PICKAXE,
			Material.DIAMOND_AXE, Material.STICK, Material.BOWL,
			Material.MUSHROOM_SOUP, Material.GOLD_SWORD, Material.GOLD_SPADE,
			Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.STRING,
			Material.FEATHER, Material.SULPHUR, Material.WOOD_HOE,
			Material.STONE_HOE, Material.IRON_HOE, Material.DIAMOND_HOE,
			Material.GOLD_HOE, Material.SEEDS, Material.WHEAT, Material.BREAD,
			Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.GOLD_HELMET, Material.GOLD_CHESTPLATE,
			Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.FLINT,
			Material.PORK, Material.GRILLED_PORK, Material.GOLDEN_APPLE,
			Material.SIGN, Material.WOOD_DOOR, Material.BUCKET,
			Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.MINECART,
			Material.SADDLE, Material.IRON_DOOR, Material.REDSTONE,
			Material.SNOW_BALL, Material.BOAT, Material.LEATHER,
			Material.MILK_BUCKET, Material.CLAY_BRICK, Material.CLAY_BALL,
			Material.SUGAR_CANE, Material.PAPER, Material.BOOK,
			Material.SLIME_BALL, Material.STORAGE_MINECART,
			Material.POWERED_MINECART, Material.EGG, Material.COMPASS,
			Material.FISHING_ROD, Material.WATCH, Material.GLOWSTONE_DUST,
			Material.RAW_FISH, Material.COOKED_FISH, Material.BONE,
			Material.SUGAR, Material.CAKE, Material.BED, Material.DIODE,
			Material.COOKIE, Material.MAP, Material.GOLD_RECORD,
			Material.GREEN_RECORD, Material.INK_SACK, Material.SIGN,
			Material.SIGN_POST };

	InventoryAction[] bannedActions = { InventoryAction.COLLECT_TO_CURSOR,
			InventoryAction.HOTBAR_SWAP, InventoryAction.HOTBAR_MOVE_AND_READD, };

	@EventHandler
	public void clickedAll(final InventoryClickEvent event) {
		if (Config.bypass.contains(event.getWhoClicked().getName()))
			return;
		for (InventoryAction ia : bannedActions) {
			if (event.getAction() == ia) {
				Player p = (Player) event.getWhoClicked();
				p.sendMessage(negTitle + "You cannot click like that in retro!");
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void stringPlaced(final BlockPlaceEvent bpe) {
		if (bpe.getBlock().getType() == Material.TRIPWIRE) {
			bpe.setCancelled(true);
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							bpe.getPlayer().updateInventory();
						}
					}, 1l);
		}
	}

	boolean debug = false;

	@SuppressWarnings("unused")
	private void debug(Object a) {
		if (debug)
			System.out.println(a.toString());
	}

	@EventHandler
	public void foodStack(final InventoryClickEvent event) {
		if (event.getClick() == ClickType.LEFT) {
			ItemStack is1 = event.getCursor();
			ItemStack is2 = event.getCurrentItem();
			if (is1 != null && is2 != null) {
				if (is1.getType() != is2.getType())
					return;
			}
			if ((event.getCursor() != null
					&& event.getCursor().getAmount() != 0 && (EFood
					.isFood(event.getCursor().getType()) || event.getCursor()
					.getType().equals(Material.SIGN)))
					|| (event.getCurrentItem() != null
							&& event.getCurrentItem().getAmount() != 0 && (EFood
							.isFood(event.getCurrentItem().getType()) || event
							.getCurrentItem().getType().equals(Material.SIGN)))) {
				int total = 0;
				EFood ef = null;
				if (event.getCurrentItem() != null) {
					if (EFood.isFood(event.getCurrentItem().getType()))
						ef = EFood.getByName(event.getCurrentItem().getType());
					total += event.getCurrentItem().getAmount();
				}
				if (event.getCursor() != null) {
					if (EFood.isFood(event.getCursor().getType()))
						ef = EFood.getByName(event.getCursor().getType());
					total += event.getCursor().getAmount();
				}
				boolean flag = false;
				System.out.println("TOTAL: " + total);
				if (ef != null && total > ef.getStackSize()) {

					flag = true;
				} else if (ef == null && total > 1) {
					flag = true;
				}
				if (flag) {
					event.setResult(Result.DENY);
					event.setCancelled(true);
					jp.getServer().getScheduler()
							.scheduleSyncDelayedTask(jp, new Runnable() {
								@SuppressWarnings("deprecation")
								@Override
								public void run() {
									Player p = (Player) event.getWhoClicked();
									p.updateInventory();
								}
							}, 0);
				}
			}
		}

	}

	@EventHandler
	public void furnaceStack(FurnaceSmeltEvent event) {

		if (EFood.isFood(event.getResult().getType())
				&& ((Furnace) event.getBlock().getState()).getInventory()
						.getResult() != null) {
			if (((Furnace) event.getBlock().getState()).getInventory()
					.getResult().getAmount() >= 1) {
				event.setCancelled(true);
			}
		}
		Material m = event.getResult().getType();
		for (Material mat : allowedItems)
			if (m.equals(mat)) {
				return;
			}
		event.setCancelled(true);
	}

	@EventHandler
	public void itemPreperation(
			final org.bukkit.event.inventory.CraftItemEvent event) {
		boolean flag = true;
		if (event.getRecipe() != null && event.getRecipe().getResult() != null) {
			ItemStack is = event.getRecipe().getResult();
			System.out.println(is.getType().name());
			for (Material m : this.allowedItems) {
				if (is.getType().equals(m)) {
					flag = false;
					break;
				}
			}
		}
		if (flag) {
			event.setCancelled(true);
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							Player p = ((Player) event.getViewers().get(0));
							p.updateInventory();
							p.sendMessage(negTitle
									+ "You cannot make this on a retro server!");
						}
					}, 0);
		}
	}

	Material leth[] = { Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE,
			Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS };

	@EventHandler
	public void itemCraft(CraftItemEvent ci) {
		boolean bo = false;
		if (ci.getCurrentItem() != null) {
			if (ci.getCurrentItem().getType().equals(Material.WOOD)) {
				ci.setCurrentItem(new ItemStack(Material.WOOD, 4));
				bo = true;
			} else if (ci.getCurrentItem().getType().equals(Material.WOOD_STEP)) {
				ItemStack isa = new ItemStack(Material.STEP, 6, (short) 2);
				ci.setCurrentItem(isa);
				bo = true;
			} else if (ci.getCurrentItem().getType().equals(Material.STEP)) {
				int currType = ci.getCurrentItem().getData().getData();
				if (currType > 3) {
					ci.setCancelled(true);
					Player p = ((Player) ci.getWhoClicked());
					p.sendMessage(negTitle
							+ "You cannot make this on a retro server!");
				}
				bo = true;
			}
			for (Material m : leth) {
				if (ci.getRecipe().getResult().getType() == m) {
					ci.setCurrentItem(new ItemStack(ci.getCurrentItem()
							.getType(), 1));
					bo = true;
					break;
				}
			}
			if (bo) {
				// Player p = ((Player) ci.getWhoClicked());
				// p.updateInventory();
			}
		}
	}

	@EventHandler
	public void prepareItem(final InventoryClickEvent event) {
		if (!(event.getInventory() instanceof CraftingInventory))
			return;

		final CraftingInventory ci = (CraftingInventory) event.getInventory();
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						boolean bo = false;
						if (ci.getResult() != null) {
							if (ci.getResult().getType().equals(Material.WOOD)) {
								ci.setResult(new ItemStack(Material.WOOD, 4));
								bo = true;
							} else if (ci.getResult().getType()
									.equals(Material.WOOD_STEP)) {
								ItemStack isa = new ItemStack(Material.STEP, 6,
										(short) 2);
								ci.setResult(isa);
								bo = true;
							} else if (ci.getResult().getType()
									.equals(Material.STEP)) {
								int currType = ci.getResult().getData()
										.getData();
								if (currType > 3) {
									ci.setResult(new ItemStack(Material.AIR));
									Player p = ((Player) event.getWhoClicked());
									p.sendMessage(negTitle
											+ "You cannot make this on a retro server!");
								}
								bo = true;
							}
							for (Material m : leth) {
								if (ci.getRecipe().getResult().getType() == m) {
									ci.setResult(new ItemStack(ci.getResult()
											.getType(), 1));
									bo = true;
									break;
								}
							}
							if (bo) {
								Player p = ((Player) event.getWhoClicked());
								p.updateInventory();
							}
						}
					}
				}, 0);
	}
}
