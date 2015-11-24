package com.addongaming.hcessentials.items;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.config.Config;
import com.addongaming.hcessentials.items.objects.ChestSaver;
import com.addongaming.hcessentials.items.objects.MiningLaser;
import com.addongaming.hcessentials.logging.DataLog;

public class CustomItems implements SubPlugin, Listener {
	private final List<InfCustomItem> itemList = new ArrayList<InfCustomItem>();
	private final JavaPlugin jp;
	private final DataLog dl;

	public CustomItems(JavaPlugin jp) {
		dl = HcEssentials.getDataLogger().addLogger("CustomItems");
		this.jp = jp;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("customitems.enabled", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
		if (fc.getBoolean("customitems.enabled")) {
			jp.getServer().getPluginManager().registerEvents(this, jp);
			loadStockItems(jp);
			return true;
		}
		return false;
	}

	private void loadStockItems(JavaPlugin jp) {
		registerItem(new ChestSaver(jp));
		registerItem(new MiningLaser(jp));
	}

	public void registerItem(InfCustomItem item) {
		jp.getServer().getPluginManager().registerEvents(item, jp);
		itemList.add(item);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerInteract(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.LEFT_CLICK_BLOCK
				&& (pie.getClickedBlock().getType() == Material.WALL_SIGN || pie
						.getClickedBlock().getType() == Material.SIGN_POST)) {
			Sign sign = (Sign) pie.getClickedBlock().getState();
			for (InfCustomItem h : itemList)
				if (sign.getLine(1).equalsIgnoreCase(h.getName())) {
					pie.getPlayer().sendMessage(
							ChatColor.GRAY + "[" + ChatColor.GOLD + "HcItems"
									+ ChatColor.GRAY + "] " + ChatColor.BLUE
									+ h.getDescription());
					return;
				}
		} else if (pie.getPlayer() != null
				&& pie.getPlayer().getItemInHand() != null
				&& pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (pie.getClickedBlock().getType() == Material.WALL_SIGN || pie
						.getClickedBlock().getType() == Material.SIGN_POST)) {
			Sign sign = (Sign) pie.getClickedBlock().getState();
			if (!sign.getLine(0).equalsIgnoreCase(Config.itemSign))
				return;
			if (pie.getPlayer().getInventory().firstEmpty() < 0) {
				pie.getPlayer().sendMessage(
						ChatColor.GRAY + "[" + ChatColor.GOLD + "HcItems"
								+ ChatColor.GRAY + "] " + ChatColor.BLUE
								+ "You need one free inventory slot.");
				return;
			}
			InfCustomItem enchant = null;
			for (InfCustomItem h : itemList)
				if (sign.getLine(1).equalsIgnoreCase(h.getName())) {
					enchant = h;
				}
			if (!HcEssentials.economy.has(pie.getPlayer().getName(),
					Integer.parseInt(sign.getLine(3).substring(1)))) {
				pie.getPlayer().sendMessage(
						ChatColor.GRAY
								+ "["
								+ ChatColor.GOLD
								+ "HcItems"
								+ ChatColor.GRAY
								+ "] "
								+ ChatColor.BLUE
								+ "You need "
								+ sign.getLine(3)
								+ " you have $"
								+ new DecimalFormat("####.##")
										.format(HcEssentials.economy
												.getBalance(pie.getPlayer()
														.getName())) + ".");
				return;
			}
			if (!pie.getPlayer().isOp()) {
				String str = enchant.canBuy(pie.getPlayer());
				if (str != null) {
					pie.getPlayer().sendMessage(str);
					return;
				}
			}
			pie.getPlayer().getInventory().addItem(enchant.getItem());
			pie.getPlayer().updateInventory();
			HcEssentials.economy.withdrawPlayer(pie.getPlayer().getName(),
					Integer.parseInt(sign.getLine(3).substring(1)));
			pie.getPlayer().sendMessage(
					ChatColor.GRAY + "[" + ChatColor.GOLD + "HcItems"
							+ ChatColor.GRAY + "] " + ChatColor.BLUE
							+ "Enjoy your new " + enchant.getName());
			pie.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void signPlace(final SignChangeEvent event) {
		if (event.isCancelled() || !event.getPlayer().isOp()
				|| !Config.enchantSignPurchasable)
			return;
		if (event.getLine(0).equalsIgnoreCase("HcItem")) {
			for (InfCustomItem h : itemList) {
				if (event.getLine(1).equalsIgnoreCase(h.getName())) {
					event.setLine(0, Config.itemSign);
					event.getPlayer().sendMessage("Setup sign");
					return;
				}
			}
			event.getPlayer()
					.sendMessage(
							"Please use Line 1 - HcItem Line 2 - Item name Line 3 - Empty Line 4- Price");
		}
	}
}
