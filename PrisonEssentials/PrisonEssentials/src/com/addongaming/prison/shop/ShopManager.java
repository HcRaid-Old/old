package com.addongaming.prison.shop;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.player.PrisonerManager;

public class ShopManager implements Listener {
	private final String buyString = ChatColor.YELLOW + "[HcBuy]";
	private JavaPlugin jp;
	private final String sellString = ChatColor.YELLOW + "[HcSell]";

	public ShopManager(JavaPlugin jp) {
		this.jp = jp;
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	private void addBalance(Player play, int bal) {
		PrisonerManager.getInstance().getPrisonerInfo(play.getName())
				.addBalance(bal);
	}

	private int getAmount(ItemStack is, PlayerInventory pi) {
		int amount = 0;
		for (ItemStack items : pi.getContents())
			if (items != null && items.getType() == is.getType()
					&& items.getDurability() == is.getDurability())
				amount += items.getAmount();
		return amount;
	}

	private int getInteger(String line) {
		try {
			return Integer.parseInt(line);
		} catch (Exception e) {
		}
		return 0;
	}

	private boolean hasBalance(Player play, int bal) {
		return PrisonerManager.getInstance().getPrisonerInfo(play.getName())
				.hasBalance(bal);
	}

	private void removeBalance(Player play, int bal) {
		PrisonerManager.getInstance().getPrisonerInfo(play.getName())
				.removeBalance(bal);
	}

	private void removeItems(ItemStack is, PlayerInventory pi,
			int amountToRemove) {
		pi.removeItem(new ItemStack[] { is });
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void signClickEvent(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (event.getClickedBlock().getState() != null
					&& event.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (sign.getLine(0).equalsIgnoreCase(buyString)) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(
										buyString
												+ " "
												+ ChatColor.RESET
												+ "Right click to buy 1 "
												+ ChatColor.stripColor(sign
														.getLine(3))
												+ " or sneak right click to buy a stack.");
					} else if (sign.getLine(0).equalsIgnoreCase(sellString)) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(
										buyString
												+ " "
												+ ChatColor.RESET
												+ "Right click to sell 1"
												+ " "
												+ ChatColor.stripColor(sign
														.getLine(3))
												+ " or sneak right click to sell a stack.");
					}
					return;
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					boolean fullStack = event.getPlayer().isSneaking();
					if (sign.getLine(0).equalsIgnoreCase(buyString)) {
						event.setCancelled(true);
						int realAmount = (fullStack ? new ItemStack(
								getInteger(sign.getLine(1))).getMaxStackSize()
								: 1);
						ItemStack is;
						if (sign.getLine(1).contains(":")) {
							is = new ItemStack(getInteger(sign.getLine(1)
									.split("(:)")[0]), realAmount,
									(short) getInteger(sign.getLine(1).split(
											"(:)")[1]));
						} else {
							is = new ItemStack(getInteger(sign.getLine(1)),
									realAmount);
						}
						int cost = realAmount * getInteger(sign.getLine(2));
						if (hasBalance(event.getPlayer(), cost)) {
							event.getPlayer().getInventory().addItem(is);
							removeBalance(event.getPlayer(), cost);
							event.getPlayer()
									.sendMessage(
											buyString + ChatColor.RESET
													+ " Bought " + realAmount
													+ " for " + cost + ".");
							updateInventory(event.getPlayer());
							return;
						} else {
							event.getPlayer()
									.sendMessage(
											buyString
													+ ChatColor.RESET
													+ " Sorry, you don't have enough money, "
													+ realAmount
													+ " "
													+ ChatColor.stripColor(sign
															.getLine(3))
													+ " costs " + cost + ".");
							return;
						}
					} else if (sign.getLine(0).equalsIgnoreCase(sellString)) {
						event.setCancelled(true);
						int realAmount = (fullStack ? 64 : 1);
						ItemStack is;
						if (sign.getLine(1).contains(":")) {
							is = new ItemStack(getInteger(sign.getLine(1)
									.split("(:)")[0]), realAmount,
									(short) getInteger(sign.getLine(1).split(
											"(:)")[1]));
						} else {
							is = new ItemStack(getInteger(sign.getLine(1)),
									realAmount);
						}
						int total = getAmount(is, event.getPlayer()
								.getInventory());
						if (fullStack && realAmount > total) {
							realAmount = total;
							is.setAmount(realAmount);
						}
						int cost = realAmount * getInteger(sign.getLine(2));
						if (total > 0) {
							removeItems(is, event.getPlayer().getInventory(),
									is.getAmount());
							addBalance(event.getPlayer(), cost);
							event.getPlayer()
									.sendMessage(
											sellString + ChatColor.RESET
													+ " Sold " + realAmount
													+ " for " + cost + ".");
							updateInventory(event.getPlayer());
							return;
						} else {
							event.getPlayer().sendMessage(
									sellString
											+ ChatColor.RESET
											+ " Sorry, you don't have enough, "
											+ ChatColor.stripColor(sign
													.getLine(3)) + ".");
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void signPlace(final SignChangeEvent event) {
		if (event.isCancelled() || !event.getPlayer().isOp())
			return;
		if (event.getLine(0).equalsIgnoreCase("[HcBuy]")) {
			if (event.getLine(1).matches("[0-9]{1,}(:[0-9])?")
					&& getInteger(event.getLine(2)) > 0) {
				event.setLine(0, buyString);
				return;
			} else {
				event.getPlayer()
						.sendMessage(
								"Please use, Line 1: [HcBuy], Line 1: ID(:Data),Line 3: Amount,Line 4: Name");
				return;
			}
		} else if (event.getLine(0).equalsIgnoreCase("[HcSell]")) {
			if (event.getLine(1).matches("[0-9]{1,}(:[0-9])?")
					&& getInteger(event.getLine(2)) > 0) {
				event.setLine(0, sellString);
				return;
			} else {
				event.getPlayer()
						.sendMessage(
								"Please use, Line 1: [HcSell], Line 1: ID(:Data),Line 3: worth,Line 4: Name");
				return;
			}
		}
	}

	private void updateInventory(final Player play) {
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						play.updateInventory();
					}
				});
	}
}
