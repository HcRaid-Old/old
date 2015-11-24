package com.addongaming.hcessentials.perks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class SilkSpawners implements Listener, SubPlugin {
	private final JavaPlugin jp;

	public SilkSpawners(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		if (jp.getConfig().getBoolean("spawntouch.enabled") == false) {
			return false;
		}
		jp.getServer().getPluginManager().registerEvents(this, jp);
		return true;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void silkTouch(BlockBreakEvent bde) {
		if (!(bde.getBlock().getType() == Material.MOB_SPAWNER))
			return;
		if (bde.getPlayer().getItemInHand().getEnchantments()
				.containsKey(Enchantment.SILK_TOUCH)) {
			if (bde.getPlayer().hasPermission(
					jp.getConfig().getString("spawntouch.perm"))) {
				CreatureSpawner ms = (CreatureSpawner) bde.getBlock()
						.getState();
				String type = ms.getSpawnedType().toString();
				ItemStack is = new ItemStack(Material.MOB_SPAWNER, 1);
				ItemMeta im = is.getItemMeta();
				StringBuilder sb = new StringBuilder();
				for (String str : type.split("(_)")) {
					sb.append(Character.toUpperCase(str.charAt(0)));
					sb.append(str.substring(1, str.length()).toLowerCase());
					sb.append(" ");
				}
				ChatColor c = ChatColor.RED;
				EntityType[] crea = new EntityType[] { EntityType.COW,
						EntityType.CHICKEN, EntityType.MUSHROOM_COW,
						EntityType.PIG, EntityType.SHEEP, EntityType.OCELOT,
						EntityType.WOLF };
				for (EntityType et : crea)
					if (et == ms.getSpawnedType())
						c = ChatColor.GREEN;
				im.setDisplayName(c + sb.toString() + "spawner");
				is.setItemMeta(im);
				bde.getBlock().getLocation().getWorld()
						.dropItemNaturally(bde.getBlock().getLocation(), is);
				bde.setExpToDrop(0);
			} else {
				bde.setCancelled(true);
				bde.getPlayer()
						.sendMessage(
								ChatColor.GOLD
										+ "["
										+ ChatColor.BLUE
										+ "SilkTouch"
										+ ChatColor.GOLD
										+ "] "
										+ ChatColor.RED
										+ "You don't have the correct rank to silk touch spawners.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void spawnerPlaced(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() != Material.MOB_SPAWNER
				|| event.isCancelled())
			return;
		ItemStack is = event.getItemInHand();
		if (is.getItemMeta() != null
				&& is.getItemMeta().getDisplayName() != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0, len = ChatColor.stripColor(
					is.getItemMeta().getDisplayName()).split("( )").length - 1; i < len; i++) {
				sb.append(ChatColor.stripColor(
						is.getItemMeta().getDisplayName()).split("( )")[i]);
			}
			CreatureSpawner cs = (CreatureSpawner) event.getBlockPlaced()
					.getState();
			cs.setCreatureTypeByName(sb.toString().toUpperCase());
		}
	}
}
