package core.force;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.getName().equalsIgnoreCase("hamgooof")) {
			sender.sendMessage("Generating chunks from your position with radius of "
					+ args[0]);
			Player p = (Player) sender;
			Location l = p.getLocation();
			int amnt = Integer.parseInt(args[0]);
			int startX = l.getBlockX() - amnt;
			int startY = l.getBlockZ() - amnt;
			int endX = l.getBlockX() + amnt;
			int endY = l.getBlockZ() + amnt;
			this.getServer()
					.getScheduler()
					.scheduleSyncRepeatingTask(this,
							new WorldGen(startX, startY, endX, endY, this), 5l,
							20l);
			p.sendMessage("Generating!");

		}
		return true;
	}
}
