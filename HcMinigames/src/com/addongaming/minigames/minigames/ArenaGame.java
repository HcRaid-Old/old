package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.flag.Flag;
import com.addongaming.minigames.management.killstreak.ItemKillStreak;
import com.addongaming.minigames.management.killstreak.KillStreak;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.weapon.Weapon;

public abstract class ArenaGame {
	public enum Status {
		LOBBY, INGAME, FINISHED, CHOOSING_PLAYERS
	}

	protected List<ArenaPlayer> arenaList = new ArrayList<ArenaPlayer>();
	private Lobby lobby;
	private int winner;
	private HcMinigames minigames;
	private Arena arena;
	private Status currentStatus = Status.LOBBY;
	private String prefix = ChatColor.GOLD + "[" + ChatColor.AQUA + "AG"
			+ ChatColor.GOLD + "] " + ChatColor.GREEN;

	public ArenaGame(Arena arena, Lobby lobby) {
		this.arena = arena;
		this.lobby = lobby;
		minigames = lobby.getMinigames();
		arena.connectGame(this);
		for (MinigameUser mg : lobby.getLobby())
			arenaList.add(new ArenaPlayer(mg));
	}

	public ArenaGame(Arena arena, Lobby lobby, boolean setupUsers) {
		this.arena = arena;
		this.lobby = lobby;
		minigames = lobby.getMinigames();
		arena.connectGame(this);
		if (setupUsers)
			for (MinigameUser mg : lobby.getLobby())
				arenaList.add(new ArenaPlayer(mg));
	}

	/**
	 * Gets the lobby that is currently in the arenagame
	 * 
	 * @return Lobby instance of the players in this game
	 */
	public Lobby getLobby() {
		return lobby;
	}

	public void setStatus(Status status) {
		this.currentStatus = status;
	}

	/**
	 * Sets the message prefix - should be done at the beginning of a
	 * specialised game
	 * 
	 * @param prefix
	 *            New prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Called when an arena player is killed by another arena player Gives the
	 * player a killstreak for the gamemode when superclass is called
	 * 
	 * @param killer
	 *            The player that killed
	 * @param died
	 *            The player that died
	 */
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		KillStreak ks = minigames.getManagement().getKillStreakManagement()
				.getGameStreak(arena.getGameMode())
				.getKillStreak(killer.getKillStreak());
		killer.addToKilledCache(died.getName());
		if (ks != null)
			onKillStreak(ks, killer);
	}

	/**
	 * Called when an arena player is damaged by another arena player
	 * 
	 * @param damager
	 *            The player that damaged
	 * @param hurt
	 *            The player that got hurt
	 */
	public boolean onDamage(ArenaPlayer damager, ArenaPlayer hurt) {
		return false;
	}

	/**
	 * Called when a arenaplayer consumes an item
	 * 
	 * @param is
	 *            ItemStack consumed
	 * @param consume
	 *            The player that consumed the item
	 */
	public void onConsume(ItemStack is, ArenaPlayer consume) {

	}

	/**
	 * Called when a killstreak is being applied to a player. <br/>
	 * Override this if you want it to be executed differently
	 * 
	 * @param killStreak
	 *            Killstreak being applied
	 * @param arenaPlayer
	 *            Arena Player receiving the killstreak.
	 */
	public void onKillStreak(KillStreak killStreak, ArenaPlayer arenaPlayer) {
		if (killStreak instanceof ItemKillStreak) {
			arenaPlayer
					.getBase()
					.getInventory()
					.addItem(
							((ItemKillStreak) (killStreak)).getItemStack()
									.getBukkitItemStack());
			arenaPlayer.getBase().sendMessage(
					prefix + "Killstreak for " + killStreak.getNeededKills()
							+ " kills, use it wisely!");
			arenaPlayer.getBase().updateInventory();
		}
	}

	public List<ArenaPlayer> getArenaList() {
		return arenaList;
	}

	/**
	 * When a player gets a kill (any entity) it will call this DO NOT USE FOR
	 * TIME BEING
	 * 
	 * @param killer
	 *            The player that killed
	 */
	@Deprecated
	public void onKill(ArenaPlayer killer) {

	}

	/**
	 * When a player dies this method will be called DO NOT USE FOR TIME BEING
	 * 
	 * @param died
	 *            Player that died
	 */
	@Deprecated
	public void onDeath(ArenaPlayer died) {

	}

	/**
	 * Teleports the player to their spawn location if applicable
	 * 
	 * @param ap
	 *            ArenaPlayer which is about to spawn
	 */
	public void onSpawn(ArenaPlayer ap) {
		if (currentStatus == Status.LOBBY
				|| currentStatus == Status.CHOOSING_PLAYERS) {
			ap.getBase().teleport(
					arena.getLobbyLocation().getRandomFreeLocation());
		} else if (currentStatus == Status.INGAME) {
			ap.getBase().teleport(
					arena.getTeamSpawnMap().get(ap.getTeam())
							.getRandomLocation());
			equipKits(ap);
			ap.getBase().updateInventory();
		}
	}

	/**
	 * Called after onSpawn() and is used to set players equipment
	 * (armour/items) <strong>Super </strong> class equips players inventories
	 * and armour by Kit Management and should be called before adding/removing
	 * items
	 * 
	 * @param ap
	 *            ArenaPlayer that's respawning
	 */
	public void equipKits(ArenaPlayer ap) {
		EKit ek = ap.getKit();
		if (ap.getKit() == null)
			ek = getDefaultKit();
		ap.setArmour(minigames.getManagement().getKitManagement()
				.getArmourContents(ek));
		ap.getBase()
				.getInventory()
				.setContents(
						minigames.getManagement().getKitManagement()
								.getInvenContents(ek));
		for (PotionEffect pe : minigames.getManagement().getKitManagement()
				.getPotionEffects(ek))
			ap.getBase().addPotionEffect(pe);
	}

	/**
	 * Called when the game starts and moves from lobby->ingame mode. Setting
	 * lives etc should be done here.
	 */
	public void onStart() {

	}

	/**
	 * Sets the winning team
	 * 
	 * @param team
	 *            ID of the team that has won. Use Team class for constant value
	 */
	public void setWinner(int team) {
		this.winner = team;
	}

	/**
	 * Method to fully indicate that the minigame has now been completed and all
	 * schedulers should be stopped as well as player teleportation.
	 */
	public final void finished() {
		getLobby().getMinigames().getManagement().getScoreManagement()
				.incrementArena(lobby.getGameMode(), arena.getArenaType());
		arena.cancelGame();
		arenaList.clear();
		currentStatus = Status.FINISHED;
	}

	/**
	 * This method is called when the minigame is called to finish whether or
	 * not a team won, or if something else has forced it to end. Cleaning up
	 * should be done here
	 */
	public void onFinish() {
		if (currentStatus == Status.FINISHED)
			return;
		finished();
	}

	public final void onInteract(Player player, ItemStack is) {
		onInteract(player, is, null);
	}

	public final void onInteract(Player player, Block block) {
		onInteract(player, null, block);
	}

	public void onInteract(Player player, ItemStack is, Block block) {

	}

	/**
	 * This method will be called before onFinish to give out rewards
	 */
	public void onWin() {
		for (ArenaPlayer ap : arenaList)
			if (ap.getScore() > 0)
				minigames
						.getManagement()
						.getScoreManagement()
						.incrementOverallPointsEarnt(ap.getName(),
								ap.getScore());
		ArenaPlayer[] winners = getWinningPlayers();
		if (winners.length == 0)
			return;
		for (ArenaPlayer list : getArenaList()) {
			boolean found = false;
			for (ArenaPlayer check : winners)
				if (list == check)
					found = true;
			if (found)
				minigames.getManagement().getScoreManagement()
						.incrementPlayerWin(list.getName());
			else
				minigames.getManagement().getScoreManagement()
						.incrementPlayerLoss(list.getName());
		}
	}

	/**
	 * Gets the winning team, can be converted to a colour using the Team object
	 * 
	 * @return Winning team id
	 */
	public int getWinningTeam() {
		return winner;
	}

	/**
	 * Messages all players in the game
	 * 
	 * @param message
	 *            Message to send to all players
	 */
	public void messageAll(String message) {
		for (ArenaPlayer ap : arenaList)
			if (ap.isValid())
				ap.getBase().sendMessage(prefix + message);
	}

	public void message(Player player, String string) {
		player.sendMessage(prefix + string);
	}

	/**
	 * Messages all players in a team
	 * 
	 * @param team
	 *            Team id to message, use Team for team colour reference
	 * @param message
	 *            Message to send to the team
	 */
	public void messageAll(int team, String message) {
		for (ArenaPlayer ap : arenaList)
			if (ap.getTeam() == team)
				if (ap.isValid())
					ap.getBase().sendMessage(prefix + message);
	}

	public Status getStatus() {
		return currentStatus;
	}

	/**
	 * Removes a User from the game, handling for teleporting should be done by
	 * the calling method
	 * 
	 * @param user
	 *            User to remove from the game
	 */
	public final void removePlayer(MinigameUser user) {
		removePlayer(user.getName());
	}

	/**
	 * Gets the superclass (this) so that it can force some methods
	 * 
	 * @return Superclass of the game
	 */
	public final ArenaGame getSuperGame() {
		return this;
	}

	/**
	 * This updates a players base if they happen to re-log before being kicked
	 * 
	 * @param user
	 *            MinigameUser to update
	 */
	public void updateArenaPlayer(MinigameUser user) {
		ArenaPlayer toAdd = null;
		for (Iterator<ArenaPlayer> iter = arenaList.iterator(); iter.hasNext();) {
			ArenaPlayer ap = iter.next();
			if (user.getBase().getName()
					.equalsIgnoreCase(ap.getBase().getName())) {
				toAdd = ap;
				iter.remove();
			}
		}
		if (toAdd == null)
			toAdd = new ArenaPlayer(user);
		else
			toAdd.setBase(user.getBase());
		arenaList.add(toAdd);
		preSpawn(toAdd);
		onSpawn(toAdd);
	}

	/**
	 * Called pre-spawn to set-up any last minute things when joining a game
	 * 
	 * @param toAdd
	 *            Player which is being added
	 */
	public void preSpawn(ArenaPlayer toAdd) {

	}

	/**
	 * This checks to see if a user is in the arenagame. Checks using name
	 * 
	 * @param user
	 *            User to check
	 * @return true if playeris in game, false if not
	 */
	public boolean hasPlayer(String user) {
		for (Iterator<ArenaPlayer> iter = arenaList.iterator(); iter.hasNext();) {
			ArenaPlayer ap = iter.next();
			if (user.equalsIgnoreCase(ap.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when a player clicks a sign
	 * 
	 * @param sign
	 *            Sign that was clicked
	 * @param ap
	 *            ArenaPlayer that clicked the sign
	 */
	public void signClicked(Sign sign, ArenaPlayer ap) {
		if (!sign.getLine(0).equalsIgnoreCase(
				ChatColor.stripColor(sign.getLine(0)))
				&& sign.getLine(0).contains("Kit")) {
			EKit kit = EKit.getByName(sign.getLine(2));
			if (kit != null && kit != ap.getKit()) {
				ap.setKit(kit);
				ap.getBase().sendMessage(
						prefix + "Selected kit "
								+ ChatColor.stripColor(sign.getLine(2)));
			} else if (kit != null) {
				ap.getBase().sendMessage(
						prefix + "You already have that kit selected!");
			}
		}
	}

	public final Arena getArena() {
		return arena;
	}

	public ArenaPlayer getPlayer(String name) {
		for (Iterator<ArenaPlayer> iter = arenaList.iterator(); iter.hasNext();) {
			ArenaPlayer ap = iter.next();
			if (name.equalsIgnoreCase(ap.getName())) {
				return ap;
			}
		}
		return null;
	}

	public void stopGame() {
		lobby.exitGame();
	}

	public void removePlayer(String user) {
		for (Iterator<ArenaPlayer> iter = arenaList.iterator(); iter.hasNext();) {
			ArenaPlayer ap = iter.next();
			if (user.equalsIgnoreCase(ap.getName())) {
				iter.remove();
			}
		}
	}

	/**
	 * Called when a block is placed This can/will be used for rollbacks
	 * 
	 * @param block
	 *            Block that has been placed * @return True if it shouldn't be
	 *            cancelled, false if it should
	 */
	public boolean blockPlace(Block block) {
		return false;
	}

	/**
	 * Called when a block is broken This can/will be used for rollbacks
	 * 
	 * @param block
	 *            Block that has been broken @return True if it shouldn't be
	 *            cancelled, false if it should
	 */
	public boolean blockBreak(Block block) {
		return false;
	}

	/**
	 * Called when a chest is successfully interacted with. This will be used
	 * for snap-shotting a chests inventory for rollbacks
	 * 
	 * @param block
	 *            Chest that has been interacted with
	 * @return True if it shouldn't be cancelled, false if it should
	 */
	public boolean chestInteraction(InventoryHolder inventoryHolder) {
		return false;

	}

	public EKit getDefaultKit() {
		return null;
	}

	public List<ArenaPlayer> getByTeam(int teamId) {
		List<ArenaPlayer> playerList = new ArrayList<ArenaPlayer>();
		for (ArenaPlayer ap : this.arenaList)
			if (ap.getTeam() == teamId)
				playerList.add(ap);
		return playerList;
	}

	/**
	 * Called when a block is broken by a playerThis can/will be used for
	 * rollbacks
	 * 
	 * @param block
	 *            Block that has been broken @return True if it shouldn't be
	 *            cancelled, false if it should
	 */
	public boolean blockBreak(ArenaPlayer ap, Block block) {
		return false;
	}

	/**
	 * Final method for calling blockBroken,
	 * 
	 * @param ap
	 *            Arena player that broke the block, null if not applicable
	 * @param block
	 *            Block that was broken
	 * @return true if it succeeded, false if not
	 */
	public final boolean blockBroken(ArenaPlayer ap, Block block) {
		if (ap == null)
			return blockBreak(block);
		else
			return blockBreak(ap, block);
	}

	/**
	 * Called when a block is placed This can/will be used for rollbacks
	 * 
	 * @param ap
	 *            Arena player that placed the block
	 * @param block
	 *            Block that has been placed
	 * @return True if it shouldn't be cancelled, false if it should
	 */
	public boolean blockPlace(ArenaPlayer ap, Block block) {
		return false;
	}

	/**
	 * Final method for calling blockPlaced,
	 * 
	 * @param ap
	 *            Arena player that placed the block, null if not applicable
	 * @param block
	 *            Block that was placed
	 * @return true if it succeeded, false if not
	 */
	public final boolean blockPlaced(ArenaPlayer ap, Block block) {
		if (ap == null)
			return blockPlace(block);
		else
			return blockPlace(ap, block);
	}

	/**
	 * Sets whether or not a redstone signal can be activated
	 * 
	 * @param block
	 *            Block where the restone level is being changed
	 * @return true if it shouldn't be cancelled, false if it should
	 */
	public boolean redstoneEvent(Block block) {
		return false;
	}

	/**
	 * Method for working out whether or not a block should be removed from an
	 * entity exploding Also used for rollback purposes.
	 * 
	 * @param block
	 *            Block that was damaged by the explosion
	 * @return True if the block shouldn't be removed, false if it should
	 */
	public boolean entityExplode(Block block) {
		return false;
	}

	/**
	 * Method for seeing whether or not a player can click this item - or for
	 * inventory GUI's
	 * 
	 * @param player
	 *            ArenaPlayer that clicked the item
	 * @param inventory
	 *            The inventory where this click occurred
	 * @param cursor
	 *            Cursor item of what was clicked
	 * @return True if it successfully happened, false if it needs to be
	 *         cancelled
	 */
	public boolean onItemClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return false;
	}

	/**
	 * Method to be called when a GUI item is clicked. This is 'defined' by the
	 * title of the inventory stripped of colour != orignal name
	 * 
	 * @param player
	 *            ArenaPlayer that clicked the GUI item
	 * @param inventory
	 *            Inventory that was associated with this click
	 * @param cursor
	 *            Item that was clicked
	 * @return True if succeeded, false if not.
	 */
	public abstract boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor);

	public abstract void openKits(Player player);

	/**
	 * Sets a players name tag.
	 * 
	 * @param tagChange
	 *            The players name tag being altered
	 * @param viewer
	 *            The person that will see that name tag
	 * @return null if you do not want to change their tag otherwise a string
	 *         representing what it should be changed to
	 */
	public abstract String getNameChange(ArenaPlayer tagChange,
			ArenaPlayer viewer);

	/**
	 * This method is called when a flag is captured. Here you can end the game
	 * if all flags belong to a single team or manipulate it appropiately.
	 * 
	 * @param flag
	 *            Flag that has been captured
	 */
	public void onFlagCapture(Flag flag) {

	}

	/**
	 * This method should be called when an item is picked up by the player.
	 * 
	 * @param item
	 *            Item that was picked up
	 * @param ap
	 *            ArenaPlayer that picked up the item
	 * @return -2 to cancel event and remove item -1 to cancel event and 0 allow
	 *         the pickup
	 */
	public int onItemPickup(Item item, ArenaPlayer ap) {
		return -1;
	}

	/**
	 * 
	 * @param from
	 *            From location
	 * @param to
	 *            To location
	 * @param player
	 *            Player that moved
	 * @return Successfulness of the move
	 */
	public boolean playerMove(Location from, Location to, Player player) {
		return true;
	}

	public abstract void playerChat(Player player, String message,
			boolean global);

	public abstract ArenaPlayer[] getWinningPlayers();

	public void swapSide() {
	}

	public void onWeaponUse(Weapon weapon, ArenaPlayer attacker,
			ArenaPlayer defender) {

	}

	public void playerInteractPlayer(ArenaPlayer player, ArenaPlayer player2) {

	}
}
