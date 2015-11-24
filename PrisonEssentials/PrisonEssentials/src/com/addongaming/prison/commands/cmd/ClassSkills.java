package com.addongaming.prison.commands.cmd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class ClassSkills implements CommandExecutor {
	HashMap<String, Date> claimTime = new HashMap<String, Date>();

	private DataReturn canIssueCommand(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance()
				.getPrisonerInfo(p.getName());
		if (!claimTime.containsKey(p.getName())
				|| claimTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!(arg0 instanceof Player))
			return true;
		Prisoner p = PrisonerManager.getInstance()
				.getPrisonerInfo(arg0.getName());
		if (canIssueCommand((Player) (arg0)) == DataReturn.FAILURE) {
			arg0.sendMessage(ChatColor.RED
					+ "You cannot issue that command just yet.");
			return true;
		}
		ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) is.getItemMeta();
		bm.setTitle(ChatColor.GRAY + p.getPlayerClass().toText() + " Skills");
		bm.setAuthor(ChatColor.DARK_GRAY + "The Hidden "
				+ p.getPlayerClass().toText());
		List<String> bookList = new ArrayList<String>();
		switch (p.getPlayerClass()) {
		case ASSASSIN:
			bookList.add("By sneaking behind a player you may surprise them with a sneak attack.\nThis can cause bonus damage.\nMust not be in combat.");
			break;
		case EXOTICDEALER:
			break;
		case GUARD:
			break;
		case LIMBO:
			break;
		case MURDERER:
			bookList.add("By jumping and sneaking when not in combat you have extra power to add to your attack.");
			break;
		case SNITCH:
			break;
		case THIEF:
			bookList.add("By sneaking behind a player and right clicking you can try to reach and steal an item from their pockets.");
			break;
		default:
			break;
		}
		bm.setPages(bookList);
		is.setItemMeta(bm);
		((Player) (arg0)).getInventory().addItem(is);
		((Player) (arg0)).updateInventory();
		arg0.sendMessage(ChatColor.GREEN + "Here you go!");
		claimTime.put(arg0.getName(), new Date(new Date().getTime() + 30000));
		return true;
	}
}
