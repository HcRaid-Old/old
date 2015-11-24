package com.addongaming.prison.commands.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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

public class WarpCmd implements CommandExecutor {
	private JavaPlugin jp;

	// TODO setup Spawns specific for islands using WorldGuard or distance to
	// islands.
	public WarpCmd(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			final Player p = (Player) arg0;
			Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
					p.getName());
			if (!prisoner.hasPermission(CommandData.WARP.getPermission())) {
				p.sendMessage(ChatColor.RED
						+ "You have not bought the permission to execute this command.");
				return true;
			}
			if (arg3.length == 0) {
				Prison prison = PrisonManager.getInstance().getPrison(
						((Entity) arg0).getLocation());
				Warp[] warps = WarpSystem.getInstance().getWarpByPrison(prison);
				StringBuilder sb = new StringBuilder();
				for (Warp warp : warps) {
					sb.append(warp.getName() + ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				p.sendMessage(ChatColor.DARK_BLUE
						+ "-----------------------------------------------");
				p.sendMessage(ChatColor.AQUA + "Prison Warp System");
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Prison: "
						+ ChatColor.RESET + "" + ChatColor.GOLD
						+ prison.getName());
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD
						+ "You may warp to: " + ChatColor.RESET + ""
						+ ChatColor.GOLD + sb.toString() + ".");
				p.sendMessage(ChatColor.DARK_BLUE
						+ "-----------------------------------------------");
				return true;
			}
			String area = arg3[0];
			Prison warpPrison = PrisonManager.getInstance().getPrison(
					((Entity) arg0));
			if (warpPrison == null) {
				p.sendMessage(ChatColor.RED
						+ "You are not in an warp allowed area.");
				return true;
			}
			Warp warpArea = WarpSystem.getInstance().getWarpByNameAndPrison(
					area, warpPrison.getName());
			if (warpArea == null) {
				p.sendMessage(ChatColor.RED + area + " is not a valid warp.");
				return true;
			}
			if (!Combat.getCombatInstance().isInCombat(p.getName())) {
				final Location old = p.getLocation();
				final Location loc = warpArea.getLocation();
				p.sendMessage(ChatColor.BLUE
						+ "-----------------------------------------");
				p.sendMessage(ChatColor.GRAY + "Teleporting to "
						+ ChatColor.AQUA + warpPrison.getName() + "'s "
						+ warpArea.getToText() + ChatColor.GRAY + " in "
						+ ChatColor.AQUA + "5 " + ChatColor.GRAY + "seconds.");
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
								p.teleport(loc);
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
