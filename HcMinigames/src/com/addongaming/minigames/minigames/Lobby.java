package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.events.PlayerEnteredGameEvent;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.SurvivalGameArena;
import com.addongaming.minigames.management.arena.TheShipArena;
import com.addongaming.minigames.management.scheduling.ArenaRun;

public class Lobby {
	private List<MinigameUser> lobby = new ArrayList<MinigameUser>();
	private ArenaGame currentGame = null;
	private GameMode gameMode;
	private String lastArenaType = "";
	private HcMinigames minigames;

	public Lobby(HcMinigames minigames, GameMode gameMode) {
		this.minigames = minigames;
		this.gameMode = gameMode;
	}

	public boolean isInGame() {
		return currentGame != null;
	}

	public void exitGame() {
		if (currentGame != null) {
			if (lobby.size() > 1)
				minigames.getManagement().getScoreManagement()
						.incrementArena(gameMode, lastArenaType);
			currentGame.onFinish();
			currentGame = null;
		}
		onGameEnd();
	}

	public void onGameEnd() {
		for (MinigameUser mu : lobby)
			if (mu.getBase() != null && mu.getBase().isOnline())
				clearInven(mu.getBase());
		if (lobby.size() < gameMode.getMinSize()) {
			minigames.getManagement().getQueueManagement().removeLobby(this);
			for (MinigameUser mu : lobby) {
				if (mu.isValid()) {
					mu.getBase()
							.sendMessage(
									ChatColor.GREEN
											+ "["
											+ ChatColor.GOLD
											+ gameMode.toReadableText()
											+ ChatColor.GREEN
											+ "] "
											+ ChatColor.AQUA
											+ "There are not enough players for a game. You will be placed back into the queue.");
					minigames.getManagement().getQueueManagement()
							.removePlayerFromAll(mu.getName());
					minigames.getHub().queuePlayer(mu.getBase(), gameMode);
				}
			}
			return;
		}
		List<Arena> arenas = Arrays.asList(minigames.getManagement()
				.getArenaManagement().getAllFreeArenas(gameMode));
		Collections.shuffle(arenas);
		Arena arena = null;
		for (Arena aren : arenas)
			if (aren.getArenaType().equalsIgnoreCase(lastArenaType)) {
				continue;
			} else {
				arena = aren;
				break;
			}
		for (MinigameUser ap : lobby)
			if (ap.getBase() != null && ap.getBase().isOnline())
				TagAPI.refreshPlayer(ap.getBase());
		if (arena == null)
			arena = arenas.get(0);
		switch (gameMode) {
		case KITS:
			currentGame = new KitPvP(arena, this);
			break;
		case TEAMDEATHMATCH:
			currentGame = new TeamDeathmatch(arena, this);
			System.out.println("Lobby loaded arena: " + arena.getId());
			break;
		case INFECTION:
			currentGame = new Infection(arena, this);
			break;
		case RVB:
			currentGame = new RedVsBlue(arena, this);
			break;
		case CONQUEST:
			currentGame = new Conquest(arena, this);
			break;
		case MODERN_WARFARE:
			currentGame = new ModernWarfare(arena, this);
			break;
		case KILL_CONFIRMED:
			currentGame = new KillConfirmed(arena, this);
			break;
		case SURVIVAL_GAMES:
			currentGame = new SurvivalGames((SurvivalGameArena) arena, this);
			break;
		case TACTICAL_INTERVENTION:
			currentGame = new TacticalIntervention(arena, this);
			break;
		case GUN_GAME:
			currentGame = new GunGame(arena, this);
			break;
		case THE_SHIP:
			currentGame = new TheShip((TheShipArena) arena, this);
			break;
		default:
			break;
		}
		lastArenaType = currentGame.getArena().getArenaType();
		minigames
				.getManagement()
				.getSchedulerManagement()
				.runScheduler(currentGame,
						new ArenaRun(currentGame, gameMode.getLobbyTime()), 0L,
						20L);
	}

	public void addMinigameUser(MinigameUser user) {
		clearInven(user.getBase());
		reLogin(user);
	}

	private void clearInven(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}

	public void removeMinigameUser(MinigameUser user) {
		lobby.remove(user);
		if (currentGame != null)
			currentGame.removePlayer(user);
	}

	public void removeMinigameUser(String user) {
		for (Iterator<MinigameUser> iter = lobby.iterator(); iter.hasNext();)
			if (iter.next().getName().equalsIgnoreCase(user))
				iter.remove();
		if (currentGame != null)
			currentGame.removePlayer(user);
	}

	public MinigameUser getByPlayer(Player player) {
		for (MinigameUser mu : lobby)
			if (mu.getBase() == player)
				return mu;
		return null;
	}

	public List<MinigameUser> getLobby() {
		return lobby;
	}

	public ArenaGame getCurrentGame() {
		return currentGame;
	}

	public void reLogin(MinigameUser mg) {
		Bukkit.getPluginManager().callEvent(new PlayerEnteredGameEvent(mg));
		for (Iterator<MinigameUser> iter = lobby.iterator(); iter.hasNext();) {
			MinigameUser user = iter.next();
			if (user.getName().equalsIgnoreCase(mg.getName()))
				iter.remove();
		}
		lobby.add(mg);
		if (currentGame != null) {
			currentGame.updateArenaPlayer(mg);
		}
		TagAPI.refreshPlayer(mg.getBase());
		switch (gameMode) {
		case GUN_GAME:
		case KILL_CONFIRMED:
		case MODERN_WARFARE:
		case TACTICAL_INTERVENTION:
			if (!mg.isTextureChanges()) {
				mg.getBase().setResourcePack(
						"http://textures.hcraid.com:8070/guns.zip");
				mg.setTextureChanges(true);
			}
			break;
		case THE_SHIP:
		case CONQUEST:
		case INFECTION:
		case KITS:
		case RVB:
		case SURVIVAL_GAMES:
		case TEAMDEATHMATCH:
		default:
			if (mg.isTextureChanges()) {
				mg.getBase().setResourcePack(
						"http://textures.hcraid.com:8070/normal.zip");
				mg.setTextureChanges(false);
			}

		}

	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public HcMinigames getMinigames() {
		return minigames;
	}

	public boolean containsPlayer(MinigameUser mg) {
		for (MinigameUser mu : lobby)
			if (mu == mg)
				return true;
		return false;
	}

	public boolean containsPlayer(String mg) {
		for (MinigameUser user : lobby)
			if (user.getName().equalsIgnoreCase(mg))
				return true;
		return false;
	}

	public void playerChat(Player player, String message) {
		String prefix;
		// TODO Ranks
		if (player.hasPermission("hcraid.premium.chat")) {
			prefix = ChatColor.BLUE + "Patron " + ChatColor.AQUA
					+ player.getName() + " " + ChatColor.WHITE;
		} else {
			prefix = ChatColor.GRAY + player.getName() + " ";
		}
		for (MinigameUser mu : getLobby()) {
			if (!mu.isValid())
				continue;
			mu.getBase().sendMessage(prefix + message);
		}
	}

	public void playerVote(MinigameUser user, int i) {
		if (lastArenaType.length() == 0) {
			user.getBase().sendMessage(
					ChatColor.RED + "You're still playing the first game.");
			return;
		}
		if (user.hasVotedFor(lastArenaType, gameMode)) {
			user.getBase().sendMessage(
					ChatColor.RED + "You've already voted for this map.");
			return;
		}
		user.votedFor(lastArenaType, gameMode);
		if (i == 1)
			minigames.getManagement().getScoreManagement()
					.upvoteArena(gameMode, lastArenaType);
		else
			minigames.getManagement().getScoreManagement()
					.downvoteArena(gameMode, lastArenaType);
	}

	public void shutdown() {
		if (currentGame != null)
			currentGame.onWin();
	}
}
