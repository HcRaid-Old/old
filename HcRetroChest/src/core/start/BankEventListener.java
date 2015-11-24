package core.start;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import core.bank.BankFileHandler;
import core.bank.BankInstance;
import core.bank.IncorrectRankException;

public class BankEventListener implements Listener {
	@EventHandler
	public void playerKick(PlayerKickEvent pke) {
		playerLeft(pke.getPlayer());
	}

	@EventHandler
	public void itemDrop(org.bukkit.event.player.PlayerDropItemEvent event) {
		if (Main.playerMap.containsKey(event.getPlayer().getName())) {
			event.getPlayer()
					.sendMessage(
							Main.title
									+ ChatColor.RED
									+ "You cannot drop items while your chest is open.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerClicked(org.bukkit.event.inventory.InventoryClickEvent ic) {
		if (Main.playerMap.containsKey(ic.getWhoClicked().getName())) {
			BankInstance bi = Main.playerMap.get(ic.getWhoClicked().getName());
			if (ic.getCurrentItem() != null) {
				if (!(ic.getCurrentItem().getType() == Material.PAPER)) {
					return;
				}
			} else
				return;
			if (ic.getCurrentItem() != null) {
				ItemStack is = ic.getCurrentItem();
				if (is.getItemMeta() != null) {
					ItemMeta im = is.getItemMeta();
					if (im.getDisplayName() != null
							&& !im.getDisplayName().isEmpty()) {
						if (im.getDisplayName().contains("Upgrade to ")) {
							ic.setCancelled(true);
							return;
						}
					}
				}
			}
			for (int i : bi.getCurrentchest().getSlots(
					bi.getCurrentchest().getRank())) {
				if (ic.getSlot() == i) {
					ic.setCancelled(true);
					if (i == 0) {
						if (bi.canChange(-1))
							try {
								bi.changeChest(-1);
							} catch (IncorrectRankException e) {
								e.printStackTrace();
							}
					} else if (i == 8) {
						if (bi.canChange(+1))
							try {
								bi.changeChest(+1);
							} catch (IncorrectRankException e) {
								e.printStackTrace();
							}
					}
					return;
				}
			}
		} else if (ic.getInventory().getName().contains("bank")) {
			if (ic.getCursor() != null
					&& ic.getCursor().getType() != Material.AIR) {
				writeToFile((Player) ic.getWhoClicked(), ic.getCursor());
			}
			ic.setCancelled(true);
			ic.getWhoClicked().closeInventory();
		}
	}

	private void writeToFile(Player whoClicked, ItemStack cursor) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(d) + "| ");
		sb.append(" Name: " + cursor.getType().name());
		sb.append(" Amount: " + cursor.getAmount());
		File duper = new File(Main.dupeFolder + "\\" + whoClicked.getName()
				+ ".txt");

		try {
			if (!duper.exists())
				duper.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(duper, true));
			pw.println(sb.toString() + "\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final ArrayList<String> toOpen = new ArrayList<String>();

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e) {
		if (e.getClickedBlock() != null)
			if (e.getClickedBlock().getType().equals(Material.CHEST)) {
				if (e.getClickedBlock().getData() == (byte) 10) {
					if (!e.getPlayer().hasPermission("HcRaid.Ghast")) {
						e.getPlayer()
								.sendMessage(
										Main.title
												+ ChatColor.RED
												+ "You need to be a Ghast + subscriber to access the bank chest.");

					} else {
						System.out.println("Map length: "
								+ Main.playerMap.size());
						if (Main.playerMap.containsKey(e.getPlayer().getName())) {
							Player p = e.getPlayer();
							BankFileHandler.saveBank(p.getName(),
									Main.playerMap.get(p.getName())
											.getAllBanks());
							Main.playerMap.remove(p.getName());
							if (naughtyMap.containsKey(p.getName())) {
								naughtyMap.put(p.getName(),
										naughtyMap.get(p.getName()) + 1);
							} else {
								naughtyMap.put(p.getName(), 1);
							}
							System.out.println("[HcBankDupe]" + p.getName()
									+ " already had bank open... "
									+ naughtyMap.get(p.getName()));
						}
						Main.playerMap.put(e.getPlayer().getName(),
								new BankInstance((Player) e.getPlayer()));
					}
					e.setCancelled(true);
				}
			}
	}

	HashMap<String, Integer> naughtyMap = new HashMap<String, Integer>();

	@EventHandler
	public void playerOpenEvent(InventoryOpenEvent ioe) {
		if (!ioe.getInventory().getName().contains("bank")) {
			if (!Main.playerMap.containsKey(ioe.getPlayer().getName())
					&& toOpen.contains(ioe.getPlayer().getName())) {
				toOpen.remove(ioe.getPlayer().getName());
			} else {
				if (Main.playerMap.containsKey(ioe.getPlayer().getName()))
					if (!Main.playerMap.get(ioe.getPlayer().getName())
							.isSwitching()) {
						Main.playerMap.remove(ioe.getPlayer().getName());
						return;
					}
			}
		}
	}

	public static void playerLeft(Player p) {
		if (Main.playerMap.containsKey(p.getName())) {
			if (p.getOpenInventory() != null) {
				if (p.isOnline())
					p.sendMessage(Main.title + ChatColor.RED
							+ "A reload closed your bank.");
				p.closeInventory();
			}
			BankFileHandler.saveBank(p.getName(),
					Main.playerMap.get(p.getName()).getAllBanks());
			Main.playerMap.remove(p.getName());
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent pqe) {
		playerLeft(pqe.getPlayer());
	}

	@EventHandler
	public void playerClose(InventoryCloseEvent ice) {
		if (Main.playerMap.containsKey(ice.getPlayer().getName())) {
			if (ice.getInventory().getName().contains("bank")) {
				BankFileHandler.saveBank(ice.getPlayer().getName(),
						Main.playerMap.get(ice.getPlayer().getName())
								.getAllBanks());
				BankInstance bi = Main.playerMap.get(ice.getPlayer().getName());
				if (!bi.isSwitching()) {
					Main.playerMap.remove(ice.getPlayer().getName());
				} else {
					bi.setSwitching(false);
				}

			} else {
				BankFileHandler.saveBank(ice.getPlayer().getName(),
						Main.playerMap.get(ice.getPlayer().getName())
								.getAllBanks());
				Main.playerMap.remove(ice.getPlayer().getName());
			}
			boolean flag = false;
			System.out.println("Checking");
			for (ItemStack is : ice.getPlayer().getInventory().getContents()) {
				if (is != null && is.getType() == Material.PAPER
						&& is.getItemMeta() != null) {
					System.out.println(is.getItemMeta().getDisplayName());
					if (is.getItemMeta().getDisplayName()
							.contains("lick to see your")
							|| is.getItemMeta().getDisplayName()
									.contains("to access the next bank")) {
						ice.getPlayer().getInventory().remove(is);
						flag = true;
					}
				}
			}
			if (flag && ice.getPlayer() != null) {
				Player p = (Player) ice.getPlayer();
				if (p != null && p.isOnline())
					p.updateInventory();
			}
		}
	}
}
