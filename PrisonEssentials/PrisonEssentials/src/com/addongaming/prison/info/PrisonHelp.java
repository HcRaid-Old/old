package com.addongaming.prison.info;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class PrisonHelp implements Listener {
	private final String prisonInfoTitle = ChatColor.DARK_BLUE + "["
			+ ChatColor.GOLD + "HcInfo" + ChatColor.DARK_BLUE + "]";
	private final String suppliesTitle = ChatColor.DARK_RED + "Supplies";
	private final String nathan = ChatColor.GOLD + "[" + ChatColor.GRAY
			+ "Nathan The Shop Keeper" + ChatColor.GOLD + " -> me] ";
	File folder;

	public PrisonHelp(JavaPlugin jp) {
		folder = new File(jp.getDataFolder() + File.separator + "PrisonInfo");
		if (!folder.exists())
			folder.mkdirs();
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	private void createFile(String line) {
		try {
			File file = new File(folder + File.separator + line + ".txt");
			if (!file.exists())
				file.createNewFile();
		} catch (Exception e) {
		}
	}

	private String[] getMessages(String line) {
		List<String> list = new ArrayList<String>();
		try {
			Scanner scan = new Scanner(new File(folder + File.separator + line
					+ ".txt"));
			list.add(prisonInfoTitle + ChatColor.AQUA + " " + line);
			while (scan.hasNextLine())
				list.add(ChatColor.translateAlternateColorCodes('&',
						scan.nextLine()));
			return list.toArray(new String[list.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	HashMap<String, Date> supplyRedeemTime = new HashMap<String, Date>();

	private DataReturn canGetSupplies(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance().getPrisonerInfo(
				p.getName());
		if (!supplyRedeemTime.containsKey(p.getName())
				|| supplyRedeemTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	@EventHandler
	public void signClickEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()) {
			if (event.getClickedBlock().getState() != null
					&& event.getClickedBlock().getState() instanceof Sign) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase(prisonInfoTitle)) {
					event.getPlayer().sendMessage(getMessages(sign.getLine(2)));
				} else if (sign.getLine(0).equalsIgnoreCase(suppliesTitle)) {
					getSupplies(event.getPlayer());
				}
			}
		}
	}

	private void getSupplies(Player player) {
		if (canGetSupplies(player) == DataReturn.FAILURE) {
			player.sendMessage(nathan
					+ "Hey! I only gave you some supplies a short while back! Be Patient!");
		} else {
			if (PrisonerManager.getInstance()
					.getPrisonerInfo(player.getUniqueId()).hasBalance(3000)) {
				player.sendMessage(nathan
						+ "C'mon now, you don't really need free stuff if you've got cash kid.");
				return;
			} else {
				switch (PrisonerManager.getInstance()
						.getPrisonerInfo(player.getUniqueId()).getPlayerClass()) {
				case ASSASSIN:
					player.getInventory().addItem(
							new ItemStack[] { new ItemStack(
									Material.MUSHROOM_SOUP, 2) });
					break;
				case EXOTICDEALER:
					break;
				case GUARD:
					break;
				case LIMBO:
					break;
				case MURDERER:
					player.getInventory()
							.addItem(
									new ItemStack[] { new ItemStack(
											Material.WOOD_SWORD) });
					break;
				case SNITCH:
					break;
				case THIEF:
					player.getInventory().addItem(
							new ItemStack[] { new ItemStack(
									Material.COOKED_CHICKEN, 2) });
					break;
				default:
					break;

				}
				player.getInventory()
						.addItem(
								new ItemStack[] { new ItemStack(
										Material.WOOD_PICKAXE) });
				player.getInventory().addItem(
						new ItemStack[] { new ItemStack(Material.WOOD_AXE) });
				player.getInventory().addItem(
						new ItemStack[] { new ItemStack(Material.COAL, 2) });
				player.getInventory().addItem(
						new ItemStack[] { new ItemStack(Material.APPLE, 2) });
				player.sendMessage(nathan
						+ "Here you go, a few basic supplies. Now don't lose this!");
				supplyRedeemTime.put(player.getName(),
						new Date(new Date().getTime() + 1200000));
				player.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void signPlace(final SignChangeEvent event) {
		if (event.isCancelled() || !event.getPlayer().isOp())
			return;
		if (event.getLine(0).equalsIgnoreCase("HcInfo")) {
			if (event.getLine(2).length() <= 2) {
				event.getPlayer().sendMessage(
						"Please put the file name on third line.");
				return;
			}
			event.setLine(0, prisonInfoTitle);
			createFile(event.getLine(2));
		} else if (event.getLine(0).equalsIgnoreCase("Supplies"))
			event.setLine(0, suppliesTitle);
	}
}
