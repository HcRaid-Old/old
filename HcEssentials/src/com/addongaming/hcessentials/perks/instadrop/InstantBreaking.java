package com.addongaming.hcessentials.perks.instadrop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;

public class InstantBreaking implements SubPlugin, Listener {
	private JavaPlugin jp;
	List<InstantBlock> instantBreak = new ArrayList<InstantBlock>();

	public InstantBreaking(JavaPlugin jp) {
		this.jp = jp;
		setupConfig();
	}

	private void setupConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("instabreak.enabled", Boolean.FALSE);
		fc.addDefault("instabreak.blocks", new ArrayList<String>() {
			{
				this.add("melon_block|Hcraid.creeper");
				this.add("pumpkin|Hcraid.ghast");
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
	public void blockBreak(BlockDamageEvent event) {
		for (InstantBlock pb : instantBreak)
			if (pb.canUse(event.getPlayer(), event.getBlock())) {
				if (HcEssentials.worldGuard.canBuild(event.getPlayer(),
						event.getBlock()))
					event.getBlock().breakNaturally();
				return;
			}
	}

	@Override
	public boolean onEnable() {
		FileConfiguration fc = jp.getConfig();
		if (fc.getBoolean("instabreak.enabled")) {
			List<String> craftingList = (List<String>) fc
					.getList("instabreak.blocks");
			for (String s : craftingList) {
				String[] thingy = s.split("[|]");
				instantBreak.add(new InstantBlock(thingy[0], thingy[1]));
			}
			if (instantBreak.size() >= 1) {
				jp.getServer().getPluginManager().registerEvents(this, jp);
				return true;
			}
		}
		return false;
	}
}
