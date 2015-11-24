package core.force;

import org.bukkit.plugin.java.JavaPlugin;

public class WorldGen implements Runnable {
	private final int startx;
	private final int starty;
	private int currX = 0;
	private int currY = 0;
	private final int endx;
	private final int endy;
	private JavaPlugin jp;

	public WorldGen(int startx, int starty, int endx, int endy, JavaPlugin jp) {
		this.startx = startx;
		this.starty = starty;
		currX = startx;
		currY = starty;
		this.endx = endx;
		this.endy = endy;
		this.jp = jp;

	}

	@Override
	public void run() {
		System.out.println("First genning chunk: " + currX + "  " + currY);
		for (int i = 0; i < 50; i++) {
			if (currX >= endx && currY >= endy) {
				System.out.println("FINISHED GENERATION");
				return;
			}
			if (currX > endx) {
				currX += startx;
				currY += 16;
			} else
				currX += 16;
			jp.getServer().getWorld("world").loadChunk(currX, currY);
		}
		System.out.println("Last genning chunk: " + currX + "  " + currY);
	}
}
