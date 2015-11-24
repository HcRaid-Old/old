package com.addongaming.prison.mines;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.prison.core.Main;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Mine {
	private BlockChance[] blocks;
	private Flags[] flags;
	private String mineName;
	private String region;
	private Location teleportLocation;
	private int tick;
	private int timer;
	private String world;

	public Mine(String mineName, String world, String region,
			BlockChance[] blocks, int timer, Flags[] flags,
			Location teleportLocation) {
		this.mineName = mineName;
		this.blocks = blocks;
		this.timer = timer;
		this.tick = new Random().nextInt(timer);
		this.world = world;
		this.region = region;
		this.flags = flags;
		this.teleportLocation = teleportLocation;
	}

	private void checkStatus() {
		if (timer == tick) {
			resetMine();
			for (Player p : Bukkit.getOnlinePlayers()) {
				ApplicableRegionSet ars = HcEssentials.worldGuard
						.getRegionManager(p.getWorld()).getApplicableRegions(
								p.getLocation());
				for (Iterator<ProtectedRegion> it = ars.iterator(); it
						.hasNext();) {
					if (it.next().getId().equalsIgnoreCase(region)) {
						sendMessage(p, "Regenerating mine.");
						p.teleport(teleportLocation);
					}
				}
			}
			tick = 0;
		} else if (timer - 1 == tick) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				ApplicableRegionSet ars = HcEssentials.worldGuard
						.getRegionManager(p.getWorld()).getApplicableRegions(
								p.getLocation());
				for (Iterator<ProtectedRegion> it = ars.iterator(); it
						.hasNext();) {
					if (it.next().getId().equalsIgnoreCase(region)) {
						sendMessage(p, "Regenerating in 1 minute.");
					}
				}
			}
		} else if (timer - 5 == tick) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				ApplicableRegionSet ars = HcEssentials.worldGuard
						.getRegionManager(p.getWorld()).getApplicableRegions(
								p.getLocation());
				for (Iterator<ProtectedRegion> it = ars.iterator(); it
						.hasNext();) {
					if (it.next().getId().equalsIgnoreCase(region)) {
						sendMessage(p, "Regenerating in 5 minutes.");
					}
				}
			}
		} else if (tick % 5 == 0) {
			checkMine();
		}
	}

	private void checkMine() {
		if (tick > timer - 10)
			return;
		ProtectedRegion pr = Main.wg.getRegionManager(Bukkit.getWorld(world))
				.getRegion(region);
		World wor = Bukkit.getWorld(this.world);
		int counter = 0;
		for (int x = pr.getMinimumPoint().getBlockX(); x <= pr
				.getMaximumPoint().getBlockX(); x++) {
			for (int y = pr.getMinimumPoint().getBlockY(); y <= pr
					.getMaximumPoint().getBlockY(); y++) {
				for (int z = pr.getMinimumPoint().getBlockZ(); z <= pr
						.getMaximumPoint().getBlockZ(); z++) {
					if (wor.getBlockAt(x, y, z).getType() == Material.AIR) {
						counter++;
					}
				}
			}
		}
		int total = (pr.getMaximumPoint().getBlockX() - pr.getMinimumPoint()
				.getBlockX())
				* (pr.getMaximumPoint().getBlockY() - pr.getMinimumPoint()
						.getBlockY())
				* (pr.getMaximumPoint().getBlockZ() - pr.getMinimumPoint()
						.getBlockZ());
		if (((total / 100) * 40) < counter)
			tick = timer - 6;
	}

	public BlockChance[] getBlocks() {
		return blocks;
	}

	public String getMineName() {
		return mineName;
	}

	public String getRegionName() {
		return region;
	}

	public int getTick() {
		return this.tick;
	}

	public int getTimer() {
		return timer;
	}

	private void resetMine() {
		ProtectedRegion pr = Main.wg.getRegionManager(Bukkit.getWorld(world))
				.getRegion(region);
		World wor = Bukkit.getWorld(this.world);

		for (int x = pr.getMinimumPoint().getBlockX(); x <= pr
				.getMaximumPoint().getBlockX(); x++) {
			for (int y = pr.getMinimumPoint().getBlockY(); y <= pr
					.getMaximumPoint().getBlockY(); y++) {
				for (int z = pr.getMinimumPoint().getBlockZ(); z <= pr
						.getMaximumPoint().getBlockZ(); z++) {
					int ran = new Random().nextInt(100);
					int counter = 0;
					boolean fail = true;
					for (BlockChance bc : blocks) {
						counter += bc.getChance();
						if (counter >= ran) {
							wor.getBlockAt(x, y, z).setType(bc.getM());
							fail = false;
							break;
						}
					}
					if (fail == true)
						wor.getBlockAt(x, y, z).setType(Material.STONE);
				}
			}
		}
	}

	private void sendMessage(Player p, String string) {
		p.sendMessage(ChatColor.GOLD + "[" + ChatColor.GREEN + mineName
				+ ChatColor.GOLD + "] " + ChatColor.RESET + string);
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public void tick() {
		tick++;
		checkStatus();
	}

	public boolean isInMine(Location loc) {
		if (!loc.getWorld().getName().equalsIgnoreCase(world))
			return false;
		ProtectedRegion pr = Main.wg.getRegionManager(Bukkit.getWorld(world))
				.getRegion(region);
		if (pr.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
			return true;
		return false;
	}

	Material[] dangerousItems = { Material.LAVA_BUCKET, Material.LAVA,
			Material.GRAVEL, Material.FLINT_AND_STEEL };

	public boolean blockPlace(Material place, Player player) {
		System.out.println(place.name());
		for (Flags flag : flags) {
			System.out.println("FLAG: " + flag.name());
			if (flag == Flags.SAFE) {
				for (Material mat : dangerousItems)
					if (place == mat) {
						sendMessage(player,
								"You cannot place this in a safe area.");
						return true;
					}
			}
		}
		return false;
	}
}
