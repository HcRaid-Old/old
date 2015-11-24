package com.addongaming.prison.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.utils.Utils;
import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.skills.FarmingData;
import com.addongaming.prison.data.skills.HatchetData;
import com.addongaming.prison.data.skills.MiningData;
import com.addongaming.prison.data.skills.PickaxeData;
import com.addongaming.prison.data.skills.TreeData;
import com.addongaming.prison.data.utilities.CommandData;
import com.addongaming.prison.data.utilities.CraftingData;
import com.addongaming.prison.data.utilities.InventoryData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class Introduction implements Listener {
	private JavaPlugin jp;

	private final HashMap<String, Integer> sceneTaskMap = new HashMap<String, Integer>();

	String[] startPerms;

	public Introduction(JavaPlugin jp) {
		this.jp = jp;
		jp.getServer().getPluginManager().registerEvents(this, jp);
		setupStartPerms();
	}

	@EventHandler
	public void chooseClass(PlayerInteractEvent event) {
		if (event.hasBlock()
				&& (event.getClickedBlock().getType() == Material.SIGN
						|| event.getClickedBlock().getType() == Material.SIGN_POST || event
						.getClickedBlock().getType() == Material.WALL_SIGN)) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String firstLine = sign.getLine(0);
			if (firstLine.equalsIgnoreCase(ChatColor.GOLD + "["
					+ ChatColor.BLUE + "Classes" + ChatColor.GOLD + "]")) {
				event.setCancelled(true);
				if (PrisonerManager.getInstance()
						.getPrisonerInfo(event.getPlayer().getName())
						.getPlayerClass() == PlayerClasses.LIMBO)
					openClassSelector(event.getPlayer());
				else
					event.getPlayer().sendMessage(
							"You are not in Limbo, you cannot change class.");
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getTitle()
				.equalsIgnoreCase("Class Selection")) {
			event.setCancelled(true);
			if (event.getCurrentItem() != null) {
				String name = event.getCurrentItem().getItemMeta()
						.getDisplayName();
				for (PlayerClasses pc : PlayerClasses.values()) {
					if (pc.name().equalsIgnoreCase(ChatColor.stripColor(name))) {
						Player player = (Player) event.getWhoClicked();
						Prisoner prisoner = PrisonerManager.getInstance()
								.getPrisonerInfo(player.getName());
						for (String perm : startPerms) {
							prisoner.addPermission(perm);
						}
						PrisonerManager.getInstance()
								.getPrisonerInfo(player.getName())
								.setPlayerClass(pc);
						player.sendMessage("Oh yeah... I think I remember");
						player.closeInventory();
						startScene(player, pc);
					}
				}
			}
		}
	}

	private void openClassSelector(Player player) {
		Inventory i = Bukkit.createInventory(null, 9, "Class Selection");
		int counter = 2;
		i.setItem(counter++, Utils.setLore(Utils.setName("NOT AVAILABLE",
				new ItemStack(Material.WOOL, 1, (short) 14))));
		i.setItem(counter++, Utils.setLore(
				Utils.setName("Thief", new ItemStack(Material.WOOD_DOOR)),
				"Thief allows you to", "both pick pocket players,",
				"and crack door/chest locks."));
		i.setItem(counter++, Utils.setLore(Utils.setName("Assassin",
				new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)),
				"Assassins can be contracted,", "to kill.",
				"They cause bonus damage", "when attacking from behind."));
		i.setItem(counter++, Utils.setLore(Utils.setName("Murderer",
				new ItemStack(Material.DIAMOND_SWORD)),
				"Murderers have a thirst for blood,",
				"they cause bonus damage", "when attacking from behind."));
		i.setItem(counter++, Utils.setLore(Utils.setName("NOT AVAILABLE",
				new ItemStack(Material.WOOL, 1, (short) 14))));

		player.openInventory(i);
	}

	@EventHandler
	public void playerJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) {
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							try {
								event.getPlayer().teleport(
										HcEssentials.essentials.getWarps()
												.getWarp("start"));
								event.getPlayer()
										.sendMessage(
												"Woah... I'm in prison? What did I do?");
							} catch (WarpNotFoundException
									| InvalidWorldException e) {
								e.printStackTrace();
							}
						}
					}, 10L);

		}
	}

	@EventHandler
	public void playerLogout(PlayerQuitEvent event) {
		if (sceneTaskMap.containsKey(event.getPlayer().getName())) {
			Player player = event.getPlayer();
			jp.getServer().getScheduler()
					.cancelTask(sceneTaskMap.get(player.getName()));
			sceneTaskMap.remove(player.getName());
			try {
				player.teleport(HcEssentials.essentials.getWarps().getWarp(
						"tutorial"));
			} catch (WarpNotFoundException | InvalidWorldException e) {
				e.printStackTrace();
			}
		}
	}

	private void setupStartPerms() {
		List<String> list = new ArrayList<String>();
		// Setup init perms
		for (MiningData md : MiningData.values())
			if (md.getLevelReq() <= 1)
				list.add(md.getPermission());
		for (PickaxeData pd : PickaxeData.values())
			if (pd.getLevelReq() <= 1)
				list.add(pd.getPermission());
		for (TreeData td : TreeData.values())
			if (td.getLevelReq() <= 1)
				list.add(td.getPermission());
		for (HatchetData hd : HatchetData.values())
			if (hd.getLevelReq() <= 1)
				list.add(hd.getPermission());
		for (CommandData hd : CommandData.values())
			if (hd.getCharLevel() <= 1)
				list.add(hd.getPermission());
		for (CraftingData hd : CraftingData.values())
			if (hd.getMat() != null && hd.getCharLevel() <= 1)
				list.add(hd.getPermission());
		for (FarmingData hd : FarmingData.values())
			if (hd.getLevelReq() <= 1)
				list.add(hd.getPermission());
		for (InventoryData hd : InventoryData.values())
			if (hd.getCharLevel() <= 1)
				list.add(hd.getPermission());

		startPerms = list.toArray(new String[list.size()]);
	}

	private void startScene(final Player player, final PlayerClasses pc) {
		try {
			player.teleport(HcEssentials.essentials.getWarps().getWarp("court"));
		} catch (WarpNotFoundException | InvalidWorldException e) {
			e.printStackTrace();
		}
		String guiltyOf = null;
		switch (pc) {
		case ASSASSIN:
			guiltyOf = "assassinating the president";
			break;
		case EXOTICDEALER:
			guiltyOf = "distributing over five million rupees worth of drugs";
			break;
		case LIMBO:
			guiltyOf = "ERROR - Contact an admin";
			break;
		case MURDERER:
			guiltyOf = "killing an entire tribe";
			break;
		case SNITCH:
			guiltyOf = "selling government secrets to other countries";
			break;
		case THIEF:
			guiltyOf = "stealing tea from the queen";
			break;

		}
		final String guilt = guiltyOf;
		sceneTaskMap.put(player.getName(), jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {
					int counter = 0;

					@Override
					public void run() {
						counter++;
						if (!player.isOnline()) {
							jp.getServer()
									.getScheduler()
									.cancelTask(
											sceneTaskMap.get(player.getName()));
							sceneTaskMap.put(player.getName(), -1);
							return;
						}
						String judge = "<" + ChatColor.GREEN + "Court "
								+ ChatColor.GOLD + "Judge" + ChatColor.RESET
								+ "> ";
						String jury = "<" + ChatColor.GREEN + "Court "
								+ ChatColor.AQUA + "Jury" + ChatColor.RESET
								+ "> ";

						switch (counter) {
						case 1:
							player.sendMessage(judge
									+ "Jury, have you reached a verdict?");
							return;
						case 2:
							player.sendMessage(jury
									+ "Yes, we find the defendant, "
									+ player.getName() + " guilty of " + guilt);
							break;
						case 3:
							player.sendMessage(judge + player.getName()
									+ " you have been found guilty of " + guilt
									+ ", you will now spend life in prison.");
							break;
						case 4:
							jp.getServer()
									.getScheduler()
									.cancelTask(
											sceneTaskMap.get(player.getName()));
							sceneTaskMap.remove(player.getName());
							PrisonerManager.getInstance()
									.getPrisonerInfo(player.getName())
									.refreshScoreboard();
							player.sendMessage("Looks like I'm in prison... Oh well.");
							try {
								player.teleport(HcEssentials.essentials
										.getWarps().getWarp("tutorial"));
							} catch (WarpNotFoundException
									| InvalidWorldException e) {
								e.printStackTrace();
							}
						}

					}
				}, 80L, 160L));
	}
}
