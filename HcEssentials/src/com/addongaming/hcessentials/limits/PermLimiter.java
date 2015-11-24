package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.limits.objects.PermBlock;

public class PermLimiter implements Listener, SubPlugin {
	private JavaPlugin jp;
	private String errorMsg;
	private List<PermBlock> blocks;

	public PermLimiter(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("limit.perm.enabled", false);
		fc.addDefault("limit.perm.errorMsg",
				"&2[&6HcRaid&2] &cSorry, you need a higher rank to use this. /buyrank");
		fc.addDefault("limit.perm.ids", new ArrayList<String>() {
			{
				this.add("351:2|HcRaid.premium");
			}
		});
		fc.options().copyDefaults(true);
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		if (!fc.getBoolean("limit.perm.enabled"))
			return false;
		errorMsg = ChatColor.translateAlternateColorCodes('&',
				fc.getString("limit.perm.errorMsg"));
		for (String str : fc.getStringList("limit.perm.ids"))
			blocks.add(new PermBlock(str));
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void itemPickup(PlayerPickupItemEvent event) {
		for (PermBlock pb : blocks)
			if (pb.isValid(event.getItem().getItemStack())) {
				if (!pb.hasPermission(event.getPlayer())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(errorMsg);
					return;
				}
			}
	}
}
