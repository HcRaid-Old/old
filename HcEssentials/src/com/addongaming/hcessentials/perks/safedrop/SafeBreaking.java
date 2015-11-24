package com.addongaming.hcessentials.perks.safedrop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.data.ItemType;

public class SafeBreaking implements SubPlugin, Listener {
	private JavaPlugin jp;
	List<SafeDropBlock> safeDrop = new ArrayList<SafeDropBlock>();

	public SafeBreaking(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("safedrop.enabled", Boolean.FALSE);
		fc.addDefault("safedrop.blocks", new ArrayList<String>() {
			{
				this.add(Material.BOOKSHELF.name() + "|" + ItemType.AXE);
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
		jp.reloadConfig();
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if (event.isCancelled() || event.getPlayer().getItemInHand() == null)
			return;
		for (SafeDropBlock pb : safeDrop)
			if (pb.canUse(event.getPlayer(), event.getBlock())) {
				ItemStack is = new ItemStack(event.getBlock().getType(), 1,
						event.getBlock().getData());
				event.getBlock().getDrops().clear();
				event.getBlock().setType(Material.AIR);
				event.getBlock().getWorld()
						.dropItemNaturally(event.getBlock().getLocation(), is);
				return;
			}
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		if (fc.getBoolean("safedrop.enabled")) {
			List<String> craftingList = (List<String>) fc
					.getList("safedrop.blocks");
			for (String s : craftingList) {
				String[] thingy = s.split("[|]");
				safeDrop.add(new SafeDropBlock(thingy[0], thingy[1]));
			}
			if (safeDrop.size() >= 1) {
				jp.getServer().getPluginManager().registerEvents(this, jp);
				return true;
			}
		}
		return false;
	}
}
