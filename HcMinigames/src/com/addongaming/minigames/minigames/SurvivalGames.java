package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.addongaming.hcessentials.data.Position;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.SurvivalGameArena;
import com.addongaming.minigames.management.chest.ChestFiller;
import com.addongaming.minigames.management.kits.EKit;

public class SurvivalGames extends ArenaGame {
	private SurvivalGameArena arena;
	private final HashMap<String, Position> spawnMap = new HashMap<String, Position>();
	private final Position[] positions;

	public SurvivalGames(SurvivalGameArena arena, Lobby lobby) {
		super(arena, lobby);
		this.arena = arena;
		this.positions = arena.getPositions();
		arena.putProperty(ArenaProperty.FRIENDLY_FIRE_ENABLED, true);
		arena.putProperty(ArenaProperty.INVENTORY_CLICK, true);
		arena.putProperty(ArenaProperty.INVENTORY_OPEN, true);
		arena.putProperty(ArenaProperty.ARMOUR_REMOVABLE, true);
		arena.putProperty(ArenaProperty.ITEM_DROP, true);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "SG" + ChatColor.GOLD
				+ "] " + ChatColor.GREEN);
		for (ArenaPlayer ap : getArenaList()) {
			onSpawn(ap);
		}
	}

	@Override
	public void onStart() {
		super.setStatus(Status.INGAME);
		HashMap<Position, Integer> chestMap = arena.getChestMap();
		ChestFiller cf = getLobby().getMinigames().getManagement()
				.getChestFillingManagement()
				.getChestFiller(GameMode.SURVIVAL_GAMES);
		for (Position pos : chestMap.keySet()) {
			Block block = pos.getLoc().getBlock();
			if (block.getType() == Material.CHEST && block.getState() != null
					&& block.getState() instanceof Chest) {
				cf.fillChest((Chest) block.getState(), chestMap.get(pos));
			}
		}
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.SURVIVAL_GAMES;
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		if (getStatus() == Status.LOBBY) {
			if (spawnMap.containsKey(ap.getName())) {
				ap.getBase().teleport(spawnMap.get(ap.getName()).getLoc());
				return;
			}
			for (Position pos : positions) {
				if (!spawnMap.values().contains(pos)) {
					spawnMap.put(ap.getName(), pos);
					ap.setProperty("startloc", pos);
					ap.getBase().teleport(pos.getLoc());
					ap.setLives(1);
					return;
				}
			}
			getLobby().getMinigames().getManagement().getQueueManagement()
					.removePlayerFromAll(ap.getName());
			ap.setLives(1);
		} else {
			ap.setLives(0);
			ap.getBase().teleport(
					arena.getLobbyLocation().getRandomFreeLocation());
			message(ap.getBase(), "You will enter next game.");
		}
		ap.getBase().updateInventory();

	}

	@Override
	public void removePlayer(String user) {
		super.removePlayer(user);
		if (spawnMap.containsKey(user))
			spawnMap.remove(user);
		checkEnd();
	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		if (getArena().getArenaProperties().containsKey(
				ArenaProperty.SCORE_PER_KILL))
			killer.incrementScore(getArena().getInt(
					ArenaProperty.SCORE_PER_KILL));
		else
			killer.incrementScore(50);
		killer.incrementKills();
		died.resetKillStreak();
		died.decrementLives();
		died.died();
		sendScore(killer, true);
		sendScore(died, false);
		super.onKill(killer, died);
		checkEnd();
	}

	private void checkEnd() {
		int livesLeft = 0;
		for (ArenaPlayer ap : super.getArenaList()) {
			livesLeft += ap.getLivesLeft();
			System.out.println(ap.getName() + " Lives " + ap.getLivesLeft());
		}
		System.out.println("Lives Left: " + livesLeft);
		if (livesLeft == 1)
			getLobby().exitGame();
	}

	@Override
	public void equipKits(ArenaPlayer ap) {
		super.equipKits(ap);
	}

	@Override
	public void onFinish() {
		onWin();
		super.onFinish();
	}

	@Override
	public void onWin() {
		int livesLeft = 0;
		for (ArenaPlayer ap : super.getArenaList())
			livesLeft += ap.getLivesLeft();
		if (livesLeft == 1) {
			for (ArenaPlayer ap : super.getArenaList())
				if (ap.getLivesLeft() == 1) {
					messageAll(ap.getName() + " won!");
				}
		} else {
			messageAll("Time is up!");
		}
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
		if (killstreak)
			ap.getBase().sendMessage(
					ChatColor.DARK_PURPLE + "Killstreak: " + ChatColor.AQUA
							+ ap.getKillStreak());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return false;
	}

	@Override
	public String getNameChange(ArenaPlayer tagChange, ArenaPlayer viewer) {
		return null;
	}

	@Override
	public boolean playerMove(Location from, Location to, Player player) {
		if (Status.LOBBY == getStatus()) {
			if (from.getBlockX() != to.getBlockX()
					|| from.getBlockZ() != to.getBlockZ()) {
				player.teleport(spawnMap.get(player.getName()).getLoc());
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

	@Override
	public boolean onItemClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return true;
	}

	@Override
	public int onItemPickup(Item item, ArenaPlayer ap) {
		return 0;
	}

	@Override
	public boolean chestInteraction(InventoryHolder inventoryHolder) {
		return true;
	}

	@Override
	public void playerChat(Player player, String message, boolean global) {
		for (ArenaPlayer arenaPlayer : getArenaList()) {
			if (arenaPlayer.isValid()) {
				arenaPlayer
						.getBase()
						.sendMessage(
								(player.hasPermission("hcraid.premium.chat") ? ChatColor.BLUE
										: ChatColor.GRAY)
										+ player.getName()
										+ " "
										+ ChatColor.WHITE + message);
			}
		}
	}

	@Override
	public ArenaPlayer[] getWinningPlayers() {
		// TODO
		List<ArenaPlayer> winners = new ArrayList<ArenaPlayer>();
		for (ArenaPlayer ap : getArenaList())
			if (ap.getTeam() == getWinningTeam())
				winners.add(ap);
		return winners.toArray(new ArenaPlayer[winners.size()]);
	}

	@Override
	public void openKits(Player player) {
		message(player, "Sorry you cannot choose your kits in this game.");
	}
}
