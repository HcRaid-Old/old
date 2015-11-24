package com.addongaming.hcessentials.combat.antilogging;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.events.CombatLogStartEvent;

public class CombatLogging implements Listener, CommandExecutor {
	public static HashMap<String, CombatInstance> combatMap = new HashMap<String, CombatInstance>();
	private static String posTitle = ChatColor.GREEN + "[" + ChatColor.GOLD
			+ "HcCombat" + ChatColor.GREEN + "] ";

	public static int timeOutOfCombat;// 30 ms * 1000 = 30seconds

	public static void message(Player p, String msg) {
		p.sendMessage(posTitle + msg);
	}

	public CombatLogging(int amount) {
		timeOutOfCombat = amount * 1000;
		for (Player pl : Bukkit.getOnlinePlayers()) {
			combatMap.put(pl.getName(), new CombatInstance());
		}
	}

	public boolean isInCombat(String playerName) {
		if (!combatMap.containsKey(playerName))
			return false;
		return combatMap.get(playerName).isInCombat();
	}

	private void exitCombat(String str) {
		Player p = Bukkit.getPlayer(str);
		if (p != null && p.isOnline()) {
			message(p, "You are now out of combat.");
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("[HcCombat] OMG YOUR GONNA DIE RUNNN");
			return true;
		}
		Player player = (Player) arg0;
		if (combatMap.containsKey(player.getName())) {
			CombatInstance ci = combatMap.get(player.getName());
			if (!ci.isInCombat()) {
				message(player, "You are not currently in combat.");
				return true;
			}
			String names = Arrays.toString(ci.getAllCombats());
			names = names.substring(1, names.length() - 1);
			message(player, "You are in combat for " + ci.getTimeLeft()
					+ " seconds. You are currently fighting " + names + ".");
			return true;
		} else {
			message(player, "You are not currently in combat.");
			return true;
		}
	}

	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().isOp())
			return;
		else if (combatMap.containsKey(event.getPlayer().getName())
				&& combatMap.get(event.getPlayer().getName()).isInCombat()) {
			String[] allowed = { "team", "msg", "w", "t", "tell", "whisper",
					"help", "spell", "cast", "c", "combattag", "combattime",
					"combatlog", "bounty" };
			String first = "";
			if (!event.getMessage().contains(" "))
				first = event.getMessage();
			else
				first = event.getMessage().split("( )")[0];
			first = first.toLowerCase();
			for (String str : allowed)
				if (first.equalsIgnoreCase("/" + str))
					return;
			message(event.getPlayer(), "You cannot use " + first
					+ " whilst in combat!");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;

		Player damaged = null;
		Player damager = null;
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				damager = (Player) arrow.getShooter();
			}
		} else if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else
			return;
		if (event.getEntity() instanceof Player) {
			damaged = (Player) event.getEntity();
		} else
			return;
		if (damaged == null || damager == null)
			return;
		CombatLogStartEvent ev = new CombatLogStartEvent(damaged.getName(),
				damager.getName());
		Bukkit.getPluginManager().callEvent(ev);
		if (ev.isCancelled())
			return;
		if (combatMap.containsKey(damaged.getName())) {
			CombatInstance toAlter = combatMap.get(damaged.getName());
			if (!toAlter.isInCombat())
				message(damaged, "You are now engaged in combat.");
			if (toAlter.isInCombatWith(damager.getName())) {
				toAlter.attack();
			} else {
				toAlter.enteredCombat(damager.getName());
			}
		} else
			combatMap.put(damaged.getName(), new CombatInstance());
		if (combatMap.containsKey(damager.getName())) {
			CombatInstance toAlter = combatMap.get(damager.getName());
			if (!toAlter.isInCombat())
				message(damager, "You are now engaged in combat.");
			if (toAlter.isInCombatWith(damaged.getName())) {
				toAlter.attack();
			} else {
				toAlter.enteredCombat(damaged.getName());
			}
		} else
			combatMap.put(damager.getName(), new CombatInstance());
	}

	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		if (combatMap.containsKey(event.getEntity().getName())) {
			CombatInstance ci = combatMap.get(event.getEntity().getName());
			if (!ci.isInCombat())
				return;
			for (String str : ci.getAllCombats()) {
				CombatInstance toProc = combatMap.get(str);
				toProc.exitCombat(event.getEntity().getName());
				if (!toProc.reCalcTimer()) {
					exitCombat(str);
				}
			}
			ci.exitCombat();
			exitCombat(event.getEntity().getName());
		} else if (event.getEntity().isOnline()) {
			combatMap.put(event.getEntity().getName(), new CombatInstance());
		}
	}

	@EventHandler
	public void playerJoin(PlayerLoginEvent event) {
		if (event.getResult() == Result.ALLOWED)
			combatMap.put(event.getPlayer().getName(), new CombatInstance());
	}

	private void playerLeft(Player p) {
		if (combatMap.containsKey(p.getName())) {
			CombatInstance ci = combatMap.get(p.getName());
			if (ci.isInCombat()) {
				if (!p.getWorld().getName().equalsIgnoreCase("other")) {
					p.setHealth(0.0d);
					for (String s : ci.getAllCombats())
						if (!combatMap.get(s).exitCombat(p.getName()))
							exitCombat(s);
				}
			}
			combatMap.remove(p.getName());
		}
	}

	@EventHandler
	public void playerQuit(PlayerKickEvent pke) {
		if (pke.getReason().contains("You're not allowed")
				&& pke.getReason().contains("spam"))
			return;
		Player p = pke.getPlayer();
		if (combatMap.containsKey(p.getName())) {
			combatMap.remove(p.getName());
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent pqe) {
		playerLeft(pqe.getPlayer());
		System.out.println("Player quit");
	}

	@EventHandler
	public void playerTeleport(PlayerTeleportEvent event) {
		if (!event.isCancelled()
				&& !event.getFrom().getWorld().getName().toLowerCase()
						.contains("bedrock") && !Config.Combat.teleportInCombat
				&& event.getCause() != TeleportCause.PLUGIN) {
			if (combatMap.containsKey(event.getPlayer().getName())
					&& combatMap.get(event.getPlayer().getName()).isInCombat()) {
				if (Config.Combat.enderPearlsInCombat == true
						&& event.getCause() == TeleportCause.ENDER_PEARL)
					return;
				if (event.getCause() != TeleportCause.ENDER_PEARL
						&& event.getFrom().distanceSquared(event.getTo()) < 10)
					return;
				event.setCancelled(true);
				message(event.getPlayer(),
						"You can't teleport whilst in combat!");
			}
		}
	}
}
