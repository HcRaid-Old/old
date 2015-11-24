package core.essentials.perks.farm;

import org.bukkit.entity.Player;

public enum CropAmount {
	grunt("HcRaid.grunt", 40), creeper("HcRaid.creeper", 100), mod(
			"Hcraid.mod", 150), blaze("HcRaid.blaze", 250), ghast(
			"HcRaid.ghast", 500), ender("HcRaid.ender", 750), hero(
			"Hcraid.hero", 1250);
	private int amount;
	private String perm;

	CropAmount(String perm, int amount) {
		this.perm = perm;
		this.amount = amount;
	}

	public String getPerm() {
		return perm;
	}

	public int getAmount() {
		return amount;
	}

	public static CropAmount getHighestAmount(Player p) {
		for (int i = CropAmount.values().length - 1; i != 0; i--) {
			if (p.hasPermission(CropAmount.values()[i].getPerm()))
				return CropAmount.values()[i];
		}
		return CropAmount.grunt;
	}
}
