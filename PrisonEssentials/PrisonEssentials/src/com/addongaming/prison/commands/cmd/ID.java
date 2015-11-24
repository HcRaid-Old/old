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

import com.addongaming.prison.classes.PlayerClasses;
import com.addongaming.prison.data.DataReturn;
import com.addongaming.prison.data.skills.HatchetData;
import com.addongaming.prison.data.skills.MiningData;
import com.addongaming.prison.data.skills.PickaxeData;
import com.addongaming.prison.data.skills.TreeData;
import com.addongaming.prison.player.Prisoner;
import com.addongaming.prison.player.PrisonerManager;

public class ID implements CommandExecutor {
	HashMap<String, Date> claimTime = new HashMap<String, Date>();

	private DataReturn canIssueCommand(Player p) {
		Prisoner prisoner = PrisonerManager.getInstance()
				.getPrisonerInfo(p.getName());
		if (!claimTime.containsKey(p.getName())
				|| claimTime.get(p.getName()).before(new Date()))
			return DataReturn.SUCCESS;
		return DataReturn.FAILURE;

	}

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
		bm.setTitle(ChatColor.GREEN + arg0.getName() + "'s passport");
		bm.setAuthor(ChatColor.DARK_GREEN + arg0.getName());
		List<String> bookList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		// Front Page
		sb.append(ChatColor.BOLD + "   " + ChatColor.UNDERLINE
				+ "---Class---\n\n" + ChatColor.RESET);
		sb.append(ChatColor.BOLD + "  --" + p.getPlayerClass().toText()
				+ "--\n\n" + ChatColor.RESET);
		sb.append(ChatColor.BOLD + "Level: " + ChatColor.RESET
				+ PlayerClasses.getLevel(p.getClassExp()) + "\n");
		sb.append(ChatColor.BOLD + "Exp: " + ChatColor.RESET + ""
				+ p.getClassExp() + "\n");
		sb.append(ChatColor.BOLD + "Exp till level: " + ChatColor.RESET
				+ PlayerClasses.expTillLevel(p.getClassExp()) + "\n\n");
		sb.append(ChatColor.BOLD + "" + ChatColor.UNDERLINE
				+ "---Character---\n" + ChatColor.RESET + "\n");
		sb.append(ChatColor.BOLD + "Level: " + ChatColor.RESET
				+ PlayerClasses.getLevel(p.getCharacterExp()) + "\n");
		sb.append(ChatColor.BOLD + "Exp: " + ChatColor.RESET + ""
				+ p.getCharacterExp() + "\n");
		sb.append(ChatColor.BOLD + "Exp till level: " + ChatColor.RESET
				+ PlayerClasses.expTillLevel(p.getCharacterExp()) + "\n");
		bookList.add(sb.toString());
		// Mining skills
		sb = new StringBuilder();
		sb.append(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Mining\n");
		for (MiningData md : MiningData.values())
			if (p.hasPermission(md.getPermission()))
				sb.append(ChatColor.GREEN + md.toText() + "\n");
			else
				sb.append(ChatColor.RED + md.toText() + "\n");
		bookList.add(sb.toString());
		// Pickaxes
		sb = new StringBuilder();
		sb.append(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Pickaxes\n");
		for (PickaxeData md : PickaxeData.values())
			if (p.hasPermission(md.getPermission()))
				sb.append(ChatColor.GREEN + md.toText() + "\n");
			else
				sb.append(ChatColor.RED + md.toText() + "\n");
		bookList.add(sb.toString());
		// Trees
		sb = new StringBuilder();
		sb.append(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Woodcutting\n");
		for (TreeData md : TreeData.values())
			if (p.hasPermission(md.getPermission()))
				sb.append(ChatColor.GREEN + md.toText() + "\n");
			else
				sb.append(ChatColor.RED + md.toText() + "\n");
		bookList.add(sb.toString());
		// Axes
		sb = new StringBuilder();
		sb.append(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Hatchets\n");
		for (HatchetData md : HatchetData.values())
			if (p.hasPermission(md.getPermission()))
				sb.append(ChatColor.GREEN + md.toText() + "\n");
			else
				sb.append(ChatColor.RED + md.toText() + "\n");
		bookList.add(sb.toString());
		bm.setPages(bookList);
		is.setItemMeta(bm);
		((Player) (arg0)).getInventory().addItem(is);
		arg0.sendMessage(ChatColor.GREEN + "Here you go!");
		claimTime.put(arg0.getName(), new Date(new Date().getTime() + 30000));
		return true;
	}
}
