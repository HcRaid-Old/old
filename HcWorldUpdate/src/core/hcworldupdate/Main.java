package core.hcworldupdate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	// Commands
	// WorldUpdate start X radius Y radius
	// WorldUpdate stop
	int id = -1;

	@Override
	public void onEnable() {
		this.getServer().getPluginCommand("worldupdate").setExecutor(this);
	}

	@Override
	public void onDisable() {
		if (id > 0)
			this.getServer().getScheduler().cancelTask(id);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (!p.isOp())
				return true;
			if (args.length == 0) {
				sender.sendMessage("Please use /worldupdate start x y to update the world.");
				sender.sendMessage("Please use /worldupdate stop to end the update thread.");
				return true;
			} else {
				switch (args[0].toLowerCase()) {
				case "start":
					id = this
							.getServer()
							.getScheduler()
							.scheduleSyncRepeatingTask(
									this,
									new WorldUpdater(this, p.getLocation(),
											Integer.parseInt(args[1]), Integer
													.parseInt(args[2]), p),
									10l, 5l);
					p.sendMessage("Started");
					break;
				case "stop":
					if (id == -1) {
						sender.sendMessage("There's currently no thread running");
						return true;
					} else {
						sender.sendMessage("Stopping thread.");
						this.getServer().getScheduler().cancelTask(id);
						id = -1;
						return true;
					}

				default:
					sender.sendMessage("Please use /worldupdate start x y to update the world.");
					sender.sendMessage("Please use /worldupdate stop to end the update thread.");
					break;
				}
			}
		}
		return true;
	}

}
