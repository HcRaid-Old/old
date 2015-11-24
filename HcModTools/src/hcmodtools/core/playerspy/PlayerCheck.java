package hcmodtools.core.playerspy;

import hcmodtools.core.ModTool;
import hcmodtools.core.Tools;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerCheck extends Tools implements ModTool, CommandExecutor {

	public PlayerCheck(JavaPlugin jp) {
		super(ChatColor.GOLD + "[" + ChatColor.GREEN + "HcPlayerCheck"
				+ ChatColor.GOLD + "] " + ChatColor.RESET, ChatColor.DARK_RED
				+ "[" + ChatColor.RED + "HcPlayerCheck" + ChatColor.DARK_RED
				+ "] " + ChatColor.RESET);
		jp.getCommand("playercheck").setExecutor(this);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("hcraid.mod")) {
			arg0.sendMessage(ChatColor.RED
					+ "Sorry, you do not have permission for this command.");
			return true;
		}
		if (arg3.length == 0) {
			return warn(arg0, "Plase use /playercheck <playername>");

		}
		Player target = Bukkit.getPlayer(arg3[0]);
		String[] names = ChatColor.stripColor(target.getDisplayName()).split(
				"( )");
		String name = names[names.length - 1];
		if (target == null || !target.isOnline())
			return warn(arg0, "Player " + arg3[0] + " cannot be found.");
		if (target.getName().equalsIgnoreCase(name))
			msg(arg0, "-Player: " + target.getName() + "-");
		else
			msg(arg0, "-Player: " + target.getName() + "-Display name: " + name
					+ "-");
		ItemStack i = target.getInventory().getHelmet();
		// helmet
		if (i == null)
			smsg(arg0, "Helmet: None");
		else {
			smsg(arg0, "Helmet: "
					+ i.getType().toString().toLowerCase().replace('_', ' '));
			if (i.getEnchantments().size() == 0) {
				smsg(arg0, "Helmet enchantments: None");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Enchantment e : i.getEnchantments().keySet()) {
					sb.append(getRealName(e) + " #" + i.getEnchantmentLevel(e)
							+ ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				smsg(arg0, "Helmet enchantments: " + sb.toString());
			}
		}
		// chestplate
		i = target.getInventory().getChestplate();
		if (i == null)
			smsg(arg0, "Chestplate: None");
		else {
			smsg(arg0, "Chestplate: "
					+ i.getType().toString().toLowerCase().replace('_', ' '));
			if (i.getEnchantments().size() == 0) {
				smsg(arg0, "Chestplate enchantments: None");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Enchantment e : i.getEnchantments().keySet()) {
					sb.append(getRealName(e) + " #" + i.getEnchantmentLevel(e)
							+ ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				smsg(arg0, "Chestplate enchantments: " + sb.toString());
			}
		}
		// leggings
		i = target.getInventory().getLeggings();
		if (i == null)
			smsg(arg0, "Leggings: None");
		else {
			smsg(arg0, "Leggings: "
					+ i.getType().toString().toLowerCase().replace('_', ' '));
			if (i.getEnchantments().size() == 0) {
				smsg(arg0, "Leggings enchantments: None");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Enchantment e : i.getEnchantments().keySet()) {
					sb.append(getRealName(e) + " #" + i.getEnchantmentLevel(e)
							+ ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				smsg(arg0, "Leggings enchantments: " + sb.toString());
			}
		}
		// boots
		i = target.getInventory().getBoots();
		if (i == null)
			smsg(arg0, "Boots: None");
		else {
			smsg(arg0, "Boots: "
					+ i.getType().toString().toLowerCase().replace('_', ' '));
			if (i.getEnchantments().size() == 0) {
				smsg(arg0, "Boots enchantments: None");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Enchantment e : i.getEnchantments().keySet()) {
					sb.append(getRealName(e) + " #" + i.getEnchantmentLevel(e)
							+ ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				smsg(arg0, "Boots enchantments: " + sb.toString());
			}
		}
		// iteminhand
		i = target.getItemInHand();
		if (i == null)
			smsg(arg0, "Item in hand: None");
		else {
			smsg(arg0, "Item in hand: "
					+ i.getType().toString().toLowerCase().replace('_', ' '));
			if (i.getEnchantments().size() == 0) {
				smsg(arg0, "Item in hand enchantments: None");
			} else {
				StringBuilder sb = new StringBuilder();
				for (Enchantment e : i.getEnchantments().keySet()) {
					sb.append(getRealName(e) + " #" + i.getEnchantmentLevel(e)
							+ ", ");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
				smsg(arg0, "Item in hand enchantments: " + sb.toString());
			}
		}
		smsg(arg0,
				"Health: "
						+ new DecimalFormat("##.#").format(target.getHealth())
						+ "/"
						+ new DecimalFormat("##.#").format(target
								.getMaxHealth()) + " saturation "
						+ target.getSaturation());
		msg(arg0, "");
		return true;
	}

	private String getRealName(Enchantment e) {
		Set<Entry<String, Enchantment>> entrySet = com.earth2me.essentials.Enchantments
				.entrySet();
		for (Entry<String, Enchantment> entry : entrySet) {
			if (entry.getValue().equals(e))
				return entry.getKey();
		}
		return e.getName().toLowerCase();
	}

}
