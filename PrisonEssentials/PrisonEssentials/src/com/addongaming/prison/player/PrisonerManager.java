package com.addongaming.prison.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.stats.Stats;

/**
 * PrisonerManager works as the 'bridge' between the plugin and the
 * loading/saving/managing of prisoner information
 * 
 * @author Jake
 * 
 */
public class PrisonerManager implements Listener {
	private final static List<Prisoner> prisonerList = new ArrayList<Prisoner>();
	private static PrisonerManager prisonManager;

	public static PrisonerManager getInstance() {
		return prisonManager;
	}

	private final JavaPlugin jp;

	private final File mainFolder;

	public PrisonerManager(JavaPlugin jp) {
		prisonManager = this;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		this.jp = jp;
		mainFolder = new File(jp.getDataFolder() + File.separator + "Players");
		if (!mainFolder.exists())
			mainFolder.mkdirs();
		for (Player p : Bukkit.getOnlinePlayers())
			loadPlayer(p);
		new AutoSaver(jp);
	}

	/**
	 * This method will ensure that all the default values are existent before
	 * attempting to be loaded. Both this method and the loading in Prisoner
	 * should be altered together
	 */
	private void checkConfig(File yamlFile) {
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
		yaml.addDefault("player.class.currentclass", PlayerClasses.LIMBO.name());
		yaml.addDefault("player.class.exp", 0);
		yaml.addDefault("player.character.exp", 0);
		yaml.addDefault("player.balance", 200d);
		List<String> started = new ArrayList<String>() {
			{
				this.add("hcraid.limbo");
				this.add("prison.inve.chest");
			}
		};
		yaml.addDefault("player.permissions", started);
		for (Stats stat : Stats.values())
			yaml.addDefault("player.stats." + stat.name().toLowerCase()
					+ ".exp", 0);
		yaml.addDefault("quests.completed", 0);
		yaml.options().copyDefaults(true);
		try {
			yaml.save(yamlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Prisoner getPrisonerInfo(String name) {
		for (Iterator<Prisoner> it = prisonerList.iterator(); it.hasNext();) {
			Prisoner p = it.next();
			if (p.getName().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}

	public Prisoner getPrisonerInfo(UUID uuid) {
		for (Iterator<Prisoner> it = prisonerList.iterator(); it.hasNext();) {
			Prisoner p = it.next();
			if (p.getUUID().toString().equals(uuid.toString()))
				return p;
		}
		return null;
	}

	/**
	 * Incase a plugin reload is called this can load all players currently
	 * online.
	 * 
	 * @param player
	 *            Player to load
	 */
	private void loadPlayer(Player player) {
		File prisonerFile = new File(mainFolder + File.separator
				+ player.getUniqueId().toString() + ".yml");
		if (!prisonerFile.exists())
			try {
				prisonerFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		checkConfig(prisonerFile);
		prisonerList.add(new Prisoner(jp, prisonerFile, player));
	}

	/**
	 * Thrown when the player joins the server - this will load their Prisoner
	 * file.
	 * 
	 * @param event
	 *            Event of player joining the server
	 */
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		loadPlayer(event.getPlayer());
	}

	/**
	 * Thrown when a player disconnects, or after the PlayerKickEvent if kicked.
	 * Also thrown when server restarts
	 * 
	 * @param event
	 *            Event of player disconnection from the server
	 */
	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		savePlayer(event.getPlayer().getName());
	}

	public void saveAll() {
		for (Prisoner p : prisonerList)
			p.saveConfig();
	}

	public void savePlayer(String name) {
		for (Iterator<Prisoner> it = prisonerList.iterator(); it.hasNext();) {
			Prisoner p = it.next();
			if (p.getName().equalsIgnoreCase(name)) {
				p.saveConfig();
				p.stopping();
				it.remove();
				return;
			}
		}
	}
}
