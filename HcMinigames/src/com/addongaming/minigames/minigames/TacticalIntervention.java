package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Iterator;
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

import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.flag.ConquestFlag;
import com.addongaming.minigames.management.flag.Flag;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.kits.Kit;
import com.addongaming.minigames.management.scheduling.FlagTicker;
import com.addongaming.minigames.management.scheduling.GameLimit;
import com.addongaming.minigames.management.scheduling.InventoryOpener;

public class TacticalIntervention extends ArenaGame {
	private List<ConquestFlag> conquestFlags = new ArrayList<ConquestFlag>();
	private int game = 0;
	private int game1 = 0, game2 = 0;

	public TacticalIntervention(Arena arena, Lobby lobby) {
		super(arena, lobby);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA
				+ "Tactical Intervention" + ChatColor.GOLD + "] "
				+ ChatColor.GREEN);
		setupInventories();
		for (ArenaPlayer ap : getArenaList()) {
			super.onSpawn(ap);
		}
		limit = new GameLimit(this, 7 * 60, 1, true);
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.EXPLOSIVE_EXPERT;
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

	GameLimit limit;

	@Override
	public void onStart() {
		int teamRed = 0, teamBlue = 0;
		super.setStatus(Status.INGAME);
		for (ArenaPlayer ap : getArenaList()) {
			if (teamRed > teamBlue) {
				ap.setTeam(Team.BLUE.getTeamId());
				teamBlue++;
			} else {
				ap.setTeam(Team.RED.getTeamId());
				teamRed++;
			}
			onSpawn(ap);
			openKitInventory(ap);
		}
		super.getLobby().getMinigames().getManagement()
				.getSchedulerManagement().runScheduler(this, limit, 0l, 20);
		for (Flag flag : getArena().getFlags()) {
			ConquestFlag cf = new ConquestFlag(flag);
			cf.spawnFlag();
			conquestFlags.add(cf);
			super.getLobby().getMinigames().getManagement()
					.getSchedulerManagement()
					.runScheduler(this, new FlagTicker(cf), 0l, 20);
		}
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
		for (ConquestFlag cf : conquestFlags)
			if (cf.getOwner() == Team.BLUE.getTeamId()) {
				setWinner(Team.NONE.getTeamId());
				return;
			}
		setWinner(Team.RED.getTeamId());
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
		for (Iterator<ConquestFlag> iter = conquestFlags.iterator(); iter
				.hasNext();) {
			iter.next().rollback();
			iter.remove();
		}
		super.onFinish();
	}

	@Override
	public void onWin() {
		if (game1 == 0 && game2 == 1)
			messageAll("The red team won!");
		else if (game1 == 1 && game2 == 0)
			messageAll("The blue team won!");
		else
			messageAll("It was a draw!");
		super.onWin();
	}

	private void sendScore(ArenaPlayer ap, boolean killstreak) {
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Score: " + ChatColor.AQUA
						+ ap.getScore());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Kills: " + ChatColor.AQUA
						+ ap.getKills());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Your Flag Caps: " + ChatColor.AQUA
						+ ap.getFlagCaps());
		if (killstreak)
			ap.getBase().sendMessage(
					ChatColor.DARK_PURPLE + "Killstreak: " + ChatColor.AQUA
							+ ap.getKillStreak());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
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
	public void onFlagCapture(Flag flag) {
		calculateScores();
		if (getWinningTeam() == Team.RED.getTeamId())
			swapSide();
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
	public void swapSide() {
		if (game == 0) {
			for (ConquestFlag flag : conquestFlags) {
				flag.setPower((short) 0);
				flag.setOwner(Team.BLUE.getTeamId());
				flag.spawnFlag();
			}
			for (ArenaPlayer ap : getArenaList()) {
				if (ap.getTeam() == Team.RED.getTeamId()) {
					ap.setTeam(Team.BLUE.getTeamId());
				} else {
					ap.setTeam(Team.RED.getTeamId());
				}
				onSpawn(ap);
			}
			calculateScores();
			if (getWinningTeam() == Team.RED.getTeamId())
				game1 = 1;
			game++;
			messageAll("Round over, swapping sides.");
			limit.reset();
		} else {
			calculateScores();
			if (getWinningTeam() == Team.RED.getTeamId())
				game2 = 1;
			getLobby().exitGame();
		}
	}

	Inventory kitLoyalInterfaceNormal, kitLoyalInterfacePremium,
			kitInsurgentInterfacePremium, kitInsurgentInterfaceNormal;

	private void openKitInventory(ArenaPlayer ap) {
		if (ap.getTeam() == Team.BLUE.getTeamId()) {
			if (ap.getBase().hasPermission("hcraid.premium.kit"))
				getLobby()
						.getMinigames()
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								getLobby().getMinigames(),
								new InventoryOpener(ap,
										kitLoyalInterfacePremium), 5l);
			else
				getLobby()
						.getMinigames()
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								getLobby().getMinigames(),
								new InventoryOpener(ap, kitLoyalInterfaceNormal),
								5l);
		} else {
			if (ap.getBase().hasPermission("hcraid.premium.kit"))
				getLobby()
						.getMinigames()
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								getLobby().getMinigames(),
								new InventoryOpener(ap,
										kitInsurgentInterfacePremium), 5l);
			else
				getLobby()
						.getMinigames()
						.getServer()
						.getScheduler()
						.scheduleSyncDelayedTask(
								getLobby().getMinigames(),
								new InventoryOpener(ap,
										kitInsurgentInterfaceNormal), 5l);
		}
	}

	@SuppressWarnings("serial")
	private void setupInventories() {
		kitLoyalInterfaceNormal = Bukkit.createInventory(null, 9,
				ChatColor.DARK_BLUE + "Kit Selection");
		kitLoyalInterfacePremium = Bukkit.createInventory(null, 9,
				ChatColor.GOLD + "Kit Selection");
		kitInsurgentInterfacePremium = Bukkit.createInventory(null, 9,
				ChatColor.DARK_BLUE + "Kit Selection");
		kitInsurgentInterfaceNormal = Bukkit.createInventory(null, 9,
				ChatColor.GOLD + "Kit Selection");
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
			if (ek.getTeam() == Team.BLUE.getTeamId()) {
				if (ek.isPremium()) {
					kitLoyalInterfaceNormal
							.addItem(kit.getPremiumDisplayItem());
					kitLoyalInterfacePremium.addItem(kit
							.getRegularDisplayItem());
				} else {
					kitLoyalInterfaceNormal
							.addItem(kit.getRegularDisplayItem());
					kitLoyalInterfacePremium.addItem(kit
							.getRegularDisplayItem());
				}
			} else {
				if (ek.isPremium()) {
					kitInsurgentInterfaceNormal.addItem(kit
							.getPremiumDisplayItem());
					kitInsurgentInterfacePremium.addItem(kit
							.getRegularDisplayItem());
				} else {
					kitInsurgentInterfaceNormal.addItem(kit
							.getRegularDisplayItem());
					kitInsurgentInterfacePremium.addItem(kit
							.getRegularDisplayItem());
				}
			}
		}
	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		// TODO Allow kit changing via GUI as well as entering
		EKit ekit = EKit.getFromReadable(cursor.getItemMeta().getDisplayName());
		if (ekit == null) {
			return true;
		}
		if (ekit.isPremium()
				&& inventory.getTitle().equalsIgnoreCase(
						kitLoyalInterfaceNormal.getTitle())) {
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
	public void openKits(Player player) {
		openKitInventory(getPlayer(player.getName()));
	}
}
