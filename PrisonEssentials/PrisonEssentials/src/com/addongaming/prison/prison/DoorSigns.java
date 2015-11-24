package com.addongaming.prison.prison;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.player.PrisonerManager;

public class DoorSigns implements Listener {
	private final String doorTitle = ChatColor.GOLD + "[" + ChatColor.BLUE
			+ "HcToll" + ChatColor.GOLD + "]";
	final Map<Location, Long> doorMap = new HashMap<Location, Long>();

	public DoorSigns(JavaPlugin jp) {
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						List<Location> toRemove = new ArrayList<Location>();
						for (Location loc : doorMap.keySet())
							if (doorMap.get(loc) < new Date().getTime()) {
								toRemove.add(loc);
								BlockState state = loc.getBlock().getState();
								Openable open = (Openable) state.getData();
								open.setOpen(false);
								state.setData((MaterialData) open);
								state.update();
							}
						if (!toRemove.isEmpty())
							for (Location loc : toRemove)
								doorMap.remove(loc);
					}
				}, 20L, 10L);
	}

	@EventHandler
	public void signPlaceEvent(SignChangeEvent event) {
		if (event.getPlayer().isOp()) {
			if (event.getLine(0).equalsIgnoreCase("HcToll")
					&& isNumber(event.getLine(2))) {
				event.setLine(0, doorTitle);
			}
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent event) {
		if (event.hasBlock() && event.getClickedBlock().getState() != null
				&& event.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (sign.getLine(0).equalsIgnoreCase(doorTitle)) {
				int cost = Integer.parseInt(sign.getLine(2));
				if (PrisonerManager.getInstance()
						.getPrisonerInfo(event.getPlayer().getName())
						.hasBalance(cost)) {
					PrisonerManager.getInstance()
							.getPrisonerInfo(event.getPlayer().getName())
							.removeBalance(cost);
					org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) event
							.getClickedBlock().getState().getData();
					openDoor(event.getClickedBlock().getRelative(
							materialSign.getAttachedFace()));
					event.getPlayer()
							.sendMessage(
									doorTitle
											+ ChatColor.GREEN
											+ " "
											+ cost
											+ " rupees have been withdrawn from your account.");
				} else {
					event.getPlayer().sendMessage(
							doorTitle + ChatColor.RED
									+ " Sorry you do not have enough rupees.");
				}
			}
		}
	}

	private void openDoor(Block relative) {
		for (BlockFace bf : BlockFace.values())
			if (relative.getRelative(bf).getType() == Material.WOODEN_DOOR
					|| relative.getRelative(bf).getType() == Material.IRON_DOOR_BLOCK
					|| relative.getRelative(bf).getType() == Material.IRON_DOOR) {
				BlockState state = relative.getRelative(bf)
						.getRelative(BlockFace.DOWN).getState();
				Openable open = (Openable) state.getData();
				open.setOpen(true);
				state.setData((MaterialData) open);
				state.update();
				doorMap.put(relative.getRelative(bf)
						.getRelative(BlockFace.DOWN).getLocation(),
						new Date().getTime() + 3000);
				return;
			}
	}

	private boolean isNumber(String line) {
		try {
			Integer.parseInt(line);
			return true;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

}
