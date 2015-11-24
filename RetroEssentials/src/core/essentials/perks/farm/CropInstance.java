package core.essentials.perks.farm;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class CropInstance implements Serializable {

	private static final long serialVersionUID = -5138882444826519790L;
	private int x, y, z, status;
	private long placed, lastUpdate, nextUpdate;
	private String world;
	private int error = 0;

	public CropInstance(String world, int x, int y, int z, long placed) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.placed = placed;
		this.lastUpdate = placed;
		status = getBlock().getData();
		genNewUpdate();
	}

	private void genNewUpdate() {
		int i = new Random().nextInt(500) + 300;
		nextUpdate = new Date().getTime() + (i * 1000);
	}

	public void update() {
		if (error == 20) {
			status = 7;
			return;
		}
		if (getBlock().getType() != Material.CROPS) {
			status = 7;
			error++;
			return;
		}
		if (getBlock().getRelative(BlockFace.UP).getLightLevel() < 8) {
			genNewUpdate();
			error++;
			return;
		}
		error = 0;
		if (getBlock().getData() != 7)
			getBlock().setData((byte) (getBlock().getData() + 1));
		status = getBlock().getData();
	}

	public boolean canUpdate() {
		if (status == 7)
			return false;
		return new Date().after(new Date(nextUpdate));
	}

	public boolean hasFinished() {
		return status == 7;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public long getPlaced() {
		return placed;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public Block getBlock() {
		return getLocation().getBlock();
	}
}
