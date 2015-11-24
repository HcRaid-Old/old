package com.addongaming.hcessentials;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.afk.AFKKicker;
import com.addongaming.hcessentials.antilag.AntiLag;
import com.addongaming.hcessentials.autoannouncer.AutoAnnouncer;
import com.addongaming.hcessentials.blocks.BlockMod;
import com.addongaming.hcessentials.bounty.Bounty;
import com.addongaming.hcessentials.carepackage.CarePackages;
import com.addongaming.hcessentials.chatmanager.ChatManager;
import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.combat.global.deathmsgs.DMTypes;
import com.addongaming.hcessentials.commands.CommandManager;
import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.hcessentials.data.Position;
import com.addongaming.hcessentials.database.DatabaseManagement;
import com.addongaming.hcessentials.enchants.EnchantListener;
import com.addongaming.hcessentials.faq.FAQCore;
import com.addongaming.hcessentials.hooks.logging.BlockLoggingHook;
import com.addongaming.hcessentials.items.CustomItems;
import com.addongaming.hcessentials.limits.CommandLimiter;
import com.addongaming.hcessentials.limits.FallDamageRemover;
import com.addongaming.hcessentials.limits.HungerRemover;
import com.addongaming.hcessentials.limits.Limiter;
import com.addongaming.hcessentials.logging.DataLogging;
import com.addongaming.hcessentials.minigames.MiniGames;
import com.addongaming.hcessentials.network.InterServerConnection;
import com.addongaming.hcessentials.perks.ColouredNames;
import com.addongaming.hcessentials.perks.EXPKeep;
import com.addongaming.hcessentials.perks.Name;
import com.addongaming.hcessentials.perks.NearCommand;
import com.addongaming.hcessentials.perks.Nerfing;
import com.addongaming.hcessentials.perks.SilkSpawners;
import com.addongaming.hcessentials.perks.TreeAssistance;
import com.addongaming.hcessentials.perks.headtrack.HeadTracking;
import com.addongaming.hcessentials.perks.instadrop.InstantBreaking;
import com.addongaming.hcessentials.perks.protection.BaseProtection;
import com.addongaming.hcessentials.perks.safedrop.SafeBreaking;
import com.addongaming.hcessentials.raiding.Raiding;
import com.addongaming.hcessentials.rankup.RankUpdater;
import com.addongaming.hcessentials.redeem.Redeem;
import com.addongaming.hcessentials.serverlogger.Logging;
import com.addongaming.hcessentials.special.SpecialDrops;
import com.addongaming.hcessentials.special.mobspawn.SpecialSpawns;
import com.addongaming.hcessentials.stats.player.PlayerStatsHandler;
import com.addongaming.hcessentials.teams.TeamCore;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.hcessentials.uuid.UUIDSystem;
import com.addongaming.hcessentials.vote.Voting;
import com.addongaming.hcessentials.world.gen.WorldRegen;
import com.addongaming.hcessentials.world.teleport.CommandPortals;
import com.addongaming.hcessentials.world.teleport.WorldPortals;
import com.addongaming.hcessentials.worldman.WorldManagement;
import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class HcEssentials extends JavaPlugin implements CommandExecutor {
	private static DataLogging dataLogger;
	public static Economy economy;
	public static Essentials essentials;
	private static HcEssentials hcessentials;

	public static Permission permission = null;

	public static WorldGuardPlugin worldGuard;

	public static DataLogging getDataLogger() {
		return dataLogger;
	}

	public static HcEssentials getHcEssentials() {
		return hcessentials;
	}

	List<SubPlugin> subPluginList = new ArrayList<SubPlugin>();

	public void checkConfig() {
		FileConfiguration fc = this.getConfig();
		// Antilag
		fc.addDefault("antilag.rainstopper.enabled", Boolean.TRUE);
		fc.addDefault("antilag.alwaysday.enabled", Boolean.FALSE);
		fc.addDefault("antilag.alwaysday.worlds", new ArrayList<String>() {
			{
				this.add("world_name_here");
			}
		});// list =
			// jp.getConfig().getStringList("antilag.entitydespawner.blacklist");
		fc.addDefault("antilag.entitydespawner.blacklist",
				new ArrayList<String>() {

					private static final long serialVersionUID = 1L;

					{
						add("CraftPiggy");
					}
				});
		// Worldmanagement
		fc.addDefault("worldmanagement.enabled", Boolean.TRUE);
		// Combat logging
		fc.addDefault("combatlog.enabled", Boolean.FALSE);
		fc.addDefault("combatlog.enderpearlteleport", Boolean.FALSE);
		fc.addDefault("combatlog.timeout", 30);
		fc.addDefault("combatlog.anyteleport", Boolean.FALSE);
		// Setup for Silk Spawners
		fc.addDefault("spawntouch.enabled", Boolean.FALSE);
		fc.addDefault("spawntouch.perm", "HcRaid.GHAST");
		// Setup for raiding homes
		fc.addDefault("joinmessage.server", "HcRaid");
		fc.addDefault("joinmessage.description", "A hard core raiding server.");
		// TODO Start of join message for first joining
		fc.addDefault("joinmessage.firstjoin", new ArrayList<String>() {
			{
				add(ChatColor.GREEN + "Welcome! I see you're new here.");
				add(ChatColor.GREEN + "...");
			}
		});
		// Teams
		fc.addDefault("teams.enabled", Boolean.FALSE);
		fc.addDefault("teams.homes.enabled", Boolean.FALSE);
		fc.addDefault("teams.homes.minreq", 5);
		fc.addDefault("teams.maxpeople", 20);
		// DeathMessages
		fc.addDefault("deathmessages.type", DMTypes.ON.name());
		// Raid homes
		fc.addDefault("raiding.enabled", Boolean.FALSE);
		if (!fc.contains("raiding.ranks")) {
			List<String> al = new ArrayList<String>();
			al.add("Hero");
			al.add("Ender");
			al.add("Ghast");
			al.add("Blaze");
			al.add("Creeper");
			al.add("Grunt");
			fc.addDefault("raiding.ranks", al);
			for (Object s : fc.getList("raiding.ranks")) {
				fc.addDefault("raiding.ranks." + s + ".homeduration", 10);
				fc.addDefault("raiding.ranks." + s + ".cooldown", 10);
			}
		}
		// blocks
		fc.addDefault("blocks.recycler", Boolean.valueOf(false));
		fc.addDefault("blocks.bookenchant.enabled", Boolean.valueOf(false));
		fc.addDefault("blocks.bookenchant.permission", "Hcraid.creeper");
		fc.options().copyDefaults(true);
		this.saveConfig();
	}

	public SubPlugin getSubPlugin(Class<? extends SubPlugin> class1) {
		for (SubPlugin sp : subPluginList)
			if (sp.getClass() == class1) {
				return sp;
			}
		return null;
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}

	private final String hcEssentials = ChatColor.GOLD + "[" + ChatColor.AQUA
			+ "HcEssentials" + ChatColor.GOLD + "] " + ChatColor.GREEN;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("hcessentials")) {
			sender.sendMessage(hcEssentials
					+ "That feature isn't currently enabled.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(hcEssentials + "Please give an option.");
			return true;
		}
		if (args[0].equalsIgnoreCase("list")) {
			StringBuilder sb = new StringBuilder();
			for (SubPlugin sp : subPluginList)
				sb.append(sp.getClass().getSimpleName() + ", ");
			sender.sendMessage(hcEssentials + "Currently loaded modules: "
					+ sb.toString());
		} else if (args[0].equalsIgnoreCase("playerdebug")) {
			if (!sender.isOp())
				return true;
			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("all")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						sender.sendMessage(hcEssentials + p.getName()
								+ ChatColor.BLUE + " Ping: " + ChatColor.AQUA
								+ +Utils.getPlayerPing(p) + ChatColor.BLUE
								+ " Network: " + ChatColor.AQUA
								+ Utils.getPlayerVersion(p));
					}
				} else {
					Player p = Bukkit.getPlayer(args[1]);
					if (p == null || !p.isOnline())
						sender.sendMessage(hcEssentials + "Play isn't online.");
					else
						sender.sendMessage(hcEssentials + p.getName()
								+ ChatColor.BLUE + " Ping: " + ChatColor.AQUA
								+ +Utils.getPlayerPing(p) + ChatColor.BLUE
								+ " Network: " + ChatColor.AQUA
								+ Utils.getPlayerVersion(p));
				}
				return true;
			}
		}
		return true;
	}

	@Override
	public void onDisable() {
		for (Iterator<SubPlugin> it = subPluginList.iterator(); it.hasNext();) {
			it.next().onDisable();
			it.remove();
		}
	}

	private void setupSerialisation() {
		ConfigurationSerialization.registerClass(LocationZone.class);
		ConfigurationSerialization.registerClass(Position.class);
	}

	@Override
	public void onEnable() {
		setupSerialisation();
		HcEssentials.hcessentials = this;
		dataLogger = new DataLogging(this);
		dataLogger.addLogger("Main");
		setupEconomy();
		setupPermissions();
		essentials = (Essentials) getServer().getPluginManager().getPlugin(
				"Essentials");
		worldGuard = getWorldGuard();

		checkConfig();
		setupSubPlugins();
		for (Iterator<SubPlugin> iter = subPluginList.iterator(); iter
				.hasNext();) {
			SubPlugin sp = iter.next();
			try {
				if (!sp.onEnable()) {
					dataLogger.getLogger("Main").log(
							"Plugin " + sp.getClass().getSimpleName()
									+ " not loaded.");
					iter.remove();
				} else {
					dataLogger.getLogger("Main").log(
							"Plugin " + sp.getClass().getSimpleName()
									+ " loaded sucessfully.");
				}
			} catch (Exception e) {
				dataLogger.getLogger("Main").log(
						"Plugin " + sp.getClass().getSimpleName()
								+ " had an error. Please check logs.");
				dataLogger.getLogger("Main").log(e.getMessage());
				e.printStackTrace();
				iter.remove();
			}
		}
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	private void setupSubPlugins() {
		// Highest priority loading
		DatabaseManagement dm = new DatabaseManagement(this);
		subPluginList.add(dm);
		UUIDSystem uuidSystem = new UUIDSystem(this);
		subPluginList.add(uuidSystem);
		// End of highest priority loading
		InterServerConnection isc = new InterServerConnection(this);
		subPluginList.add(isc);
		WorldRegen wr = new WorldRegen(this, new File(this.getDataFolder()
				+ "\\WorldGen\\Gennd.sav"));
		subPluginList.add(wr);
		Combat combat = new Combat(this);
		subPluginList.add(combat);
		Raiding raiding = new Raiding(this);
		subPluginList.add(raiding);
		new JoinMessage(getConfig().getString("joinmessage.server"),
				getConfig().getString("joinmessage.description"), getConfig()
						.getStringList("joinmessage.firstjoin"), this);
		SilkSpawners silkSpawn = new SilkSpawners(this);
		subPluginList.add(silkSpawn);
		TeamCore teamCore = new TeamCore(this);
		subPluginList.add(teamCore);
		Nerfing nerfing = new Nerfing(this);
		subPluginList.add(nerfing);
		ChatManager chatManager = new ChatManager(this);
		subPluginList.add(chatManager);
		AntiLag antiLag = new AntiLag(this);
		subPluginList.add(antiLag);
		BlockMod blockMod = new BlockMod(this);
		subPluginList.add(blockMod);
		Limiter limiter = new Limiter(this);
		subPluginList.add(limiter);
		EnchantListener enchantListener = new EnchantListener(this);
		subPluginList.add(enchantListener);
		FAQCore faq = new FAQCore(this);
		subPluginList.add(faq);
		WorldManagement worldManagement = new WorldManagement(this);
		subPluginList.add(worldManagement);
		Name itemRenamer = new Name(this);
		subPluginList.add(itemRenamer);
		Bounty bounty = new Bounty(this);
		subPluginList.add(bounty);
		ColouredNames colouredNames = new ColouredNames(this);
		subPluginList.add(colouredNames);
		EXPKeep expKeep = new EXPKeep(this);
		subPluginList.add(expKeep);
		CarePackages carePackages = new CarePackages(this);
		subPluginList.add(carePackages);
		BaseProtection baseProtection = new BaseProtection(this);
		subPluginList.add(baseProtection);
		InstantBreaking instantBreaking = new InstantBreaking(this);
		subPluginList.add(instantBreaking);
		TreeAssistance treeAssistance = new TreeAssistance(this);
		subPluginList.add(treeAssistance);
		FallDamageRemover spawnFallDamage = new FallDamageRemover(this);
		subPluginList.add(spawnFallDamage);
		CommandLimiter commandLimiter = new CommandLimiter(this);
		subPluginList.add(commandLimiter);
		SafeBreaking safeBreaking = new SafeBreaking(this);
		subPluginList.add(safeBreaking);
		Voting vote = new Voting(this);
		subPluginList.add(vote);
		PlayerStatsHandler playerStatsHandler = new PlayerStatsHandler(this);
		subPluginList.add(playerStatsHandler);
		RankUpdater rankUpdater = new RankUpdater(this);
		subPluginList.add(rankUpdater);
		NearCommand near = new NearCommand(this);
		subPluginList.add(near);
		MiniGames miniGames = new MiniGames(this);
		subPluginList.add(miniGames);
		SpecialSpawns specialSpawns = new SpecialSpawns(this);
		subPluginList.add(specialSpawns);
		SpecialDrops specialDrops = new SpecialDrops(this);
		subPluginList.add(specialDrops);
		Redeem redeem = new Redeem(this);
		subPluginList.add(redeem);
		HeadTracking headTracking = new HeadTracking(this);
		subPluginList.add(headTracking);
		CommandManager cmdManager = new CommandManager(this);
		subPluginList.add(cmdManager);
		BlockLoggingHook loggingHook = new BlockLoggingHook(this);
		subPluginList.add(loggingHook);
		AutoAnnouncer autoAnnouncer = new AutoAnnouncer(this);
		subPluginList.add(autoAnnouncer);
		Logging chatLogger = new Logging(this);
		subPluginList.add(chatLogger);
		WorldPortals worldPortals = new WorldPortals(this);
		subPluginList.add(worldPortals);
		CommandPortals cmdPortals = new CommandPortals(this);
		subPluginList.add(cmdPortals);
		CustomItems customItems = new CustomItems(this);
		subPluginList.add(customItems);
		HungerRemover hungerRemover = new HungerRemover(this);
		subPluginList.add(hungerRemover);
		AFKKicker afkKicker = new AFKKicker(this);
		subPluginList.add(afkKicker);
	}
}
