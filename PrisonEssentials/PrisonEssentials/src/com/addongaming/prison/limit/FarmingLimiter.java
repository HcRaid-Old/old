package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.skills.FarmingData;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public class FarmingLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Farming"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void treeCut(BlockBreakEvent bbe) {
		if (bbe.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (FarmingData.isFarmingRelated(bbe.getBlock().getType())) {
			DataReturn dr = FarmingData.playerHasPermission(bbe.getPlayer()
					.getName(), bbe.getBlock().getType());
			switch (dr) {
			case NOLEVEL:
				bbe.getPlayer().sendMessage(
						err
								+ "You need level "
								+ FarmingData.getFarmingDataForMaterial(
										bbe.getBlock().getType()).getLevelReq()
								+ " farming, your level is currently "
								+ Stats.getLevel(PrisonerManager
										.getInstance()
										.getPrisonerInfo(
												bbe.getPlayer().getName())
										.getStat(Stats.FARMING)) + ".");
				bbe.setCancelled(true);
				return;
			case NOPERM:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need to learn the technique"
										+ " of this block from the Farming Instructor.");
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
