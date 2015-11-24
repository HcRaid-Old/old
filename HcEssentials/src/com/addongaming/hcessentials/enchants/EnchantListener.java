package com.addongaming.hcessentials.enchants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.data.ItemType;
import com.addongaming.hcessentials.data.Ores;
import com.addongaming.hcessentials.enchants.objects.BedrockManipulator;
import com.addongaming.hcessentials.enchants.objects.Corrosion;
import com.addongaming.hcessentials.enchants.objects.Disease;
import com.addongaming.hcessentials.enchants.objects.FireBlast;
import com.addongaming.hcessentials.enchants.objects.Forge;
import com.addongaming.hcessentials.enchants.objects.Hypnosis;
import com.addongaming.hcessentials.enchants.objects.LegCrusher;
import com.addongaming.hcessentials.enchants.objects.LightBlast;
import com.addongaming.hcessentials.enchants.objects.Lightning;
import com.addongaming.hcessentials.enchants.objects.Slug;
import com.addongaming.hcessentials.enchants.objects.Sly;
import com.addongaming.hcessentials.enchants.objects.SpiderBite;
import com.addongaming.hcessentials.enchants.objects.TnTArrow;
import com.addongaming.hcessentials.hooks.logging.BlockLoggingHook;
import com.addongaming.hcessentials.utils.Utils;

public class EnchantListener implements Listener, SubPlugin {
	List<HcEnchantment> hc = new ArrayList<HcEnchantment>();
	private JavaPlugin jp;
	private List<Integer> taskIds = new ArrayList<Integer>();

	/*
	 * private final int randomNum; private final int enchantLevel1; private
	 * final int enchantLevel2; private final int enchantLevel3;
	 */
	public EnchantListener(JavaPlugin jp) {
		this.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("enchants.enabled", Boolean.TRUE);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		hc.add(new Hypnosis());
		hc.add(new Lightning());
		hc.add(new Sly());
		hc.add(new SpiderBite());
		hc.add(new LegCrusher());
		hc.add(new Slug());
		hc.add(new Corrosion());
		hc.add(new FireBlast());
		hc.add(new LightBlast());
		hc.add(new Disease());
		hc.add(new BedrockManipulator());
		hc.add(new Forge());
		hc.add(new TnTArrow());
	}

	private ItemStack addEnchantGlow(ItemStack item) {
		return item;
	}

	private void hypnosis(LivingEntity damagee, int level) {
		int chance = 30;
		if (new Random().nextInt(chance) == 1) {
			damagee.addPotionEffect(new PotionEffect(
					PotionEffectType.CONFUSION,
					120 + new Random().nextInt(300), level - 1));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockDamage(BlockDamageEvent event) {
		if (event.getPlayer() == null
				|| event.getPlayer().getItemInHand() == null
				|| event.getPlayer().getItemInHand().getType() == Material.AIR)
			return;

		ItemMeta im = event.getPlayer().getItemInHand().getItemMeta();
		if (im.getLore() == null || im.getLore().isEmpty())
			return;
		for (String lore : im.getLore()) {
			lore = ChatColor.stripColor(lore);
			if (lore.startsWith("Bedrock Manip"))
				bedrockManip(event.getBlock(), event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent event) {
		if (event.getPlayer() == null
				|| event.getPlayer().getItemInHand() == null
				|| event.getPlayer().getItemInHand().getType() == Material.AIR)
			return;
		ItemMeta im = event.getPlayer().getItemInHand().getItemMeta();
		if (im.getLore() == null || im.getLore().isEmpty())
			return;
		for (String lore : im.getLore()) {
			lore = ChatColor.stripColor(lore);
			if (lore.startsWith("Forge")) {
				event.setCancelled(true);
				forge(event.getBlock(), event.getPlayer());
			}

		}
	}

	private void forge(Block block, Player player) {
		Collection<ItemStack> drops = block.getDrops(player.getItemInHand());
		List<ItemStack> list = new ArrayList<ItemStack>();
		block.setType(Material.AIR);
		for (ItemStack item : drops) {
			Ores ore = Ores.getByMaterial(item.getType());
			if (ore == null) {
				list.add(item);
				continue;
			}
			list.add(new ItemStack(ore.getEnd(), item.getAmount()));
		}
		logBlockBreak(player, block);
		for (ItemStack is : list)
			player.getWorld().dropItemNaturally(block.getLocation(), is);

		final short duraDiff = 7;
		if (player.getItemInHand().getDurability() + duraDiff > player
				.getItemInHand().getType().getMaxDurability()) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else
			player.getItemInHand()
					.setDurability(
							(short) (player.getItemInHand().getDurability() + duraDiff));
	}

	@SuppressWarnings("deprecation")
	private void logBlockBreak(Player player, Block block) {
		if (BlockLoggingHook.hasInstance())
			BlockLoggingHook
					.getInstance()
					.getApi()
					.logRemoval(player.getName(), block.getLocation(),
							block.getType().getId(), block.getData());
	}

	private void bedrockManip(Block block, Player player) {
		if (block.getType() != Material.BEDROCK)
			return;
		if (block.getY() == 0) {
			player.sendMessage(ChatColor.RED + "You cannot mine down this low.");
			return;
		} else if (block.getWorld().getName().contains("bedrock")) {
			player.sendMessage(ChatColor.RED
					+ "You cannot break bedrock in this world.");
			return;
		}
		block.setType(Material.AIR);
		block.getWorld().dropItem(block.getLocation(),
				new ItemStack(Material.BEDROCK, 1));
		logBlockBreak(player, block);
		final short duraDiff = 50;
		if (player.getItemInHand().getDurability() + duraDiff > player
				.getItemInHand().getType().getMaxDurability()) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else
			player.getItemInHand()
					.setDurability(
							(short) (player.getItemInHand().getDurability() + duraDiff));

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void itemEnchant(EnchantItemEvent event) {
		if (event.isCancelled())
			return;
		if (!Config.enchantTable)
			return;
		HcEnchantment toUse = null;
		int val = 0;
		for (HcEnchantment hce : hc) {
			val = hce.getLevelToEnchant(event.getExpLevelCost(),
					event.getItem());
			if (val != 0) {
				toUse = hce;
				break;
			}
		}
		if (toUse != null) {
			ItemMeta im = event.getItem().getItemMeta();
			List<String> lore;
			if (im.hasLore())
				lore = new ArrayList<String>(im.getLore());
			else
				lore = new ArrayList<String>();

			lore.add(ChatColor.RESET + "" + ChatColor.GRAY + toUse.getName()
					+ " " + EnchantItem.numToNumeral(val));
			im.setLore(lore);
			event.getItem().setItemMeta(im);
		}
	}

	private void lightning(Location location, int level) {
		int chance = new Random().nextInt(20 / level);
		if (chance == 1)
			location.getWorld().strikeLightning(location);
	}

	@Override
	public void onDisable() {
		for (Integer i : taskIds)
			jp.getServer().getScheduler().cancelTask(i);
	}

	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("enchants.enabled") == false)
			return false;
		System.out.println("Enabling custom enchantments");
		jp.getServer().getPluginManager().registerEvents(this, jp);
		for (Iterator<HcEnchantment> iter = hc.iterator(); iter.hasNext();) {
			try {
				if (!iter.next().loadConfig(jp))
					iter.remove();
			} catch (Exception e) {
			}
		}
		jp.saveConfig();
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void toggleTnTBow(PlayerInteractEvent pie) {
		if (pie.hasItem()
				&& (pie.getAction() == Action.LEFT_CLICK_AIR || pie.getAction() == Action.LEFT_CLICK_BLOCK)) {
			ItemStack is = pie.getItem();
			if (!is.hasItemMeta() || is.getItemMeta().getLore() == null
					|| is.getItemMeta().getLore().isEmpty())
				return;
			List<String> lore = is.getItemMeta().getLore();
			for (String str : lore)
				if (str.contains("TnT Arrow"))
					TnTArrow.toggle(pie.getPlayer());
		}
	}

	@EventHandler
	public void bowShot(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			ItemStack is = event.getBow();
			if (!is.hasItemMeta() || is.getItemMeta().getLore() == null
					|| is.getItemMeta().getLore().isEmpty())
				return;
			List<String> lore = is.getItemMeta().getLore();
			for (String str : lore) {
				String[] parts = str.split(" ");
				int level;
				if (parts[parts.length - 1].matches("(I|II|III|IV|V|VI)"))
					level = EnchantItem.numberalToNum(parts[parts.length - 1]);
				else
					level = 1;
				if (str.contains("TnT Arrow")) {
					if (TnTArrow.canTnT(p.getName()) && TnTArrow.fire(p, level)) {
						for (int i = 0; i < level; i++) {
							Vector v = event.getProjectile().getVelocity();
							v = v.multiply(0.75);
							TNTPrimed e = (TNTPrimed) event
									.getProjectile()
									.getLocation()
									.getWorld()
									.spawnEntity(
											event.getProjectile().getLocation(),
											EntityType.PRIMED_TNT);
							e.setVelocity(v);
							e.setYield(5);
							e.setFuseTicks(((int) (e.getFuseTicks() / 4)));
							event.setProjectile(e);
						}
						Utils.removeFromInventory(p, new ItemStack(
								Material.TNT, level));
						int duraDamage = 20 * level;
						if (p.getItemInHand().getDurability() + duraDamage > p
								.getItemInHand().getType().getMaxDurability()) {
							p.setItemInHand(new ItemStack(Material.AIR));
						} else
							p.getItemInHand()
									.setDurability(
											(short) (p.getItemInHand()
													.getDurability() + duraDamage));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDamager(EntityDamageByEntityEvent event) {
		Player p;
		boolean arrow = false;
		if (event.getDamager() instanceof Arrow) {
			ProjectileSource le = ((Arrow) (event.getDamager())).getShooter();
			if (le instanceof Player) {
				arrow = true;
				p = (Player) le;
			} else
				return;
		} else if (event.getDamager() instanceof Player)
			p = (Player) event.getDamager();
		else
			return;
		/* Sly */
		if (p.getInventory().getBoots() != null
				&& p.getInventory().getBoots().getItemMeta() != null) {
			ItemMeta im = p.getInventory().getBoots().getItemMeta();
			if (im.getLore() != null)
				for (String str : im.getLore())
					if (str.contains("Sly")
							&& Sly.hiddenPlayers.contains(p.getName())) {
						for (Player pl : Bukkit.getOnlinePlayers())
							pl.showPlayer(p);
						Sly.hiddenPlayers.remove(p.getName());
						Sly.addToSlyTime(p.getName());
						p.removePotionEffect(PotionEffectType.BLINDNESS);
						p.getInventory()
								.getBoots()
								.setDurability(
										(short) (p.getInventory().getBoots()
												.getDurability() + 20));
						if (p.getInventory().getBoots().getDurability() >= p
								.getInventory().getBoots().getType()
								.getMaxDurability()) {
							p.getInventory().setBoots(
									new ItemStack(Material.AIR));
						}
					}
		}
		/* End of Sly */
		/* General weapon enchantments */
		if (p.getItemInHand() == null
				|| p.getItemInHand().getType() == Material.AIR)
			return;
		if (p.getItemInHand().hasItemMeta()
				&& p.getItemInHand().getItemMeta().hasLore()) {
			List<String> lore = p.getItemInHand().getItemMeta().getLore();
			if (lore.isEmpty())
				return;
			String spid = null;
			for (String str : lore) {
				for (HcEnchantment hcc : this.hc)
					if (str.contains(hcc.getName())) {
						spid = str;
						break;
					}
				if (spid == null)
					continue;
				String[] parts = spid.split(" ");
				int level;
				if (parts[parts.length - 1].matches("(I|II|III|IV|V|VI)"))
					level = EnchantItem.numberalToNum(parts[parts.length - 1]);
				else
					level = 1;
				if (spid.contains("Spider"))
					spiderBite((LivingEntity) event.getEntity(), level);
				if (spid.contains("Lightn") && arrow)
					lightning(event.getEntity().getLocation(), level);
				if (spid.contains("Hypnosis") && arrow)
					hypnosis((LivingEntity) event.getEntity(), level);
				if (spid.contains("Leg Crusher") || str.contains("Slug"))
					crushLeg((LivingEntity) event.getEntity(), level);
				if (spid.contains("Corrosion"))
					corrode((LivingEntity) event.getEntity(), level);
				if (spid.contains("Light Blast")) {
					lightBlast((Player) event.getDamager());
				}
				if (spid.contains("Fire Blast")) {
					fireBlast((Player) event.getDamager());
				}
				if (spid.contains("Disease")) {
					disease((Player) event.getDamager());
				}
			}
		}
		/* End of general enchantments */
	}

	private void disease(Player damager) {
		int r = new Random().nextInt((damager.isOp() ? 20 : 40)) + 1;
		if (r == 5) {
			for (Entity e : damager.getNearbyEntities(3.0d, 3.0d, 3.0d))
				if (e instanceof Player) {
					if (((Player) (e)).getName().equalsIgnoreCase(
							damager.getName()))
						continue;
					((Player) (e)).addPotionEffect(new PotionEffect(
							PotionEffectType.CONFUSION, 160 + new Random()
									.nextInt(100), 1));
					if (damager.isOp()) {
						((Player) (e)).sendMessage(ChatColor.GOLD + "["
								+ ChatColor.RED + "Disease" + ChatColor.GOLD
								+ "] " + ChatColor.GRAY
								+ "The King has struck you with disease.");
					}
				}
		}
	}

	private void fireBlast(Player damager) {
		int r = new Random().nextInt((damager.isOp() ? 20 : 40)) + 1;
		if (r == 5) {
			for (Entity e : damager.getNearbyEntities(3.0d, 3.0d, 3.0d))
				if (e instanceof Player) {
					if (((Player) (e)).getName().equalsIgnoreCase(
							damager.getName()))
						continue;
					e.setFireTicks(20 * 6);
					if (damager.isOp()) {
						((Player) (e))
								.sendMessage(ChatColor.GOLD
										+ "["
										+ ChatColor.RED
										+ "Fire Blast"
										+ ChatColor.GOLD
										+ "] "
										+ ChatColor.GRAY
										+ "The King has called the power of the Fire God.");
					}
				}
		}

	}

	private void lightBlast(Player damager) {
		int r = new Random().nextInt((damager.isOp() ? 20 : 40)) + 1;
		if (r == 5) {
			for (Entity e : damager.getNearbyEntities(3.0d, 3.0d, 3.0d))
				if (e instanceof Player) {
					if (((Player) (e)).getName().equalsIgnoreCase(
							damager.getName()))
						continue;
					damager.getWorld().strikeLightning(e.getLocation());
					if (damager.isOp()) {
						((Player) (e))
								.sendMessage(ChatColor.GOLD
										+ "["
										+ ChatColor.RED
										+ "Light Blast"
										+ ChatColor.GOLD
										+ "] "
										+ ChatColor.GRAY
										+ "The King has struck down the power of Zeus.");
					}
				}
		}
	}

	private void crushLeg(LivingEntity livingEntity, int level) {
		int ran = new Random().nextInt(20);
		if (ran == 5) {
			livingEntity.addPotionEffect(new PotionEffect(
					PotionEffectType.SLOW, 20 * 5 * level, 1));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getInventory().getChestplate() != null) {
			ItemStack is = event.getEntity().getInventory().getChestplate();
			if (!is.hasItemMeta() || !is.getItemMeta().hasLore())
				return;
			String lore = null;
			for (String str : is.getItemMeta().getLore()) {
				if (str.contains("Chasity")) {
					lore = "Chasity";
					break;
				}
			}
			if (lore == null)
				return;
			if (lore.equalsIgnoreCase("Chasity")) {
				event.getDrops().remove(is);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerInteract(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.LEFT_CLICK_BLOCK
				&& (pie.getClickedBlock().getType() == Material.WALL_SIGN || pie
						.getClickedBlock().getType() == Material.SIGN_POST)) {
			Sign sign = (Sign) pie.getClickedBlock().getState();
			for (HcEnchantment h : hc)
				if (sign.getLine(1).equalsIgnoreCase(h.getName())) {
					pie.getPlayer().sendMessage(
							ChatColor.GRAY + "[" + ChatColor.GOLD + "HcEnchant"
									+ ChatColor.GRAY + "] " + ChatColor.BLUE
									+ h.getDescription());
					return;
				}
		} else if (pie.getPlayer() != null
				&& pie.getPlayer().getItemInHand() != null
				&& pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (pie.getClickedBlock().getType() == Material.WALL_SIGN || pie
						.getClickedBlock().getType() == Material.SIGN_POST)) {
			Sign sign = (Sign) pie.getClickedBlock().getState();
			if (!sign.getLine(0).equalsIgnoreCase(Config.enchantSign))
				return;
			HcEnchantment enchant = null;
			for (HcEnchantment h : hc)
				if (sign.getLine(1).equalsIgnoreCase(h.getName())) {
					enchant = h;
				}
			if (!EnchantItem.isValid(pie.getPlayer().getItemInHand(),
					enchant.getHoldingType())) {
				pie.getPlayer()
						.sendMessage(
								ChatColor.GRAY
										+ "["
										+ ChatColor.GOLD
										+ "HcEnchant"
										+ ChatColor.GRAY
										+ "] "
										+ ChatColor.BLUE
										+ enchant.getName()
										+ " is available on "
										+ EnchantItem.toString(enchant
												.getHoldingType()));
				return;
			} else if (!HcEssentials.economy.has(pie.getPlayer().getName(),
					Integer.parseInt(sign.getLine(3).substring(1)))) {
				pie.getPlayer().sendMessage(
						ChatColor.GRAY
								+ "["
								+ ChatColor.GOLD
								+ "HcEnchant"
								+ ChatColor.GRAY
								+ "] "
								+ ChatColor.BLUE
								+ "You need "
								+ sign.getLine(3)
								+ " you have $"
								+ new DecimalFormat("####.##")
										.format(HcEssentials.economy
												.getBalance(pie.getPlayer()
														.getName())) + ".");
				return;
			}
			ItemStack is = pie.getPlayer().getItemInHand();
			if (is.getItemMeta() != null && is.getItemMeta().getLore() != null)
				for (String str : is.getItemMeta().getLore())
					if (str.contains(enchant.getName())) {
						pie.getPlayer()
								.sendMessage(
										ChatColor.GRAY
												+ "["
												+ ChatColor.GOLD
												+ "HcEnchant"
												+ ChatColor.GRAY
												+ "] "
												+ ChatColor.BLUE
												+ "It seems you already have this enchantment!");
						return;
					}
			int level = 0;
			if (EnchantItem.numberalToNum(sign.getLine(2)) == 0) {
				level = Integer.parseInt(sign.getLine(2));
			} else
				level = EnchantItem.numberalToNum(sign.getLine(2));
			is = enchant.addToItem(is, level);
			is = addEnchantGlow(is);
			pie.getPlayer().setItemInHand(is);
			HcEssentials.economy.withdrawPlayer(pie.getPlayer().getName(),
					Integer.parseInt(sign.getLine(3).substring(1)));
			pie.getPlayer().sendMessage(
					ChatColor.GRAY + "[" + ChatColor.GOLD + "HcEnchant"
							+ ChatColor.GRAY + "] " + ChatColor.BLUE
							+ "Enjoy your new " + enchant.getName()
							+ " enchantment.");
			pie.setCancelled(true);
			return;
		}
	}

	private void corrode(final LivingEntity livingEntity, final int level) {
		int chance = new Random().nextInt(10);
		if (chance <= 0)
			return;
		if (new Random().nextInt(chance) != 5) {
			return;
		}
		List<ItemStack> isList = Utils.reverseList(livingEntity.getEquipment()
				.getArmorContents());
		for (org.bukkit.inventory.ItemStack ii : isList) {
			if (ii != null)
				System.out.println(ii.getType().name());
		}
		ItemType[] it = { ItemType.CHESTPLATE, ItemType.LEGGINGS,
				ItemType.BOOTS, ItemType.HELMET };
		int[] perc = { 50, 30, 15, 5 };
		double chnc = 0.0D;
		for (int i = 0; i < isList.size(); i++) {
			if ((isList.get(i) != null)
					&& (((org.bukkit.inventory.ItemStack) isList.get(i))
							.getType() != Material.AIR))
				chnc += perc[i];
			else
				perc[i] = 0;
		}
		int min = 100;
		for (int i = 0; i < perc.length; i++) {
			if (perc[i] > 0) {
				perc[i] = ((int) (100.0D / chnc * perc[i]));
				if (perc[i] < min) {
					min = perc[i];
				}
			}
		}
		Random r = new Random();
		if (100 - min <= 0)
			return;
		int ran = r.nextInt(100 - min) + min;
		ItemType itt = null;
		for (int i = 0; i < perc.length; i++) {
			if ((perc[i] >= 1) && (ran >= perc[i])) {
				itt = it[i];
				break;
			}
		}
		if (itt == null)
			return;
		final ItemType finalType = itt;
		final short currDura = getArmourAtSlot(finalType, livingEntity)
				.getDurability();
		this.jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(this.jp, new Runnable() {
					public void run() {
						if ((!livingEntity.isDead())) {
							short newDura = EnchantListener.this
									.getArmourAtSlot(finalType, livingEntity)
									.getDurability();
							short differ = (short) (newDura - currDura);
							switch (level) {
							case 1:
								differ = (short) (differ * 1.5);
								break;
							case 2:
								differ = (short) (differ * 2);
								break;
							case 3:
								differ = (short) (differ * 2.75);
							}

							if (newDura + differ > EnchantListener.this
									.getArmourAtSlot(finalType, livingEntity)
									.getType().getMaxDurability())
								return;
							EnchantListener.this.setDuraAtSlot(finalType,
									livingEntity, (short) (newDura + differ));
						}
					}
				}, 1L);
	}

	private org.bukkit.inventory.ItemStack getArmourAtSlot(ItemType it,
			LivingEntity livingEntity) {
		if (it == ItemType.HELMET)
			return livingEntity.getEquipment().getHelmet();
		if (it == ItemType.CHESTPLATE)
			return livingEntity.getEquipment().getChestplate();
		if (it == ItemType.LEGGINGS)
			return livingEntity.getEquipment().getLeggings();
		if (it == ItemType.BOOTS) {
			return livingEntity.getEquipment().getBoots();
		}
		return null;
	}

	private void setDuraAtSlot(ItemType it, LivingEntity p, short dura) {
		if (it == ItemType.HELMET)
			p.getEquipment().getHelmet().setDurability(dura);
		else if (it == ItemType.CHESTPLATE)
			p.getEquipment().getChestplate().setDurability(dura);
		else if (it == ItemType.LEGGINGS)
			p.getEquipment().getLeggings().setDurability(dura);
		else if (it == ItemType.BOOTS)
			p.getEquipment().getBoots().setDurability(dura);
	}

	private void setArmourAtSlot(ItemType it, Player p,
			org.bukkit.inventory.ItemStack is) {
		if (it == ItemType.HELMET)
			p.getInventory().setHelmet(is);
		else if (it == ItemType.CHESTPLATE)
			p.getInventory().getChestplate();
		else if (it == ItemType.LEGGINGS)
			p.getInventory().getLeggings();
		else if (it == ItemType.BOOTS)
			p.getInventory().getBoots();
	}

	@EventHandler
	public void playerLogin(PlayerLoginEvent event) {
		for (String str : Sly.hiddenPlayers) {
			Player p = Bukkit.getPlayer(str);
			if (p == null)
				continue;
			event.getPlayer().hidePlayer(p);
		}
	}

	@EventHandler
	public void playerSneak(PlayerToggleSneakEvent event) {
		if (event.isCancelled())
			return;
		if (!event.isSneaking())
			return;
		if (event.getPlayer().getInventory().getBoots() != null
				&& event.getPlayer().getInventory().getBoots().getItemMeta() != null) {
			ItemMeta im = event.getPlayer().getInventory().getBoots()
					.getItemMeta();
			if (im.getLore() == null)
				return;
			for (String str : im.getLore())
				if (str.contains("Sly")) {
					if (Sly.canSly(event.getPlayer().getName())) {
						Sly.hiddenPlayers.add(event.getPlayer().getName());
						for (Player p : Bukkit.getOnlinePlayers())
							p.hidePlayer(event.getPlayer());
						event.getPlayer()
								.getInventory()
								.getBoots()
								.setDurability(
										(short) (event.getPlayer()
												.getInventory().getBoots()
												.getDurability() + 5));
						return;
					} else {
						event.getPlayer().sendMessage(
								ChatColor.DARK_GRAY
										+ "["
										+ ChatColor.GOLD
										+ "Sly"
										+ ChatColor.DARK_GRAY
										+ "] "
										+ ChatColor.GRAY
										+ " You cannot use sly for another "
										+ Sly.getTimeTillCanSly(event
												.getPlayer().getName())
										+ " seconds.");
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void signPlace(final SignChangeEvent event) {
		if (event.isCancelled() || !event.getPlayer().isOp()
				|| !Config.enchantSignPurchasable)
			return;
		if (event.getLine(0).equalsIgnoreCase("HcEnchant")) {
			for (HcEnchantment h : hc) {
				if (event.getLine(1).equalsIgnoreCase(h.getName())
						&& (EnchantItem.numberalToNum(event.getLine(2)) != 0)) {
					event.setLine(0, Config.enchantSign);
					event.getPlayer().sendMessage("Setup sign");
					return;
				}
			}
			event.getPlayer()
					.sendMessage(
							"Please use Line 1- HcEnchantment Line 2- Enchantment name Line 3- Level Line 4- Price");
		}
	}

	private void spiderBite(LivingEntity damagee, int level) {
		int chance = new Random().nextInt(10) + 15;
		if (new Random().nextInt(chance) == 5) {
			damagee.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
					10 * 20, level));
		}
	}
}
