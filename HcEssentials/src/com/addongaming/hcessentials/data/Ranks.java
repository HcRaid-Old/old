package com.addongaming.hcessentials.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

public enum Ranks {

	grunt("Grunt", 0), creeper("Creeper", 1), blaze("Blaze", 2), ghast("Ghast",
			3), enderdragon("Enderdragon", 4), ender("Ender", 4), hero("Hero",
			5);
	private final String name;
	private final int value;

	Ranks(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public static Ranks getHighestRank(Player p) {
		Ranks current = Ranks.grunt;
		for (Ranks r : values())
			if (p.hasPermission(r.getPermission())
					&& r.getValue() > current.getValue())
				current = r;
		return current;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public String getPermission() {
		return "Hcraid." + name;
	}

	public static Ranks[] getLowToHigh() {
		return Ranks.values();
	}

	public static Ranks[] getHighToLow() {
		List<Ranks> total = Arrays.asList(Ranks.values());
		Collections.reverse(total);
		return total.toArray(new Ranks[total.size()]);
	}
}
