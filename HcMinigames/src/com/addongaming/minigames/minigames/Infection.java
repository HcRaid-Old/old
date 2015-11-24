package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.addongaming.hcessentials.utils.TimeUtils;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.scheduling.GameLimit;
import com.addongaming.minigames.management.scheduling.RespawnScheduler;

public class Infection extends ArenaGame {
	private final String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA
			+ "Infection" + ChatColor.GOLD + "] " + ChatColor.GREEN;
	long start = -1;

	public Infection(Arena arena, Lobby lobby) {
		super(arena, lobby);
		getArena().setBoolean(ArenaProperty.INSTANT_KILL, true);
		setPrefix(prefix);
		arena.putProperty(ArenaProperty.FRIENDLY_FIRE_ENABLED, false);
		for (ArenaPlayer ap : getArenaList())
			super.onSpawn(ap);
	}

	@Override
	public void removePlayer(String user) {
		super.removePlayer(user);
		calculateScores();
		if (getStatus() != Status.INGAME)
			return;
		if (getWinningTeam() == Team.INFECTED.getTeamId()) {
			getLobby().exitGame();
		}
		for (ArenaPlayer ap : getArenaList()) {
			if (ap.getTeam() == Team.INFECTED.getTeamId()) {
				return;
			}
		}
		if (getArenaList().size() == 0)
			getLobby().exitGame();
		else {
			ArenaPlayer p = getArenaList().get(0);
			p.setTeam(Team.INFECTED.getTeamId());
			onSpawn(p);
			message(p.getBase(), "You are now infected.");
		}
	}

	@Override
	public void onStart() {
		start = System.currentTimeMillis() + 20000;
		List<ArenaPlayer> ap = getArenaList();
		Collections.shuffle(ap);
		super.setStatus(Status.INGAME);
		Iterator<ArenaPlayer> iter = ap.iterator();
		ArenaPlayer aap;
		int infectedPlayers = ap.size() / 10;
		List<String> names = new ArrayList<String>();
		if (infectedPlayers == 0)
			infectedPlayers = 1;
		for (int i = 0; i < infectedPlayers; i++) {
			aap = iter.next();
			if (aap.getBase() == null || !aap.getBase().isOnline()) {
				i = i - 1;
				continue;
			}
			names.add(aap.getName());
			aap.setTeam(Team.INFECTED.getTeamId());
			message(aap.getBase(),
					"You're infected and will spawn in 10 seconds.");
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							new RespawnScheduler(aap, this), 10 * 20l);
		}
		while (iter.hasNext()) {
			aap = iter.next();
			aap.setTeam(Team.NORMAL.getTeamId());
			onSpawn(aap);
		}
		if (names.size() == 1) {
			messageAll(Team.NORMAL.getTeamId(), names.get(0) + " is infected!");
		} else {
			messageAll(Team.NORMAL.getTeamId(), StringUtils.join(names, ", ")
					+ " are infected!");
		}
		super.getLobby().getMinigames().getManagement()
				.getSchedulerManagement()
				.runScheduler(this, new GameLimit(this, 5 * 60, 1), 0l, 20);
	}

	@Override
	public void preSpawn(ArenaPlayer ap) {
		if (getStatus() == Status.INGAME) {
			ap.setTeam(Team.INFECTED.getTeamId());
			ap.setLives(-1);
		}
		super.preSpawn(ap);
	}

	@Override
	public void onInteract(Player player, ItemStack is, Block block) {
		if (is != null && is.getType() == Material.COMPASS) {
			List<ArenaPlayer> ap = getArenaList();
			Collections.shuffle(ap);
			for (ArenaPlayer aap : ap)
				if (aap.getTeam() == Team.NORMAL.getTeamId()) {
					player.setCompassTarget(aap.getLocation());
					message(player, "Found a human");
					return;
				}
		}
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		super.onSpawn(ap);
	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		if (killer.getTeam() == Team.INFECTED.getTeamId())
			killer.incrementScore(200);
		else if (killer.getTeam() == Team.NORMAL.getTeamId())
			killer.incrementScore(50);
		killer.incrementKills();
		died.resetKillStreak();
		died.died();
		if (died.getTeam() == Team.NORMAL.getTeamId()) {
			messageAll(died.getName() + " has been infected!");

			died.getBase().sendMessage(prefix + "You are now infected.");
			died.setTeam(Team.INFECTED.getTeamId());
		}
		sendScore(killer, true);
		sendScore(died, false);
		calculateScores();
		if (getWinningTeam() == Team.INFECTED.getTeamId()) {
			getLobby().exitGame();
		}
	}

	@Override
	public boolean onDamage(ArenaPlayer damager, ArenaPlayer hurt) {
		if (getStatus() == Status.LOBBY
				|| (damager.getTeam() == hurt.getTeam()))
			return false;
		else
			return true;
	}

	private void clearInven(Player player) {
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}

	@Override
	public void onConsume(ItemStack is, ArenaPlayer consume) {
		if (is.getType() == Material.PORK) {
			if (consume.getTeam() == Team.NORMAL.getTeamId()) {
				int norm = 0;
				for (ArenaPlayer ap : getArenaList())
					if (ap.getTeam() == Team.NORMAL.getTeamId())
						norm++;
				if (norm == 1) {
					message(consume.getBase(),
							"You're the last person alive, you can't transform!");
					return;
				}
				if (start > System.currentTimeMillis()) {
					message(consume.getBase(),
							"You need to wait another "
									+ new TimeUtils(start
											- System.currentTimeMillis())
											.toString());
					return;
				}
				clearInven(consume.getBase());
				consume.setTeam(Team.INFECTED.getTeamId());
				consume.getBase().addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED,
								Integer.MAX_VALUE, 1));
				equipKits(consume);
				calculateScores();
				if (getWinningTeam() == Team.INFECTED.getTeamId()) {
					getLobby().exitGame();
				}
			}
		}
	}

	private void calculateScores() {
		setWinner(Team.INFECTED.getTeamId());
		for (ArenaPlayer ap : getArenaList())
			if (ap.getTeam() == Team.NORMAL.getTeamId()) {
				setWinner(Team.NORMAL.getTeamId());
				return;
			}
	}

	@Override
	public void equipKits(ArenaPlayer ap) {
		if (ap.getBase() != null && ap.getBase().isOnline())
			switch (ap.getTeam()) {
			case 0: {
				ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
				ItemStack bow = new ItemStack(Material.BOW);
				ItemStack arrow = new ItemStack(Material.ARROW, 10);
				ItemStack flesh = new ItemStack(Material.PORK);
				ItemMeta fleshMeta = flesh.getItemMeta();
				fleshMeta.setDisplayName(ChatColor.RED + "Zombie Flesh");
				fleshMeta.setLore(new ArrayList<String>() {
					{
						add(ChatColor.DARK_RED + "Click with this item to");
						add(ChatColor.DARK_RED + "turn infected.");
					}
				});
				flesh.setItemMeta(fleshMeta);
				ap.getBase().getInventory().addItem(sword, bow, arrow, flesh);
				ap.getBase().updateInventory();
			}
				break;
			// infected
			case 1: {
				Material mat[] = new Material[] { Material.LEATHER_BOOTS,
						Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE,
						Material.LEATHER_HELMET };
				for (Material m : mat) {
					ItemStack piece = new ItemStack(m);
					LeatherArmorMeta lam = (LeatherArmorMeta) piece
							.getItemMeta();
					lam.setColor(Color.GREEN);
					piece.setItemMeta(lam);
					ap.setArmour(new ItemStack[] { piece });
				}
				ItemStack sword = new ItemStack(Material.STONE_SWORD);
				ap.getBase().getInventory()
						.addItem(sword, new ItemStack(Material.COMPASS));
				ap.getBase().addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED,
								Integer.MAX_VALUE, 1));
			}
				break;
			}
	}

	@Override
	public void onFinish() {
		onWin();
		super.onFinish();
	}

	@Override
	public void onWin() {
		calculateScores();
		if (getWinningTeam() == Team.INFECTED.getTeamId()) {
			messageAll("The infected have won!");
			for (ArenaPlayer ap : getArenaList()) {
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(ap.getName());
				mg.setSmallLoot(mg.getSmallLoot() + 1);
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerSmallLoot(ap.getName());
				message(ap.getBase(), "You've been awarded a small loot chest.");

			}
		} else if (getWinningTeam() == Team.NORMAL.getTeamId()) {
			messageAll("The survivors have won!");
			for (ArenaPlayer ap : getArenaList()) {
				if (ap.getTeam() == Team.NORMAL.getTeamId()) {
					MinigameUser mg = getLobby().getMinigames().getHub()
							.getMinigameUser(ap.getName());
					mg.setLargeLoot(mg.getLargeLoot() + 1);
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerLargeLoot(ap.getName());
					message(ap.getBase(),
							"You've been awarded a large loot chest for surviving the zombie onslaught.");

				}
			}
		}
		super.onWin();
	}

	private void sendScore(ArenaPlayer ap, boolean killstreak) {
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Score: " + ChatColor.AQUA
						+ ap.getScore());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Kills: " + ChatColor.AQUA
						+ ap.getKills());
		if (killstreak && ap.getTeam() == Team.NORMAL.getTeamId())
			ap.getBase().sendMessage(
					ChatColor.DARK_PURPLE + "Killstreak: " + ChatColor.AQUA
							+ ap.getKillStreak());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		// Pass through & ignore
		return false;
	}

	@Override
	public String getNameChange(ArenaPlayer tagChange, ArenaPlayer viewer) {
		if (tagChange.getTeam() == Team.INFECTED.getTeamId())
			return ChatColor.GREEN + tagChange.getName();
		else if (tagChange.getTeam() == Team.NORMAL.getTeamId())
			return ChatColor.BLUE + tagChange.getName();
		return null;
	}

	@Override
	public void playerChat(Player player, String message, boolean global) {
		ArenaPlayer ap = getPlayer(player.getName());
		String prefix;
		if (ap.getTeam() == Team.INFECTED.getTeamId())
			prefix = ChatColor.GREEN + player.getName();
		else
			prefix = ChatColor.BLUE + player.getName();
		for (ArenaPlayer arenaPlayer : getArenaList()) {
			if (arenaPlayer.isValid()) {
				if (global) {
					arenaPlayer.getBase().sendMessage(
							prefix + " [!] " + ChatColor.WHITE + message);
				} else if (arenaPlayer.getTeam() == ap.getTeam()) {
					arenaPlayer.getBase().sendMessage(
							prefix + " " + ChatColor.WHITE + message);
				}
			}
		}
	}

	@Override
	public ArenaPlayer[] getWinningPlayers() {
		List<ArenaPlayer> winners = new ArrayList<ArenaPlayer>();
		for (ArenaPlayer ap : getArenaList())
			if (ap.getTeam() == getWinningTeam())
				winners.add(ap);
		return winners.toArray(new ArenaPlayer[winners.size()]);
	}

	@Override
	public void openKits(Player player) {
		message(player, "Sorry you cannot choose your kits in this game.");
	}
}
