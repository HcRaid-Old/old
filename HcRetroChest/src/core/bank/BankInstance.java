package core.bank;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import core.start.Main;

public class BankInstance {
	Bank[] banks;
	int currentChest = 0;
	final Player player;
	public boolean loading = true;
	private boolean switching = true;

	public BankInstance(Player p) {
		player = p;
		ArrayList<Bank> al = new ArrayList<Bank>();
		if (p.hasPermission("HcRaid.Ghast")) {
			al.add(new Bank(0, p));
		}
		if (p.hasPermission("HcRaid.Ender")
				|| p.hasPermission("HcRaid.Enderdragon")) {
			al.add(new Bank(1, p));
		}
		if (p.hasPermission("HcRaid.Hero")) {
			al.add(new Bank(2, p));
		}
		banks = al.toArray(new Bank[al.size()]);
		updateInventory();
		loading = false;
		switching = false;
	}

	public boolean isSwitching() {
		return switching;
	}

	public void setSwitching(boolean newValue) {
		this.switching = newValue;
	}

	public Bank[] getAllBanks() {
		return banks;
	}

	public Bank getCurrentchest() {
		return banks[currentChest];
	}

	public Inventory getCurrentInventory() {
		return banks[currentChest].getInventory();
	}

	public boolean canChange(int changeVar) {
		if (currentChest + changeVar < 0)
			return false;
		if (changeVar > banks.length)
			return false;
		else
			return true;
	}

	public void changeChest(int changeVar) throws IncorrectRankException {
		if (!canChange(changeVar))
			throw new IncorrectRankException();
		else
			currentChest += changeVar;
		updateInventory();
	}

	private void updateInventory() {
		switching = true;
		if (player.getOpenInventory() != null
				&& player.getOpenInventory().getTitle().contains("bank")) {
			player.closeInventory();
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.jp, new Runnable() {

			@Override
			public void run() {
				player.openInventory(banks[currentChest].getInventory());
				switching = false;
			}
		}, 0L);

	}
}
