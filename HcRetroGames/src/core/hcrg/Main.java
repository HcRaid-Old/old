package core.hcrg;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {


	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}
}
