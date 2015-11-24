package com.addongaming.prison.npc.guard;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.trait.Trait;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.events.PickPocketEvent;
import com.addongaming.prison.jail.JailManager;
import com.addongaming.prison.jail.PvPScheduler;
import com.addongaming.prison.player.PrisonerManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class GuardTrait extends Trait {
	enum GuardState {
		ATTACKING, DEAD, INVESTIGATING, MOVING, NONE
	}

	enum InvestigatingType {
		COMBAT, ILLEGALITEM, NONE
	}

	int counter = 0;
	private Player interacting = null;
	private InvestigatingType it = InvestigatingType.NONE;
	private Location[] path;
	private int pathIndex = 0;
	private final float cruisingSpeed = 1.0f;
	private boolean reversedPath = false;

	private GuardState state = GuardState.NONE;

	public GuardTrait() {
		super("Guard");
	}

	private void attack(Player damager) {
		state = GuardState.ATTACKING;
		this.interacting = damager;
		getNPC().getNavigator().setTarget((Entity) (damager), true);
	}

	private void attacking() {
		if (interacting == null) {
			reset();
			return;
		}
		if (!interacting.isOnline()
				|| interacting.isDead()
				|| interacting.getLocation().distance(
						getNPC().getEntity().getLocation()) > 10) {
			reset();
			return;
		}
		if (interacting.getHealth() < 4.0) {
			if (new Random().nextInt((PvPScheduler.isWarOn() ? 5 : 2)) + 1 == 1) {
				JailManager.getInstance().jailPlayer(interacting);
				reset();
			}
		} else if (!inPvPZone(interacting)) {
			JailManager.getInstance().jailPlayer(interacting);
			reset();
		}
		return;
	}

	private void check() {
		switch (state) {
		case ATTACKING:
			attacking();
			break;
		case INVESTIGATING:
			investigating();
			break;
		case MOVING:
			reevaluate(false);
			break;
		case NONE:
			reevaluate(true);
			break;
		case DEAD:
			respawn();
			break;
		default:
			reevaluate(true);
			break;
		}
	}

	private boolean checkClosePlayer(Player player, boolean move,
			boolean updateInteracting) {
		if (Combat.getCombatInstance().isInCombat(player.getName())) {
			if (updateInteracting) {
				it = InvestigatingType.COMBAT;
				state = GuardState.ATTACKING;
				interacting = player;
			}
			if (move)
				getNPC().getNavigator().setTarget(((Entity) (player)), true);
			return true;
		} else if (hasIllegalItemInHand(player)) {
			if (updateInteracting) {
				it = InvestigatingType.ILLEGALITEM;
				state = GuardState.INVESTIGATING;
				interacting = player;
			}
			if (move)
				getNPC().getNavigator().setTarget(player.getLocation());
			return true;
		}

		return false;
	}

	private boolean hasIllegalItemInHand(Player player) {
		if (Enchantable.getItemType(player.getItemInHand().getType()) == ItemType.SWORD
				|| Enchantable.getItemType(player.getItemInHand().getType()) == ItemType.BOW)
			return true;
		return false;
	}

	private boolean inPvPZone(Entity entity) {
		ApplicableRegionSet ars = HcEssentials.worldGuard.getRegionManager(
				entity.getWorld()).getApplicableRegions(entity.getLocation());
		for (Iterator<ProtectedRegion> it = ars.iterator(); it.hasNext();) {
			ProtectedRegion pr = it.next();
			if (pr.getFlag(DefaultFlag.PVP) == State.ALLOW)
				return true;
		}
		return false;
	}

	private void investigating() {
		if (interacting == null || !interacting.isOnline()) {
			reset();
			return;
		}
		getNPC().getNavigator().getLocalParameters().baseSpeed(1.0f);
		if (interacting.getLocation().distance(
				getNPC().getEntity().getLocation()) < 4) {
			if (!Combat.getCombatInstance().isInCombat(interacting.getName())) {
				if (hasIllegalItemInHand(interacting)) {
					if (PvPScheduler.isWarOn()) {
						state = GuardState.ATTACKING;
						getNPC().getNavigator().setTarget(
								((Entity) (interacting)), true);
						return;
					} else {
						interacting.sendMessage(ChatColor.GOLD + "Guard"
								+ ChatColor.RED + "> " + ChatColor.RESET
								+ "You're not allowed that here.");
						interacting.setItemInHand(new ItemStack(Material.AIR));
						reset();
						return;
					}
				}
			} else {
				if (checkClosePlayer(interacting, false, true)) {
					getNPC().getNavigator().setTarget(((Entity) (interacting)),
							true);
				}
			}
			return;
		} else if (interacting.getLocation().distance(
				getNPC().getEntity().getLocation()) < 20) {
			checkClosePlayer(interacting, true, true);
			return;
		}
		reset();
	}

	private void move() {
		getNPC().getNavigator().getLocalParameters().baseSpeed(0.7f);
		if (reversedPath) {
			pathIndex--;
			if (pathIndex < 0) {
				pathIndex = 1;
				reversedPath = false;
			}
		} else {
			pathIndex++;
			if (pathIndex >= path.length) {
				pathIndex = path.length - 2;
				reversedPath = true;
			}
		}
		getNPC().getNavigator().setTarget(path[pathIndex]);
		state = GuardState.MOVING;
	}

	@EventHandler
	public void playerPickPocket(PickPocketEvent event) {
		if (getNPC().isSpawned()
				&& getNPC().getEntity().getEntityId() == event
						.getPickpocketed().getEntityId()) {
			int ran = new Random().nextInt(event.isSucceeded() ? 5 : 3);
			if (ran == 1) {
				state = GuardState.ATTACKING;
				attack(event.getPickpocket());
			}
		}
	}

	@EventHandler
	public void navigationCompleted(NavigationCompleteEvent event) {
		if (getNPC().isSpawned()) {
			if (event.getNPC().getId() == getNPC().getId()) {
				reevaluate(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void npcDamage(NPCDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getNPC().getId() == getNPC().getId()) {
			if (!inPvPZone(event.getNPC().getEntity()))
				return;
			if (state != GuardState.ATTACKING
					|| state != GuardState.INVESTIGATING) {
				attack((Player) event.getDamager());
				return;
			}
		}
	}

	@EventHandler
	public void npcDied(NPCDeathEvent event) {
		if (event.getNPC().getId() == getNPC().getId()) {
			state = GuardState.DEAD;
			// resets counter to re-spawn in 15 seconds
			counter = -1 * (20 * 15);
			return;
		}
	}

	@Override
	public void onSpawn() {
		getNPC().setProtected(false);
		getNPC().setFlyable(false);
		getNPC().getNavigator().getLocalParameters().baseSpeed(cruisingSpeed);
		Player player = (Player) getNPC().getEntity();
		PlayerInventory pi = player.getInventory();
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.KNOCKBACK, 2);
		pi.addItem(sword);
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		helmet = setLeatherColour(helmet, "3F65A5");
		chest = setLeatherColour(chest, "3F65A5");
		legs = setLeatherColour(legs, "3F65A5");
		boots = setLeatherColour(boots, "3F65A5");
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		pi.setArmorContents(new ItemStack[] { boots, legs, chest, helmet });
		Player play = (Player) getNPC().getEntity();
		play.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	private ItemStack setLeatherColour(ItemStack armour, String hex) {
		LeatherArmorMeta lim = (LeatherArmorMeta) armour.getItemMeta();
		lim.setColor(Color.fromRGB(Integer.parseInt(hex, 16)));
		armour.setItemMeta(lim);
		return armour;
	}

	private void reevaluate(boolean moveIfFine) {
		if (!getNPC().isSpawned()){
			state=GuardState.DEAD;
			return;
		}
		Player guard = (Player) getNPC().getEntity();
		Player[] players = Utils.getPlayersInsideCone(getNPC().getEntity()
				.getNearbyEntities(10.0, 10.0, 10.0), getNPC().getEntity()
				.getLocation().toVector(), 10, 90, guard.getEyeLocation()
				.getDirection());
		ArrayList<Player> nearest = new ArrayList<Player>();
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase("Guard"))
				continue;
			if (PrisonerManager.getInstance().getPrisonerInfo(player.getName()) == null
					|| PrisonerManager.getInstance()
							.getPrisonerInfo(player.getName()).getPlayerClass() == PlayerClasses.GUARD)
				continue;
			if (hasIllegalItemInHand(player)) {
				nearest.add(player);
			} else if (Combat.getCombatInstance().isInCombat(player.getName()))
				nearest.add(player);
		}
		Collections.sort(nearest, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return (int) (getNPC().getEntity().getLocation()
						.distance(o1.getLocation()) - getNPC().getEntity()
						.getLocation().distance(o2.getLocation()));
			}
		});

		if (!nearest.isEmpty()) {
			Player play = nearest.get(0);
			checkClosePlayer(play, true, true);
		} else if (moveIfFine)
			move();
	}

	private void reset() {
		state = GuardState.NONE;
		it = InvestigatingType.NONE;
		interacting = null;
	}

	@EventHandler
	public void die(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player
				&& event.getEntity().hasMetadata("NPC") == false) {
			if (interacting != null
					&& ((Player) event.getEntity())
							.getUniqueId()
							.toString()
							.equalsIgnoreCase(
									interacting.getUniqueId().toString())) {
				reset();
				return;
			}
		} else if (getNPC().getEntity() != null
				&& event.getEntity().getEntityId() == getNPC().getEntity()
						.getEntityId()) {
			event.getDrops().clear();
			reset();
			return;
		}
	}

	private void respawn() {
		pathIndex = 0;
		getNPC().spawn(path[pathIndex]);
		reset();
	}

	@Override
	public void run() {
		if (counter == 10) {
			check();
			counter = 0;
		} else
			counter++;
	}

	public void setPath(Location[] path) {
		this.path = path;
	}

	public void debug(PrintWriter pw) {
		pw.println("------------------");
		pw.println("Counter: " + counter);
		pw.println("Is spawned: " + getNPC().isSpawned());
		pw.println("State: " + state.name());
		pw.println("IT Type: " + it.name());
		pw.println("Interacting is null: " + (interacting == null));
	}

}
