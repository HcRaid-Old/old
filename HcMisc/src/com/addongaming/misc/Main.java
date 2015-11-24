package com.addongaming.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.isOp())
			return true;
		Player p = (Player) sender;
		if (p.getTargetBlock(null, 20).getType().equals(Material.CHEST)) {
			Chest chest = (Chest) p.getTargetBlock(null, 20).getState();
			ItemStack[] is = chest.getBlockInventory().getContents();
			List<ItemStack> myList = new ArrayList<ItemStack>();
			for (ItemStack item : is)
				if (item != null && item.getType() != null
						&& item.getType().getMaxDurability() < 10)
					myList.add(item);
				else if (item != null && item.getType() != null) {
					if (new Random().nextInt(20) + 1 > 3)
						item.setDurability((short) new Random().nextInt(item
								.getType().getMaxDurability()));
					myList.add(item);
				}
			chest.getBlockInventory().setContents(
					myList.toArray(new ItemStack[myList.size()]));
			p.sendMessage(ChatColor.RED + "Randomised dura.");
		} else {
			p.sendMessage(ChatColor.RED + "Target block is not chest.");
		}
		return true;
	}
}
