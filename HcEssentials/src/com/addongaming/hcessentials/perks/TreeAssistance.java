package com.addongaming.hcessentials.perks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.data.ItemType;

public class TreeAssistance implements Listener, SubPlugin {
	private String permission;
	private JavaPlugin jp;

	public TreeAssistance(JavaPlugin jp) {
		this.jp = jp;
		jp.getConfig().addDefault("treeassistance.enabled", false);
		jp.getConfig().addDefault("treeassistance.permission", "HcRaid.Ghast");
		jp.getConfig().options().copyDefaults(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void logCut(BlockBreakEvent bbe) {
		if (bbe.isCancelled() || !bbe.getPlayer().hasPermission(permission)
				|| bbe.getPlayer().getItemInHand() == null)
			return;
		if (Enchantable.getItemType(bbe.getPlayer().getItemInHand().getType()) == ItemType.AXE)
			if (bbe.getBlock().getType() == Material.LOG
					|| bbe.getBlock().getType() == Material.LOG_2) {
				for (int x = bbe.getBlock().getX() - 1; x < bbe.getBlock()
						.getX() + 1; x++) {
					for (int y = bbe.getBlock().getY() - 3; y < bbe.getBlock()
							.getY() + 10; y++) {
						for (int z = bbe.getBlock().getZ() - 1; z < bbe
								.getBlock().getZ() + 1; z++) {
							Block b = bbe.getBlock().getWorld()
									.getBlockAt(x, y, z);
							if (b.getType() == Material.LOG
									|| b.getType() == Material.LOG_2) {
								b.breakNaturally();
							}
						}
					}
				}
			}
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		if (!jp.getConfig().getBoolean("treeassistance.enabled"))
			return false;
		permission = jp.getConfig().getString("treeassistance.permission");
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}
}
