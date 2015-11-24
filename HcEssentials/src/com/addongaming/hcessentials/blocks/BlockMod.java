package com.addongaming.hcessentials.blocks;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class BlockMod implements SubPlugin {
	BookEnchanter be = null;

	private final JavaPlugin jp;

	Recycler r = null;

	public BlockMod(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {
		if (r != null)
			r.shutDown();
		if (be != null)
			be.shutDown();
	}

	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("blocks.recycler") == true) {
			r = new Recycler(Material.ENDER_PORTAL_FRAME, jp);
			jp.getServer().getPluginManager().registerEvents(r, jp);
		}
		be = new BookEnchanter(jp, jp.getConfig().getBoolean(
				"blocks.bookenchant.enabled"));
		jp.getServer().getPluginManager().registerEvents(be, jp);
		jp.getCommand("book").setExecutor(be);

		if (r == null && be == null)
			return false;
		else
			return true;
	}
}
