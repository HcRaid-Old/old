package com.addongaming.minigames.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.kits.Kit;
import com.addongaming.minigames.management.scheduling.InventoryOpener;

public class KitPvP extends ArenaGame {
	Inventory kitInterfaceNormal, kitInterfacePremium;

	public KitPvP(Arena arena, Lobby lobby) {
		super(arena, lobby);
		setupInventories();
		arena.putProperty(ArenaProperty.FRIENDLY_FIRE_ENABLED, true);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "Kits"
				+ ChatColor.GOLD + "] " + ChatColor.GREEN);
		onStart();
		for (ArenaPlayer ap : getArenaList()) {
			ap.setProperty("respawn", true);
			onSpawn(ap);
		}
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
		kitInterfaceNormal.addItem(enter);
		kitInterfacePremium.addItem(enter);
		EKit[] eKits = EKit.getByGamemode(GameMode.KITS);
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
	}

	@Override
	public void onStart() {
		super.setStatus(Status.INGAME);
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.WARRIOR;
	}

	@Override
	public void preSpawn(ArenaPlayer ap) {
		if (getStatus() == Status.INGAME) {
			ap.setProperty("respawn", true);
			ap.setLives(-1);
		}
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		if (!ap.hasProperty("respawn")
				|| ((boolean) (ap.getProperty("respawn"))) == true) {
			ap.getBase().teleport(
					getArena().getLobbyLocation().getRandomFreeLocation());
			openKitInterface(ap);
			ap.setProperty("respawn", false);
		} else if (((boolean) (ap.getProperty("respawn"))) == false) {
			ap.getBase().teleport(
					getArena().getTeamSpawnMap().get(ap.getTeam())
							.getRandomLocation());
			equipKits(ap);
			ap.getBase().updateInventory();
		}
	}

	private void openKitInterface(ArenaPlayer ap) {
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
			message(killer.getBase(),
					"You've been awarded a medium lootbox for your killstreak of "
							+ killer.getKillStreak() + ".");
			mg.setMedLoot(mg.getMedLoot() + 1);
		}
		died.resetKillStreak();
		died.died();
		sendScore(killer, true);
		sendScore(died, false);
		super.onKill(killer, died);
		died.setProperty("respawn", true);
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
		// TODO Allow kit changing via GUI as well as entering
		EKit ekit = EKit.getFromReadable(cursor.getItemMeta().getDisplayName());
		if (ekit == null) {
			onSpawn(player);
			return true;
		}
		if (ekit.isPremium()
				&& inventory.getTitle().equalsIgnoreCase(
						kitInterfaceNormal.getTitle())) {
			message(player.getBase(), "You need to have premium for that kit.");
			return true;
		}
		player.setKit(ekit);
		message(player.getBase(), "Selected kit " + ekit.toReadableText());
		onSpawn(player);
		return true;
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
		return new ArenaPlayer[0];
	}

	@Override
	public void removePlayer(String user) {
		ArenaPlayer ap = getPlayer(user);
		if (ap == null)
			return;
		getLobby().getMinigames().getManagement().getScoreManagement()
				.incrementOverallPointsEarnt(ap.getName(), ap.getScore());
		super.removePlayer(user);
	}

	@Override
	public void openKits(Player player) {
		message(player, "Sorry, you can only do that when you respawn.");
	}
}
