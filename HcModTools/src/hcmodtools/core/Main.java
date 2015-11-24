package hcmodtools.core;

import hcmodtools.core.alt.AltChecking;
import hcmodtools.core.chatcontrol.ChatControl;
import hcmodtools.core.chatcontrol.ChatMonitor;
import hcmodtools.core.hackergrunt.HackerGrunt;
import hcmodtools.core.monitor.AccountMonitor;
import hcmodtools.core.playerspy.CommandSpy;
import hcmodtools.core.playerspy.DamageMonitor;
import hcmodtools.core.playerspy.PlayerCheck;
import hcmodtools.core.playerspy.PlayerFlagger;
import hcmodtools.core.playerspy.SocialSpy;
import hcmodtools.core.raidingreport.RaidReports;
import hcmodtools.core.silentmute.SilentMute;
import hcmodtools.core.staffchat.StaffChat;
import hcmodtools.core.vanish.Vanish;
import hcmodtools.core.xray.AntiXray;
import hcmodtools.core.xray.OreNotification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class Main extends JavaPlugin {
	ArrayList<ModTool> modTools = new ArrayList<ModTool>();
	public static Essentials essentials;
	private static JavaPlugin jp;

	@Override
	public void onEnable() {
		jp = this;
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();
		essentials = (Essentials) getServer().getPluginManager().getPlugin(
				"Essentials");
		setupPermissions();
		setupConfig();
		setupSubPlugins();
		for (ModTool mt : modTools)
			mt.onStart();
	}

	private void setupConfig() {
		FileConfiguration fc = getConfig();
		fc.addDefault("server.name", "Server name here");
		fc.options().copyDefaults(true);
		saveConfig();
		reloadConfig();
	}

	public static void save(final Object obj, final String fileName)
			throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(jp.getDataFolder().getAbsolutePath()
						+ File.separatorChar + fileName));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public static Object load(final String fileName) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(jp.getDataFolder().getAbsolutePath()
						+ File.separatorChar + fileName));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	public static boolean fileExists(final String fileName) {
		File file = new File(jp.getDataFolder().getAbsolutePath()
				+ File.separatorChar + fileName);
		return file.exists();
	}

	public static File getFile(final String fileName) {
		return new File(jp.getDataFolder().getAbsolutePath()
				+ File.separatorChar + fileName);

	}

	public static Permission permission = null;

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	@Override
	public void onDisable() {
		for (ModTool mt : modTools)
			mt.onStop();
	}

	private void setupSubPlugins() {
		Vanish van = new Vanish(this);
		modTools.add(van);
		AntiXray ax = new AntiXray(this);
		modTools.add(ax);
		SilentMute sm = new SilentMute(this);
		modTools.add(sm);
		OreNotification on = new OreNotification(this);
		modTools.add(on);
		CommandSpy cs = new CommandSpy(this);
		modTools.add(cs);
		SocialSpy sp = new SocialSpy(this);
		modTools.add(sp);
		DamageMonitor dmgMon = new DamageMonitor(this);
		modTools.add(dmgMon);
		HackerGrunt hg = new HackerGrunt(this);
		modTools.add(hg);
		PlayerFlagger pf = new PlayerFlagger(this);
		modTools.add(pf);
		ChatControl cc = new ChatControl(this);
		modTools.add(cc);
		AccountMonitor am = new AccountMonitor(getDataFolder(), this);
		modTools.add(am);
		RaidReports rr = new RaidReports(this);
		modTools.add(rr);
		PlayerCheck pc = new PlayerCheck(this);
		modTools.add(pc);
		AltChecking ac = new AltChecking(this);
		modTools.add(ac);
		StaffChat sc = new StaffChat(this);
		modTools.add(sc);
		/*
		 * Seen seen = new Seen(this); modTools.add(seen); Whois whois = new
		 * Whois(this); modTools.add(whois);
		 */
	}
}
