package core.hcrg;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import core.hcrg.bob.BoB;
import core.hcrg.spleef.Spleef;

public class MinigameHandler implements CommandExecutor {
	private JavaPlugin jp;

	public MinigameHandler(JavaPlugin jp) {
		this.jp = jp;
		Spleef spleef = new Spleef(jp);
		jp.getServer().getPluginManager().registerEvents(spleef, jp);
		minigameMap.put("spleef", spleef);
		BoB b = new BoB(jp);
		jp.getServer().getPluginManager().registerEvents(b, jp);
		minigameMap.put("bob", b);
	}

	HashMap<String, Minigame> minigameMap = new HashMap<String, Minigame>();

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg1.getName().equalsIgnoreCase("bob")) {
			minigameMap.get("bob").executeCommand((Player) arg0, arg3);
			return true;
		} else if (arg1.getName().equalsIgnoreCase("spleef")) {
			minigameMap.get("spleef").executeCommand((Player) arg0, arg3);
			return true;
		}
		return false;
	}
}
