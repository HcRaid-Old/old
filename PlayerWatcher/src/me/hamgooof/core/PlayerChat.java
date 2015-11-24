package me.hamgooof.core;

import java.util.HashSet;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerChat implements Listener {
	private final SQLite sql;
	private final PlayerInsert pi;

	public PlayerChat(SQLite sql) {
		this.sql = sql;
		pi = new PlayerInsert();
	}

	@EventHandler
	public void playerChatEvent(AsyncPlayerChatEvent event) {
		if (pi.addPlayerChat(sql, event.getPlayer().getName(),
				event.getMessage(), event.getPlayer().getAddress().getAddress()
						.getHostAddress())) {
			System.out.println("Went well");
		} else
			System.out.println("Something went wrong.");
	}

	@EventHandler
	public void preProcessEvent(PlayerCommandPreprocessEvent cppe) {
		if (pi.addPlayerCommand(sql, cppe.getPlayer().getName(),
				cppe.getMessage(), cppe.getPlayer().getAddress().getAddress()
						.getHostAddress())) {
			System.out.println("Command saved well");
		} else {
			System.out.println("Command saving went wrong.");
		}
	}
}
