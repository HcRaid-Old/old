package com.addongaming.prison.core;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.SkillManager;
import com.addongaming.prison.commands.CommandManager;
import com.addongaming.prison.data.utilities.AreaData;
import com.addongaming.prison.farm.FarmManager;
import com.addongaming.prison.info.PrisonHelp;
import com.addongaming.prison.jail.JailManager;
import com.addongaming.prison.limit.LimitManager;
import com.addongaming.prison.mines.MineManager;
import com.addongaming.prison.npc.NPCManager;
import com.addongaming.prison.npc.guard.GuardTrait;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.prison.DoorSigns;
import com.addongaming.prison.prison.PrisonManager;
import com.addongaming.prison.prison.ShipTravel;
import com.addongaming.prison.prison.warps.WarpSystem;
import com.addongaming.prison.scenes.SceneManager;
import com.addongaming.prison.shop.ShopManager;
import com.addongaming.prison.stats.StatManager;
import com.addongaming.prison.stats.Stats;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	public static Economy economy;
	public static WorldGuardPlugin wg;
	MineManager mm;
	NPCManager npcManager;
	PrisonerManager pm;

	private void npc(Player sender, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage("Please use /prison npc <add|remove> path ");
			return;
		}
		if (args[0].equalsIgnoreCase("reload")) {
			npcManager.reload();
			sender.sendMessage("Reloaded NPC's");
		}
		if (args.length <= 2) {
			sender.sendMessage("Please use /prison npc <add|remove> path ");
			return;
		}
		if (args[1].equalsIgnoreCase("add")) {
			getConfig().set(args[2],
					Utils.locationToSaveString(sender.getLocation()));
			sender.sendMessage("Added: " + args[2]);
		} else if (args[1].equalsIgnoreCase("remove")
				|| args[1].equalsIgnoreCase("del")) {
			getConfig().set(args[2], null);
			sender.sendMessage("Deleted: " + args[2]);
		}
		saveConfig();

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0)
				switch (args[0]) {
				case "save":
					for (Player player : Bukkit.getOnlinePlayers())
						PrisonerManager.getInstance().savePlayer(
								player.getName());
					p.sendMessage("Saved all files");
					return true;
				case "stats":
					for (Stats s : Stats.values()) {
						int xp = pm.getPrisonerInfo(p.getName()).getStat(s);
						int level = Stats.getLevel(xp);
						p.sendMessage("Stat: " + s.name().toLowerCase()
								+ " XP: " + xp + " level: " + level);
					}
					return true;
				case "perm":
					perm(sender, args);
					return true;
				case "npc":
					npc(p, args);
					return true;
				case "mine":
					if (args.length > 1)
						mm.refreshMines();
					else
						mm.debug(p);
					return true;
				case "warp":
					warp(sender, args);
					return true;
				case "guarddebug":
					guarddebug(sender);
					return true;
				}

		}
		return true;
	}

	private void guarddebug(CommandSender sender) {
		try {
			File file = new File(getDataFolder() + File.separator
					+ "guarddump.txt");
			if (!file.exists())
				file.createNewFile();
			PrintWriter pw = new PrintWriter(file);
			for (Iterator<NPC> iter = CitizensAPI.getNPCRegistry().iterator(); iter
					.hasNext();) {
				NPC npc = iter.next();
				if (npc.hasTrait(GuardTrait.class)) {
					GuardTrait gt = npc.getTrait(GuardTrait.class);
					gt.debug(pw);
				}
			}
			pw.close();
			sender.sendMessage("Dumped all guard info");
		} catch (Exception e) {
			sender.sendMessage("Error during guard debug dump.");
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		npcManager.stop();
		for (Player player : Bukkit.getOnlinePlayers())
			PrisonerManager.getInstance().savePlayer(player.getName());
	}

	@Override
	public void onEnable() {
		final JavaPlugin myPlugin = this;
		getServer().getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					@Override
					public void run() {
						System.out.println("HcPrison loading up...");
						pm = new PrisonerManager(myPlugin);
						new StatManager(myPlugin);
						new LimitManager(myPlugin);
						new SceneManager(myPlugin);
						npcManager = new NPCManager(myPlugin);
						mm = new MineManager(myPlugin);
						new FarmManager(myPlugin);
						new PrisonHelp(myPlugin);
						new ShopManager(myPlugin);
						new CommandManager(myPlugin);
						new SkillManager(myPlugin);
						new JailManager(myPlugin);
						new PrisonManager(myPlugin);
						new WarpSystem(myPlugin);
						new ShipTravel(myPlugin);
						myPlugin.getServer()
								.getPluginManager()
								.registerEvents(new DoorSigns(myPlugin),
										myPlugin);
						AreaData.setupLocations(myPlugin);
						getServer().getPluginManager().registerEvents(
								new ChatHandler(), myPlugin);
						setupEconomy();
						setupWorldGuard();
					}
				}, Config.Ticks.POSTWORLD);

	}

	private void perm(CommandSender sender, String[] args) {
		if (args.length == 1) {
			sender.sendMessage("Please use /prison perm <add|remove> <Username> <Permission>");
			sender.sendMessage("or /prison perm list <username>");
			return;
		}
		switch (args[1].toLowerCase()) {
		case "add":
			if (args.length == 3) {
				sender.sendMessage("Please use /prison perm <add|remove> <Username> <Permission>");
				return;
			}
			PrisonerManager.getInstance().getPrisonerInfo(args[2])
					.addPermission(args[3]);
			return;
		case "remove":
			if (args.length == 3) {
				sender.sendMessage("Please use /prison perm <add|remove> <Username> <Permission>");
				return;
			}
			PrisonerManager.getInstance().getPrisonerInfo(args[2])
					.removePermission(args[3]);
			return;
		case "list":
			if (args.length == 2)
				sender.sendMessage("or /prison perm list <username>");
			else
				sender.sendMessage(StringUtils.join(PrisonerManager
						.getInstance().getPrisonerInfo(args[2]).getAllPerms(),
						", "));
			return;
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

	private void setupWorldGuard() {
		for (Plugin pl : getServer().getPluginManager().getPlugins())
			if (pl instanceof WorldGuardPlugin) {
				Main.wg = (WorldGuardPlugin) pl;
				return;
			}
	}

	// 0 = /prison warp
	// 1 = /prison warp <add|del>
	private void warp(CommandSender sender, String[] args) {
		if (args.length < 4) {
			sender.sendMessage("Please use /prison warp <add|del> [name] [prison]");
			return;
		}
		Player p = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "add":
			WarpSystem.getInstance().addWarp(args[2], p.getLocation(), args[3]);
			break;
		case "del":
			WarpSystem.getInstance().delWarp(args[2], args[3]);
			break;
		}
	}
}
