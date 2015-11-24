package hcmodtools.core.vanish;

import hcmodtools.core.Main;
import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.earth2me.essentials.User;

public class Vanish extends Tools implements Listener, CommandExecutor, ModTool {
	List<String> vanishList;
	JavaPlugin jp;

	public Vanish(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.GREEN + "HcVanish"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_RED
				+ "[" + ChatColor.RED + "HcVanish" + ChatColor.DARK_RED + "] "
				+ ChatColor.RESET);
		this.jp = jp;

	}

	@EventHandler
	public void entityTarget(EntityTargetEvent ete) {
		if (ete.isCancelled())
			return;
		if (ete.getTarget() instanceof Player
				&& vanishList.contains(((Player) (ete.getTarget())).getName())) {
			ete.setCancelled(true);
		}
	}

	@EventHandler
	public void entityDamageEvent(EntityDamageByEntityEvent ede) {
		if (ede.isCancelled())
			return;
		if (ede.getEntity() instanceof Player) {
			if (vanishList.contains(((Player) (ede.getEntity())).getName())) {
				ede.setCancelled(true);
			}
		}
		if (ede.getDamager() instanceof Player) {
			if (vanishList.contains(((Player) (ede.getDamager())).getName())) {
				ede.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void itemPickup(PlayerPickupItemEvent ipe) {
		if (ipe.getPlayer() != null
				&& vanishList.contains(ipe.getPlayer().getName()))
			ipe.setCancelled(true);
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent pje) {
		if (vanishList.contains(pje.getPlayer().getName())) {
			final Player p = pje.getPlayer();
			final boolean perm = pje.getPlayer().hasPermission("Hcraid.mod");
			final String msg = perm ? super.getPosTitle()
					+ " You are still in vanish." : super.getPosTitle()
					+ "You have been removed from vanish.";
			jp.getServer().getScheduler()
					.scheduleSyncDelayedTask(jp, new Runnable() {

						@Override
						public void run() {
							p.sendMessage(msg);
						}
					}, 10l);
			User u = Main.essentials.getUser(p);
			if (perm) {
				u.setGodModeEnabled(true);
				u.setAllowFlight(true);
				u.setFlying(true);
				u.setHidden(true);
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.isOp()
							|| (!p.isOp() && pl.hasPermission("HcRaid.MOD")))
						continue;
					pl.hidePlayer(p);
				}
			} else {
				for (Player play : Bukkit.getOnlinePlayers())
					play.showPlayer(p);
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				u.setGodModeEnabled(false);
				u.setAllowFlight(false);
				u.setFlying(false);
				u.setHidden(false);
				vanishList.remove(p.getName());
			}
		}
		if (!pje.getPlayer().hasPermission("Hcraid.mod")) {
			for (String str : vanishList) {
				Player p = Bukkit.getPlayer(str);
				if (p != null && p.isOnline())
					pje.getPlayer().hidePlayer(p);
			}
		}
	}

	@EventHandler
	public void playerChangeWorld(PlayerChangedWorldEvent event) {
		if (vanishList.contains(event.getPlayer().getName())) {
			for (Player p : Bukkit.getOnlinePlayers())
				if (!p.isOp())
					p.hidePlayer(event.getPlayer());
		}
	}

	@EventHandler
	public void inventoryOpen(InventoryClickEvent ice) {
		if (ice.isCancelled())
			return;
		if (ice.getInventory().getTitle().contains(" ")
				&& ice.getInventory().getTitle().split(" ")[0]
						.equalsIgnoreCase("vanish")) {
			ice.setCancelled(true);
			warn((Player) ice.getWhoClicked(),
					"You cannot remove items from chests in vanish!");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void chestOpen(PlayerInteractEvent pie) {
		if (pie.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (pie.getClickedBlock().getType() == Material.CHEST || pie
						.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
			if (vanishList.contains(pie.getPlayer().getName())) {
				System.out.println("Cancelling event");
				pie.setCancelled(true);
				Chest chest = (Chest) pie.getClickedBlock().getState();
				Inventory i = Bukkit.createInventory(null, chest.getInventory()
						.getSize(), "Vanish Chest");
				i.setContents(chest.getInventory().getContents());
				pie.getPlayer().openInventory(i);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			final Player p = (Player) arg0;
			if (!p.hasPermission("HcRaid.MOD"))
				return false;
			if (!vanishList.contains(p.getName())) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
				User u = Main.essentials.getUser(p);
				u.setGodModeEnabled(true);
				u.setAllowFlight(true);
				u.setFlying(true);
				u.setHidden(true);
				msg(p, "You are now vanished!");
				vanishList.add(p.getName());
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (pl.isOp()
							|| (!p.isOp() && pl.hasPermission("HcRaid.MOD")))
						continue;
					pl.hidePlayer(p);
				}
				return true;
			} else {
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				p.removePotionEffect(PotionEffectType.INVISIBILITY);
				User u = Main.essentials.getUser(p);
				u.setGodModeEnabled(false);
				if (u.getGameMode() != GameMode.CREATIVE) {
					u.setFlying(false);
					u.setAllowFlight(false);
				}
				u.setHidden(false);
				offModList.add(p.getName());
				jp.getServer().getScheduler()
						.scheduleSyncDelayedTask(jp, new Runnable() {

							@Override
							public void run() {
								offModList.remove(p.getName());
							}
						}, 300l);
				msg(p, "You are now unvanished!");
				vanishList.remove(p.getName());
				for (Player pl : Bukkit.getOnlinePlayers())
					pl.showPlayer(p);
				return true;
			}

		}
		return false;
	}

	private final List<String> offModList = new ArrayList<String>();

	@EventHandler
	public void modDamage(EntityDamageEvent ede) {
		if (ede.getEntity() instanceof Player
				&& ede.getCause() == DamageCause.FALL) {
			Player p = (Player) ede.getEntity();
			if (offModList.contains(p.getName())) {
				ede.setCancelled(true);
			}
		}
	}

	@Override
	public void onStart() {
		jp.getServer().getPluginManager().registerEvents(this, jp);
		jp.getServer().getPluginCommand("vanish").setExecutor(this);
		try {
			if (Main.fileExists("prevVanishList.sav")) {
				vanishList = (ArrayList<String>) Main
						.load("prevVanishList.sav");
				Main.getFile("prevVanishList.sav").delete();
			} else
				vanishList = new ArrayList<String>();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void onStop() {
		if (!vanishList.isEmpty()) {
			try {
				Main.save(vanishList, "prevVanishList.sav");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
