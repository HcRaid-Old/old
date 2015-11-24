package com.addongaming.prison.classes.skills.assassin;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.classes.AssassinData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class SneakAttack implements Listener {
	HashMap<String, Date> sneakAttackTime = new HashMap<String, Date>();

	// Hold shift and jump to get air attack
	// Bar which means the more they sneak, exp bar goes up, more damage done.
	private DataReturn canSneakAttack(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (prisoner.getPlayerClass() != PlayerClasses.ASSASSIN)
			return DataReturn.NOPERM;
		if (!sneakAttackTime.containsKey(p.getName())
				|| sneakAttackTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	private void message(Player player, String message, boolean negative) {
		if (negative) {
			player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED
					+ "Assassin" + ChatColor.DARK_RED + "] " + ChatColor.RESET
					+ message);
		} else {
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN
					+ "Assassin" + ChatColor.DARK_GREEN + "] "
					+ ChatColor.RESET + message);
		}

	}

	@SuppressWarnings("serial")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void sneakAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			final Player player = (Player) event.getDamager();
			if (player.isSneaking()) {
				DataReturn vcanSneakAttack = canSneakAttack(player);
				if (vcanSneakAttack == DataReturn.FAILURE) {
					message(player, "That skill is still on cooldown", true);
					return;
				} else if (vcanSneakAttack == DataReturn.SUCCESS) {
					boolean guard = false;
					Player target = (Player) event.getEntity();
					if (Utils.isEntityInCone(player, target.getLocation()
							.toVector(), 10, 90, target.getEyeLocation()
							.getDirection())) {
						message(player,
								"You can't sneak attack if they can see you!",
								true);
						return;
					} else if (Combat.getCombatInstance().isInCombat(
							target.getName())
							|| Combat.getCombatInstance().isInCombat(
									player.getName())) {
						return;
					}
					guard = target.getName().equalsIgnoreCase("Guard")
							|| PrisonerManager.getInstance()
									.getPrisonerInfo(target.getName())
									.getPlayerClass() == PlayerClasses.GUARD;
					Prisoner prisoner = PrisonerManager.getInstance()
							.getPrisonerInfo(player.getName());
					int ran = new Random().nextInt(4 + (guard ? 5 : 0));
					if (ran == 1) {
						message(player, "Your sneak attack succeeded.", false);
						sneakAttackTime.put(player.getName(), new Date(
								new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 10)));
						prisoner.giveClassExp((guard ? 10 : 3));
						if (event.getDamage() < 1)
							event.setDamage(1.0d);
						int multiplyer;
						if (prisoner.hasPermission(AssassinData.SNEAK_ATTACK_4
								.getPermission()))
							multiplyer = 6;
						else if (prisoner
								.hasPermission(AssassinData.SNEAK_ATTACK_3
										.getPermission()))
							multiplyer = 4;
						else if (prisoner
								.hasPermission(AssassinData.SNEAK_ATTACK_2
										.getPermission()))
							multiplyer = 3;
						else
							multiplyer = 2;
						event.setDamage(event.getDamage()
								* (new Random().nextInt(multiplyer) + 1));
					} else {
						message(player, "Your sneak attack failed.", true);
						sneakAttackTime.put(player.getName(), new Date(
								new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 10)));
						prisoner.giveClassExp((guard ? 3 : 1));
					}

				}
			}
		}
	}
}
