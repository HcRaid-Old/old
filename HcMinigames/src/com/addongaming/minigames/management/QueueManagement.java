package com.addongaming.minigames.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.scheduling.HubInventory;
import com.addongaming.minigames.management.scheduling.QueueTicker;
import com.addongaming.minigames.minigames.Lobby;

public class QueueManagement implements CommandExecutor {
	private final HcMinigames minigames;
	private final static HashMap<MinigameUser, GameMode> queueMap = new HashMap<MinigameUser, GameMode>();
	private final static List<Lobby> lobbyList = new ArrayList<Lobby>();
	private final HashMap<GameMode, Integer> gamemodeTally = new HashMap<GameMode, Integer>();

	public QueueManagement(HcMinigames minigames) {
		this.minigames = minigames;
		minigames
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(minigames,
						new QueueTicker(minigames), 20L, 20 * 60);
		minigames.getCommand("exit").setExecutor(this);
		minigames.getCommand("upvote").setExecutor(this);
		minigames.getCommand("downvote").setExecutor(this);
	}

	public void queuePlayer(MinigameUser mg, GameMode gm) {
		queueMap.put(mg, gm);
		mg.getBase().sendMessage(
				ChatColor.GREEN + "Now queuing for " + gm.toReadableText());
		clearInven(mg.getBase());
	}

	public void tallyGamemode() {
		gamemodeTally.clear();
		for (GameMode gm : queueMap.values()) {
			if (!gamemodeTally.containsKey(gm)) {
				gamemodeTally.put(gm, 1);
			} else {
				gamemodeTally.put(gm, gamemodeTally.get(gm) + 1);
			}
		}
		for (Iterator<GameMode> iter = gamemodeTally.keySet().iterator(); iter
				.hasNext();) {
			GameMode gm = iter.next();
			System.out.println("GameMode " + gm.name().toLowerCase() + " has "
					+ gamemodeTally.get(gm) + " queued players.");
			if (gamemodeTally.get(gm) < gm.getMinSize())
				iter.remove();
		}
	}

	public void tick() {
		tallyGamemode();
		Collections.shuffle(lobbyList);
		for (GameMode gm : gamemodeTally.keySet()) {
			if ((minigames.getManagement().getArenaManagement()
					.getAllFreeArenas(gm).length == 1 && minigames
					.getManagement().getArenaManagement().getAllArenas(gm).length != 1)
					|| minigames.getManagement().getArenaManagement()
							.getAllFreeArenas(gm).length == 0)
				continue;
			System.out.println("Creating new lobby.");
			Lobby lobby = new Lobby(minigames, gm);
			int countermax = gm.getMaxSize();
			for (Iterator<MinigameUser> iter = queueMap.keySet().iterator(); iter
					.hasNext() && countermax > 0;) {
				MinigameUser mg = iter.next();
				if (queueMap.get(mg) == gm) {
					lobby.addMinigameUser(mg);
				}
				iter.remove();
			}
			lobby.onGameEnd();
			lobbyList.add(lobby);
		}
		for (Iterator<MinigameUser> iter = queueMap.keySet().iterator(); iter
				.hasNext();) {
			MinigameUser mg = iter.next();
			if (mg == null || mg.getBase() == null) {
				iter.remove();
				continue;
			}
			GameMode gm = queueMap.get(mg);
			Lobby[] lobbies = getAllLobbies(gm);
			if (lobbies.length == 0)
				continue;
			Lobby lobby = lobbies[new Random().nextInt(lobbies.length)];
			if (lobby.getLobby().size() < gm.getMaxSize()) {
				System.out.println("Adding " + mg.getName() + " to predefined "
						+ gm.name() + " map");
				lobby.addMinigameUser(mg);
				iter.remove();
			}
		}
	}

	private Lobby[] getAllLobbies(GameMode gm) {
		List<Lobby> toReturn = new ArrayList<Lobby>();
		for (Lobby lobby : lobbyList)
			if (lobby.getGameMode() == gm)
				toReturn.add(lobby);
		return toReturn.toArray(new Lobby[toReturn.size()]);
	}

	public void removePlayerFromQueue(Player p) {
		for (Iterator<MinigameUser> iter = queueMap.keySet().iterator(); iter
				.hasNext();) {
			if (p.getName().equalsIgnoreCase(iter.next().getName()))
				iter.remove();
		}
	}

	public void removePlayerFromAll(String name) {
		for (Iterator<MinigameUser> iter = queueMap.keySet().iterator(); iter
				.hasNext();) {
			if (name.equalsIgnoreCase(iter.next().getName())) {
				System.out.println("Removing player from queue");
				iter.remove();
			}
		}
		for (Iterator<Lobby> iter = lobbyList.iterator(); iter.hasNext();) {
			Lobby lobby = iter.next();
			if (lobby == null)
				continue;
			if (lobby.containsPlayer(name)) {
				System.out.println("Removing player from a lobby.");
				lobby.removeMinigameUser(name);
			}
		}
	}

	public void removeLobby(Lobby lobby) {
		for (Iterator<Lobby> iter = lobbyList.iterator(); iter.hasNext();)
			if (iter.next().equals(lobby))
				iter.remove();
	}

	public void cleanse() {
		for (Iterator<MinigameUser> iter = queueMap.keySet().iterator(); iter
				.hasNext();) {
			Player p = Bukkit.getPlayer(iter.next().getName());
			if (p == null || !p.isOnline())
				iter.remove();
		}
	}

	public boolean hasPlayer(String name) {
		for (MinigameUser mu : queueMap.keySet())
			if (mu.getName().equalsIgnoreCase(name))
				return true;
		for (Iterator<Lobby> iter = lobbyList.iterator(); iter.hasNext();)
			if (iter.next().containsPlayer(name))
				return true;
		return false;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg1.getName().equalsIgnoreCase("upvote")) {
			Player player = (Player) arg0;
			MinigameUser user = minigames.getHub().getMinigameUser(
					player.getName());
			Lobby lobby = minigames.getManagement().getQueueManagement()
					.getLobby(user);
			if (lobby == null) {
				arg0.sendMessage(ChatColor.RED
						+ "You need to be in a lobby to execute this.");
				return true;
			}
			lobby.playerVote(user, 1);
			return true;
		} else if (arg1.getName().equalsIgnoreCase("downvote")) {
			Player player = (Player) arg0;
			MinigameUser user = minigames.getHub().getMinigameUser(
					player.getName());
			Lobby lobby = minigames.getManagement().getQueueManagement()
					.getLobby(user);
			if (lobby == null) {
				arg0.sendMessage(ChatColor.RED
						+ "You need to be in a lobby to execute this.");
				return true;
			}
			lobby.playerVote(user, -1);
			return true;
		} else {
			if (!(arg0 instanceof Player)) {
				for (Arena arena : minigames.getManagement()
						.getArenaManagement().getAllArenas())
					if (arena.hasCurrentGame()) {
						for (ArenaPlayer ap : arena.getCurrentGame()
								.getArenaList())
							System.out.println("Team: " + ap.getTeam()
									+ " Name " + ap.getName());
					}
				System.out.println("Queue size: " + queueMap.size());
				System.out.println("Lobby size: " + lobbyList.size());
				return true;
			}
			if (arg2.equalsIgnoreCase("exit") || arg2.equalsIgnoreCase("quit")
					|| arg2.equalsIgnoreCase("hub")
					|| arg2.equalsIgnoreCase("spawn")
					|| arg2.equalsIgnoreCase("leave")) {
				removePlayerFromAll(arg0.getName());
				clearInven((Player) arg0);
				minigames
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								minigames,
								new HubInventory(minigames.getHub()
										.getMinigameUser(arg0.getName()),
										minigames.getHub()), 2l);
				((Player) (arg0)).teleport(minigames.getHub()
						.getSpawnLocation());
				return true;
			}
			if (!arg0.isOp())
				return true;
			Player player = (Player) arg0;
			if (arg3.length == 1) {
				switch (arg3[0].toLowerCase()) {
				case "tick":
					tick();
					break;
				case "status":
					arg0.sendMessage("Queue size: " + queueMap.size());
					arg0.sendMessage("Lobby size: " + lobbyList.size());
					break;
				}
			}
		}
		return false;
	}

	public Lobby getLobby(MinigameUser user) {
		for (Lobby lobby : lobbyList)
			if (lobby.containsPlayer(user))
				return lobby;
		return null;
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

}
