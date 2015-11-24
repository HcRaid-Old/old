package com.addongaming.hcessentials.minigames;

import org.bukkit.command.CommandSender;

public interface Minigame {
	public String getMinigameName();

	public boolean isInGame(String str);

	public void commandIssued(CommandSender sender, String[] args);

	public String getMinigameNotation();

	public boolean onEnable();

	void loadConfig();
}
