package core.essentials.limit;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.Openable;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.objects.Config;

public class LimitListener implements Listener {
	TreeType[] blockedTrees = { TreeType.BROWN_MUSHROOM, TreeType.JUNGLE,
			TreeType.JUNGLE_BUSH, TreeType.RED_MUSHROOM, TreeType.SMALL_JUNGLE,
			TreeType.SWAMP };
	Material[] disallowedChests = { Material.ENCHANTED_BOOK, Material.LEASH,
			Material.IRON_BARDING, Material.GOLD_BARDING,
			Material.DIAMOND_BARDING, Material.NAME_TAG };
	private HashMap<String, Integer> fireMap = new HashMap<String, Integer>();

	private final Material[] hoes = { Material.WOOD_HOE, Material.STONE_HOE,
			Material.GOLD_HOE, Material.IRON_HOE, Material.DIAMOND_HOE };

	private final JavaPlugin jp;

	@SuppressWarnings("unused")
	private final String negTitle = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "RetroPvP" + ChatColor.DARK_RED + "] " + ChatColor.RESET;

	@SuppressWarnings("unused")
	private final String postTitle = ChatColor.AQUA + "[" + ChatColor.GOLD
			+ "RetroPvP" + ChatColor.AQUA + "] " + ChatColor.RESET;

	private final int[][] woodOrientations = { { 0, 4, 8 }, { 1, 5, 9 },
			{ 2, 6, 10 }, { 3, 7, 11 } };

	public LimitListener(JavaPlugin jp) {
		this.jp = jp;
	}

	@EventHandler
	public void boneMeal(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getPlayer() != null
					&& event.getPlayer().getItemInHand() != null
					&& event.getPlayer().getItemInHand().getType() == Material.INK_SACK) {
				final ItemStack is = event.getPlayer().getItemInHand();
				final Player p = event.getPlayer();
				if (is.getData().getData() == (byte) 15) {
					final Block block = event.getClickedBlock();
					if (block.getType() == Material.CROPS) {
						final Crops c = (Crops) block.getState().getData();

						if (c.getState() == CropState.RIPE)
							return;
						block.setData(CropState.RIPE.getData());
						event.setCancelled(true);
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(jp, new Runnable() {
									@SuppressWarnings("deprecation")
									@Override
									public void run() {
										if (p.getInventory().getItemInHand()
												.getAmount() > 1) {
											is.setAmount(is.getAmount() - 1);
											p.getInventory().setItemInHand(is);
										} else
											p.getInventory()
													.setItemInHand(
															new ItemStack(
																	Material.AIR));
										p.updateInventory();

									}
								}, 0);
					} else if (block.getType() == Material.SAPLING) {
						TreeType tt = TreeType.TREE;
						switch (block.getData()) {
						case 0:
							tt = TreeType.TREE;
							break;
						case 1:
							tt = TreeType.REDWOOD;
							break;
						case 2:
							tt = TreeType.BIRCH;
							break;
						case 3:
							tt = TreeType.JUNGLE;
							break;
						}
						block.setType(Material.AIR);
						block.getWorld().generateTree(block.getLocation(), tt);
						event.setCancelled(true);
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(jp, new Runnable() {
									@SuppressWarnings("deprecation")
									@Override
									public void run() {
										if (p.getInventory().getItemInHand()
												.getAmount() > 1) {
											is.setAmount(is.getAmount() - 1);
											p.getInventory().setItemInHand(is);
										} else
											p.getInventory()
													.setItemInHand(
															new ItemStack(
																	Material.AIR));
										p.updateInventory();

									}
								}, 0);
					}
				}
			}
		}
	}

	private void debug(Object a) {
		System.out.println(a.toString());
	}

	@EventHandler
	public void doorOpen(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK
				&& event.getClickedBlock() != null
				&& event.getClickedBlock().getState() != null
				&& event.getClickedBlock().getType() == Material.WOOD_DOOR) {
			if (event.getClickedBlock().getState().getData() instanceof Openable) {
				Openable o = (Openable) event.getClickedBlock().getState()
						.getData();
				if (o.isOpen())
					event.getClickedBlock()
							.getWorld()
							.playEffect(event.getClickedBlock().getLocation(),
									Effect.DOOR_TOGGLE, 0);
				else
					event.getClickedBlock()
							.getWorld()
							.playEffect(event.getClickedBlock().getLocation(),
									Effect.DOOR_TOGGLE, 1);
				Block b = event.getClickedBlock();
				if (b.getRelative(BlockFace.DOWN).getState().getData() instanceof Openable)
					b = b.getRelative(BlockFace.DOWN);
				final Block bb = b;
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {
							@Override
							public void run() {
								bb.setData((byte) (bb.getData() ^ 0x4));
							}
						}, 0);

			}
		}
	}

	@EventHandler
	public void enchInve(InventoryOpenEvent event) {
		for (Material m : disallowedChests)
			while (event.getInventory().contains(m))
				event.getInventory().remove(m);
	}

	@EventHandler
	public void fishDura(PlayerFishEvent pfe) {
		pfe.setExpToDrop(0);
		if (pfe.getPlayer().getItemInHand() != null) {
			debug("Setting fishing rod durability");
			ItemStack is = pfe.getPlayer().getItemInHand();
			if (is.getType() == Material.FISHING_ROD) {
				is.setDurability((short) 0);
			}
			pfe.getPlayer().setItemInHand(is);
		}
	}

	@EventHandler
	public void furnace(FurnaceExtractEvent event) {
		event.setExpToDrop(0);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void instaBow(PlayerInteractEvent pie) {
		if (pie.getPlayer() != null
				&& (pie.getAction() == Action.RIGHT_CLICK_AIR || pie
						.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player p = pie.getPlayer();
			if (p.getItemInHand().getType().equals(Material.BOW)
					&& p.getInventory().contains(Material.ARROW)) {
				if (p.getItemInHand().getDurability() > 0
						|| !p.getItemInHand().getEnchantments().isEmpty()) {
					ItemStack is = p.getItemInHand();
					is.setDurability((short) 0);
					for (Enchantment ench : is.getEnchantments().keySet())
						is.removeEnchantment(ench);
					p.setItemInHand(is);

				}
				Projectile arrow = p.launchProjectile(Arrow.class);
				arrow.setShooter(p);
				arrow.setVelocity(arrow.getVelocity().multiply(2.0f));
				int i = p.getInventory().first(Material.ARROW);
				pie.setUseItemInHand(Result.DENY);
				if (p.getInventory().getItem(i).getAmount() > 1) {
					p.getInventory().setItem(
							i,
							new ItemStack(Material.ARROW, p.getInventory()
									.getItem(i).getAmount() - 1));
				} else {
					p.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
				fireMap.put(p.getName(), p.getTicksLived());
				p.updateInventory();
			}
		}
	}

	@EventHandler
	public void inventoryOpen(PlayerInteractEvent eve) {
		if (eve.getPlayer() != null
				&& eve.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (eve.getClickedBlock() != null
					&& eve.getClickedBlock() instanceof Chest) {
				@SuppressWarnings("unused")
				Chest chest = (Chest) eve.getClickedBlock();
				// TODO Chest opening animation
			}
		}
	}

	@EventHandler
	public void playerShoot(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerSpawn(final PlayerRespawnEvent event) {
		debug("Player spawn: " + event.getPlayer().getName());
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						System.out.println("Set food level");
						event.getPlayer().setFoodLevel(2);
						event.getPlayer().updateInventory();
					}
				}, 2l);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerTill(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& event.getClickedBlock().getType() == Material.GRASS
				&& event.getPlayer().getItemInHand() != null) {
			for (Material hoe : hoes)
				if (event.getPlayer().getItemInHand().getType() == hoe) {
					if (new Random().nextInt(20) < 5) {
						final Block bl = event.getClickedBlock();
						jp.getServer().getScheduler()
								.scheduleSyncDelayedTask(jp, new Runnable() {

									@Override
									public void run() {
										if (bl.getType() == Material.SOIL)
											bl.getWorld()
													.dropItemNaturally(
															bl.getRelative(
																	BlockFace.UP)
																	.getLocation(),
															new ItemStack(
																	Material.SEEDS));
									}
								}, 1l);
					}
					return;
				}
		}
	}

	@EventHandler
	public void potionBrewed(org.bukkit.event.inventory.BrewEvent pbe) {
		pbe.setCancelled(true);
	}

	@EventHandler
	public void sidewaysLogs(BlockPlaceEvent bpe) {
		if (Config.bypass.contains(bpe.getPlayer().getName()))
			return;
		if (bpe.getBlockPlaced() != null) {
			Block b = bpe.getBlock();
			if (b.getType() == Material.LOG)
				for (int i = 0; i < woodOrientations.length; i++) {
					for (int ii = 1; ii < woodOrientations[i].length; ii++) {
						if (((int) b.getData()) == woodOrientations[i][ii]) {
							System.out.println("Checked ");
							b.setData((byte) woodOrientations[i][0]);
							return;
						}
					}
				}
		}
	}

	@EventHandler
	public void sprintCancel(
			org.bukkit.event.player.PlayerToggleSprintEvent event) {
		if (!event.getPlayer().getWorld().getName()
				.equalsIgnoreCase("minigames"))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void tallGrassBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		if (event.getBlock().getType() == Material.LONG_GRASS) {
			ItemStack grass = new ItemStack(Material.LONG_GRASS);
			grass.setDurability((short) 1);
			if (event.getBlock().getData() == (byte) 1)
				event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
			if (new Random().nextInt(4) == 2)
				event.getBlock()
						.getWorld()
						.dropItemNaturally(event.getBlock().getLocation(),
								grass);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerChangeWorld(PlayerChangedWorldEvent event) {
		if (event.getPlayer().getWorld().getName()
				.equalsIgnoreCase("minigames")) {
			event.getPlayer().setFoodLevel(20);
		} else {
			event.getPlayer().setFoodLevel(2);
			event.getPlayer().setSprinting(false);
		}
	}

	@EventHandler
	public void treeGrowth(StructureGrowEvent event) {
		for (TreeType tt : blockedTrees) {
			if (tt == event.getSpecies()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void xpGain(PlayerExpChangeEvent event) {
		event.setAmount(0);
	}
}
