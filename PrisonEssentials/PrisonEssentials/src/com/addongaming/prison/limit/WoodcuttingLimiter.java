package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.skills.HatchetData;
import com.addongaming.prison.data.skills.TreeData;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public class WoodcuttingLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Woodcutting"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void pickaxeUsed(BlockBreakEvent bbe) {
		if (bbe.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (TreeData.isTreeRelated(bbe.getBlock().getType())
				&& bbe.getPlayer().getItemInHand() != null) {
			DataReturn dr = HatchetData.playerHasPermission(bbe.getPlayer()
					.getName(), bbe.getPlayer().getItemInHand().getType());
			switch (dr) {
			case NOLEVEL:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need level "
										+ HatchetData
												.getHatchetDataForMaterial(
														bbe.getPlayer()
																.getItemInHand()
																.getType())
												.getLevelReq()
										+ " woodcutting to use this hatchet, your level is currently "
										+ Stats.getLevel(PrisonerManager
												.getInstance()
												.getPrisonerInfo(
														bbe.getPlayer()
																.getName())
												.getStat(Stats.WOODCUTTING))
										+ ".");
				bbe.setCancelled(true);
				return;
			case NOPERM:
				bbe.getPlayer().sendMessage(
						err + "You need to learn how to use this"
								+ " axe from the Woodcutting Instructor.");
				bbe.setCancelled(true);
				return;
			case SUCCESS:
				return;
			default:
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void treeCut(BlockBreakEvent bbe) {
		if (bbe.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (TreeData.isTreeRelated(bbe.getBlock().getType())) {
			DataReturn dr = TreeData.playerHasPermission(bbe.getPlayer()
					.getName(), bbe.getBlock().getType(), bbe.getBlock()
					.getData());
			switch (dr) {
			case NOLEVEL:
				bbe.getPlayer().sendMessage(
						err
								+ "You need level "
								+ TreeData.getTreeDataForMaterial(
										bbe.getBlock().getType(),
										bbe.getBlock().getData()).getLevelReq()
								+ " woodcutting, your level is currently "
								+ Stats.getLevel(PrisonerManager
										.getInstance()
										.getPrisonerInfo(
												bbe.getPlayer().getName())
										.getStat(Stats.WOODCUTTING)) + ".");
				bbe.setCancelled(true);
				return;
			case NOPERM:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need to learn the technique"
										+ " of this log from the Woodcutting Instructor.");
				bbe.setCancelled(true);
				return;
			case SUCCESS:
				return;
			default:
				return;
			}
		}
	}
}
