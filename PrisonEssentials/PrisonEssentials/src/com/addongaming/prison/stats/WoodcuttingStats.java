package com.addongaming.prison.stats;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.skills.TreeData;
import com.addongaming.prison.player.PrisonerManager;

public class WoodcuttingStats implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockMined(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (TreeData.isTreeRelated(event.getBlock().getType())) {
			PrisonerManager
					.getInstance()
					.getPrisonerInfo(event.getPlayer().getName())
					.incrementStat(
							Stats.WOODCUTTING,
							TreeData.getTreeDataForMaterial(
									event.getBlock().getType(),
									event.getBlock().getData()).getValue());
			int xp = TreeData.getTreeDataForMaterial(
					event.getBlock().getType(), event.getBlock().getData())
					.getValue() / 4;
			if (xp < 1)
				xp = 1;
			PrisonerManager.getInstance()
					.getPrisonerInfo(event.getPlayer().getName())
					.giveCharacterExp(xp);
		}
	}
}
