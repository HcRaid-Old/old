package com.addongaming.hcessentials.stats.player.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.stats.player.DatabaseHandling;
import com.addongaming.hcessentials.stats.player.EPlayerStat;
import com.addongaming.hcessentials.stats.player.PlayerStatInstance;

public class PlayerStatReader implements Runnable {
	private DatabaseHandling dh;
	private Player p;
	private PlayerStatInstance current;

	public PlayerStatReader(Player p, DatabaseHandling dh,
			PlayerStatInstance current) {
		this.p = p;
		this.dh = dh;
		this.current = current;
	}

	@Override
	public void run() {
		PlayerStatInstance psi = dh.getStoredDataAsInstance(p.getName(),
				dh.getConnection(), true);
		if (p == null || !p.isOnline())
			return;
		if (psi == null) {
			p.sendMessage("Sorry, there seemed to be an issue getting your stats.");
		} else {
			if (current != null)
				psi.merge(current);
			List<String> messageList = new ArrayList<String>();
			messageList.add(ChatColor.GREEN + "-----" + ChatColor.BLUE
					+ "[ALPHA] Player Stats" + ChatColor.GREEN + "-----");
			messageList.add(ChatColor.DARK_GREEN + "Time online: "
					+ ChatColor.GREEN + psi.getLoggedInString());
			for (EPlayerStat eps : EPlayerStat.values())
				if (psi.getStat(eps) > 0)
					messageList.add(ChatColor.DARK_GREEN
							+ eps.getReadableName() + ": " + ChatColor.GREEN
							+ psi.getStat(eps));
			messageList.add(ChatColor.GREEN + "-----" + ChatColor.BLUE
					+ "[ALPHA] Player Stats" + ChatColor.GREEN + "-----");
			if (p == null || !p.isOnline())
				return;
			p.sendMessage(messageList.toArray(new String[messageList.size()]));
		}
	}

}
