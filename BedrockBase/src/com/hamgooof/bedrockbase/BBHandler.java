package com.hamgooof.bedrockbase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.asyncworldedit.PluginMain;

import com.hamgooof.bedrockbase.core.BBListener;
import com.hamgooof.bedrockbase.core.BBPlugin;
import com.hamgooof.bedrockbase.core.PlayerHandler;
import com.hamgooof.bedrockbase.objects.BedrockSchematic;
import com.hamgooof.bedrockbase.worldedit.AWEListener;
import com.hamgooof.bedrockbase.worldedit.PastingRunnable;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class BBHandler implements CommandExecutor {
	private int x, z, initialheight;
	private final JavaPlugin jp;
	private List<BedrockSchematic> schematics = new ArrayList<BedrockSchematic>();
	private PlayerHandler playerHandler;

	public BBHandler(JavaPlugin jp, int x, int z, int initialheight,
			List<String> bases) {
		this.jp = jp;
		this.x = x;
		this.z = z;
		this.initialheight = initialheight;
		loadBases(bases);
		playerHandler = new PlayerHandler(jp.getDataFolder(), this);
		PluginMain.getInstance().getBlockPlacer()
				.addListener(new AWEListener(playerHandler));
		jp.getServer().getPluginManager().registerEvents(new BBListener(), jp);
	}

	private void loadBases(List<String> bases) {
		for (String str : bases) {
			String[] split = str.split("[|]");
			schematics.add(new BedrockSchematic(split[0], split[1]));
		}
	}

	public void updateConfig() {
		FileConfiguration fc = jp.getConfig();
		fc.set("currentx", x);
		fc.set("currentz", z);
		jp.saveConfig();
	}

	public void updateHomeOffset(int x, int y, int z) {
		BBPlugin.xHomeOffset = x;
		BBPlugin.yHomeOffset = y;
		BBPlugin.zHomeOffset = z;
		FileConfiguration fc = jp.getConfig();
		fc.set("homeoffset.x", x);
		fc.set("homeoffset.y", y);
		fc.set("homeoffset.z", z);
		jp.saveConfig();
	}

	// Command /bb
	// /bb redeem
	// /bb upgrade
	// /bb resetbase <username>
	//
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player)) {
			arg0.sendMessage("Please execute your command via a player");
			return true;
		}
		Player player = (Player) arg0;
		boolean anyPerm = false;
		for (BedrockSchematic bs : schematics) {
			System.out.println("Player " + player.getName()
					+ " has permission " + bs.getPermission() + " "
					+ player.hasPermission(bs.getPermission()));
			if (player.hasPermission(bs.getPermission())) {
				anyPerm = true;
				break;
			}
		}
		if (!anyPerm) {
			player.sendMessage(BBPlugin.title
					+ "You do not have permission for this command");
			return true;
		}
		if (AWEListener.isPasting(player.getUniqueId())) {
			player.sendMessage(BBPlugin.title
					+ "You base is currently under construction. Please try again in a minute");
			return true;
		}
		if (arg3.length == 0) {
			player.sendMessage(BBPlugin.title
					+ "Please use one of the following commands");
			player.sendMessage(ChatColor.GOLD + "/bb redeem " + ChatColor.AQUA
					+ "- redeems a bedrock base.");
			player.sendMessage(ChatColor.GOLD + "/bb upgrade " + ChatColor.AQUA
					+ "- upgrades a bedrock base.");
			player.sendMessage(ChatColor.GOLD + "/bb return " + ChatColor.AQUA
					+ "- takes you to your base.");
			if (player.isOp()) {
				player.sendMessage(ChatColor.GOLD + "/bb reset <name> "
						+ ChatColor.AQUA
						+ "- resets a base - player must be online.");
				player.sendMessage(ChatColor.GOLD + "/bb test "
						+ ChatColor.AQUA + "- creates a test base.");
				player.sendMessage(ChatColor.GOLD + "/bb testset "
						+ ChatColor.AQUA
						+ "- sets the offset positions for /bb return.");
			}
			return true;
		}

		switch (arg3[0].toLowerCase()) {
		case "return":
			if (!playerHandler.hasFile(player.getUniqueId())) {
				player.sendMessage(BBPlugin.title
						+ "You do not have a bedrock base.");
				return true;
			}
			Location loc = playerHandler.getPlayersInitialLocation(player
					.getUniqueId());
			loc.setX(loc.getX() + BBPlugin.xHomeOffset);
			loc.setY(loc.getY() + BBPlugin.yHomeOffset);
			loc.setZ(loc.getZ() + BBPlugin.zHomeOffset);
			// Make scheduler
			player.sendMessage(BBPlugin.title + "Teleporting...");
			player.teleport(loc);
			break;
		case "redeem":
		case "upgrade":
			if (!playerHandler.hasFile(player.getUniqueId())) {
				player.sendMessage(BBPlugin.title
						+ "Your file doesn't exists. Creating one now.");
				playerHandler.createFile(player.getUniqueId());
			}
			pasteBases(player);
			break;
		case "test":
			if (!player.isOp())
				break;
			int iterAmount = arg3.length == 2 ? Integer.parseInt(arg3[1]) : 1;
			for (int i = 0; i < iterAmount; i++) {
				playerHandler.createFile(UUID
						.fromString("00000000-0000-0000-0000-000000000000"));
				pasteTestBase();
			}
			break;
		case "testset":
			if (!player.isOp())
				break;
			if (!playerHandler.hasFile(UUID
					.fromString("00000000-0000-0000-0000-000000000000"))) {
				player.sendMessage(BBPlugin.title
						+ "Please create a test base first");
				return true;
			}
			Location playerLoc = player.getLocation();
			Location testLoc = playerHandler.getPlayersInitialLocation(UUID
					.fromString("00000000-0000-0000-0000-000000000000"));
			if (playerLoc.getWorld() != testLoc.getWorld()) {
				player.sendMessage(BBPlugin.title
						+ "You need to be in the same world.");
				return true;
			}
			int xOff = playerLoc.getBlockX() - testLoc.getBlockX(),
			yOff = playerLoc.getBlockY() - testLoc.getBlockY(),
			zOff = playerLoc.getBlockZ() - testLoc.getBlockZ();
			updateHomeOffset(xOff, yOff, zOff);
			player.sendMessage(BBPlugin.title + "X offset " + xOff
					+ " Y offset " + yOff + " Z offset " + zOff);
			break;
		case "reset":
			if (!player.isOp())
				break;
			if (arg3.length == 1) {
				player.sendMessage(BBPlugin.title
						+ "Please use /bb reset <username>");
				break;
			}
			Player toBeReset = Bukkit.getPlayer(arg3[1]);
			if (toBeReset == null)
				player.sendMessage(BBPlugin.title
						+ "Please make sure the player is online and you spelt their name correctly.");
			else {
				playerHandler.deleteFile(toBeReset.getUniqueId());
				player.sendMessage(BBPlugin.title + "Reset "
						+ toBeReset.getName() + "'s base.");
			}
			break;
		}
		return true;
	}

	public int getInitialheight() {
		return initialheight;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void setX(int x) {
		this.x = x;
	}

	private void pasteTestBase() {
		UUID testUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
		Location loc = playerHandler.getPlayersInitialLocation(testUUID);
		loc.setY(loc.getY());
		for (int i = 0; i < schematics.size(); i++) {
			System.out.println("Y: " + loc.getBlockY());
			jp.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							jp,
							new PastingRunnable(testUUID.toString(), schematics
									.get(i), new int[] { loc.getBlockX(),
									loc.getBlockY(), loc.getBlockZ() }), i * 10);
			loc.setY(loc.getY() + schematics.get(i).getHeight() - 1);
		}
		createRegion("TEST", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	private void pasteBases(Player player) {
		List<BedrockSchematic> toPaste = new ArrayList<BedrockSchematic>();
		List<String> used = playerHandler.getUsedSchematics(player
				.getUniqueId());
		for (String s : used) {
			System.out.println("Used |" + s + "|");
		}
		int currentY = 0;
		for (BedrockSchematic bs : schematics) {
			System.out.println("Player " + player.getName()
					+ " has permission " + bs.getPermission() + " "
					+ player.hasPermission(bs.getPermission()));
			if (player.hasPermission(bs.getPermission())) {
				if (used.contains(bs.getName()))
					currentY += (bs.getHeight() - 1);
				else
					toPaste.add(bs);
			}
		}
		if (toPaste.isEmpty()) {
			player.sendMessage(BBPlugin.title
					+ "You do not have any levels for your base remaining.");
			return;
		}
		player.sendMessage(BBPlugin.title + "Pasting " + toPaste.size()
				+ " level" + (toPaste.size() > 1 ? "s" : "")
				+ ". You will recieve a message when the pasting is finished.");
		Location loc = playerHandler.getPlayersInitialLocation(player
				.getUniqueId());
		loc.setY(loc.getY() + currentY);
		for (int i = 0; i < toPaste.size(); i++) {
			System.out.println("Y: " + loc.getBlockY());
			playerHandler.setPasting(toPaste.get(i).getName(),
					player.getUniqueId());
			AWEListener.addPlayer(player.getUniqueId(), toPaste.get(i));
			jp.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							jp,
							new PastingRunnable(
									player.getUniqueId().toString(), toPaste
											.get(i), new int[] {
											loc.getBlockX(), loc.getBlockY(),
											loc.getBlockZ() }), i * 20 * 5);

			loc.setY(loc.getY() + toPaste.get(i).getHeight() - 1);
		}
		createRegion(player.getName(), loc.getBlockX(), loc.getBlockY(),
				loc.getBlockZ());

	}

	private void createRegion(String name, int blockX, int blockY, int blockZ) {
		RegionManager rm = BBPlugin.worldGuard.getRegionManager(Bukkit
				.getWorld(BBPlugin.world));
		BedrockSchematic bs = schematics.get(0);
		System.out.println("b1 " + blockX + " " + initialheight + " " + blockZ);
		System.out.println("b2 " + blockX + bs.getDepth() + " " + blockY + " "
				+ blockZ + bs.getWidth());
		BlockVector b1 = new BlockVector(blockX + 1, initialheight, blockZ + 1);
		BlockVector b2 = new BlockVector(blockX + bs.getWidth(), blockY, blockZ
				+ bs.getDepth());
		ProtectedCuboidRegion pr = new ProtectedCuboidRegion(name, b1, b2);
		pr.setPriority(1);
		pr.setFlag(DefaultFlag.BUILD, State.ALLOW);
		pr.setFlag(DefaultFlag.CHEST_ACCESS, State.ALLOW);
		BBPlugin.worldGuard.getGlobalRegionManager()
				.get(Bukkit.getWorld(BBPlugin.world)).addRegion(pr);
		try {
			rm.save();
			BBPlugin.worldGuard.getGlobalRegionManager()
					.get(Bukkit.getWorld(BBPlugin.world)).save();
		} catch (ProtectionDatabaseException e) {
			e.printStackTrace();
		}
	}
}
