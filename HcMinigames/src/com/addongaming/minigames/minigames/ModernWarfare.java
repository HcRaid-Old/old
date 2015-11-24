package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.kits.Kit;
import com.addongaming.minigames.management.scheduling.GameLimit;
import com.addongaming.minigames.management.scheduling.InventoryOpener;

public class ModernWarfare extends ArenaGame {

	public ModernWarfare(Arena arena, Lobby lobby) {
		super(arena, lobby);
		setupInventories();
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "ModernWarfare"
				+ ChatColor.GOLD + "] " + ChatColor.GREEN);
		for (ArenaPlayer ap : getArenaList()) {
			super.onSpawn(ap);
			openTeamInventory(ap);
		}
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.ASSAULT;
	}

	@Override
	public void removePlayer(String user) {
		super.removePlayer(user);
		if (getStatus() == Status.INGAME) {
			int teamRed = 0, teamBlue = 0;
			for (ArenaPlayer ap : getArenaList()) {
				if (ap.getTeam() == Team.BLUE.getTeamId()) {
					teamBlue++;
				} else if (ap.getTeam() == Team.RED.getTeamId()) {
					teamRed++;
				}
			}
			if (teamRed == 0 || teamBlue == 0) {
				calculateScores();
				getLobby().exitGame();
			}
		}
	}

	@Override
	public void onStart() {
		int teamRed = 0, teamBlue = 0;
		for (ArenaPlayer ap : getArenaList()) {
			if (ap.getTeam() == Team.RED.getTeamId())
				teamRed++;
			else if (ap.getTeam() == Team.BLUE.getTeamId())
				teamBlue++;
		}
		super.setStatus(Status.INGAME);
		for (ArenaPlayer ap : getArenaList()) {
			if (ap.getTeam() != Team.NONE.getTeamId()) {
				if (teamRed > teamBlue) {
					ap.setTeam(Team.BLUE.getTeamId());
					teamBlue++;
				} else {
					ap.setTeam(Team.RED.getTeamId());
					teamRed++;
				}
			}
			onSpawn(ap);
			openKitInventory(ap);
		}
		super.getLobby().getMinigames().getManagement()
				.getSchedulerManagement()
				.runScheduler(this, new GameLimit(this, 5 * 60, 1), 0l, 20);
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		if (getStatus() == Status.INGAME
				&& ap.getTeam() == Team.NONE.getTeamId()) {
			int teamRed = 0, teamBlue = 0;
			for (ArenaPlayer p : getArenaList()) {
				if (p.getTeam() == Team.BLUE.getTeamId()) {
					teamBlue++;
				} else if (p.getTeam() == Team.RED.getTeamId()) {
					teamRed++;
				}
			}
			if (teamRed > teamBlue)
				ap.setTeam(Team.BLUE.getTeamId());
			else
				ap.setTeam(Team.RED.getTeamId());
			openKitInventory(ap);
		}
		super.onSpawn(ap);
	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		if (getStatus() != Status.INGAME) {
			return;
		}
		if (getArena().getArenaProperties().containsKey(
				ArenaProperty.SCORE_PER_KILL))
			killer.incrementScore(getArena().getInt(
					ArenaProperty.SCORE_PER_KILL));
		else
			killer.incrementScore(50);
		killer.incrementKills();
		died.resetKillStreak();
		died.died();
		sendScore(killer, true);
		sendScore(died, false);
		super.onKill(killer, died);
		calculateScores();
	}

	private void calculateScores() {
		int redScore = 0, blueScore = 0;
		for (ArenaPlayer ap : super.getArenaList())
			if (ap.getTeam() == Team.RED.getTeamId())
				redScore += ap.getScore();
			else if (ap.getTeam() == Team.BLUE.getTeamId())
				blueScore += ap.getScore();
		if (redScore > blueScore)
			setWinner(Team.RED.getTeamId());
		else if (blueScore > redScore)
			setWinner(Team.BLUE.getTeamId());
		else
			setWinner(Team.NONE.getTeamId());
	}

	@Override
	public void equipKits(ArenaPlayer ap) {
		super.equipKits(ap);
		ItemStack piece = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta lam = (LeatherArmorMeta) piece.getItemMeta();
		if (ap.getTeam() == Team.BLUE.getTeamId())
			lam.setColor(Color.BLUE);
		else
			lam.setColor(Color.RED);
		piece.setItemMeta(lam);
		ap.setArmour(new ItemStack[] { piece });
	}

	@Override
	public void onFinish() {
		onWin();
		super.onFinish();
	}

	@Override
	public void onWin() {
		calculateScores();
		if (getWinningTeam() == Team.RED.getTeamId()) {
			messageAll("The red team won!");
			for (ArenaPlayer ap : getArenaList()) {
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(ap.getName());
				if (ap.getTeam() == Team.RED.getTeamId()) {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerMedLoot(ap.getName());
					mg.setMedLoot(mg.getMedLoot() + 1);
				} else {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerSmallLoot(ap.getName());
					mg.setSmallLoot(mg.getSmallLoot() + 1);
				}
			}
		} else if (getWinningTeam() == Team.BLUE.getTeamId()) {
			messageAll("The blue team won!");
			for (ArenaPlayer ap : getArenaList()) {
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(ap.getName());
				if (ap.getTeam() == Team.BLUE.getTeamId()) {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerMedLoot(ap.getName());
					mg.setMedLoot(mg.getMedLoot() + 1);
				} else {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerSmallLoot(ap.getName());
					mg.setSmallLoot(mg.getSmallLoot() + 1);
				}
			}
		} else
			messageAll("Both teams have drawn!");
		ArenaPlayer redHighest = null, blueHighest = null;
		for (ArenaPlayer ap : getArenaList())
			if (ap.getTeam() == Team.RED.getTeamId()) {
				if (redHighest == null || ap.getKills() > redHighest.getKills())
					redHighest = ap;
			} else if (ap.getTeam() == Team.BLUE.getTeamId()) {
				if (blueHighest == null
						|| ap.getKills() > blueHighest.getKills())
					blueHighest = ap;
			}
		if (blueHighest != null)
			blueHighest.incrementScore(300);
		if (redHighest != null)
			redHighest.incrementScore(300);
		super.onWin();
	}

	private void sendScore(ArenaPlayer ap, boolean killstreak) {
		String winningTeamString;
		int redKills = 0, blueKills = 0;
		for (ArenaPlayer a : super.getArenaList())
			if (a.getTeam() == Team.RED.getTeamId())
				redKills += a.getKills();
			else if (a.getTeam() == Team.BLUE.getTeamId())
				blueKills += a.getKills();
		if (redKills > blueKills)
			winningTeamString = "Team " + ChatColor.RED + "Red"
					+ ChatColor.DARK_PURPLE + " are winning by "
					+ ChatColor.RED + (redKills - blueKills)
					+ ChatColor.DARK_PURPLE + " kill"
					+ (redKills - blueKills == 1 ? "" : "s") + ".";
		else if (blueKills > redKills)
			winningTeamString = "Team " + ChatColor.BLUE + "Blue"
					+ ChatColor.DARK_PURPLE + " are winning by "
					+ ChatColor.BLUE + (blueKills - redKills)
					+ ChatColor.DARK_PURPLE + " kill"
					+ (blueKills - redKills == 1 ? "" : "s") + ".";
		else
			winningTeamString = "Both teams are drawing with " + redKills
					+ " kill" + (blueKills == 1 ? "" : "s") + ".";
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		ap.getBase().sendMessage(ChatColor.DARK_PURPLE + winningTeamString);
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Score: " + ChatColor.AQUA
						+ ap.getScore());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Kills: " + ChatColor.AQUA
						+ ap.getKills());
		if (killstreak)
			ap.getBase().sendMessage(
					ChatColor.DARK_PURPLE + "Killstreak: " + ChatColor.AQUA
							+ ap.getKillStreak());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	Inventory kitInterfaceNormal, kitInterfacePremium, teamInterfaceNormal,
			teamInterfacePremium;

	private void openKitInventory(ArenaPlayer ap) {
		if (ap.getBase().hasPermission("hcraid.premium.kit"))
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							new InventoryOpener(ap, kitInterfacePremium), 5l);
		else
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							new InventoryOpener(ap, kitInterfaceNormal), 5l);
	}

	private void openTeamInventory(ArenaPlayer ap) {
		if (ap.getBase().hasPermission("hcraid.premium.teamjoin"))
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							new InventoryOpener(ap, teamInterfacePremium), 5l);
		else
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							new InventoryOpener(ap, teamInterfaceNormal), 5l);
	}

	@SuppressWarnings("serial")
	private void setupInventories() {
		kitInterfaceNormal = Bukkit.createInventory(null, 9,
				ChatColor.DARK_BLUE + "Kit Selection");
		kitInterfacePremium = Bukkit.createInventory(null, 9, ChatColor.GOLD
				+ "Kit Selection");
		ItemStack enter = new Wool(DyeColor.GRAY).toItemStack();
		ItemMeta im = enter.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + "Enter");
		enter.setItemMeta(im);
		// kitInterfaceNormal.addItem(enter);
		// kitInterfacePremium.addItem(enter);
		EKit[] eKits = EKit.getByGamemode(GameMode.MODERN_WARFARE);
		for (EKit ek : eKits) {
			Kit kit = getLobby().getMinigames().getManagement()
					.getKitManagement().getKit(ek);
			if (ek.isPremium()) {
				kitInterfaceNormal.addItem(kit.getPremiumDisplayItem());
				kitInterfacePremium.addItem(kit.getRegularDisplayItem());
			} else {
				kitInterfaceNormal.addItem(kit.getRegularDisplayItem());
				kitInterfacePremium.addItem(kit.getRegularDisplayItem());
			}
		}
		teamInterfaceNormal = Bukkit.createInventory(null, 9,
				ChatColor.DARK_BLUE + "Team Selection");
		teamInterfacePremium = Bukkit.createInventory(null, 9, ChatColor.GOLD
				+ "Team Selection");

		ItemStack is = new Wool(DyeColor.GRAY).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Auto-Assign");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Auto-selects a team");
			}
		});
		is.setItemMeta(im);
		teamInterfaceNormal.addItem(is);
		is = new Wool(DyeColor.BLUE).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Blue team");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Premium members can");
				add(ChatColor.GRAY + "select a team");
			}
		});
		is.setItemMeta(im);
		teamInterfaceNormal.addItem(is);
		is = new Wool(DyeColor.RED).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Red team");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Premium members can");
				add(ChatColor.GRAY + "select a team");
			}
		});
		is.setItemMeta(im);
		teamInterfaceNormal.addItem(is);
		// Premium
		is = new Wool(DyeColor.GRAY).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Auto-Assign");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Auto-selects a team");
			}
		});
		is.setItemMeta(im);
		teamInterfacePremium.addItem(is);
		is = new Wool(DyeColor.BLUE).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Blue team");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Join the blue team");
			}
		});
		is.setItemMeta(im);
		teamInterfacePremium.addItem(is);
		is = new Wool(DyeColor.RED).toItemStack();
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Red team");
		im.setLore(new ArrayList<String>() {
			{
				add(ChatColor.GRAY + "Join the red team");
			}
		});
		is.setItemMeta(im);
		teamInterfacePremium.addItem(is);
	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		// TODO Allow kit changing via GUI as well as entering
		if (cursor != null && cursor.getType() == Material.WOOL) {
			Wool wool = new Wool(DyeColor.getByWoolData((byte) cursor
					.getDurability()));
			if (wool.getColor() == DyeColor.GRAY) {
				player.getBase().closeInventory();
				return true;
			} else {
				if (!inventory.getTitle().equalsIgnoreCase(
						this.teamInterfacePremium.getTitle())) {
					message(player.getBase(), "You need premium to pick a team");
					player.getBase().closeInventory();
					return true;
				} else {
					if (wool.getColor() == DyeColor.RED) {
						player.setTeam(Team.RED.getTeamId());
						message(player.getBase(), "You are now on the red team");
						player.getBase().closeInventory();
						return true;
					} else if (wool.getColor() == DyeColor.BLUE) {
						player.setTeam(Team.BLUE.getTeamId());
						message(player.getBase(),
								"You are now on the blue team");
						player.getBase().closeInventory();
						return true;
					}
				}
			}
		}
		EKit ekit = EKit.getFromReadable(cursor.getItemMeta().getDisplayName());
		if (ekit == null) {
			return true;
		}
		if (ekit.isPremium()
				&& inventory.getTitle().equalsIgnoreCase(
						kitInterfaceNormal.getTitle())) {
			message(player.getBase(), "You need to have premium for that kit.");
			return true;
		}
		player.setKit(ekit);
		message(player.getBase(), "Selected kit " + ekit.toReadableText()
				+ " use /kit to open the menu again.");
		player.getBase().closeInventory();
		return true;
	}

	@Override
	public String getNameChange(ArenaPlayer tagChange, ArenaPlayer viewer) {
		if (tagChange.getTeam() == Team.RED.getTeamId())
			return ChatColor.RED + tagChange.getName();
		else if (tagChange.getTeam() == Team.BLUE.getTeamId())
			return ChatColor.BLUE + tagChange.getName();
		return null;
	}

	@Override
	public void playerChat(Player player, String message, boolean global) {
		ArenaPlayer ap = getPlayer(player.getName());
		String prefix;
		if (ap.getTeam() == Team.BLUE.getTeamId())
			prefix = ChatColor.BLUE + player.getName();
		else
			prefix = ChatColor.RED + player.getName();
		for (ArenaPlayer arenaPlayer : getArenaList()) {
			if (arenaPlayer.isValid()) {
				if (global) {
					arenaPlayer.getBase().sendMessage(
							prefix + " [!] " + ChatColor.WHITE + message);
				} else if (arenaPlayer.getTeam() == ap.getTeam()) {
					arenaPlayer.getBase().sendMessage(
							prefix + " " + ChatColor.WHITE + message);
				}
			}
		}
	}

	@Override
	public ArenaPlayer[] getWinningPlayers() {
		List<ArenaPlayer> winners = new ArrayList<ArenaPlayer>();
		for (ArenaPlayer ap : getArenaList())
			if (ap.getTeam() == getWinningTeam())
				winners.add(ap);
		return winners.toArray(new ArenaPlayer[winners.size()]);
	}

	@Override
	public void openKits(Player player) {
		openKits(player);
	}
}
