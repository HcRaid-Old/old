package core.godflesh;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GodRunnable implements Runnable {
	int counter = 0;
	final Player play;
	String[] str = new String[] {
			ChatColor.AQUA + "And I think it's gonna be a long long time ",
			ChatColor.AQUA
					+ "Till touch down brings me round again to find I'm not the man they think I am at home ",
			ChatColor.BOLD + "" + ChatColor.BLUE
					+ "Oh no no no I'm a god flesh man",
			ChatColor.BOLD + "" + ChatColor.BLUE
					+ "God flesh man burning out his fuse up here alone" };
	private final Main main;

	public GodRunnable(final Player player, final Main main) {
		this.play = player;
		this.main = main;
	}

	@Override
	public void run() {
		counter++;
		if (!play.isOnline())
			return;
		switch (counter) {
		case 3:
			Location loc = new Location(play.getWorld(), play.getLocation()
					.getX(), 2000.0, play.getLocation().getZ());
			// play.getLocation().setY(10000);
			play.teleport(loc);
			System.out.println("Teleported");
			break;
		case 5:
			play.sendMessage(str[0]);
			break;
		case 10:
			play.sendMessage(str[1]);
			break;
		case 13:
			play.sendMessage(str[2]);
			break;
		case 17:
			play.sendMessage(str[3]);
			break;
		case 50:
			main.stopRunnable(play.getName());
		}
	}
}
