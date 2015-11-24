package com.addongaming.hcessentials.hooks.logging;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class BlockLoggingHook implements SubPlugin {
	private JavaPlugin jp;

	public BlockLoggingHook(JavaPlugin jp) {
		this.jp = jp;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	private CoreProtectAPI api;
	private static BlockLoggingHook instance = null;

	public static BlockLoggingHook getInstance() {
		return instance;
	}

	public static boolean hasInstance() {
		return instance != null;
	}

	public CoreProtectAPI getApi() {
		return api;
	}

	@Override
	public boolean onEnable() {
		api = getCoreProtect();
		if (api == null)
			return false;
		instance = this;
		return true;
	}

	private CoreProtectAPI getCoreProtect() {
		Plugin plugin = jp.getServer().getPluginManager()
				.getPlugin("CoreProtect");
		// Check that CoreProtect is loaded
		if (plugin == null || !(plugin instanceof CoreProtect)) {
			return null;
		}
		// Check that the API is enabled
		CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
		if (CoreProtect.isEnabled() == false) {
			return null;
		}
		// Check that a compatible version of the API is loaded
		if (CoreProtect.APIVersion() < 2) {
			return null;
		}
		return CoreProtect;
	}
}
