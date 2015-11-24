package com.addongaming.minigames.management.flag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.addongaming.minigames.management.arena.Arena;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.minigames.ArenaGame;

public class Flag {
	private Location origLoc, currentLoc;
	private Arena arena;
	private int owner = Team.NONE.getTeamId();
	private List<Block> flagBlocks = new ArrayList<Block>();
	private short power = 0;

	public Flag(Arena arena, Location loc) {
		this.origLoc = loc;
		this.currentLoc = loc;
		this.arena = arena;
	}

	public short getPower() {
		return power;
	}

	public void setPower(short power) {
		this.power = power;
	}

	public Flag(Flag flag) {
		this.origLoc = flag.getCurrentLoc();
		this.currentLoc = flag.getOrigLoc();
		this.arena = flag.getArena();
	}

	public int getOwner() {
		return owner;
	}

	public void tick() {

	}

	public void rollback() {
		if (!flagBlocks.isEmpty()) {
			for (Iterator<Block> iter = flagBlocks.iterator(); iter.hasNext();) {
				Block b = iter.next();
				b.setType(Material.AIR);
				iter.remove();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void spawnFlag() {
		rollback();
		for (int y = currentLoc.getBlockY(); y < currentLoc.getBlockY() + 7; y++) {
			Block fence = currentLoc.getWorld().getBlockAt(
					currentLoc.getBlockX(), y, currentLoc.getBlockZ());
			fence.setType(Material.FENCE);
			flagBlocks.add(fence);
		}
		DyeColor c;
		switch (owner) {
		case 0:
			c = DyeColor.BLUE;
			break;
		case 1:
			c = DyeColor.RED;
			break;
		default:
			c = DyeColor.GRAY;
			break;
		}
		for (int y = currentLoc.getBlockY() + 6; y > currentLoc.getBlockY() + 2; y--) {
			int x = currentLoc.getBlockX(), z = currentLoc.getBlockZ();
			World world = currentLoc.getWorld();
			Block b = world.getBlockAt(x - 1, y, z);
			b.setType(Material.WOOL);
			b.setData(c.getWoolData());
			flagBlocks.add(b);
			b = world.getBlockAt(x - 2, y, z);
			b.setType(Material.WOOL);
			b.setData(c.getWoolData());
			flagBlocks.add(b);
			b = world.getBlockAt(x - 3, y, z - 1);
			b.setType(Material.WOOL);
			b.setData(c.getWoolData());
			flagBlocks.add(b);
			b = world.getBlockAt(x - 4, y, z - 1);
			b.setType(Material.WOOL);
			b.setData(c.getWoolData());
			flagBlocks.add(b);
		}
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setCurrentLoc(Location currentLoc) {
		this.currentLoc = currentLoc;
	}

	public Location getCurrentLoc() {
		return currentLoc;
	}

	public Location getOrigLoc() {
		return origLoc;
	}

	public void resetLoc() {
		this.currentLoc = this.origLoc;
	}

	public ArenaGame getAg() {
		return arena.getCurrentGame();
	}

	public Arena getArena() {
		return arena;
	}

	public final ArenaPlayer[] getNearbyPlayers(int radius) {
		List<ArenaPlayer> tempList = new ArrayList<ArenaPlayer>();
		for (ArenaPlayer ap : getAg().getArenaList())
			if (ap.getBase() != null && ap.getBase().isOnline())
				if (ap.getBase().getWorld().getName()
						.equalsIgnoreCase(origLoc.getWorld().getName()))
					if (ap.getBase().getLocation().distance(origLoc) <= radius)
						tempList.add(ap);
		return tempList.toArray(new ArenaPlayer[tempList.size()]);
	}
}
