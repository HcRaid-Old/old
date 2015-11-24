package core.hcrg.bob;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import core.hcrg.Minigame;

public class BoB implements Minigame {
	private JavaPlugin jp;
	public static World world = null;
	private HashSet<String> players = null;
	private boolean started = false;
	private String pluginName = "[BoB] ";
	private BobTeam red = null;
	private BobTeam blue = null;

	public BoB(JavaPlugin jp) {
		for (World w : jp.getServer().getWorlds())
			if (w.getName().equalsIgnoreCase(
					jp.getConfig().getString("minigames.bob.world"))) {
				world = w;
				break;
			}
	}

	public void updateScoreboard() {

	}

	@Override
	public String getName() {
		return "Bridge Out Battle";
	}

	@EventHandler
	public void playerLeft(PlayerQuitEvent pqe) {
		if (players != null && players.contains(pqe.getPlayer().getName())) {
			players.remove(pqe.getPlayer().getName());
			if (started)
				checkEnd();
		}
	}

	@EventHandler
	public void playerKick(PlayerKickEvent pqe) {
		if (players != null && players.contains(pqe.getPlayer().getName())) {
			players.remove(pqe.getPlayer().getName());
			if (started)
				checkEnd();
		}
	}

	@Override
	public HashSet<String> getPlayers() {
		return players;
	}

	@Override
	public void warn(Player p, String warning) {
		p.sendMessage(ChatColor.RED + pluginName + ChatColor.WHITE + warning);
	}

	@Override
	public void msg(Player p, String message) {
		p.sendMessage(ChatColor.GREEN + pluginName + ChatColor.WHITE + message);
	}

	@Override
	public boolean join(Player p) {
		if (players == null) {
			warn(p, "No game has been started");
			return false;
		} else if (started) {
			warn(p, "Game is already in progress!");
			return false;
		} else {
			players.add(p.getName());
			msg(p, "You have joined the BoB game.");
			Random r = new Random();
			int i = r.nextInt(3);
			// p.teleport(spleefWaiting[i]);
			// TODO Make waiting area.
			return true;
		}
	}

	@Override
	public boolean leave(Player p) {
		if (players == null || !players.contains(p.getName())) {
			warn(p, "You are not in a BoB game");
			return false;
		} else if (started) {
			warn(p, "You are in the middle of a game! You can't leave now!");
			return false;
		} else {
			msg(p, "You have left the game.");
			players.remove(p.getName());
			/*
			 * p.teleport(new Location(
			 * jp.getServer().getWorld(Config.Spawn.world), Config.Spawn.x,
			 * Config.Spawn.y, Config.Spawn.z));
			 */
			// TODO tp to spawn
			return true;
		}
	}

	@Override
	public boolean start(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean run(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void executeCommand(Player sender, String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean stop(Player sender, boolean force) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void listPlayers(Player sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkEnd() {
		// TODO Auto-generated method stub

	}
}
