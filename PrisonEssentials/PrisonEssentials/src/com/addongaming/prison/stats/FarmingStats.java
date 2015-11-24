package com.addongaming.prison.stats;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.skills.FarmingData;
import com.addongaming.prison.player.PrisonerManager;

public class FarmingStats implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockMined(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		Block block = event.getBlock();
		for (int i = 0; i < 3; i++) {
			if (FarmingData.isFarmingRelated(block.getType())) {
				PrisonerManager
						.getInstance()
						.getPrisonerInfo(event.getPlayer().getName())
						.incrementStat(
								Stats.FARMING,
								FarmingData.getFarmingDataForMaterial(
										block.getType()).getValue());
				int xp = FarmingData.getFarmingDataForMaterial(block.getType())
						.getValue() / 6;
				if (xp < 1)
					xp = 1;
				PrisonerManager.getInstance()
						.getPrisonerInfo(event.getPlayer().getName())
						.giveCharacterExp(xp);
				block = block.getRelative(BlockFace.UP);
			} else
				return;
		}
	}
}
