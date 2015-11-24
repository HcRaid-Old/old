package com.addongaming.prison.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.prison.data.utilities.CommandData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.prison.Prison;
import com.addongaming.prison.prison.PrisonManager;
import com.addongaming.prison.prison.warps.Warp;
import com.addongaming.prison.prison.warps.WarpSystem;

public class Spawn implements CommandExecutor {
	private JavaPlugin jp;

	// TODO setup Spawns specific for islands using WorldGuard or distance to
	// islands.
	public Spawn(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			final Player p = (Player) arg0;
			Prisoner prison = PrisonerManager.getInstance().getPrisonerInfo(
					p.getName());
			if (!prison.hasPermission(CommandData.SPAWN.getPermission())) {
				p.sendMessage(ChatColor.RED
						+ "You have not bought the permission to execute this command.");
				return true;
			}
			if (!Combat.getCombatInstance().isInCombat(p.getName())) {
				final Location old = p.getLocation();
				final Prison pris = PrisonManager.getInstance().getPrison(p);
				final Warp spawn = WarpSystem.getInstance()
						.getWarpByNameAndPrison("spawn", pris.getName());
				p.sendMessage(ChatColor.BLUE
						+ "-----------------------------------------");
				p.sendMessage(ChatColor.GRAY + "Teleporting to "
						+ ChatColor.AQUA + pris.getName() + "'s Spawn"
						+ ChatColor.GRAY + " in " + ChatColor.AQUA + "5 "
						+ ChatColor.GRAY + "seconds.");
				p.sendMessage(ChatColor.BLUE
						+ "-----------------------------------------");
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {
							@Override
							public void run() {
								if (!p.isOnline())
									return;
								if (p.getLocation().distance(old) > 2) {
									p.sendMessage(ChatColor.RED
											+ "Teleporting cancelled.");
									return;
								}
								p.sendMessage(ChatColor.GRAY + "Teleporting...");
								p.teleport(spawn.getLocation());
							}
						}, 20 * 5l);
			} else {
				p.sendMessage(ChatColor.RED
						+ "You cannot do this whilst in combat.");
			}
		}
		return true;
	}
}
