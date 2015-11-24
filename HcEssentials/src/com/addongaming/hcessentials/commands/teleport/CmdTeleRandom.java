package com.addongaming.hcessentials.commands.teleport;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.combat.Combat;
import com.addongaming.hcessentials.events.PlayerTeleportCommandEvent;
import com.addongaming.hcessentials.utils.TimeUtils;

public class CmdTeleRandom implements CommandExecutor, Teleport {
	HashMap<String, Date> teleportDate = new HashMap<String, Date>();
	private int distance;
	private int offset;
	private int cooldown;
	private JavaPlugin jp;

	public CmdTeleRandom(JavaPlugin jp, int distance, int offset, int cooldown) {
		this.distance = distance;
		this.offset = offset;
		this.cooldown = cooldown;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		this.jp = jp;
	}

	private boolean canTeleport(Player p) {
		if (!teleportDate.containsKey(p.getName())
				|| teleportDate.get(p.getName()).before(new Date()))
			return true;
		return false;
	}

	private final String succeed = ChatColor.GOLD + "[" + ChatColor.GOLD
			+ "HcTele" + ChatColor.GOLD + "] " + ChatColor.AQUA;
	private final String fail = ChatColor.DARK_RED + "[" + ChatColor.RED
			+ "HcTele" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage(fail + "You cannot teleport.");
			return true;
		}
		Player player = (Player) arg0;
		if (Combat.getCombatInstance() != null
				&& Combat.getCombatInstance().isInCombat(player.getName())) {
			player.sendMessage(fail + "You cannot do this in combat.");
		} else if (canTeleport(player)) {
			player.sendMessage(succeed + "Teleporting in 5 seconds.");
			int x = new Random().nextInt(distance) + offset;
			int z = new Random().nextInt(distance) + offset;
			Location to = Bukkit.getWorld("world").getHighestBlockAt(x, z)
					.getRelative(BlockFace.UP).getLocation();
			jp.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							jp,
							new TeleportRunnable(to, player, succeed, fail,
									(Class<Teleport>) getClass()), 5 * 20L);
			teleportDate.put(player.getName(), new Date(
					new Date().getTime() + 10000));
		} else {
			player.sendMessage(fail
					+ "You cannot use this command again for "
					+ new TimeUtils(teleportDate.get(player.getName())
							.getTime() - new Date().getTime()).toString() + ".");
		}
		return true;
	}

	@Override
	@EventHandler
	public void onTeleport(PlayerTeleportCommandEvent event) {
		System.out.println("Calling: " + event.getCalling().getName());
		System.out.println("CmdTeleRandom: " + getClass().getName());
		if (event.getCalling().getName().equalsIgnoreCase(getClass().getName())) {
			teleportDate.put(event.getPlayer().getName(),
					new Date(new Date().getTime() + (cooldown * 60000)));
		}
	}
}
