package com.addongaming.overkill;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public class NetherHomeLimiter implements Listener {

	private final Essentials essentials;
	private final String[] commands = { "/esethome", "/sethome", "/createhome",
			"/ecreatehome" };

	public NetherHomeLimiter(Essentials essentials) {
		this.essentials = essentials;
	}

	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent event) {
		String lower = event.getMessage().toLowerCase();
		if (event.getPlayer().getWorld().getName()
				.equalsIgnoreCase("world_nether")) {
			for (String str : commands) {
				if (lower.startsWith(str)) {
					User user = essentials.getUser(event.getPlayer());
					int homes = getHomeCount("world_nether", user);
					if (homes >= 2) {
						event.getPlayer()
								.sendMessage(
										ChatColor.RED
												+ "You can only have two homes in the nether.");
						event.setCancelled(true);
					}
					return;
				}
			}
		}
	}

	private int getHomeCount(String world, User user) {
		int counter = 0;
		for (String str : user.getHomes()) {
			try {
				if (user.getHome(str).getWorld().getName()
						.equalsIgnoreCase(world))
					counter++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return counter;
	}
}
