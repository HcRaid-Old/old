package com.addongaming.prison.mines;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.utils.Utils;

public class MineManager implements Listener {

	private JavaPlugin jp;
	private List<Mine> mineList = new ArrayList<Mine>();

	public MineManager(JavaPlugin jp) {
		this.jp = jp;
		initConfig();
		loadConfig();
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getScheduler()
				.scheduleSyncRepeatingTask(jp, new Runnable() {

					@Override
					public void run() {
						for (Mine mine : mineList)
							mine.tick();
					}
				}, 0L, 20 * 60);
	}

	public void debug(Player p) {
		for (Mine m : mineList) {
			p.sendMessage("Mine: " + m.getMineName() + " Current tick: "
					+ m.getTick() + " Timer: " + m.getTimer() + " Region "
					+ m.getRegionName());
		}
	}

	private void initConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("prison.mine.testmine.name", "Beginners Mine");
		fc.addDefault("prison.mine.testmine.timer", 3);
		fc.addDefault("prison.mine.testmine.world", "world");
		fc.addDefault("prison.mine.testmine.region", "bmine");
		fc.addDefault("prison.mine.testmine.teleportlocation",
				"world|0.0|0.0|0.0");
		fc.addDefault("prison.mine.testmine.flags", new ArrayList<String>() {
			{
				this.add(Flags.SAFE.name());
			}
		});
		fc.addDefault("prison.mine.testmine.blocks", new ArrayList<String>() {

			private static final long serialVersionUID = 1L;

			{
				this.add("15|20");
				this.add("16|30");
			}
		});
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	private void loadConfig() {
		FileConfiguration fc = jp.getConfig();
		for (String part : fc.getConfigurationSection("prison.mine").getKeys(
				false)) {
			String name = fc.getString("prison.mine." + part + ".name");
			int timer = fc.getInt("prison.mine." + part + ".timer");
			String world = fc.getString("prison.mine." + part + ".world");
			String region = fc.getString("prison.mine." + part + ".region");
			List<String> flagsRaw = fc.getStringList("prison.mine." + part
					+ ".flags");
			List<Flags> flagList = new ArrayList<Flags>();
			for (String flag : flagsRaw)
				flagList.add(Flags.valueOf(flag));
			List<String> list = fc.getStringList("prison.mine." + part
					+ ".blocks");
			List<BlockChance> lb = new ArrayList<BlockChance>();
			for (String st : list) {
				lb.add(new BlockChance(st.split("[|]")[0], st.split("[|]")[1]));
			}
			mineList.add(new Mine(name, world, region, lb
					.toArray(new BlockChance[lb.size()]), timer, flagList
					.toArray(new Flags[flagList.size()]), Utils.loadLoc(fc
					.getString("prison.mine." + part + ".teleportlocation"))));
			System.out.println("Loaded mine: " + name);
		}
	}

	public void refreshMines() {
		for (Mine mine : mineList) {
			mine.setTick(mine.getTimer() - 1);
		}
	}

	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent event) {
		for (Mine mine : mineList)
			if (mine.isInMine(event.getBlock().getLocation()))
				if (mine.blockPlace(event.getBlockPlaced().getType(),
						event.getPlayer())) {
					event.setCancelled(true);
					return;
				} else
					return;
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem()) {
			for (Mine mine : mineList)
				if (mine.isInMine(event.getClickedBlock().getLocation())) {
					if (mine.blockPlace(event.getItem().getType(),
							event.getPlayer())) {
						event.setCancelled(true);
						return;
					} else
						return;
				}
		}
	}
}
