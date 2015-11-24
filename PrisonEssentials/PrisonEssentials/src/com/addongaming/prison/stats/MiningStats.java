package com.addongaming.prison.stats;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.skills.MiningData;
import com.addongaming.prison.player.PrisonerManager;

public class MiningStats implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockMined(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (MiningData.isMiningRelated(event.getBlock().getType())) {
			PrisonerManager
					.getInstance()
					.getPrisonerInfo(event.getPlayer().getName())
					.incrementStat(
							Stats.MINING,
							MiningData.getMiningDataForMaterial(
									event.getBlock().getType()).getValue());
			int xp = MiningData.getMiningDataForMaterial(
					event.getBlock().getType()).getValue() / 5;
			if (xp < 1)
				xp = 1;
			PrisonerManager.getInstance()
					.getPrisonerInfo(event.getPlayer().getName())
					.giveCharacterExp(xp);
		}
	}
}
