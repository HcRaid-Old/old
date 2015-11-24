package com.addongaming.hcessentials.teams.listeners;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.events.TeamProtectEvent;
import com.addongaming.hcessentials.teams.Team;
import com.addongaming.hcessentials.teams.TeamCore;
import com.addongaming.hcessentials.teams.TeamMethods;

public class PlayerInteractions extends TeamMethods implements Listener {
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			Player player = event.getPlayer();
			if (TeamChatHandler.chatToggled.contains(player.getName())) {
				event.setCancelled(true);
				Team teamIn = null;
				for (Team t : TeamCore.teamSet
						.toArray(new Team[TeamCore.teamSet.size()])) {
					if (t.containsPlayer(player.getName())) {
						teamIn = t;
						break;
					} else
						continue;
				}
				if (teamIn == null) {
					warn(player, "You are not in a team. Toggling chat off");
					TeamChatHandler.chatToggled.remove(player.getName());
					return;
				} else {
					ChatColor rank = null;
					ChatColor chat = null;
					switch (teamIn.getRank(player.getName())) {
					case 0:
						rank = super.GRY;
						chat = super.WHT;
						break;
					case 1:
						rank = super.AQU;
						chat = super.GREEN;
						break;
					case 2:
						rank = super.GOLD;
						chat = super.GOLD;
						break;
					}
					for (String playersName : teamIn.getMembers()) {
						if (Bukkit.getPlayer(playersName) != null) {
							Bukkit.getPlayer(playersName).sendMessage(
									DGREN
											+ "[HcTeams] "
											+ "<"
											+ rank
											+ player.getName()
											+ super.GREEN
											+ "> "
											+ chat
											+ " "
											+ ChatColor.stripColor(event
													.getMessage()));

						}
					}
				}
			}
		}
	}

	@EventHandler
	public void PlayerDamaged(EntityDamageByEntityEvent event) {
		// Add in chooseable non-affected minigame worlds
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof Player) {
			String dmgr = ((Player) event.getDamager()).getName();
			String dmge = ((Player) event.getEntity()).getName();
			for (Iterator<Team> it = TeamCore.teamSet.iterator(); it.hasNext();) {
				Team t = it.next();
				if (t.containsPlayer(dmgr) && t.containsPlayer(dmge)) {
					TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge, t);
					Bukkit.getPluginManager().callEvent(ev);
					if (!ev.isCancelled())
						event.setCancelled(true);
					return;
				}
			}
		} else if (event.getDamager() instanceof Arrow
				&& event.getEntity() instanceof Player) {
			Arrow arr = (Arrow) event.getDamager();
			if (arr.getShooter() instanceof Player) {
				String dmgr = ((Player) arr.getShooter()).getName();
				String dmge = ((Player) event.getEntity()).getName();
				for (Iterator<Team> it = TeamCore.teamSet.iterator(); it
						.hasNext();) {
					Team t = it.next();
					if (t.containsPlayer(dmgr) && t.containsPlayer(dmge)) {
						TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge,
								t);
						Bukkit.getPluginManager().callEvent(ev);
						if (!ev.isCancelled())
							event.setCancelled(true);
						return;
					}
				}
			}
		} else if (event.getDamager() instanceof org.bukkit.entity.ThrownPotion
				&& event.getEntity() instanceof Player) {
			// System.out.println("Pot thrown!");
			ThrownPotion tp = (ThrownPotion) event.getDamager();
			if (tp.getShooter() instanceof Player) {
				// System.out.println("The shooter of the pot is a player! zomfg");
				String dmgr = ((Player) tp.getShooter()).getName();
				String dmge = ((Player) event.getEntity()).getName();
				for (Iterator<Team> it = TeamCore.teamSet.iterator(); it
						.hasNext();) {
					Team t = it.next();
					if (t.containsPlayer(dmgr) && t.containsPlayer(dmge)) {
						TeamProtectEvent ev = new TeamProtectEvent(dmgr, dmge,
								t);
						Bukkit.getPluginManager().callEvent(ev);
						if (!ev.isCancelled())
							event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void playerMove(PlayerMoveEvent pme) {

		if (TeamChatHandler.teleporting.containsKey(pme.getPlayer().getName())) {
			if (pme.getTo().distanceSquared(pme.getFrom()) == 0.0)
				return;
			warn(pme.getPlayer(), "Teleportation cancelled.");
			TeamChatHandler.teleporting.remove(pme.getPlayer().getName());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void PotionsSplash(PotionSplashEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Team teamIn = null;
			Player S = (Player) e.getEntity().getShooter();
			for (Iterator<Team> it = TeamCore.teamSet.iterator(); it.hasNext();) {
				Team t = it.next();
				if (t.containsPlayer(S.getName())) {
					teamIn = t;
				}
			}
			PotionEffectType[] badTypes = new PotionEffectType[] {
					PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING,
					PotionEffectType.HARM, PotionEffectType.CONFUSION,
					PotionEffectType.BLINDNESS, PotionEffectType.POISON,
					PotionEffectType.WITHER };
			boolean flag = false;
			for (PotionEffect pet : e.getEntity().getEffects())
				System.out.println(pet.getType().getName());
			for (PotionEffect pe : e.getEntity().getEffects()) {
				for (PotionEffectType pet : badTypes) {
					if (pe.getType().getId() == pet.getId()) {
						// System.out.println("Bad pot thrown");
						flag = true;
						break;
					}
				}
			}
			if (teamIn == null || !flag)
				return;
			Collection<LivingEntity> T = e.getAffectedEntities();
			for (Iterator<LivingEntity> li = T.iterator(); li.hasNext();) {
				LivingEntity next = li.next();
				if (next instanceof Player) {
					Player play = (Player) next;
					if (!play.getName().equalsIgnoreCase(S.getName()))
						if (teamIn.containsPlayer(play.getName())) {
							e.setIntensity(next, 0.0);
						}
				}
			}
		}
	}
}
