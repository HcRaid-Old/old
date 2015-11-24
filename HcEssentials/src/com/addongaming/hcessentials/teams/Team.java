package com.addongaming.hcessentials.teams;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Team {
	private Location home;
	private HashSet<String> invitedNames = new HashSet<String>();
	private HashMap<String, Integer> members = new HashMap<String, Integer>();
	private String teamName;

	public Team(String teamName, HashMap<String, Integer> members) {
		this.teamName = teamName;
		this.members = members;
	}

	public Team(String leaderName, String teamName, Location home) {
		members.put(leaderName, Ranks.leader.getRank());
		this.home = home;
		this.teamName = teamName;
	}

	public void addInvite(String name) {
		invitedNames.add(name);
	}

	public void addPlayer(String name) {
		members.put(name, Ranks.member.getRank());
	}

	public void addPlayer(String name, int rank) {
		members.put(name, rank);
	}

	public boolean canInvite(String name) {
		if (!members.containsKey(name))
			return false;
		return members.get(name) >= Ranks.mod.getRank();
	}

	public boolean canKick(String name) {
		if (!members.containsKey(name))
			return false;
		return members.get(name) >= Ranks.mod.getRank();
	}

	public boolean containsInvite(String name) {
		return invitedNames.contains(name);
	}

	public boolean containsPlayer(String name) {
		if (members.containsKey(name))
			return true;
		else
			return false;
	}

	public boolean demotePlayer(String playerName) {
		members.put(playerName, members.get(playerName) - 1);
		return true;
	}

	public Location getHome() {
		return home;
	}

	public String[] getInvited() {
		return invitedNames.toArray(new String[invitedNames.size()]);
	}

	public String[] getListedPlayers() {
		StringBuilder offline = new StringBuilder();
		StringBuilder online = new StringBuilder();
		for (String s : members.keySet()) {
			if (members.get(s) == 2) {
				Player p = Bukkit.getPlayerExact(s);
				if (p == null || !p.isOnline())
					offline.append(ChatColor.GOLD + "[Leader] " + s + ", ");
				else
					online.append(ChatColor.GOLD + "[Leader] " + s + ", ");
			}
		}
		for (String s : members.keySet()) {
			if (members.get(s) == 1) {
				Player p = Bukkit.getPlayerExact(s);
				if (p == null || !p.isOnline())
					offline.append(ChatColor.AQUA + "[Mod] " + s + ", ");
				else
					online.append(ChatColor.AQUA + "[Mod] " + s + ", ");
			}
		}
		for (String s : members.keySet()) {
			if (members.get(s) == 0) {
				Player p = Bukkit.getPlayerExact(s);
				if (p == null || !p.isOnline())
					offline.append(ChatColor.GREEN + s + ", ");
				else
					online.append(ChatColor.GREEN + s + ", ");

			}
		}
		if (offline.toString().length() > 2) {
			offline.deleteCharAt(offline.toString().length() - 2);

		}
		if (online.toString().length() > 2) {
			online.deleteCharAt(online.toString().length() - 2);
		}
		return new String[] { online.toString(), offline.toString() };
	}

	public String[] getMembers() {
		return members.keySet().toArray(new String[members.keySet().size()]);
	}

	public int getRank(String player) {
		return members.get(player);
	}

	public String getTeamName() {
		return teamName;
	}

	public boolean isLeader(String name) {
		if (!members.containsKey(name))
			return false;
		else
			return members.get(name) == Ranks.leader.getRank();
	}

	public boolean promotePlayer(String playerName) {
		members.put(playerName, members.get(playerName) + 1);
		return true;
	}

	public void removeInvite(String name) {
		invitedNames.remove(name);
	}

	public void removePlayer(String name) {
		members.remove(name);
	}

	public void setHome(Location loc) {
		this.home = loc;
	}

	public void setLeader(String name) {
		members.put(name, Ranks.leader.getRank());
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
}
