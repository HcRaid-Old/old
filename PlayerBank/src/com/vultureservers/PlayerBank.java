package com.vultureservers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class PlayerBank extends JavaPlugin implements Listener, CommandExecutor{
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + "Version" + pdfFile.getVersion() + ChatColor.GREEN + "Has Been Enabled!");
		this.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + "Version" + pdfFile.getVersion() + ChatColor.GREEN + "Has Been Enabled!");
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("createbank")) {
			Player p = (Player) sender;
			 if (p.getTargetBlock(null, 20).getType().equals(Material.CHEST)) {
				   p.getTargetBlock(null, 20).setData((byte) 9);
				   p.sendMessage(ChatColor.RED + "[VultureCraft] " + ChatColor.GOLD + "Bank chest has been created!");
				  } else {
				   p.sendMessage(ChatColor.RED + "[VultureCraft] " + ChatColor.GOLD + "Target block is not a chest.");
				  }
			
		}
		return false;
			
		}
	
		
	@EventHandler
	public void playerClickedChest(PlayerInteractEvent pie){
		if(pie.getClickedBlock()!=null && pie.getClickedBlock().getType() == Material.CHEST){
		if(pie.getClickedBlock().getData()==(byte)9){
			Player p = pie.getPlayer();
			if(p.hasPermission("playerbank.donor")){

			} else {
				
			p.sendMessage(ChatColor.RED + "[VultureCraft] " + ChatColor.GOLD + "You must be " + ChatColor.GREEN + "Ghast-Donor +" + ChatColor.GOLD + " to access this bank.");
			pie.setCancelled(true);
			}
		}
	}
	}
	
}
