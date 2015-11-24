package com.addongaming.overkill;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;

public class Starter implements Listener, NCPHook, CommandExecutor {
	private AtomicHashMap<String, Long> toggled = new AtomicHashMap<String, Long>();
	private File dataFolder;

	public Starter(File dataFolder) {
		NCPHookManager.addHook(new CheckType[] { CheckType.MOVING,
				CheckType.MOVING_NOFALL }, this);
		this.dataFolder = new File(dataFolder, "StarterSaves");
		this.dataFolder.mkdirs();
	}

	@Override
	public String getHookName() {
		return "Starter hook";
	}

	@Override
	public String getHookVersion() {
		return "0.1";
	}

	@EventHandler
	public void playerEvent(PlayerEvent event) {
		if (event instanceof PlayerMoveEvent || !(event instanceof Cancellable))
			return;
		if (toggled.containsKey(event.getPlayer().getName())) {
			event.getPlayer().sendMessage(
					ChatColor.RED
							+ "Please use /starter to exit out of protection.");
			((Cancellable) event).setCancelled(true);
		}
	}

	@Override
	public boolean onCheckFailure(CheckType arg0, Player arg1,
			IViolationInfo arg2) {
		if (arg0 == CheckType.MOVING || arg0 == CheckType.MOVING_NOFALL) {
			return toggled.containsKey(arg1.getName());
		}
		return false;
	}

	public boolean canStart(Player p) {
		return false;
	}

	public void start() {

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {

		}
		return false;
	}
}
