package com.addongaming.hcessentials.minigames.games.onevone;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import net.ess3.api.InvalidWorldException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.addongaming.hcessentials.HcEssentials;
import com.addongaming.hcessentials.minigames.games.OneVOne;
import com.addongaming.hcessentials.minigames.games.OneVOne.Tier;
import com.addongaming.hcessentials.redeem.SyncInventory;
import com.earth2me.essentials.commands.WarpNotFoundException;

public class OneVOneGame {
	private final String player1;
	private final String player2;
	public final static HashMap<String, SyncInventory> armour = new HashMap<String, SyncInventory>();
	public final static HashMap<String, SyncInventory> inven = new HashMap<String, SyncInventory>();
	private long start;
	private Tier tier;
	private boolean started = false, finished = false;
	private final String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN
			+ "1v1" + ChatColor.GOLD + "] " + ChatColor.RESET;

	public boolean shouldRemove() {
		if (!started && start + 30000 < new Date().getTime() && !finished) {
			Player issuer = Bukkit.getPlayer(player1);
			Player reciever = Bukkit.getPlayer(player2);
			if (issuer != null && issuer.isOnline())
				issuer.sendMessage(prefix + "Request to " + player2
						+ " has timed out.");
			if (reciever != null && reciever.isOnline() && tier != null)
				reciever.sendMessage(prefix + "Request has timed out.");
			return true;
		} else {
			Player issuer = Bukkit.getPlayer(player1);
			Player reciever = Bukkit.getPlayer(player2);
			if ((issuer == null || !issuer.isOnline())
					|| (reciever == null || !reciever.isOnline()))
				return true;
		}
		return finished;
	}

	public OneVOneGame(String player1, String player2, Tier tier) {
		this.tier = tier;
		this.player1 = player1;
		this.player2 = player2;
		if (tier != null)
			start = new Date().getTime();
		else
			start = new Date().getTime() + 20000;
	}

	public boolean isInGame(String str) {
		return (player1.equalsIgnoreCase(str) || player2.equalsIgnoreCase(str));
	}

	public void playerDied(String str) {
		Player winner = (!player1.equalsIgnoreCase(str) ? Bukkit
				.getPlayer(player1) : Bukkit.getPlayer(player2));
		Player loser = (player1.equalsIgnoreCase(str) ? Bukkit
				.getPlayer(player1) : Bukkit.getPlayer(player2));
		OneVOne.getInstance().scheduleItemBack(loser,
				inven.get(loser.getName()).getContents(),
				armour.get(loser.getName()).getContents());
		for (PotionEffect pe : winner.getActivePotionEffects())
			winner.removePotionEffect(pe.getType());
		for (PotionEffect pe : loser.getActivePotionEffects())
			loser.removePotionEffect(pe.getType());
		/*
		 * OneVOne.getInstance().scheduleItemBack(winner,
		 * inven.get(winner.getName()).getContents(),
		 * armour.get(winner.getName()).getContents());
		 */
		/**
		 * OneVOne.getInstance().scheduleItemBack(Bukkit.getPlayer(player2),
		 * inven.get(player2).getContents(), armour.get(player2).getContents());
		 */
		loser.setHealth(20.0d);
		winner.setHealth(20.0d);
		winner.setFoodLevel(20);
		loser.setFoodLevel(20);
		winner.setFireTicks(0);
		loser.setFireTicks(0);
		winner.sendMessage(prefix + "Congratulations on winning!");
		loser.sendMessage(prefix + winner.getName()
				+ " won. Better luck next time.");
		finished = true;
		try {
			Bukkit.getPlayer(player1).getInventory().clear();
			Bukkit.getPlayer(player1).getInventory()
					.setArmorContents(armour.get(player1).getContents());
			Bukkit.getPlayer(player1).getInventory()
					.setContents(inven.get(player1).getContents());
			Bukkit.getPlayer(player2).getInventory().clear();
			Bukkit.getPlayer(player2).getInventory()
					.setArmorContents(armour.get(player2).getContents());
			Bukkit.getPlayer(player2).getInventory()
					.setContents(inven.get(player2).getContents());
			for (Player p : Bukkit.getOnlinePlayers())
				if (!HcEssentials.essentials.getUser(p).isVanished()) {
					winner.showPlayer(p);
					loser.showPlayer(p);
				}
			winner.teleport(HcEssentials.essentials.getWarps().getWarp("spawn"));
			loser.teleport(HcEssentials.essentials.getWarps().getWarp("spawn"));
		} catch (WarpNotFoundException | InvalidWorldException e) {
			e.printStackTrace();
		}
	}

	public void debug(CommandSender cs) {
		cs.sendMessage("Finished:" + finished);
		cs.sendMessage("Player1:" + player1);
		cs.sendMessage("Player2:" + player2);
		cs.sendMessage("Tier:" + (tier == null ? "null" : tier.name()));

	}

	public void start() {
		Player issuer = Bukkit.getPlayer(player1);
		Player reciever = Bukkit.getPlayer(player2);
		for (PotionEffect pe : issuer.getActivePotionEffects())
			issuer.removePotionEffect(pe.getType());
		for (PotionEffect pe : reciever.getActivePotionEffects())
			reciever.removePotionEffect(pe.getType());
		issuer.sendMessage(prefix + "Teleporting to 1v1 arena, good luck!");
		reciever.sendMessage(prefix + "Teleporting to 1v1 arena, good luck!");
		started = true;
		issuer.setHealth(20.0d);
		reciever.setHealth(20.0d);
		reciever.setFoodLevel(20);
		issuer.setFoodLevel(20);
		reciever.setFireTicks(0);
		issuer.setFireTicks(0);
		issuer.teleport(OneVOne.getInstance().getLocation1());
		reciever.teleport(OneVOne.getInstance().getLocation2());
		inven.put(
				player1,
				new SyncInventory(Arrays.copyOf(issuer.getInventory()
						.getContents(),
						issuer.getInventory().getContents().length)));
		armour.put(
				player1,
				new SyncInventory(Arrays.copyOf(issuer.getInventory()
						.getArmorContents(), issuer.getInventory()
						.getArmorContents().length)));
		inven.put(
				player2,
				new SyncInventory(Arrays.copyOf(reciever.getInventory()
						.getContents(),
						reciever.getInventory().getContents().length)));
		armour.put(
				player2,
				new SyncInventory(Arrays.copyOf(reciever.getInventory()
						.getArmorContents(), reciever.getInventory()
						.getArmorContents().length)));
		switch (tier) {
		case tier1:
			giveItemsTier1(issuer);
			giveItemsTier1(reciever);
			break;
		case tier2:
			giveItemsTier2(issuer);
			giveItemsTier2(reciever);
			break;
		case tier3:
			giveItemsTier3(issuer);
			giveItemsTier3(reciever);
			break;
		case instasoup:
			giveItemsInstaSoup(issuer);
			giveItemsInstaSoup(reciever);
			break;
		case archery:
			giveArchery(issuer);
			giveArchery(reciever);
			break;
		case sniper:
			giveSniper(issuer);
			giveSniper(reciever);
			break;
		case custom:
			break;
		default:
			break;
		}
		for (Player p : Bukkit.getOnlinePlayers())
			if (!p.getName().equalsIgnoreCase(issuer.getName())
					&& !p.getName().equalsIgnoreCase(reciever.getName())) {
				issuer.hidePlayer(p);
				reciever.hidePlayer(p);
			}
	}

	private void giveItemsTier1(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 3);
		issuer.getInventory().addItem(weapon);
	}

	private void giveItemsTier2(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 4);
		weapon.addEnchantment(Enchantment.KNOCKBACK, 1);
		issuer.getInventory().addItem(weapon);
	}

	private void giveItemsTier3(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		helmet.addEnchantment(Enchantment.THORNS, 1);
		chest.addEnchantment(Enchantment.THORNS, 1);
		legs.addEnchantment(Enchantment.THORNS, 1);
		boots.addEnchantment(Enchantment.THORNS, 1);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 4);
		weapon.addEnchantment(Enchantment.KNOCKBACK, 1);
		weapon.addEnchantment(Enchantment.DURABILITY, 2);
		issuer.getInventory().addItem(weapon);
	}

	private void giveItemsInstaSoup(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 4);
		weapon.addEnchantment(Enchantment.KNOCKBACK, 1);
		issuer.getInventory().addItem(weapon);
		for (int i = 0; i < 5; i++)
			issuer.getInventory().addItem(
					new ItemStack(Material.MUSHROOM_SOUP, 1));
	}

	private void giveArchery(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.BOW);
		weapon.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		issuer.getInventory().addItem(weapon);
		weapon = new ItemStack(Material.WOOD_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		issuer.getInventory().addItem(weapon);
		issuer.getInventory().addItem(new ItemStack(Material.ARROW, 32));
	}

	private void giveSniper(Player issuer) {
		issuer.getInventory().clear();
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		issuer.getInventory().setHelmet(helmet);
		issuer.getInventory().setChestplate(chest);
		issuer.getInventory().setLeggings(legs);
		issuer.getInventory().setBoots(boots);
		ItemStack weapon = new ItemStack(Material.BOW);
		weapon.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
		issuer.getInventory().addItem(weapon);
		weapon = new ItemStack(Material.WOOD_SWORD);
		weapon.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		issuer.getInventory().addItem(weapon);
		issuer.getInventory().addItem(new ItemStack(Material.ARROW, 10));
	}

	public void markRemoval() {
		finished = true;
	}

	public boolean hasStarted() {
		return started;
	}

	public String getIssuer() {
		return player1;
	}

	public void decline() {
		finished = true;
		if (Bukkit.getPlayer(player1) != null
				&& Bukkit.getPlayer(player1).isOnline())
			Bukkit.getPlayer(player1).sendMessage(prefix + " Game cancelled.");
		if (Bukkit.getPlayer(player2) != null
				&& Bukkit.getPlayer(player2).isOnline())
			Bukkit.getPlayer(player2).sendMessage(prefix + " Game cancelled.");
	}

	public void setTier(Tier tier) {
		this.tier = tier;
		start = new Date().getTime();
	}

	public boolean isCurrentTier(Tier checkTier) {
		return this.tier == checkTier;
	}

	public void initInvite() {
		Player p = Bukkit.getPlayer(this.player2);
		Player issuer = Bukkit.getPlayer(player1);
		issuer.sendMessage(prefix + "Sent request to " + p.getName());
		p.sendMessage(prefix + issuer.getName()
				+ " has invited you to a game of 1v1!");
		p.sendMessage(prefix
				+ "To accept do /1v1 accept, to decline /1v1 decline");
	}

}
