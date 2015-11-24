package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.scheduling.GameLimit;
import com.addongaming.minigames.management.weapon.Weapons;

public class GunGame extends ArenaGame {
	Inventory kitInterfaceNormal, kitInterfacePremium;
	List<ItemStack> weaponList = new ArrayList<ItemStack>();
	private Weapons weapons[] = new Weapons[] { Weapons.GLOCK, Weapons.USP,
			Weapons.P228, Weapons.DEAGLE, Weapons.MAC10, Weapons.TMP,
			Weapons.MP5, Weapons.UMP, Weapons.P90, Weapons.M3, Weapons.XM1014,
			Weapons.GALIL, Weapons.FAMAS, Weapons.AK74, Weapons.M4A1,
			Weapons.AUG, Weapons.SCOUT, Weapons.AWP, Weapons.SAW };
	private ItemStack knife;

	public GunGame(Arena arena, Lobby lobby) {
		super(arena, lobby);
		for (Weapons wep : weapons)
			weaponList.add(getLobby().getMinigames().getManagement()
					.getWeaponManagement().getWeapon(wep).getWeapon());
		// knife =
		// getLobby().getMinigames().getManagement().getWeaponManagement().getWeapon(Weapons.GOLDEN_KNIFE)
		arena.putProperty(ArenaProperty.FRIENDLY_FIRE_ENABLED, true);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "GunGame"
				+ ChatColor.GOLD + "] " + ChatColor.GREEN);
		for (ArenaPlayer ap : getArenaList()) {
			onSpawn(ap);
		}
		super.getLobby().getMinigames().getManagement()
				.getSchedulerManagement()
				.runScheduler(this, new GameLimit(this, 10 * 60, 1), 0l, 20);
	}

	@Override
	public void onStart() {
		setStatus(Status.INGAME);
		for (ArenaPlayer ap : getArenaList())
			onSpawn(ap);
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.GUN_GAME;
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		if (getStatus() == Status.LOBBY)
			ap.getBase().teleport(
					getArena().getLobbyLocation().getRandomFreeLocation());
		else {
			ap.getBase().teleport(
					getArena().getTeamSpawnMap().get(ap.getTeam())
							.getRandomLocation());
			equipKits(ap);
		}
		ap.getBase().updateInventory();

	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		if (getArena().getArenaProperties().containsKey(
				ArenaProperty.SCORE_PER_KILL))
			killer.incrementScore(getArena().getInt(
					ArenaProperty.SCORE_PER_KILL));
		else
			killer.incrementScore(100);
		killer.incrementKills();
		if (killer.getKillStreak() > 0 && killer.getKillStreak() % 10 == 0) {
			MinigameUser mg = getLobby().getMinigames().getHub()
					.getMinigameUser(killer.getName());
			getLobby().getMinigames().getManagement().getScoreManagement()
					.incrementPlayerMedLoot(killer.getName());
			mg.setMedLoot(mg.getMedLoot() + 1);
		}
		died.resetKillStreak();
		died.died();
		if (killer.getKills() >= this.weaponList.size()) {
			getLobby().exitGame();
			return;
		}
		equipKits(killer);
		sendScore(killer);
		sendScore(died);
		super.onKill(killer, died);
	}

	@Override
	public void equipKits(ArenaPlayer ap) {
		super.equipKits(ap);
		ap.getBase().getInventory().addItem(weaponList.get(ap.getKills()));
		ap.getBase().getInventory().addItem(knife);
		ap.getBase().updateInventory();
	}

	@Override
	public void onFinish() {
		onWin();
		super.onFinish();
	}

	@Override
	public void onWin() {
		for (ArenaPlayer ap : getArenaList())
			if (ap.getKills() >= this.weaponList.size())
				winner = ap;
		if (winner != null) {
			MinigameUser mg = getLobby().getMinigames().getHub()
					.getMinigameUser(winner.getName());
			mg.setLargeLoot(mg.getLargeLoot() + 1);
			getLobby().getMinigames().getManagement().getScoreManagement()
					.incrementPlayerLargeLoot(winner.getName());
			messageAll(winner.getName() + " has won the game.");
		}
		super.onWin();
	}

	private ArenaPlayer winner = null;

	private void sendScore(ArenaPlayer ap) {
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Score: " + ChatColor.AQUA
						+ ap.getScore());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Kills: " + ChatColor.AQUA
						+ ap.getKills());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Weapons left: " + ChatColor.AQUA
						+ (weaponList.size() - ap.getKills()));

		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	@Override
	public void signClicked(Sign sign, ArenaPlayer ap) {
		if (!sign.getLine(0).equalsIgnoreCase(
				ChatColor.stripColor(sign.getLine(0)))
				&& sign.getLine(0).contains("Enter")) {
			ap.getBase().sendMessage(ChatColor.DARK_AQUA + "Entering...");
			ap.setProperty("respawn", false);
			onSpawn(ap);
		} else
			super.signClicked(sign, ap);
	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return false;
	}

	@Override
	public String getNameChange(ArenaPlayer tagChange, ArenaPlayer viewer) {
		// Let HcEssentials handle this
		return null;
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
		ArenaPlayer app = null;
		for (ArenaPlayer ap : getArenaList())
			if (ap.getKills() >= weaponList.size())
				app = ap;
		if (app == null)
			return new ArenaPlayer[0];
		else
			return new ArenaPlayer[] { app };
	}

	@Override
	public void removePlayer(String user) {
		ArenaPlayer ap = getPlayer(user);
		if (ap == null)
			return;
		getLobby().getMinigames().getManagement().getScoreManagement()
				.incrementOverallPointsEarnt(ap.getName(), ap.getScore());
		super.removePlayer(user);
		if (getArenaList().size() == 1)
			getLobby().exitGame();
	}

	@Override
	public void openKits(Player player) {
		message(player, "Sorry, you can only do that when you respawn.");
	}
}
