package com.addongaming.hcessentials.perks.protection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class BaseProtection implements SubPlugin, Listener {
	private JavaPlugin jp;
	List<ProtectedBlock> protectedBlocks = new ArrayList<ProtectedBlock>();

	public BaseProtection(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("protectedblocks.enabled", Boolean.FALSE);
		fc.addDefault("protectedblocks.blocks", new ArrayList<String>() {
			{
				this.add("bricks|HcRaid.Ender");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
		jp.reloadConfig();
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void pistonExtend(BlockPistonExtendEvent event) {
		for (Block b : event.getBlocks())
			for (ProtectedBlock pb : protectedBlocks)
				if (pb.isProtected(b)) {
					event.setCancelled(true);
					return;
				}
	}

	@EventHandler
	public void pistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			for (ProtectedBlock pb : protectedBlocks)
				if (pb.isProtected(event.getRetractLocation().getBlock())) {
					event.setCancelled(true);
					return;
				}
		}
	}

	@EventHandler
	public void explosion(EntityExplodeEvent event) {
		for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
			Block b = it.next();
			for (ProtectedBlock pb : protectedBlocks)
				if (pb.isProtected(b)) {
					it.remove();
					break;
				}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		for (ProtectedBlock pb : protectedBlocks)
			if (!pb.canUse(event.getPlayer(), event.getBlock())) {
				event.setCancelled(true);
				event.getPlayer()
						.sendMessage(
								ChatColor.AQUA
										+ "Sorry, you do not have the rank to break this block.");
				return;
			}
	}

	@EventHandler
	public void explosion(ExplosionPrimeEvent event) {
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		for (ProtectedBlock pb : protectedBlocks)
			if (!pb.canUse(event.getPlayer(), event.getBlock())) {
				event.setCancelled(true);
				event.getPlayer()
						.sendMessage(
								ChatColor.AQUA
										+ "Sorry, you do not have the rank to place this block.");
				return;
			}
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		if (fc.getBoolean("protectedblocks.enabled")) {
			List<String> craftingList = (List<String>) fc
					.getList("protectedblocks.blocks");
			for (String s : craftingList) {
				String[] thingy = s.split("[|]");
				protectedBlocks.add(new ProtectedBlock(thingy[0], thingy[1]));
			}
			if (protectedBlocks.size() >= 1) {
				jp.getServer().getPluginManager().registerEvents(this, jp);
				return true;
			}
		}
		return false;
	}
}
