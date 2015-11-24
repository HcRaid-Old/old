package core.essentials;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R1.Item;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import core.essentials.enums.EFood;
import core.essentials.gen.NetherGen;
import core.essentials.limit.CombatHandler;
import core.essentials.limit.CraftingHandler;
import core.essentials.limit.EntityHandler;
import core.essentials.limit.FoodHandler;
import core.essentials.limit.LimitListener;
import core.essentials.objects.Config;
import core.essentials.perks.IceBlockPlace;
import core.essentials.perks.SpawnerChange;
import core.essentials.perks.SpawnerHarvest;
import core.essentials.perks.TntHandler;
import core.essentials.perks.farm.FarmMonitor;
import core.essentials.perks.farm.TreeAssistance;
import core.hcrg.MinigameHandler;

public class Main extends JavaPlugin implements CommandExecutor {
	List<Disableable> toDisable = new ArrayList<Disableable>();

	@Override
	public void onEnable() {
		System.out.println("Enabling Retro Essentials");
		if (!new File(this.getDataFolder().getAbsolutePath()
				.replaceAll(" ", "")).exists())
			new File(this.getDataFolder().getAbsolutePath().replaceAll(" ", ""))
					.mkdirs();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new TreeAssistance(), this);
		pm.registerEvents(new LimitListener(this), this);
		pm.registerEvents(new FoodHandler(this), this);
		pm.registerEvents(new EntityHandler(), this);
		pm.registerEvents(new CombatHandler(), this);
		pm.registerEvents(new CraftingHandler(this), this);
		pm.registerEvents(new SpawnerHarvest(), this);
		pm.registerEvents(new IceBlockPlace(), this);
		pm.registerEvents(new SpawnerChange(), this);
		pm.registerEvents(new TntHandler(), this);
		FarmMonitor fm = new FarmMonitor(this);
		pm.registerEvents(fm, this);
		toDisable.add(fm);
		NetherGen ng = new NetherGen(new File(this.getDataFolder()
				.getAbsolutePath().replaceAll(" ", "")
				+ "\\genned.sav"), this);
		toDisable.add(ng);
		pm.registerEvents(ng, this);
		setupFood();
		final JavaPlugin jp = this;

		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable() {

					@Override
					public void run() {
						setupEconomy();
						if (economy == null) {
							System.out
									.println("RETRO ESSENTIALS - Vault not found - shutting down plugin");
							jp.getPluginLoader().disablePlugin(jp);
						}
						MinigameHandler mgh = new MinigameHandler(jp);
						jp.getCommand("spleef").setExecutor(mgh);
						jp.getCommand("bob").setExecutor(mgh);
					}
				}, 5);
		System.out.println("Enabled Retro Essentials");
	}

	private void setupFood() {

		Field field;
		try {
			field = Item.class.getDeclaredField("maxStackSize");
			field.setAccessible(true);
			for (EFood ef : EFood.values()) {
				field.setInt(ef.getItemClass(), ef.getStackSize());
			}
			field.setInt(Item.d(Material.SIGN.getId()), 1);
			field.setInt(Item.d(Material.BUCKET.getId()), 1);
			field.setAccessible(false);

		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		for (Disableable dis : toDisable)
			dis.onDisable();
	}

	public static Economy economy;

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender.isOp() && command.getName().equalsIgnoreCase("retrobypass")) {
			if (Config.bypass.contains(sender.getName())) {
				sender.sendMessage("Bypass disabled");

				Config.bypass.remove(sender.getName());
			} else {
				sender.sendMessage("Bypass enabled");
				Config.bypass.add(sender.getName());
			}
			return true;
		} else if (command.getName().equalsIgnoreCase("bedrockswap")
				&& Config.bypass.contains(sender.getName())) {
			ItemStack is = new ItemStack(Material.BEDROCK, 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("Spawner type changer");
			im.setLore(new ArrayList<String>() {
				{
					this.add("Right click on a spawner");
					this.add("to open the changer menu.");
				}
			});
			is.setItemMeta(im);
			Player p = (Player) sender;
			p.getInventory().addItem(is);
			return true;

		} else if (command.getName().equalsIgnoreCase("enderpearldrop")
				&& Config.bypass.contains(sender.getName())) {
			ItemStack is = new ItemStack(Material.ENDER_PEARL, 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("Spawner dropper");
			im.setLore(new ArrayList<String>() {
				{
					this.add("Right click on a spawner");
					this.add("for it to drop as an item.");
				}
			});
			is.setItemMeta(im);
			Player p = (Player) sender;
			p.getInventory().addItem(is);
			return true;

		}
		return false;
	}
}
