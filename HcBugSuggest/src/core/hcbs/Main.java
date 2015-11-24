package core.hcbs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements CommandExecutor {
	File bug;
	File suggest;

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();
		bug = new File(this.getDataFolder() + "\\bug.txt");
		suggest = new File(this.getDataFolder() + "\\suggest.txt");
		try {
			if (!bug.exists())
				bug.createNewFile();
			if (!suggest.exists())
				suggest.createNewFile();
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length <= 3) {
			sender.sendMessage("Please use more than 3 words. /"
					+ command.getName() + " [message]");
			return true;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(sender.getName()).append("  |  ");
		for (String s : args)
			sb.append(s + " ");
		switch (command.getName().toLowerCase()) {
		case "bug":
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(bug, true));
				pw.println(sb.toString());
				pw.close();
				sender.sendMessage("Thank you for your bug report! Please do not abuse this system.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "suggest":
			PrintWriter pw;
			try {
				pw = new PrintWriter(new FileWriter(suggest, true));
				pw.println(sb.toString());
				pw.close();
				sender.sendMessage("Thank you for your suggestion! Please do not abuse this system.");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return true;
	}
}
