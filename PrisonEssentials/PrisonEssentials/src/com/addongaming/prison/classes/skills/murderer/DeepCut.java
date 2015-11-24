package com.addongaming.prison.classes.skills.murderer;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.classes.MurdererData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class DeepCut implements Listener {
	HashMap<String, Date> deepCutTime = new HashMap<String, Date>();

	// Bar which means the more they sneak, exp bar goes up, more damage done.
	private DataReturn canDeepCut(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (prisoner.getPlayerClass() != PlayerClasses.MURDERER)
			return DataReturn.NOPERM;
		if (!deepCutTime.containsKey(p.getName())
				|| deepCutTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	private void message(Player player, String message, boolean negative) {
		if (negative) {
			player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED
					+ "Murderer" + ChatColor.DARK_RED + "] " + ChatColor.RESET
					+ message);
		} else {
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN
					+ "Murderer" + ChatColor.DARK_GREEN + "] "
					+ ChatColor.RESET + message);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void deepCut(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.isSneaking() && !((Entity) (player)).isOnGround()) {
				DataReturn vcanDeepCut = canDeepCut(player);
				if (vcanDeepCut == DataReturn.FAILURE) {
					message(player, "That skill is still on cooldown", true);
					return;
				} else if (vcanDeepCut == DataReturn.SUCCESS) {
					boolean guard = false;
					Player target = (Player) event.getEntity();
					guard = target.getName().equalsIgnoreCase("Guard")
							|| PrisonerManager.getInstance()
									.getPrisonerInfo(target.getName())
									.getPlayerClass() == PlayerClasses.GUARD;
					Prisoner prisoner = PrisonerManager.getInstance()
							.getPrisonerInfo(player.getName());
					int ran = new Random().nextInt(4 + (guard ? 5 : 0));
					if (ran == 1) {
						message(player, "Your deep cut attack succeeded.",
								false);
						deepCutTime.put(player.getName(),
								new Date(new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 15)));
						prisoner.giveClassExp((guard ? 10 : 3));
						if (event.getDamage() < 1)
							event.setDamage(1.0d);
						int multiplyer;
						if (prisoner.hasPermission(MurdererData.DEEP_CUT_4
								.getPermission()))
							multiplyer = 6;
						else if (prisoner.hasPermission(MurdererData.DEEP_CUT_3
								.getPermission()))
							multiplyer = 4;
						else if (prisoner.hasPermission(MurdererData.DEEP_CUT_2
								.getPermission()))
							multiplyer = 3;
						else
							multiplyer = 2;
						event.setDamage(event.getDamage()
								* (new Random().nextInt(multiplyer) + 1));
					} else {
						message(player, "Your deep cut failed.", true);
						deepCutTime.put(player.getName(),
								new Date(new Date().getTime()
										+ (guard ? 1000 * 30 : 1000 * 15)));
						prisoner.giveClassExp((guard ? 3 : 1));
					}
				}
			}
		}
	}
}
