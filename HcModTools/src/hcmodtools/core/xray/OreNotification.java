package hcmodtools.core.xray;

import hcmodtools.core.ModTool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OreNotification implements Listener, ModTool {
	private final JavaPlugin jp;

	public OreNotification(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
	}

	@EventHandler
	public void blockInteracted(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.LEFT_CLICK_BLOCK
				&& pie.getClickedBlock() != null
				&& pie.getClickedBlock().getData() == 0) {
			String name = "";
			Material mat = pie.getClickedBlock().getType();
			int scan = 0;
			if (mat == Material.DIAMOND_ORE) {
				name = "diamonds";
				scan = OreScan.diamond;
			} else if (mat == Material.EMERALD_ORE) {
				name = "emeralds";
				scan = OreScan.emerald;
			} else if (mat == Material.IRON_ORE) {
				name = "iron";
				scan = OreScan.iron;
			} else if (mat == Material.GOLD_ORE) {
				name = "gold";
				scan = OreScan.gold;
			}
			if (1 > scan)
				return;
			int count = 0;
			Block clicked = pie.getClickedBlock();
			for (int x = clicked.getX() - scan; x < clicked.getX() + scan; x++) {
				for (int y = clicked.getY() - scan; y < clicked.getY() + scan; y++) {
					for (int z = clicked.getZ() - scan; z < clicked.getZ()
							+ scan; z++) {
						if (clicked.getWorld().getBlockAt(x, y, z).getType() == mat) {
							count++;
							clicked.getWorld().getBlockAt(x, y, z)
									.setData((byte) 1);
						}
					}
				}
			}
			if (count >= 2)
				Bukkit.broadcast(ChatColor.GREEN + "[" + ChatColor.YELLOW
						+ "HcOreMonitor" + ChatColor.GREEN + "] "
						+ pie.getPlayer().getName() + " has found " + count
						+ " " + name, "HcRaid.MOD");
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	static class OreScan {
		public static int emerald = 7;
		public static int gold = 7;
		public static int iron = 5;
		public static int diamond = 5;
	}
}
