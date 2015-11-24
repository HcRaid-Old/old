package com.addongaming.prison.player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.stats.Stats;

/**
 * Class which will load/save and contain all prisoner (player) data
 * 
 * @author Jake
 * 
 */
public class Prisoner {
	/**
	 * YAMLConfiguration for the file to be read/written to
	 */
	private double balance;
	private Scoreboard board;
	private int currentCharacterExp;
	private short currentCharacterLevel;
	private PlayerClasses currentClass;
	private int currentClassExp;
	private short currentClassLevel;
	private long lastAction = new Date().getTime();
	private Stats lastTraining = Stats.NONE;
	private final String name;
	private final UUID uuid;
	private List<String> permissions;
	private final File prisonerFile;
	private short quests;
	private HashMap<Stats, Integer> statMap = new HashMap<Stats, Integer>();
	PermissionAttachment attachment;

	public Prisoner(JavaPlugin jp, File prisonerFile, Player player) {
		this.prisonerFile = prisonerFile;
		this.name = player.getName();
		this.uuid = player.getUniqueId();
		attachment = player.addAttachment(jp);
		loadConfig();
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		setupScoreboard();
	}

	public void addBalance(double amount) {
		balance += amount;
		saveConfig();
	}

	public void addPermission(String permission) {
		permissions.add(permission);
		attachment.setPermission(permission, true);
		saveConfig();
	}

	public String[] getAllPerms() {
		return permissions.toArray(new String[permissions.size()]);
	}

	public double getBalance() {
		return balance;
	}

	public int getCharacterExp() {
		return currentCharacterExp;
	}

	public int getClassExp() {
		return currentClassExp;
	}

	public String getName() {
		return name;
	}

	public PlayerClasses getPlayerClass() {
		return this.currentClass;
	}

	public int getStat(Stats stat) {
		if (stat == Stats.NONE)
			return 0;
		return statMap.get(stat);
	}

	public void giveCharacterExp(int xp) {
		int initialLevel = PlayerClasses.getLevel(currentCharacterExp);
		this.currentCharacterExp += xp;
		if (initialLevel < PlayerClasses.getLevel(currentCharacterExp)) {
			Bukkit.getPlayer(uuid)
					.sendMessage(
							ChatColor.GREEN
									+ "["
									+ ChatColor.GOLD
									+ "Character"
									+ ChatColor.GREEN
									+ "] "
									+ ChatColor.RESET
									+ "Congratulations on level "
									+ PlayerClasses
											.getLevel(currentCharacterExp)
									+ "!");
		}
		recalculateLevels();
		refreshScoreboard();
	}

	public void giveClassExp(int xp) {
		int initialLevel = PlayerClasses.getLevel(currentClassExp);
		this.currentClassExp += xp;
		if (initialLevel < PlayerClasses.getLevel(currentClassExp)) {
			Bukkit.getPlayer(uuid).sendMessage(
					ChatColor.GREEN + "[" + ChatColor.GOLD
							+ currentClass.toText() + ChatColor.GREEN + "] "
							+ ChatColor.RESET + "Congratulations on level "
							+ PlayerClasses.getLevel(currentClassExp) + "!");
		}
		recalculateLevels();
		refreshScoreboard();
	}

	public boolean hasBalance(double amount) {
		return balance >= amount;
	}

	public boolean hasPermission(String str) {
		return permissions.contains(str);
	}

	public void incrementStat(Stats stat, int i) {
		int currXp = statMap.get(stat);
		if (Stats.getLevel(currXp) < Stats.getLevel(currXp + i)) {
			Player play = Bukkit.getPlayer(name);
			Stats.levelledUp(play, stat, Stats.getLevel(currXp + i));
		}
		statMap.put(stat, statMap.get(stat) + i);
		if (lastTraining != stat)
			lastTraining = stat;
		refreshScoreboard();
	}

	private final void loadConfig() {
		YamlConfiguration prisonerConfig = YamlConfiguration
				.loadConfiguration(prisonerFile);
		permissions = prisonerConfig.getStringList("player.permissions");
		for (String str : permissions)
			attachment.setPermission(str, true);
		currentClass = PlayerClasses.valueOf(prisonerConfig
				.getString("player.class.currentclass"));
		currentClassExp = prisonerConfig.getInt("player.class.exp");
		currentCharacterExp = prisonerConfig.getInt("player.character.exp");
		balance = prisonerConfig.getDouble("player.balance");
		for (Stats stat : Stats.values())
			if (stat != Stats.NONE)
				statMap.put(
						stat,
						prisonerConfig.getInt("player.stats."
								+ stat.name().toLowerCase() + ".exp"));
		recalculateLevels();
	}

	private void recalculateLevels() {
		currentClassLevel = PlayerClasses.getLevel(currentClassExp);
		currentCharacterLevel = PlayerClasses.getLevel(currentCharacterExp);
	}

	public void refreshScoreboard() {
		if (currentClass == PlayerClasses.LIMBO)
			return;
		int counter = 10;
		if (lastTraining != Stats.NONE)
			counter += 4;
		if (board.getObjective(DisplaySlot.SIDEBAR) == null) {
			setupScoreboard();
			return;
		}
		board.getObjective(DisplaySlot.SIDEBAR).unregister();
		Objective obj = board.registerNewObjective(ChatColor.BOLD
				+ "Prisoner Info", "dummy");
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + " ")).setScore(
				counter--);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Class Info"))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + currentClass.toText()))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Level: "
						+ currentClassLevel)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Exp: "
						+ currentClassExp)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Left: "
						+ PlayerClasses.expTillLevel(currentClassExp)))
				.setScore(counter--);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Character Info"))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Level: "
						+ currentCharacterLevel)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Exp: "
						+ currentCharacterExp)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Left:  "
						+ PlayerClasses.expTillLevel(currentCharacterExp)))
				.setScore(counter--);

		if (lastTraining != Stats.NONE) {
			obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Stats Info"))
					.setScore(counter--);
			obj.getScore(
					Bukkit.getOfflinePlayer(ChatColor.BOLD + ""
							+ lastTraining.toText() + "")).setScore(counter--);
			obj.getScore(
					Bukkit.getOfflinePlayer(ChatColor.RED + "Level: "
							+ Stats.getLevel(statMap.get(lastTraining))))
					.setScore(counter--);
			obj.getScore(
					Bukkit.getOfflinePlayer(ChatColor.RED + "Exp: "
							+ statMap.get(lastTraining))).setScore(counter--);

			obj.getScore(
					Bukkit.getOfflinePlayer(ChatColor.RED + "Left: "
							+ Stats.expTillLevel(statMap.get(lastTraining))))
					.setScore(counter--);

		}
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Bukkit.getPlayer(name).setScoreboard(board);
	}

	public boolean removeBalance(double amount) {
		if (balance >= amount) {
			balance -= amount;
			saveConfig();
			return true;
		} else
			return false;
	}

	public void removePermission(String permission) {
		for (Iterator<String> iter = permissions.iterator(); iter.hasNext();)
			if (iter.next().equalsIgnoreCase(permission))
				iter.remove();
		attachment.unsetPermission(permission);
		saveConfig();
	}

	public void saveConfig() {
		YamlConfiguration prisonerConfig = YamlConfiguration
				.loadConfiguration(prisonerFile);
		prisonerConfig.set("player.class.currentclass", currentClass.name());
		prisonerConfig.set("player.class.exp", currentClassExp);
		prisonerConfig.set("player.character.exp", currentCharacterExp);
		prisonerConfig.set("player.permissions", permissions);
		prisonerConfig.set("player.balance", balance);
		for (Stats stat : statMap.keySet())
			prisonerConfig.set("player.stats." + stat.name().toLowerCase()
					+ ".exp", statMap.get(stat));
		prisonerConfig.set("quests.completed", quests);
		try {
			prisonerConfig.save(prisonerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPlayerClass(PlayerClasses pc) {
		this.currentClass = pc;
		saveConfig();
	}

	private void setupScoreboard() {
		if (currentClass == PlayerClasses.LIMBO)
			return;
		Objective obj = board.registerNewObjective(ChatColor.BOLD
				+ "Prisoner Info", "dummy");
		int counter = 9;
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + " ")).setScore(
				counter--);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Class Info"))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + currentClass.toText()))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Level: "
						+ currentClassLevel)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Exp: "
						+ currentClassExp)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.AQUA + "Left: "
						+ PlayerClasses.expTillLevel(currentClassExp)))
				.setScore(counter--);
		obj.getScore(Bukkit.getOfflinePlayer(ChatColor.BOLD + "Character Info"))
				.setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Level: "
						+ currentCharacterLevel)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Exp: "
						+ currentCharacterExp)).setScore(counter--);
		obj.getScore(
				Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Left: "
						+ PlayerClasses.expTillLevel(currentCharacterExp)))
				.setScore(counter--);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Bukkit.getPlayer(uuid).setScoreboard(board);
	}

	public void stopping() {
		Bukkit.getPlayer(uuid).removeAttachment(attachment);
	}

	public UUID getUUID() {
		return uuid;
	}
}
