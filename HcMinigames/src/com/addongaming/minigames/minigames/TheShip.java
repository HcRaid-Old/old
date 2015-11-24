package com.addongaming.minigames.minigames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.kitteh.tag.TagAPI;

import com.addongaming.hcessentials.data.Position;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.ArenaProperty;
import com.addongaming.minigames.management.arena.GameMode;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.management.arena.TheShipArena;
import com.addongaming.minigames.management.chest.ChestFiller;
import com.addongaming.minigames.management.kits.EKit;
import com.addongaming.minigames.management.scheduling.QuarryUpdater;
import com.addongaming.minigames.management.scheduling.ShipTimer;
import com.addongaming.minigames.management.weapon.Weapon;
import com.addongaming.minigames.management.weapon.Weapons;
import com.addongaming.minigames.minigames.ship.Needs;
import com.addongaming.minigames.minigames.ship.ShipPlayer;

public class TheShip extends ArenaGame {
	private TheShipArena arena;
	private final Position[] positions;
	private final HashMap<Position, Needs> needSignMap = new HashMap<Position, Needs>();
	private ShipTimer shipTimer;
	private QuarryUpdater quarryUpdater;
	private HashMap<Weapons, Integer> origWeaponsMap = new HashMap<Weapons, Integer>();
	private HashMap<Weapons, Integer> killedBy = new HashMap<Weapons, Integer>();
	private HashMap<Weapons, Integer> updatedWeaponsMap = new HashMap<Weapons, Integer>();
	private int round = 0;

	public TheShip(TheShipArena arena, Lobby lobby) {
		super(arena, lobby, false);
		for (Weapons weapons : new Weapons[] { Weapons.CLAYMORE,
				Weapons.WRENCH, Weapons.KATANA, Weapons.FRYING_PAN,
				Weapons.PIPE, Weapons.CROUQET_MALLET, Weapons.KITCHEN_KNIFE,
				Weapons.POTS, Weapons.SPADE, Weapons.SHANK,
				Weapons.BASEBALL_BAT, Weapons.TENNIS_RACKET, Weapons.REVOLVER,
				Weapons.WINCHESTER, Weapons.BLUNDERBUSS, Weapons.TOMMY_GUN }) {
			int points = (new Random().nextInt(1200 - 600) + 600);
			origWeaponsMap.put(weapons, points);
			killedBy.put(weapons, 0);
			points += (((points / 100) * 9) * 1);
			points -= (points / 100) * (11 * (killedBy.get(weapons) * 1));
			if (100 > points)
				points = 100;
			else if (6000 < points)
				points = 6000;
			updatedWeaponsMap.put(weapons, points);
		}
		for (MinigameUser user : lobby.getLobby())
			arenaList.add(new ShipPlayer(user));
		this.arena = arena;
		this.positions = arena.getPositions();
		shipTimer = new ShipTimer(this);
		quarryUpdater = new QuarryUpdater(this);
		getLobby().getMinigames().getManagement().getSchedulerManagement()
				.runScheduler(this, shipTimer, 20l, 20l);
		getLobby().getMinigames().getManagement().getSchedulerManagement()
				.runScheduler(this, quarryUpdater, 20 * 60l, 20 * 15l);
		cf = getLobby().getMinigames().getManagement()
				.getChestFillingManagement().getChestFiller(GameMode.THE_SHIP);
		arena.putProperty(ArenaProperty.FRIENDLY_FIRE_ENABLED, true);
		arena.putProperty(ArenaProperty.INVENTORY_CLICK, true);
		arena.putProperty(ArenaProperty.INVENTORY_OPEN, true);
		arena.putProperty(ArenaProperty.ARMOUR_REMOVABLE, true);
		arena.putProperty(ArenaProperty.PUNCH_DAMAGE, false);
		arena.putProperty(ArenaProperty.ITEM_DROP, true);
		setPrefix(ChatColor.GOLD + "[" + ChatColor.AQUA + "The Ship"
				+ ChatColor.GOLD + "] " + ChatColor.GREEN);
		for (ArenaPlayer ap : getArenaList()) {
			ShipPlayer sp = (ShipPlayer) ap;
			setupNewPlayerName(sp);
			onSpawn(sp);
		}
		for (ArenaPlayer ap : getArenaList()) {
			if (ap.isValid())
				TagAPI.refreshPlayer(ap.getBase());
		}
	}

	ChestFiller cf;
	// Thank Bomie >.>
	private String[] firstNames = { "Abb", "Abe", "Ace", "Acy", "Ada", "Add",
			"Aja", "Ala", "Alf", "Ali", "Ama", "Ami", "Amy", "Ana", "Ann",
			"Ara", "Ari", "Art", "Asa", "Ava", "Bea", "Bee", "Ben", "Bev",
			"Bob", "Bud", "Cal", "Cam", "Cap", "Cas", "Che", "Con", "Coy",
			"Dan", "Dax", "Deb", "Dee", "Del", "Doc", "Don", "Dot", "Dow",
			"Ean", "Ebb", "Eda", "Edd", "Edw", "Ela", "Eli", "Ell", "Ely",
			"Ema", "Ena", "Era", "Eva", "Eve", "Exa", "Fae", "Fay", "Fed",
			"Flo", "Foy", "Gee", "Geo", "Gia", "Gil", "Gus", "Guy", "Hal",
			"Ham", "Hoy", "Huy", "Ian", "Ica", "Icy", "Ida", "Ike", "Ila",
			"Ilo", "Ima", "Imo", "Ina", "Ira", "Irl", "Isa", "Iva", "Ivy",
			"Iza", "Jan", "Jax", "Jay", "Jeb", "Jed", "Jep", "Jim", "Job",
			"Joe", "Joi", "Jon", "Joy", "Kai", "Kay", "Kem", "Ken", "Kia",
			"Kim", "Kip", "Kit", "Kya", "Lea", "Lee", "Lem", "Len", "Leo",
			"Les", "Lew", "Lex", "Lia", "Lim", "Liz", "Lon", "Lou", "Loy",
			"Luc", "Lue", "Lum", "Luz", "Lyn", "Mac", "Mae", "Mai", "Mal",
			"Mat", "Max", "May", "Meg", "Mel", "Mia", "Moe", "Mya", "Nan",
			"Nat", "Ned", "Nia", "Nim", "Noe", "Nya", "Obe", "Oda", "Ola",
			"Ole", "Oma", "Ona", "Ora", "Osa", "Ota", "Ott", "Ova", "Pam",
			"Pat", "Rae", "Ras", "Ray", "Red", "Rex", "Rey", "Rob", "Rod",
			"Roe", "Ron", "Roy", "Sal", "Sam", "Sid", "Sie", "Sim", "Sky",
			"Sol", "Son", "Sue", "Tab", "Tad", "Tai", "Taj", "Tal", "Tea",
			"Ted", "Tex", "Tia", "Tim", "Tod", "Tom", "Toy", "Tre", "Tye",
			"Ula", "Una", "Ura", "Val", "Van", "Vic", "Von", "Wes", "Yee",
			"Zeb", "Zed", "Zoa", "Zoe" };
	private String[] surNames = { "Patrick", "Abraham", "Smithers", "Turner",
			"Abbott", "Tillman", "Tidwell", "Balshaw", "Barsby", "Dauncey",
			"Dickens", "Ostler", "Overson", "Jessop", "Jowett", "Quirke",
			"Quinton", "Yardley", "Youlden", "Harward", "Haldane", "Armsden",
			"Caygill", "Chance", "Kellett", "Knight", "Marland", "Mallory",
			"Nuttall", "Noakes", "Veevers", "Verney", "Savard", "Salter",
			"Sansome", "Perrins", "Paynter", "Palmer", "Earley", "Exton",
			"Feeney", "Forgham", "Ingleby", "Innalls" };

	private void setupNewPlayerName(ShipPlayer arenaPlayer) {
		String name = firstNames[new Random().nextInt(firstNames.length)] + " "
				+ surNames[new Random().nextInt(surNames.length)];
		arenaPlayer.setCharName(name);
		if (arenaPlayer.isValid())
			message(arenaPlayer.getBase(), "You're name is now " + name + ".");
	}

	private void removePlayerName(String name) {
		for (ArenaPlayer ap : getArenaList()) {
			ShipPlayer sp = (ShipPlayer) ap;
			if (sp.hasFoundPlayer(name))
				sp.removeFoundPlayer(name);
		}
	}

	@Override
	public void onStart() {
		super.setStatus(Status.INGAME);
		HashMap<Position, Integer> chestMap = arena.getChestMap();
		// Spawn NPC's
		if (chestMap != null)
			for (Position pos : chestMap.keySet()) {
				Block block = pos.getLoc().getBlock();
				if (block.getType() == Material.CHEST
						&& block.getState() != null
						&& block.getState() instanceof Chest) {
					cf.fillChest((Chest) block.getState(), chestMap.get(pos));
				}
			}
	}

	@Override
	public EKit getDefaultKit() {
		return EKit.THE_SHIP;
	}

	@Override
	public void onSpawn(ArenaPlayer ap) {
		ap.getBase().setHealth(20);
		for (PotionEffect pe : ap.getBase().getActivePotionEffects())
			ap.getBase().removePotionEffect(pe.getType());
		ap.getBase().setFoodLevel(19);
		ap.getBase().teleport(
				arena.getTeamSpawnMap().get(Team.NONE.getTeamId())
						.getRandomLocation());
		ap.getBase().updateInventory();

	}

	@Override
	public void removePlayer(String user) {
		super.removePlayer(user);
		checkEnd();
	}

	@Override
	public void onKill(ArenaPlayer killer, ArenaPlayer died) {
		killer.incrementKills();
		died.resetKillStreak();
		died.decrementLives();
		died.died();
		if (killer instanceof ShipPlayer && died instanceof ShipPlayer) {
			ShipPlayer shipKiller = (ShipPlayer) killer;
			ShipPlayer shipDied = (ShipPlayer) died;
			if (shipKiller.getQuarry() == shipDied) {
				ItemStack is = shipKiller.getBase().getItemInHand();
				if (is == null
						|| !HcMinigames.getInstance().getManagement()
								.getWeaponManagement().isWeapon(is))
					return;
				Weapon weap = HcMinigames.getInstance().getManagement()
						.getWeaponManagement().getWeapon(is);
				killedBy.put(weap.getWeapons(),
						killedBy.get(weap.getWeapons()) + 1);
				shipDied.setQuarry(null);
				shipKiller.setQuarry(null);
				removePlayerName(shipDied.getName());
				shipDied.clearFoundPlayers();
				setupNewPlayerName(shipDied);
				TagAPI.refreshPlayer(shipDied.getBase());
				shipKiller.incrementKills();
				shipKiller.incrementBank(updatedWeaponsMap.get(weap
						.getWeapons()));
				if (shipKiller.getBank() >= arena
						.getInt(ArenaProperty.THE_SHIP_WIN)) {
					getLobby().exitGame();
					return;
				}
				message(shipDied.getBase(),
						"You will get a new quarry next round.");
				// TODO Code for kiling the player
				if (shipTimer.getTimeLeft() >= 90 || !shipTimer.hasTimeLeft())
					shipTimer.setTimeLeft(90);
			} else if (shipDied.getQuarry() == shipKiller) {
				// TODO Self defense
				messageAll(shipKiller.getName() + " killed "
						+ shipDied.getName() + " in self-defense");
			} else {
				messageAll(shipKiller.getName() + " killed "
						+ shipDied.getName() + " in cold blood");
				message(shipKiller.getBase(),
						"If you continue to kill in cold-blood you will be kicked.");
				shipKiller.incrementColdbloodedKills();
				if (shipKiller.getColdbloodedKills() >= 6) {
					removePlayer(shipKiller);
					shipKiller.getBase().kickPlayer("Team killing");
				}
				// TODO coldblooded murder
			}
		} else {
			System.out.println("Not ship players");
		}
		sendScore(killer);
		sendScore(died);
		super.onKill(killer, died);
		checkEnd();
	}

	private void checkEnd() {
		if (getArenaList().size() <= 1)
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
		List<ArenaPlayer> winnerList = getArenaList();
		Collections.sort(winnerList, new Comparator<ArenaPlayer>() {

			@Override
			public int compare(ArenaPlayer o1, ArenaPlayer o2) {
				return o1.getKills() - o2.getKills();
			}
		});
		if (!winnerList.isEmpty()) {
			Iterator<ArenaPlayer> iter = winnerList.iterator();
			messageAll(winnerList.get(0).getName() + " won with $"
					+ ((ShipPlayer) winnerList.get(0)).getBank());
			if (iter.hasNext()) {
				ShipPlayer sp = (ShipPlayer) iter.next();
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(sp.getName());
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerLargeLoot(sp.getName());
				mg.setLargeLoot(mg.getLargeLoot() + 1);
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerMedLoot(sp.getName());
				mg.setMedLoot(mg.getMedLoot() + 1);
			}
			if (iter.hasNext()) {
				ShipPlayer sp = (ShipPlayer) iter.next();
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(sp.getName());
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerMedLoot(sp.getName());
				mg.setMedLoot(mg.getMedLoot() + 1);
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerLargeLoot(sp.getName());
				mg.setLargeLoot(mg.getLargeLoot() + 1);
			}
			while (iter.hasNext()) {
				ShipPlayer sp = (ShipPlayer) iter.next();
				MinigameUser mg = getLobby().getMinigames().getHub()
						.getMinigameUser(sp.getName());
				getLobby().getMinigames().getManagement().getScoreManagement()
						.incrementPlayerMedLoot(sp.getName());
				mg.setMedLoot(mg.getMedLoot() + 1);

			}
		}

		super.onWin();
	}

	private void sendScore(ArenaPlayer ap) {
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Quarry kills: " + ChatColor.AQUA
						+ ap.getKills());
		ap.getBase().sendMessage(
				ChatColor.DARK_PURPLE + "Bank: " + ChatColor.AQUA + "$"
						+ ((ShipPlayer) ap).getBank());
		ap.getBase().sendMessage(ChatColor.RED + "---------------------------");

	}

	@Override
	public boolean onGUIClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		return false;
	}

	@Override
	public String getNameChange(ArenaPlayer tagChange, ArenaPlayer viewer) {
		// TODO Set it to be their unknown & known names depending
		ShipPlayer shipTag = (ShipPlayer) tagChange;
		ShipPlayer shipView = (ShipPlayer) viewer;
		if (shipView.hasFoundPlayer(tagChange.getName())) {
			if (shipView.getQuarry() == shipTag) {
				return ChatColor.RED + shipTag.getCharName();
			}
			return ChatColor.GREEN + shipTag.getCharName();
		} else
			return tagChange.getName();
	}

	private boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onItemClick(ArenaPlayer player, Inventory inventory,
			ItemStack cursor) {
		for (int i = 0; i < inventory.getContents().length; i++) {
			ItemStack is = inventory.getItem(i);
			if (is != null)
				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
					if (HcMinigames.getInstance().getManagement()
							.getWeaponManagement().isWeapon(is)) {
						ItemMeta im = is.getItemMeta();
						Weapon weapon = HcMinigames.getInstance()
								.getManagement().getWeaponManagement()
								.getWeapon(is);
						if (!im.hasLore())
							continue;
						ArrayList<String> lore = new ArrayList<String>();
						lore.addAll(im.getLore());
						String eol = im.getLore().get(im.getLore().size() - 1);
						if (isInteger(eol.substring(1))) {
							if (Integer.parseInt(eol.substring(1)) == updatedWeaponsMap
									.get(weapon.getWeapons()))
								continue;
							lore.remove(lore.size() - 1);
						} else
							lore.add("");
						lore.add("$"
								+ updatedWeaponsMap.get(weapon.getWeapons()));
						im.setLore(lore);
						is.setItemMeta(im);
						inventory.setItem(i, is);
					}
				}
		}
		return true;
	}

	@Override
	public int onItemPickup(Item item, ArenaPlayer ap) {
		return 0;
	}

	@Override
	public boolean chestInteraction(InventoryHolder inventoryHolder) {
		Inventory inventory = inventoryHolder.getInventory();
		for (int i = 0; i < inventory.getContents().length; i++) {
			ItemStack is = inventory.getItem(i);
			if (is != null)
				if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
					if (HcMinigames.getInstance().getManagement()
							.getWeaponManagement().isWeapon(is)) {
						ItemMeta im = is.getItemMeta();
						Weapon weapon = HcMinigames.getInstance()
								.getManagement().getWeaponManagement()
								.getWeapon(is);
						if (!im.hasLore())
							continue;
						ArrayList<String> lore = new ArrayList<String>();
						lore.addAll(im.getLore());
						String eol = im.getLore().get(im.getLore().size() - 1);
						if (isInteger(eol.substring(1))) {
							if (Integer.parseInt(eol.substring(1)) == updatedWeaponsMap
									.get(weapon.getWeapons()))
								continue;
							lore.remove(lore.size() - 1);
						} else
							lore.add("");
						lore.add("$"
								+ updatedWeaponsMap.get(weapon.getWeapons()));
						im.setLore(lore);
						is.setItemMeta(im);
						inventory.setItem(i, is);
					}
				}
		}
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

	@Override
	public void updateArenaPlayer(MinigameUser user) {
		ShipPlayer toAdd = null;
		for (Iterator<ArenaPlayer> iter = arenaList.iterator(); iter.hasNext();) {
			ArenaPlayer ap = iter.next();
			if (user.getBase().getName()
					.equalsIgnoreCase(ap.getBase().getName())) {
				toAdd = (ShipPlayer) ap;
				iter.remove();
			}
		}
		if (toAdd == null)
			toAdd = new ShipPlayer(user);
		else
			toAdd.setBase(user.getBase());
		arenaList.add(toAdd);
		setupNewPlayerName(toAdd);
		preSpawn(toAdd);
		onSpawn(toAdd);
	}

	@Override
	public void swapSide() {
		round++;
		if (round >= 15 || arenaList.size() <= 3) {
			getLobby().exitGame();
			return;
		}
		organiseQuarries();
		HashMap<Position, Integer> chestMap = arena.getChestMap();
		// Spawn NPC's
		if (chestMap != null)
			for (Position pos : chestMap.keySet()) {
				Block block = pos.getLoc().getBlock();
				if (block.getType() == Material.CHEST
						&& block.getState() != null
						&& block.getState() instanceof Chest) {
					cf.fillChest((Chest) block.getState(), chestMap.get(pos));
				}
			}
		updatedWeaponsMap.clear();
		for (Weapons weapons : origWeaponsMap.keySet()) {
			int points = origWeaponsMap.get(weapons);
			points += (((points / 100) * 9) * round);
			points -= (points / 100) * (11 * (killedBy.get(weapons) * round));
			if (100 > points)
				points = 100;
			else if (6000 < points)
				points = 6000;
			updatedWeaponsMap.put(weapons, points);
		}
		for (ArenaPlayer ap : getArenaList()) {
			for (int i = 0; i < ap.getBase().getInventory().getContents().length; i++) {
				ItemStack is = ap.getBase().getInventory().getItem(i);
				if (is != null)
					if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
						if (HcMinigames.getInstance().getManagement()
								.getWeaponManagement().isWeapon(is)) {
							ItemMeta im = is.getItemMeta();
							Weapon weapon = HcMinigames.getInstance()
									.getManagement().getWeaponManagement()
									.getWeapon(is);
							if (!im.hasLore())
								continue;
							ArrayList<String> lore = new ArrayList<String>();
							lore.addAll(im.getLore());
							String eol = im.getLore().get(
									im.getLore().size() - 1);
							if (isInteger(eol.substring(1))) {
								if (Integer.parseInt(eol.substring(1)) == updatedWeaponsMap
										.get(weapon.getWeapons()))
									continue;
								lore.remove(lore.size() - 1);
							} else
								lore.add("");
							lore.add("$"
									+ updatedWeaponsMap.get(weapon.getWeapons()));
							im.setLore(lore);
							is.setItemMeta(im);
							ap.getBase().getInventory().setItem(i, is);
							ap.getBase().updateInventory();
						}
					}
			}
		}
	}

	private void organiseQuarries() {
		List<ArenaPlayer> players = new ArrayList<ArenaPlayer>();
		players.addAll(getArenaList());
		for (Iterator<ArenaPlayer> iter = players.iterator(); iter.hasNext();) {
			ShipPlayer ap = (ShipPlayer) iter.next();
			if (!ap.isValid())
				iter.remove();
		}
		Collections.shuffle(players);
		for (int i = 0; i < players.size() - 1; i++) {
			((ShipPlayer) players.get(i)).setQuarry((ShipPlayer) players
					.get(i + 1));
			message(players.get(i).getBase(), "Your new quarry is "
					+ ((ShipPlayer) players.get(i)).getQuarry().getCharName());
		}
		((ShipPlayer) players.get(players.size() - 1))
				.setQuarry((ShipPlayer) players.get(0));
		message(players.get(players.size() - 1).getBase(),
				"Your new quarry is "
						+ ((ShipPlayer) players.get(players.size() - 1))
								.getQuarry().getCharName());
		for (ArenaPlayer ap : getArenaList()) {
			if (ap.isValid())
				TagAPI.refreshPlayer(ap.getBase());
		}
	}

	@Override
	public void onWeaponUse(Weapon weapon, ArenaPlayer attacker,
			ArenaPlayer defender) {
		if (((ShipPlayer) attacker).getQuarry() == ((ShipPlayer) defender)) {
			((ShipPlayer) defender).setLastAttackedBy((ShipPlayer) attacker,
					weapon.getWeapons());
		}
	}

	@Override
	public void playerInteractPlayer(ArenaPlayer player, ArenaPlayer player2) {
		ShipPlayer interacter = (ShipPlayer) player;
		ShipPlayer clickedOn = (ShipPlayer) player2;
		if (!interacter.hasFoundPlayer(clickedOn.getName())) {
			interacter.foundPlayer(clickedOn.getName());
			TagAPI.refreshPlayer(clickedOn.getBase(), interacter.getBase());
		}
	}
}
