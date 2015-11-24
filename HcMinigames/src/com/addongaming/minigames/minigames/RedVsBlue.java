package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.kitteh.tag.TagAPI;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.Rollback;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.scheduling.RVBPlayerPicker;
import com.addongaming.minigames.management.scheduling.WallRemoveScheduler;

public class RedVsBlue extends ArenaGame {
	private Rollback rollback;
	private RVBPlayerPicker rvbPlayerPicker = null;
	private WallRemoveScheduler wallRemoveScheduler = null;

	public RedVsBlue(Arena arena, Lobby lobby) {
		super(arena, lobby);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "RvB"
				+ ChatColor.GOLD + "] " + ChatColor.GREEN);
		for (ArenaPlayer ap : getArenaList())
			super.onSpawn(ap);
		getArena().setBoolean(ArenaProperty.BLOCK_BREAK, true);
		getArena().setBoolean(ArenaProperty.BLOCK_PLACE, true);
		getArena().setBoolean(ArenaProperty.INVENTORY_OPEN, true);
		getArena().setBoolean(ArenaProperty.INVENTORY_CLICK, true);
		getArena().setBoolean(ArenaProperty.HUNGER_19, false);
		getArena().setBoolean(ArenaProperty.HUNGER_REGEN_HEALTH, true);
		rollback = lobby.getMinigames().getManagement().getRollbackManagement()
				.createRollback();
		for (Block b : arena.getLocationZone(ArenaProperty.ARENA)
				.getAllBlocks()) {
			if (b.isLiquid()) {
				if (b.getType() == Material.STATIONARY_WATER
						|| b.getType() == Material.WATER)
					b.setType(Material.AIR);
			}
		}
	}

	@Override
	public void onStart() {
		if (getStatus() == Status.LOBBY) {
			super.setStatus(Status.CHOOSING_PLAYERS);
			Iterator<ArenaPlayer> iter = getArenaList().iterator();
			ArenaPlayer red = null;
			ArenaPlayer blue = null;
			for (ArenaPlayer ap : getArenaList())
				if (ap.isValid())
					if (red == null)
						red = ap;
					else if (blue == null)
						blue = ap;
			red.setTeam(Team.RED.getTeamId());
			blue.setTeam(Team.BLUE.getTeamId());
			rvbPlayerPicker = new RVBPlayerPicker(this, red, blue, getArena()
					.getSpawnZone(ArenaProperty.RED_TEAM_CHOOSE), getArena()
					.getSpawnZone(ArenaProperty.BLUE_TEAM_CHOOSE));
			getLobby().getMinigames().getManagement().getSchedulerManagement()
					.runScheduler(this, rvbPlayerPicker, 20l, 20);
		} else if (getStatus() == Status.CHOOSING_PLAYERS) {
			setStatus(Status.INGAME);
			for (ArenaPlayer ap : getArenaList())
				if (ap.getBase() != null && ap.getBase().isOnline())
					TagAPI.refreshPlayer(ap.getBase());
			wallRemoveScheduler = new WallRemoveScheduler(getArena()
					.getLocationZone(ArenaProperty.PARTITION_WALL_RED),
					getArena().getLocationZone(
							ArenaProperty.PARTITION_WALL_BLUE), this,
					getArena());
			getLobby()
					.getMinigames()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(getLobby().getMinigames(),
							wallRemoveScheduler,
							getArena().getInt(ArenaProperty.PARTITION_TIMER));
			for (ArenaPlayer ap : getArenaList()) {
				ap.setLives(3);
				onSpawn(ap);
			}
		} else if (getStatus() == Status.INGAME) {
			for (ArenaPlayer ap : getArenaList()) {
				ap.setLives(3);
				onSpawn(ap);
			}
		}
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(20);
		if (getStatus() == Status.INGAME
				&& ap.getTeam() == Team.NONE.getTeamId()) {
			ap.getBase().teleport(
					getArena().getLobbyLocation().getRandomFreeLocation());
			message(ap.getBase(), "You need to wait till next game.");
			return;
		} else if (getStatus() == Status.INGAME && ap.getLivesLeft() <= 0) {
			ap.getBase().teleport(
					getArena().getLobbyLocation().getRandomFreeLocation());
			message(ap.getBase(), "Looks like you've used all your lives up.");
			return;
		} else if (getStatus() == Status.CHOOSING_PLAYERS
				|| getStatus() == Status.LOBBY) {
			ap.getBase().teleport(
					getArena().getLobbyLocation().getRandomFreeLocation());
			return;
		}
		super.onSpawn(ap);
	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		if (getArena().getArenaProperties().containsKey(
				ArenaProperty.SCORE_PER_KILL))
			killer.incrementScore(getArena().getInt(
					ArenaProperty.SCORE_PER_KILL));
		else
			killer.incrementScore(150);
		killer.incrementKills();
		died.resetKillStreak();
		died.died();
		if (died.getLivesLeft() > 0)
			died.setLives(died.getLivesLeft() - 1);
		sendScore(killer, false);
		sendScore(died, true);
		super.onKill(killer, died);
		calculateScores();
		if (getWinningTeam() != Team.NONE.getTeamId())
			getLobby().exitGame();
	}

	@Override
	public void onDeath(ArenaPlayer died) {
		if (died.getLivesLeft() > 0)
			died.setLives(died.getLivesLeft() - 1);
		calculateScores();
		if (getWinningTeam() != Team.NONE.getTeamId())
			getLobby().exitGame();
	}

	private void calculateScores() {
		int rlives = 0, blives = 0;
		for (ArenaPlayer ap : super.getArenaList())
			if (ap.getTeam() == Team.RED.getTeamId())
				rlives += ap.getLivesLeft();
			else if (ap.getTeam() == Team.BLUE.getTeamId())
				blives += ap.getLivesLeft();
		if (rlives == 0)
			setWinner(Team.BLUE.getTeamId());
		else if (blives == 0)
			setWinner(Team.RED.getTeamId());
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
	public EKit getDefaultKit() {
		return EKit.REDVSBLUE;
	}

	@Override
	public void onFinish() {
		onWin();
		super.onFinish();
		rollback.rollBack();
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
							.incrementPlayerLargeLoot(ap.getName());
					mg.setLargeLoot(mg.getLargeLoot() + 1);
					message(ap.getBase(),
							"You've been awarded a large loot chest for winning.");
				} else if (ap.getTeam() == Team.BLUE.getTeamId()) {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerMedLoot(ap.getName());
					mg.setMedLoot(mg.getMedLoot() + 1);
					message(ap.getBase(),
							"You've been awarded a medium loot chest for trying.");
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
							.incrementPlayerLargeLoot(ap.getName());
					message(ap.getBase(),
							"You've been awarded a large loot chest for winning.");
					mg.setLargeLoot(mg.getLargeLoot() + 1);
				} else if (ap.getTeam() == Team.RED.getTeamId()) {
					getLobby().getMinigames().getManagement()
							.getScoreManagement()
							.incrementPlayerMedLoot(ap.getName());
					mg.setMedLoot(mg.getMedLoot() + 1);
					message(ap.getBase(),
							"You've been awarded a medium loot chest for trying.");
				}
			}
		} else
			messageAll("Both teams have drawn!");
		super.onWin();
	}

	private void sendScore(ArenaPlayer ap, boolean lives) {
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		if (lives)
			ap.getBase().sendMessage(
					ChatColor.DARK_PURPLE + "Lives left: " + ChatColor.AQUA
							+ ap.getLivesLeft());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Score: " + ChatColor.AQUA
						+ ap.getScore());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Kills: " + ChatColor.AQUA
						+ ap.getKills());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	@Override
	public void signClicked(Sign sign, ArenaPlayer ap) {
		if (getPlayer(sign.getLine(2)) == null)
			return;
		playerChosen(sign.getLine(2), ap);
		rvbPlayerPicker.switchSign();
	}

	public void playerChosen(String line, ArenaPlayer ap) {
		getPlayer(line).setTeam(ap.getTeam());
	}

	@Override
	public boolean blockBreak(Block block) {
		if (block.getType() == Material.GLOWSTONE)
			return false;
		rollback.blockRemove(block);
		return true;
	}

	@Override
	public boolean blockBreak(ArenaPlayer ap, Block block) {
		if (!getArena().getLocationZone(ArenaProperty.ARENA).isInZone(
				block.getLocation())
				|| (getArena()
						.getLocationZone(ArenaProperty.PARTITION_WALL_RED)
						.isInZone(block.getLocation()) || getArena()
						.getLocationZone(ArenaProperty.PARTITION_WALL_BLUE)
						.isInZone(block.getLocation())))
			return false;
		if (block.getType() == Material.GLOWSTONE)
			return false;
		if ((ap.getTeam() == Team.RED.getTeamId() && !getArena()
				.getLocationZone(ArenaProperty.RED_TEAM_SIDE).isInZone(
						block.getLocation()))
				|| (ap.getTeam() == Team.BLUE.getTeamId() && !getArena()
						.getLocationZone(ArenaProperty.BLUE_TEAM_SIDE)
						.isInZone(block.getLocation()))) {
			message(ap.getBase(), "You can only do this in your area.");
			return false;
		}
		rollback.blockRemove(block);
		return true;
	}

	@Override
	public boolean onItemClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return true;
	}

	@Override
	public boolean blockPlace(ArenaPlayer ap, Block block) {
		if (block.getType() == Material.GLOWSTONE)
			return false;
		if (block.getType() == Material.SPONGE) {
			rollback.blockPlaced(block);
			return true;
		}
		if (!getArena().getLocationZone(ArenaProperty.ARENA).isInZone(
				block.getLocation())
				|| (getArena()
						.getLocationZone(ArenaProperty.PARTITION_WALL_RED)
						.isInZone(block.getLocation()) || getArena()
						.getLocationZone(ArenaProperty.PARTITION_WALL_BLUE)
						.isInZone(block.getLocation())))
			return false;
		if ((ap.getTeam() == Team.RED.getTeamId() && !getArena()
				.getLocationZone(ArenaProperty.RED_TEAM_SIDE).isInZone(
						block.getLocation()))
				|| (ap.getTeam() == Team.BLUE.getTeamId() && !getArena()
						.getLocationZone(ArenaProperty.BLUE_TEAM_SIDE)
						.isInZone(block.getLocation()))) {
			message(ap.getBase(), "You can only do this in your area.");
			return false;
		}
		rollback.blockPlaced(block);
		return true;
	}

	@Override
	public boolean blockPlace(Block block) {
		rollback.blockPlaced(block);
		return true;
	}

	@Override
	public boolean chestInteraction(InventoryHolder inventoryHolder) {
		rollback.addInventory(inventoryHolder);
		return true;
	}

	@Override
	public boolean redstoneEvent(Block block) {
		if (getStatus() == Status.INGAME
				&& (this.wallRemoveScheduler != null && wallRemoveScheduler
						.isRemoved())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean entityExplode(Block block) {
		if (block.getType() == Material.GLOWSTONE
				|| getStatus() != Status.INGAME
				|| !wallRemoveScheduler.isRemoved())
			return false;
		rollback.blockRemove(block);
		return true;
	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		// Pass through & ignore
		return false;
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
		message(player, "Sorry you cannot choose your kits in this game.");
	}
}
