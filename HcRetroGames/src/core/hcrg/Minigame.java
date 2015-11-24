package core.hcrg;

import java.util.HashSet;

import org.bukkit.entity.Player;

public interface Minigame {
	public String getName();

	public HashSet<String> getPlayers();

	public boolean join(Player p);

	public boolean leave(Player p);

	public boolean start(Player p);

	boolean run(Player p);

	public void warn(Player p, String warning);

	public void msg(Player p, String message);

	void executeCommand(Player sender, String[] args);

	boolean stop(Player sender, boolean force);

	void listPlayers(Player sender);

	void checkEnd();
}
