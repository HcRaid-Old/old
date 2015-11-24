package com.addongaming.hcessentials.limits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.SubPlugin;
import com.addongaming.hcessentials.limits.objects.LimitedBlock;
import com.addongaming.hcessentials.logging.DataLog;

public class Limiter implements SubPlugin {
	private CraftingLimiter cl;
	List<EntityType> disAllowed = new ArrayList<EntityType>();
	private JavaPlugin jp;
	List<LimitedBlock> limitedBlocks = new ArrayList<LimitedBlock>();
	private MobHandler mh = null;
	public static Limiter instance;
	private MultiHealthBlocks multiHealthBlocks = null;

	public Limiter(JavaPlugin jp) {
		this.jp = jp;
		dl = HcEssentials.getDataLogger().addLogger("Limiter");
		instance = this;
	}

	public static Limiter getInstance() {
		return instance;
	}

	public boolean hasMultiHealthBlocks() {
		return multiHealthBlocks != null;
	}

	public MultiHealthBlocks getMultiHealthBlocks() {
		return multiHealthBlocks;
	}

	private DataLog dl;

	@SuppressWarnings({ "serial", "unchecked" })
	private void checkConfig() {
		dl.log("Setting up default config");
		FileConfiguration fc = jp.getConfig();
		// Mob Limiting
		fc.addDefault("limit.nocheatplus.mins", 10);
		fc.addDefault("limit.mobspawning.enabled", Boolean.FALSE);
		for (EntityType et : EntityType.values())
			if (et.isAlive()) {
				fc.addDefault("limit.mobspawning.mobs."
						+ et.name().toLowerCase(), Boolean.valueOf(false));
			}
		// Inve limiting
		fc.addDefault("limit.inventory.enabled", false);
		for (InventoryType it : InventoryType.values())
			fc.addDefault("limit.inventory.name." + it.name(), false);
		// Crafting limiting
		fc.addDefault("limit.crafting.enabled", Boolean.FALSE);
		fc.addDefault("limit.crafting.items", new ArrayList<String>() {
			{
				this.add(Material.ENCHANTMENT_TABLE.name().toLowerCase()
						+ "|[HcRaid] You cannot craft this on a HardCore server! Use /buy instead");
			}
		});
		// MultiHealthBlocks
		fc.addDefault("limit.multihealthblocks.enabled", Boolean.FALSE);
		fc.addDefault("limit.multihealthblocks.inspector", Material.BONE.name()
				.toLowerCase());
		fc.addDefault("limit.multihealthblocks.blocks",
				new ArrayList<String>() {
					{
						this.add(Material.OBSIDIAN.name().toLowerCase()
								+ "|8|[HcRaid] This block has <health> health left.");
					}
				});
		fc.addDefault("limit.multihealthblocks.tntradius", 4);
		// Spawn Protection
		fc.addDefault("limit.spawnprotection.enabled", true);
		fc.addDefault("limit.spawnprotection.world", "world");
		fc.addDefault("limit.spawnprotection.region", "innerspawn");
		fc.addDefault("limit.spawnprotection.errorMsg",
				"&2[&6HcRaid&2] &cSorry, you cannot use this in spawn.");
		fc.addDefault("limit.spawnprotection.itemframesinteractable", false);
		fc.addDefault("limit.spawnprotection.ids", new ArrayList<String>() {
			{
				this.add(Material.ANVIL.name());
				this.add(Material.TRAP_DOOR.name());
			}
		});
		// Saving config
		fc.options().copyDefaults(true);
		dl.log("Setup default config.");
		jp.saveConfig();
		// End of saving config
		jp.reloadConfig();
		dl.log("Loading config");
		fc = jp.getConfig();
		if (fc.getBoolean("limit.mobspawning.enabled")) {
			for (String str : fc.getConfigurationSection(
					"limit.mobspawning.mobs").getKeys(false))
				if (fc.getBoolean("limit.mobspawning.mobs." + str)) {
					disAllowed.add(EntityType.valueOf(str.toUpperCase()));
					dl.log("Disallowed entity: " + str);
				}
		}
		if (fc.getBoolean("limit.inventory.enabled")) {
			inveList = new ArrayList<InventoryType>();
			for (String str : fc
					.getConfigurationSection("limit.inventory.name").getKeys(
							false)) {
				if (fc.getBoolean("limit.inventory.name." + str)) {
					inveList.add(InventoryType.valueOf(str.toUpperCase()));
					dl.log("Inventory limit: " + str);
				}
			}

		}
		if (fc.getBoolean("limit.crafting.enabled")) {
			List<String> craftingList = (List<String>) fc
					.getList("limit.crafting.items");
			for (String s : craftingList) {
				limitedBlocks.add(new LimitedBlock(s));
				dl.log("Limited block: " + s);
			}
		}
		if (fc.getBoolean("limit.multihealthblocks.enabled")) {
			multiHealthBlocks = new MultiHealthBlocks(
					fc.getStringList("limit.multihealthblocks.blocks"),
					fc.getString("limit.multihealthblocks.inspector"),
					fc.getInt("limit.multihealthblocks.tntradius"));
			jp.getServer().getPluginManager()
					.registerEvents(multiHealthBlocks, jp);
		}
		if (fc.getBoolean("limit.spawnprotection.enabled")) {
			String world = fc.getString("limit.spawnprotection.world");
			String region = fc.getString("limit.spawnprotection.region");
			String errorMsg = fc.getString("limit.spawnprotection.errorMsg");
			boolean canInteract = fc
					.getBoolean("limit.spawnprotection.itemframesinteractable");
			List<String> ids = fc.getStringList("limit.spawnprotection.ids");
			jp.getServer()
					.getPluginManager()
					.registerEvents(
							new SpawnProtection(world, region, errorMsg, ids,
									canInteract), jp);
		}
		loadNoCheatPlusCleaner();
		dl.log("Loaded config");
	}

	private List<InventoryType> inveList = null;

	private void loadNoCheatPlusCleaner() {
		org.bukkit.plugin.Plugin plugin = jp.getServer().getPluginManager()
				.getPlugin("NoCheatPlus");
		if (plugin == null)
			return;
		jp.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(
						jp,
						new NCPCleaner(),
						20 * 60,
						jp.getConfig().getInt("limit.nocheatplus.mins") * 20 * 60);

	}

	@Override
	public void onDisable() {
		if (mh != null)
			mh.disable();

	}

	@Override
	public boolean onEnable() {
		checkConfig();
		if (!disAllowed.isEmpty()) {
			mh = new MobHandler(jp, disAllowed);
			jp.getServer().getPluginManager().registerEvents(mh, jp);
			dl.log("Registered mob handler.");
		}
		if (!limitedBlocks.isEmpty()) {
			jp.getServer().getPluginManager()
					.registerEvents(new CraftingLimiter(jp, limitedBlocks), jp);
			dl.log("Registered limited blocks");
		}
		if (inveList != null && !inveList.isEmpty()) {
			jp.getServer().getPluginManager()
					.registerEvents(new InventoryLimiter(inveList), jp);
			dl.log("Registered limited inventories");

		}
		return true;
	}
}
