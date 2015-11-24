package com.addongaming.hcessentials.teams.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.addongaming.hcessentials.teams.Ranks;
import com.addongaming.hcessentials.teams.Team;
import com.addongaming.hcessentials.teams.TeamCore;
import com.addongaming.hcessentials.teams.TeamMethods;

public class TeamChatHandler extends TeamMethods implements CommandExecutor {
	public static boolean canTeam = false;
	public static List<String> chatToggled = new ArrayList<String>();
	public static final Map<String, Integer> teleporting = new HashMap<String, Integer>();
	JavaPlugin jp;
	private final int maxPlayers;

	private final int minPlayersForHome;

	private SQLite sqlite;

	public TeamChatHandler(JavaPlugin jp, SQLite sqlite) {
		this.jp = jp;
		maxPlayers = jp.getConfig().getInt("teams.maxpeople");
		this.sqlite = sqlite;
		minPlayersForHome = jp.getConfig().getInt("teams.homes.minreq");
	}

	private void accept(Player cmdsndr, boolean yes) {
		Team toaccept = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cmdsndr.getName()) && yes) {
				warn(cmdsndr, "You are already in a team.");
				return;
			}
			if (t.containsInvite(cmdsndr.getName())) {
				toaccept = t;
				break;
			}
		}
		if (toaccept == null) {
			warn(cmdsndr, "You have no invitations to accept.");
			return;
		}
		if (yes) {
			toaccept.addPlayer(cmdsndr.getName());
			String teamName = toaccept.getTeamName();
			int rs;
			try {
				rs = sqlite
						.query("SELECT t_id FROM team WHERE t_name='"
								+ teamName + "'").getInt("t_id");
				sqlite.query("INSERT INTO members(t_id, m_name, m_status) VALUES("
						+ rs
						+ ", '"
						+ cmdsndr.getName()
						+ "', "
						+ Ranks.member.getRank() + ");");

			} catch (SQLException e) {
				e.printStackTrace();
			}
			for (String playersName : toaccept.getMembers()) {
				if (Bukkit.getPlayer(playersName) != null) {
					Bukkit.getPlayer(playersName).sendMessage(
							DGREN + "[HcTeams] " + cmdsndr.getName()
									+ " has joined " + toaccept.getTeamName());

				}
			}
		}
		toaccept.removeInvite(cmdsndr.getName());
		Player leader = null;
		for (Player lea : jp.getServer().getOnlinePlayers()) {
			if (toaccept.isLeader(lea.getName())) {
				leader = lea;
				break;
			}
		}

		if (leader != null) {
			if (!yes) {
				leader.sendMessage(ChatColor.RED + TeamCore.pluginName + DAQU
						+ cmdsndr.getName() + " has declined your invite for "
						+ BLU + toaccept.getTeamName() + ".");
				cmdsndr.sendMessage(ChatColor.RED + TeamCore.pluginName + DRED
						+ " You Declined the Team invite for " + BLU
						+ toaccept.getTeamName() + ".");
			}
		}
	}

	public boolean argsLength(String[] args, int amt) {
		return args.length > amt;
	}

	public void changePlayerRank(CommandSender cs, int amnt, String name) {
		Team teamIn = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cs.getName())) {
				teamIn = t;
				break;
			} else
				continue;
		}
		if (teamIn == null) {
			warn(cs, "You are not in a team.");
			return;
		}
		if (!teamIn.containsPlayer(name)) {
			warn(cs, name + " is not in your team.");
			return;
		}
		try {
			/*
			 * int inviterRank = sqlite
			 * .query("SELECT * FROM members WHERE m_name='" + cs.getName() +
			 * "'").getInt("m_status"); int inviteeRank = sqlite.query(
			 * "SELECT * FROM members WHERE m_name='" + name + "'")
			 * .getInt("m_status");
			 */
			int inviterRank = teamIn.getRank(cs.getName());
			int inviteeRank = teamIn.getRank(name);
			if (inviteeRank == Ranks.leader.getRank()) {
				warn(cs, "You cannot demote/promote the leader.");
				return;
			}
			Player p = Bukkit.getPlayer(name);
			if (amnt > 0) {
				switch (inviterRank) {
				case 0:// member
					warn(cs, "You don't have permission to promote.");
					return;
				case 1:// mod
					warn(cs, "You don't have permission to promote.");
					return;
				case 2:// leader
					if (inviteeRank > 0) {
						warn(cs, "You cannot promote any further!");
						return;
					}
					sqlite.query("UPDATE members SET m_status=1"
							+ " WHERE m_name='" + name + "'");
					teamIn.promotePlayer(name);
					msg(cs, "You have promoted " + name + ".");
					if (p != null)
						msg(p, "You have been promoted.");
					break;
				}
			} else {
				switch (inviterRank) {
				case 0:// member
					warn(cs, "You don't have permission to demote.");
					return;
				case 1:// mod
					warn(cs, "You don't have permission to demote.");
					return;
				case 2:// leader
					if (inviteeRank < 1) {
						warn(cs, "You cannot demote any further!");
						return;
					}
					sqlite.query("UPDATE members SET m_status=0"
							+ " WHERE m_name='" + name + "'");
					teamIn.demotePlayer(name);
					msg(cs, "You have demoted " + name + ".");
					if (p != null)
						msg(p, "You have been demoted.");
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void changeTeamName(Player p, String newName) {
		Team tl = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(p.getName())) {
				tl = t;
				break;
			}
		}
		if (tl == null) {
			warn(p, "You are not in a team.");
			return;
		} else if (!tl.isLeader(p.getName())) {
			warn(p, "You are not the leader of " + tl.getTeamName());
			return;
		}
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.getTeamName().equalsIgnoreCase(newName)) {
				warn(p, "The name " + newName + " is already in use.");
				return;
			}
		}
		try {
			sqlite.query("UPDATE team SET t_name = '" + newName
					+ "' WHERE t_name = '" + tl.getTeamName() + "'");
			tl.setTeamName(newName);
		} catch (SQLException e) {
			warn(p, "Sorry something went wrong changing your team name.");
			e.printStackTrace();
			return;
		}
		msg(p, "Successfully changed your team name to " + newName);
	}

	public void closeConnection() {
		sqlite.close();
	}

	private void createTeam(Player cmdsndr, String teamName) {
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.isLeader(cmdsndr.getName())) {
				warn(cmdsndr,
						"You already are a team leader. Please use /team disband to disband.");
				return;
			}
			if (t.containsPlayer(cmdsndr.getName())) {
				warn(cmdsndr, "You are already in a team.");
				return;
			}
			if (t.getTeamName().equalsIgnoreCase(teamName)) {
				warn(cmdsndr, "Your team name is in use.");
				return;
			}
		}
		TeamCore.teamSet.add(new Team(cmdsndr.getName(), teamName, null));
		try {

			sqlite.query("INSERT INTO team(t_name) VALUES('" + teamName + "');");

			int i = sqlite.query(
					"SELECT * FROM team WHERE t_name='" + teamName + "'")
					.getInt("t_id");
			sqlite.query("INSERT INTO members(t_id,m_name, m_status) VALUES("
					+ i + ", '" + cmdsndr.getName() + "', "
					+ Ranks.leader.getRank() + ");");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cmdsndr.sendMessage(ChatColor.GREEN + TeamCore.pluginName + DGREN
				+ "Your team " + teamName + " has been created!");
	}

	private void disbandTeam(Player cmdsndr) {
		Team leaderof = null;

		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()]))
			if (t.isLeader(cmdsndr.getName()))
				leaderof = t;
		if (leaderof == null) {
			warn(cmdsndr, "You are not a leader of a team.");
			return;
		}

		try {
			int i = sqlite.query(
					"SELECT * FROM team WHERE t_name='"
							+ leaderof.getTeamName() + "'").getInt("t_id");
			String sqlTeams = "	DELETE FROM team" + " WHERE t_id=" + i;
			String sqlMembers = "	DELETE FROM members" + " WHERE t_id=" + i;
			sqlite.query(sqlTeams);
			sqlite.query(sqlMembers);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeamCore.teamSet.remove(leaderof);
		cmdsndr.sendMessage(ChatColor.GREEN + "[HcTeams]" + DGREN
				+ " You disbanded " + DAQU + leaderof.getTeamName());

	}

	private void invitePlayer(Player cmdsndr, String invitee) {
		Team tl = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cmdsndr.getName())) {
				tl = t;
				break;
			}
		}
		if (tl == null) {
			warn(cmdsndr, "You are not in a team.");
			return;
		}
		if (tl.getRank(cmdsndr.getName()) < Ranks.mod.getRank()) {
			warn(cmdsndr,
					"You are not a high enough rank to invite another player.");
			return;
		}
		if (tl.getMembers().length + tl.getInvited().length >= this.maxPlayers) {
			warn(cmdsndr,
					"Too many players currently in, or invited to "
							+ tl.getTeamName() + " maximum  amount is "
							+ this.maxPlayers + " players.");
			return;
		}
		Player toadd = null;
		for (Player pl : jp.getServer().getOnlinePlayers())
			if (pl.getName().equalsIgnoreCase(invitee)) {
				toadd = pl;
				break;
			}
		if (toadd == null) {
			warn(cmdsndr, "The player you wish to invite isn't online.");
			return;
		}

		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsInvite(invitee)) {
				t.removeInvite(invitee);
			}
		}
		toadd.sendMessage(ChatColor.GREEN + "[HcTeams] " + DGREN
				+ "You have been invited by " + cmdsndr.getDisplayName()
				+ " to join " + tl.getTeamName() + ".");
		toadd.sendMessage(ChatColor.GREEN + "[HcTeams] " + DGREN + "Do "
				+ ChatColor.BOLD + "/team accept" + ChatColor.RESET + DGREN
				+ " to join. Invitation will last 30 seconds.");
		cmdsndr.sendMessage(ChatColor.GREEN + "[HcTeams] " + DGREN
				+ "You have invited " + toadd.getDisplayName()
				+ " to be in your team.");
		tl.addInvite(toadd.getName());
		final Player player = toadd;
		final Team team = tl;
		jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						if (team.containsInvite(player.getName())) {
							player.sendMessage(GREEN + TeamCore.pluginName
									+ DGREN + "Your invite to " + DAQU
									+ team.getTeamName() + DGREN
									+ " has expired.");
							team.removeInvite(player.getName());
						}
					}
				}, 600);
	}

	private void kick(CommandSender cs, String name) {
		Team teamIn = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cs.getName())) {
				teamIn = t;
				break;
			} else
				continue;
		}
		if (teamIn == null) {
			warn(cs, "You are not in a team.");
			return;
		}
		if (!teamIn.containsPlayer(name)) {
			warn(cs, name + " is not in your team.");
			return;
		}
		int inviterRank = teamIn.getRank(cs.getName());
		int inviteeRank = teamIn.getRank(name);
		Player p = Bukkit.getPlayer(name);
		if (inviterRank > inviteeRank) {
			try {
				sqlite.query("DELETE FROM members WHERE m_name='" + name + "'");
				teamIn.removePlayer(name);
				if (p != null)
					warn(p,
							"You have been kicked out of "
									+ teamIn.getTeamName());
				this.msg(cs, "You have kicked " + name + " out of your team.");
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	private void leaveTeam(Player cmdsndr) {
		String playername = cmdsndr.getName();
		Team toleave = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cmdsndr.getName()))
				toleave = t;
		}
		if (toleave == null) {
			warn(cmdsndr, "You are not in a team.");
			return;
		}
		if (toleave.isLeader(playername)) {
			warn(cmdsndr,
					"You are the leader of a team, please use /team disband");
			return;
		}

		toleave.removePlayer(playername);
		try {
			sqlite.query("DELETE FROM members WHERE m_name='" + playername
					+ "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cmdsndr.sendMessage(ChatColor.GREEN + TeamCore.pluginName + DGREN
				+ "You have left " + DAQU + toleave.getTeamName());
		return;
	}

	private void listCommands(CommandSender sender) {
		msg(sender, "");
		safeWarn(sender, "/team create <name> - Creates a new team.");
		safeWarn(sender,
				"/team invite <player> - Invites a player to your team.");
		safeWarn(sender, "/team disband - Disbands your team.");
		safeWarn(sender,
				"/team list - Lists all online/offline members of your team.");
		safeWarn(sender, "/team kick <player> - Kicks a player from your team.");
		safeWarn(sender,
				"/team accept|decline - Accepts/declines team invitations.");
		safeWarn(sender,
				"/team promote|demote <player> - Promotes/demotes a player in your team");
		safeWarn(sender, "/team chat|c - Toggles team chat.");
		safeWarn(sender, "/team rename <newname> - Rename your team.");
		safeWarn(sender,
				"/team leader <playername> - Hand over leadership of your team.");
		if (canTeam) {
			safeWarn(sender, "/team sethome - Sets your team home, min "
					+ minPlayersForHome + " team members");
			safeWarn(sender, "/team home - Teleports you to your team home.");
		}
		msg(sender, "");
	}

	private void listTeam(Player p) {
		boolean in = false;
		Team team = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(p.getName())) {
				in = true;
				team = t;
				break;
			}
		}
		if (!in) {
			warn(p, "You are not in a team.");
			return;
		}
		p.sendMessage(ChatColor.GREEN + TeamCore.pluginName
				+ "Team members of " + team.getTeamName() + " are: ");
		String[] onoff = team.getListedPlayers();

		p.sendMessage(ChatColor.GREEN + " Online players:" + onoff[0]);
		p.sendMessage(ChatColor.GREEN + " Offline players:" + onoff[1]);
	}

	private boolean newLeader(Player cs, String string) {
		Team teamIn = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cs.getName())) {
				teamIn = t;
				break;
			} else
				continue;
		}
		if (teamIn == null) {
			warn(cs, "You are not in a team.");
			return true;
		}
		Team otherTeam = null;
		for (Team t : TeamCore.teamSet) {
			if (t.containsPlayer(string)) {
				otherTeam = t;
				break;
			} else
				continue;
		}
		if (otherTeam == null
				|| !(otherTeam.getTeamName().equalsIgnoreCase(teamIn
						.getTeamName()))) {
			warn(cs, string + " is not in your team.");
			return true;
		}
		if (teamIn.getRank(cs.getName()) == Ranks.leader.getRank()) {
			if (!teamIn.containsPlayer(string)) {
				Player p = Bukkit.getPlayer(string);
				if (p == null) {
					warn(cs, "Player: " + string + " not found in your team.");
					return true;
				} else
					string = p.getName();
			}
			try {
				sqlite.query("UPDATE members SET m_status=1"
						+ " WHERE m_name='" + cs.getName() + "'");
				sqlite.query("UPDATE members SET m_status=2"
						+ " WHERE m_name='" + string + "'");
				Player newLeader = Bukkit.getPlayer(string);
				if (newLeader != null)
					msg(newLeader,
							"You are now the leader of " + teamIn.getTeamName());
				teamIn.demotePlayer(cs.getName());
				teamIn.setLeader(string);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			msg(cs, "You are now a moderator. " + cs.getName() + ".");

		} else {
			warn(cs, "You are not the leader of " + teamIn.getTeamName());
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (command.getName().equalsIgnoreCase("team")) {
			Player cmdsndr = (Player) sender;
			if (args.length == 0) {
				listCommands(sender);
				return false;
			}
			String com = args[0];
			switch (com.toLowerCase()) {
			case "stats":
				if (!sender.isOp())
					break;
				msg(sender, "Team size: " + TeamCore.teamSet.size());
				int count = 0;
				for (Team t : TeamCore.teamSet)
					if (t.getHome() != null)
						count++;
				msg(sender, "Teams that have homes: " + count);
				break;
			case "dump":
				if (!sender.isOp())
					break;
				File f = new File(jp.getDataFolder() + "//" + "teamdump.txt");
				PrintWriter pw;
				try {
					pw = new PrintWriter(f);

					for (Team t : TeamCore.teamSet) {
						pw.println(t.getTeamName());
					}
					pw.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case "togglehome":
				if (!sender.isOp())
					return warn(sender,
							"You do not have permission to use this command.");
				if (canTeam) {
					canTeam = false;
					jp.getConfig().set("hometoggle", false);
					jp.saveConfig();
					msg(sender, "Toggled homes off.");
				} else {
					canTeam = true;
					jp.getConfig().set("hometoggle", true);
					jp.saveConfig();
					msg(sender, "Toggled homes on.");
				}
				break;
			case "sethome":
				setHome(cmdsndr);
				break;
			case "home":
				teleport(cmdsndr);
				break;
			case "create":
				if (!argsLength(args, 1))
					return this.warn(sender, "Please enter a team name.");
				else
					this.createTeam(cmdsndr, args[1]);
				break;
			case "rename":
				if (!argsLength(args, 1))
					return this.warn(sender, "Please enter a new team name.");
				else
					this.changeTeamName(cmdsndr, args[1]);
				break;
			case "chat":
			case "c":
				return toggleChat(cmdsndr, args);
			case "leader":
				if (!argsLength(args, 1))
					return this
							.warn(sender,
									"Please enter the player you wish to transfer leadership to. /team leader <name>");
				else
					return newLeader(cmdsndr, args[1]);
			case "invite":

				if (!argsLength(args, 1))
					return warn(sender,
							"Please enter the player name to invite.");
				else
					invitePlayer(cmdsndr, args[1]);
				break;
			case "leave":
				this.leaveTeam(cmdsndr);
				break;
			case "disband":
				this.disbandTeam(cmdsndr);
				break;
			case "list":
				this.listTeam(cmdsndr);
				break;
			case "kick":
				if (!this.argsLength(args, 1))
					return warn(sender, "Enter a players name to kick.");
				kick(sender, args[1]);
				break;
			case "accept":
				this.accept(cmdsndr, true);
				break;
			case "decline":
				this.accept(cmdsndr, false);
				break;
			case "promote":
				if (!this.argsLength(args, 1))
					return warn(sender,
							"You need to enter a player name to promote.");
				this.changePlayerRank(cmdsndr, 1, args[1]);
				break;
			case "demote":
				if (!this.argsLength(args, 1))
					return warn(sender,
							"You need to enter a player name to promote.");
				this.changePlayerRank(cmdsndr, -1, args[1]);
				break;
			default:
				listCommands(sender);
				return true;
			}
		}
		return true;
	}

	private void setHome(Player p) {
		if (!canTeam) {
			warn(p, "You cannot use team homes on this server.");
			return;
		}
		Team tl = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(p.getName())) {
				tl = t;
				break;
			}
		}
		if (tl == null) {
			warn(p, "You are not in a team.");
			return;
		} else if (!tl.isLeader(p.getName())) {
			warn(p, "You are not the leader of " + tl.getTeamName());
			return;
		} else if (tl.getMembers().length < minPlayersForHome) {
			warn(p, "You do not have enough people in your team to set a home.");
			warn(p, "You have " + tl.getMembers().length + " and you need "
					+ minPlayersForHome);
			return;
		}
		Location loc = p.getLocation();
		tl.setHome(loc);
		String world = loc.getWorld().getName();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		StringBuilder sb = new StringBuilder();
		sb.append(world).append("|").append(x).append("|").append(y)
				.append("|").append(z);
		try {
			sqlite.query("UPDATE team SET t_home = '" + sb.toString()
					+ "' WHERE t_name = '" + tl.getTeamName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
			msg(p, "Something went wrong whilst setting your team home.");
			return;
		}
		msg(p, "Set your team home.");
	}

	private void teleport(final Player p) {
		if (teleporting.containsKey(p.getName())) {
			warn(p, "You are already teleporting.");
			return;
		}
		if (!canTeam) {
			warn(p, "You cannot use team homes on this server.");
			return;
		}
		Team tl = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(p.getName())) {
				tl = t;
				break;
			}
		}
		if (tl == null) {
			warn(p, "You are not in a team.");
			return;
		}
		if (tl.getHome() == null) {
			warn(p, "Your team leader has not set a team home.");
			return;
		}
		final Location hom = tl.getHome();
		msg(p, "Teleporting in 5 seconds. Please do not move.");
		int i = jp.getServer().getScheduler()
				.scheduleSyncDelayedTask(jp, new Runnable() {

					@Override
					public void run() {
						if (!p.isOnline()
								|| !teleporting.containsKey(p.getName()))
							return;
						Location loc = hom;
						if (hom.getBlock().getType() != Material.AIR
								|| hom.getBlock().getRelative(BlockFace.UP)
										.getType() != Material.AIR) {
							for (BlockFace bf : BlockFace.values()) {
								if (hom.getBlock().getRelative(bf).getType() == Material.AIR
										&& hom.getBlock().getRelative(bf)
												.getRelative(BlockFace.UP)
												.getType() == Material.AIR) {
									loc = hom.getBlock().getRelative(bf)
											.getLocation();
									break;

								}
							}
						} else {
							p.teleport(loc);
							teleporting.remove(p.getName());
							return;
						}
						if (loc.toVector().equals(hom.toVector())) {
							for (int y = loc.getBlockY(); y <= 255; y++) {
								Block b = loc.getWorld().getBlockAt(
										loc.getBlockX(), y, loc.getBlockZ());
								if (b.getType() == Material.AIR
										&& b.getRelative(BlockFace.UP)
												.getType() == Material.AIR) {
									loc = b.getLocation();
									break;
								}
							}
						}
						p.teleport(loc);
						teleporting.remove(p.getName());
					}
				}, 20 * 5);
		teleporting.put(p.getName(), i);

	}

	private boolean toggleChat(Player cs, String[] args) {
		Team teamIn = null;
		for (Team t : TeamCore.teamSet
				.toArray(new Team[TeamCore.teamSet.size()])) {
			if (t.containsPlayer(cs.getName())) {
				teamIn = t;
				break;
			} else
				continue;
		}
		if (teamIn == null) {
			warn(cs, "You are not in a team.");
			return true;
		}
		if (args.length >= 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= args.length - 1; i++)
				sb.append(args[i] + " ");

			ChatColor rank = null;
			ChatColor chat = null;
			switch (teamIn.getRank(cs.getName())) {
			case 0:
				rank = super.GRY;
				chat = super.WHT;
				break;
			case 1:
				rank = super.AQU;
				chat = super.GREEN;
				break;
			case 2:
				rank = super.GOLD;
				chat = super.GOLD;
				break;
			}
			for (String playersName : teamIn.getMembers()) {
				if (Bukkit.getPlayer(playersName) != null) {
					Bukkit.getPlayer(playersName).sendMessage(
							DGREN + "[HcTeams] " + "<" + rank + cs.getName()
									+ super.GREEN + "> " + chat + " "
									+ ChatColor.stripColor(sb.toString()));

				}
			}
			return true;
		}
		if (TeamChatHandler.chatToggled.contains(cs.getName())) {
			msg(cs, "Team chat is now off!");
			chatToggled.remove(cs.getName());
			return true;
		} else {
			msg(cs, "Team chat is now on!");
			chatToggled.add(cs.getName());
			return true;
		}
	}

}