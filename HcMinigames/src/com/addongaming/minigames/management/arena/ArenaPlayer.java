package com.addongaming.minigames.management.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bukkit.inventory.ItemStack;

import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;
import com.addongaming.minigames.core.HcMinigames;
import com.addongaming.minigames.core.MinigameUser;
import com.addongaming.minigames.management.kits.EKit;

public class ArenaPlayer extends MinigameUser {
	private int kills = 0, score = 0, livesLeft = -1, team = -1,
			killstreak = 0, confirms = 0, denies = 0, flagCaps = 0;
	private EKit kit = null;
	private final Properties properties = new Properties();
	private List<String> killedCache = new ArrayList<String>();

	public void setProperty(String arg0, Object arg1) {
		properties.put(arg0, arg1);
	}

	public void addToKilledCache(String ap) {
		killedCache.add(ap);
		if (killedCache.size() >= 10)
			killedCache.remove(0);
	}

	public List<String> getKilledCache() {
		return killedCache;
	}

	public boolean hasProperty(String string) {
		return properties.containsKey(string);
	}

	public Object getProperty(String string) {
		return properties.get(string);
	}

	public ArenaPlayer(MinigameUser user) {
		super(user.getBase());
	}

	public int getTeam() {
		return team;
	}

	public int getKills() {
		return kills;
	}

	public int getScore() {
		return score;
	}

	public int getLivesLeft() {
		return livesLeft;
	}

	public void decrementLives() {
		livesLeft--;
	}

	public void incrementConfirms() {
		confirms++;
	}

	public void incrementDenies() {
		denies++;
	}

	public int getDenies() {
		return denies;
	}

	public int getConfirms() {
		return confirms;
	}

	public void incrementScore(int amount) {
		score += amount;
	}

	public void decrementScore(int amount) {
		score -= amount;
		if (score < 0)
			score = 0;
	}

	public void setLives(int amount) {
		livesLeft = amount;
	}

	public void setTeam(int teamId) {
		this.team = teamId;
	}

	public void resetKillStreak() {
		killstreak = 0;
	}

	public int getKillStreak() {
		return killstreak;
	}

	public void died() {
		HcMinigames.getInstance().getManagement().getScoreManagement()
				.incrementPlayerDeaths(getName());
	}

	public void incrementKills() {
		killstreak++;
		kills++;
		HcMinigames.getInstance().getManagement().getScoreManagement()
				.incrementPlayerKills(getName());
	}

	public void giveItem(ItemStack item) {
		getBase().getInventory().addItem(item);
	}

	public EKit getKit() {
		return kit;
	}

	public void setKit(EKit kit) {
		this.kit = kit;
	}

	public void setArmour(ItemStack[] array) {
		for (ItemStack is : array) {
			if (is == null)
				continue;
			ItemType it = Enchantable.getItemType(is.getType());
			switch (it) {
			case BOOTS:
				getBase().getInventory().setBoots(is);
				break;
			case CHESTPLATE:
				getBase().getInventory().setChestplate(is);
				break;
			case HELMET:
				getBase().getInventory().setHelmet(is);
				break;
			case LEGGINGS:
				getBase().getInventory().setLeggings(is);
				break;
			case AXE:
			case BOW:
			case FISHINGROD:
			case HOE:
			case PICKAXE:
			case SPADE:
			case SWORD:
			case UNDEFINED:
			default:
				continue;
			}
		}
	}

	public int getFlagCaps() {
		return flagCaps;
	}

	public void incrementFlagCaps() {
		this.flagCaps++;
	}

}
