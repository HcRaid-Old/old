package core.godflesh;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	private static HashMap<String, Integer> playerMap = new HashMap<String, Integer>();

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

	}

	@EventHandler
	public void PlayerDamageEvent(EntityDamageByEntityEvent pde) {
		if (pde.getDamager() instanceof Player) {
			Player attacker = (Player) pde.getDamager();
			if (playerMap.containsKey(attacker.getName()))
				return;
			ItemStack is = attacker.getItemInHand();
			if (!(is.getType() == Material.ROTTEN_FLESH))
				return;
			if (!is.getEnchantments().isEmpty()) {
				initiateTrolling(attacker);
				pde.setCancelled(true);
			}
		}

	}

	public void stopRunnable(String name) {
		if (playerMap.containsKey(name)) {
			int num = playerMap.get(name);
			if (num != -1)
				getServer().getScheduler().cancelTask(num);
			playerMap.remove(name);
		}
	}

	@EventHandler
	public void playerDied(PlayerDeathEvent pde) {
		if (playerMap.containsKey(pde.getEntity().getName())) {
			int num = playerMap.get(pde.getEntity().getName());
			if (num != -1)
				this.getServer().getScheduler().cancelTask(num);
			playerMap.remove(pde.getEntity().getName());
		}
	}

	public void playerLogin(PlayerJoinEvent event) {
		Player play = event.getPlayer();
		int task = this
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(this, new GodRunnable(play, this),
						10, 15);
		playerMap.put(play.getName(), task);
	}

	@EventHandler
	public void playerLogOut(PlayerQuitEvent pqe) {
		if (playerMap.containsKey(pqe.getPlayer().getName())) {
			int num = playerMap.get(pqe.getPlayer().getName());
			this.getServer().getScheduler().cancelTask(num);
			playerMap.put(pqe.getPlayer().getName(), -1);
		}
	}

	private void initiateTrolling(final Player attacker) {
		Firework f = (Firework) attacker.getWorld().spawnEntity(
				attacker.getLocation(), EntityType.FIREWORK);
		FireworkMeta fm = f.getFireworkMeta();
		fm.setPower(4);
		Random r = new Random();
		f.setFireworkMeta(fm);
		f.setPassenger(attacker);
		int task = this
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(this,
						new GodRunnable(attacker, this), 40, 15);
		playerMap.put(attacker.getName(), task);
		attacker.setItemInHand(new ItemStack(Material.AIR));
	}
}
