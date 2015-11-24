package com.addongaming.prison.limit;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.skills.MiningData;
import com.addongaming.prison.data.skills.PickaxeData;
import com.addongaming.prison.player.PrisonerManager;
import com.addongaming.prison.stats.Stats;

public class MiningLimiter implements Listener {
	String err = ChatColor.GRAY + "[" + ChatColor.DARK_RED + "Mining"
			+ ChatColor.GRAY + "] " + ChatColor.RED;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMined(BlockBreakEvent bbe) {
		if (bbe.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (MiningData.isMiningRelated(bbe.getBlock().getType())) {
			DataReturn dr = MiningData.playerHasPermission(bbe.getPlayer()
					.getName(), bbe.getBlock().getType());
			switch (dr) {
			case NOLEVEL:
				bbe.getPlayer().sendMessage(
						err
								+ "You need level "
								+ MiningData.getMiningDataForMaterial(
										bbe.getBlock().getType()).getLevelReq()
								+ " mining, your level is currently "
								+ Stats.getLevel(PrisonerManager
										.getInstance()
										.getPrisonerInfo(
												bbe.getPlayer().getName())
										.getStat(Stats.MINING)) + ".");
				bbe.setCancelled(true);
				return;
			case NOPERM:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need to learn the technique of this ore from the Mining Instructor.");
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
	public void pickaxeUsed(BlockBreakEvent bbe) {
		if (bbe.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;
		if (MiningData.isMiningRelated(bbe.getBlock().getType())
				&& bbe.getPlayer().getItemInHand() != null) {
			DataReturn dr = PickaxeData.playerHasPermission(bbe.getPlayer()
					.getName(), bbe.getPlayer().getItemInHand().getType());
			switch (dr) {
			case NOLEVEL:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need level "
										+ PickaxeData
												.getPickaxeDataForMaterial(
														bbe.getPlayer()
																.getItemInHand()
																.getType())
												.getLevelReq()
										+ " mining to use this pickaxe, your level is currently "
										+ Stats.getLevel(PrisonerManager
												.getInstance()
												.getPrisonerInfo(
														bbe.getPlayer()
																.getName())
												.getStat(Stats.MINING)) + ".");
				bbe.setCancelled(true);

				return;
			case NOPERM:
				bbe.getPlayer()
						.sendMessage(
								err
										+ "You need to learn how to use this pick from the Mining Instructor.");
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
