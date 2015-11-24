package com.addongaming.hcessentials.chatmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.SubPlugin;

public class ChatManager implements SubPlugin {
	private static JavaPlugin jp;

	public static boolean fileExists(final String fileName) {
		File file = new File(jp.getDataFolder().getAbsolutePath() + "/"
				+ fileName);
		return file.exists();
	}

	public static File getFile(final String fileName) {
		return new File(jp.getDataFolder().getAbsolutePath() + "/" + fileName);
	}

	public static Object load(final String fileName) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(jp.getDataFolder().getAbsolutePath() + "/"
						+ fileName));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	public static void save(final Object obj, final String fileName)
			throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(jp.getDataFolder().getAbsolutePath() + "/"
						+ fileName));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public ChatManager(JavaPlugin jp) {
		ChatManager.jp = jp;
		FileConfiguration fc = jp.getConfig();
		fc.addDefault("chat.filterenabled", true);
		fc.options().copyDefaults(true);
		jp.saveConfig();
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onEnable() {
		ChatFilter cf = new ChatFilter(jp, jp.getConfig().getBoolean(
				"chat.filterenabled"));
		jp.getServer().getPluginManager().registerEvents(cf, jp);
		jp.getServer().getPluginCommand("chatfilter").setExecutor(cf);
		jp.getServer().getPluginCommand("chattoggle").setExecutor(cf);
		jp.getServer().getPluginManager()
				.registerEvents(new ChatSorting(jp), jp);
		ChatDeaths cd = new ChatDeaths(jp);
		jp.getServer().getPluginCommand("chatdeath").setExecutor(cd);
		jp.getServer().getPluginManager().registerEvents(cd, jp);
		YTChatManager ytc = new YTChatManager(jp);
		jp.getServer().getPluginManager().registerEvents(ytc, jp);

		// Check that the ChatManager folder exists
		File d = new File(jp.getDataFolder() + File.separator + "chatmanager");
		// If the directory does not exist
		if (!d.exists()) {
			// Create the directory
			if (d.mkdir()) {
				// TODO Write to the log that the folder was created
				System.out.println("chatmanager folder was created");
			} else {
				// TODO Write to the log that the folder was not created
				System.out.println("charmanager folder could not be created");
			}

			// Check if the bannedWords.txt file exists
			File f = new File(jp.getDataFolder() + File.separator
					+ "chatmanager" + File.separator + "bannedWords.txt");
			// If the files does not exist
			if (!f.exists()) {
				// Create the file
				try {
					if (f.createNewFile()) {
						// Write to the log that the file was created
						System.out.println("bannedWords.txt file was created");
					} else {
						// Write to the log that the file was not created
						System.out
								.println("bannedWords.txt file could not be created");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return true;
	}
}
