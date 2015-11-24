package hcmodtools.core.xray;

import hcmodtools.core.Tools;

import java.text.DecimalFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class XrayInstance extends Tools {
	private long lastBroadcast = 0;
	private int misc, iron, gold, diamond, lapis, spawner = 0;

	private String playerName;

	public XrayInstance(Tools tool, String plString) {
		super(tool);
		this.playerName = plString;

	}

	void blockMined(Material m) {
		switch (m.getId()) {
		case 14:// Gold
			gold++;
			break;
		case 15:// Iron
			iron++;
			break;
		case 52:// Spawner
			spawner++;
			break;
		case 21:// lapis
			lapis++;
			break;
		case 56:// Diamond
			diamond++;
			break;
		default:
			misc++;
			break;
		}
	}

	public void asMessage(Player p) {
		msg(p, "");
		smsg(p, "----- " + playerName + "'s Xray Statistics -----");
		smsg(p, "Total blocks broken: "
				+ (misc + iron + gold + diamond + spawner + lapis));
		smsg(p, "Misc blocks broken: " + misc + " (" + getPercentage(misc)
				+ "%)");
		smsg(p, "Total iron mined: " + iron + " (" + getPercentage(iron) + "%)");
		smsg(p, "Total gold mined: " + gold + " (" + getPercentage(gold) + "%)");
		smsg(p, "Total diamond mined: " + diamond + " ("
				+ getPercentage(diamond) + "%)");
		smsg(p, "Total spawners mined: " + spawner + " ("
				+ getPercentage(spawner) + "%)");
		smsg(p, "Total lapis mined: " + lapis + " (" + getPercentage(lapis)
				+ "%)");
		smsg(p, "----------------------------------");
	}

	private double getPercentage(double block) {
		double total = misc + iron + gold + diamond + lapis + spawner;
		if (total == 0 || block == 0)
			return 0.0d;
		double first = block / total;
		double finalCalc = first * 100;
		return Double.parseDouble(new DecimalFormat("##.#").format(finalCalc));
	}

	public boolean shouldBroadcast() {
		if (misc + iron + gold + diamond + lapis + spawner < 100)
			return false;
		if (new Date()
				.after(new Date(lastBroadcast + XrayValues.alertCooldown))) {
			if (XrayValues.iron <= getPercentage(iron)
					|| XrayValues.diamond <= getPercentage(diamond)
					|| XrayValues.spawner <= getPercentage(spawner)
					|| XrayValues.lapis <= getPercentage(lapis)
					|| XrayValues.gold <= getPercentage(gold)) {
				lastBroadcast = new Date().getTime();
				return true;
			}
		}

		return false;
	}

	public String getPlayerName() {
		return playerName;
	}

}
