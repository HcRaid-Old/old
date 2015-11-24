package com.addongaming.minigames.management.scheduling;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.addongaming.hcessentials.data.LocationZone;
import com.addongaming.minigames.management.arena.ArenaPlayer;
import com.addongaming.minigames.management.arena.SpawnZone;
import com.addongaming.minigames.management.arena.Team;
import com.addongaming.minigames.minigames.ArenaGame.Status;
import com.addongaming.minigames.minigames.RedVsBlue;

public class RVBPlayerPicker implements HcRepeat {
	private RedVsBlue rvb;
	private ArenaPlayer red, blue;
	private SpawnZone redChoosing, blueChoosing;
	private int id;
	private boolean finished;
	private long lastRun = 0;
	private Location[] redSigns;
	private Location[] blueSigns;

	public RVBPlayerPicker(RedVsBlue rvb, ArenaPlayer red, ArenaPlayer blue,
			SpawnZone redChoosing, SpawnZone blueChoosing) {
		this.rvb = rvb;
		this.red = red;
		this.blue = blue;
		this.redChoosing = redChoosing;
		this.blueChoosing = blueChoosing;
		red.getBase().teleport(redChoosing.getRandomFreeLocation());
		blue.getBase().teleport(blueChoosing.getRandomFreeLocation());
		rvb.message(red.getBase(), "Take it in turns to pick your teams.");
		rvb.message(blue.getBase(), "Take it in turns to pick your teams.");
		System.out.println("Scanning red");
		redSigns = scanSigns((LocationZone) redChoosing);
		System.out.println("Scanning blue");
		blueSigns = scanSigns((LocationZone) blueChoosing);
		switchSign();
	}

	private Location[] scanSigns(LocationZone location) {
		Block[] blocks = location.getAllBlocks();
		Location sign1 = null;
		Location sign2 = null;
		System.out.println("Scanning signs");
		for (Block block : blocks)
			if (block != null && block.getState() != null
					&& block.getState() instanceof Sign) {
				System.out.println("Found sign");
				Sign sign = (Sign) block.getState();
				if (ChatColor.stripColor(sign.getLine(0)).contains("Player")) {
					System.out.println("Found player sign");
					if (ChatColor.stripColor(sign.getLine(0)).contains("1")) {
						sign1 = sign.getLocation();
						sign.setLine(1, "The other");
						sign.setLine(2, "team is");
						sign.setLine(3, "picking");
						sign.update(true);
						System.out.println("Found player1 sign");
					} else if (ChatColor.stripColor(sign.getLine(0)).contains(
							"2")) {
						System.out.println("Found player2 sign");
						sign.setLine(1, "The other");
						sign.setLine(2, "team is");
						sign.setLine(3, "picking");
						sign.update(true);
						sign2 = sign.getLocation();
					}
				}
			}
		return new Location[] { sign1, sign2 };
	}

	@Override
	public void run() {
		if (finished || rvb.getArena() == null
				|| !rvb.getArena().hasCurrentGame()
				|| rvb.getStatus() != Status.CHOOSING_PLAYERS) {
			finished = true;
			return;
		}
		if (lastRun + 10000 < System.currentTimeMillis()) {
			if (lastRun != 0)
				forceChoose();
			else
				switchSign();
			lastRun = System.currentTimeMillis();
		}
	}

	private int currentTeam = Team.RED.getTeamId();

	public void forceChoose() {
		lastRun = System.currentTimeMillis();
		switch (currentTeam) {
		// blue
		case 0: {
			rvb.signClicked(((Sign) blueSigns[new Random().nextInt(2)]
					.getBlock().getState()), red);
			return;
		}
		case 1: {
			rvb.signClicked(((Sign) redSigns[new Random().nextInt(2)]
					.getBlock().getState()), blue);
			return;
		}
		}
	}

	public void switchSign() {
		lastRun = System.currentTimeMillis();
		switch (currentTeam) {
		// blue
		case 0: {
			currentTeam = Team.RED.getTeamId();
			List<ArenaPlayer> players = rvb.getByTeam(Team.NONE.getTeamId());
			if (players.size() == 0 || players.size() == 1) {
				if (players.size() == 1)
					balanceTeams();
				for (Location loc : blueSigns) {
					Sign sign = (Sign) loc.getBlock().getState();
					sign.setLine(1, "The other");
					sign.setLine(2, "team is");
					sign.setLine(3, "picking");
					sign.update(true);
				}
				for (Location loc : redSigns) {
					Sign sign = (Sign) loc.getBlock().getState();
					sign.setLine(1, "The other");
					sign.setLine(2, "team is");
					sign.setLine(3, "picking");
					sign.update(true);
				}
				rvb.onStart();
				finished = true;
				return;
			}
			Collections.shuffle(players);
			Iterator<ArenaPlayer> iter = players.iterator();
			for (Location loc : redSigns) {
				BlockState bs = loc.getBlock().getState();
				Sign sign = (Sign) bs;
				sign.setLine(1, "");
				sign.setLine(2, iter.next().getName());
				sign.setLine(3, "");
				sign.update(true);
			}
			for (Location loc : blueSigns) {
				BlockState bs = loc.getBlock().getState();
				Sign sign = (Sign) bs;
				sign.setLine(1, "The other");
				sign.setLine(2, "team is");
				sign.setLine(3, "picking");
				sign.update(true);
			}
			break;
		}
		case 1: {
			currentTeam = Team.BLUE.getTeamId();
			List<ArenaPlayer> players = rvb.getByTeam(Team.NONE.getTeamId());
			if (players.size() == 0 || players.size() == 1) {
				if (players.size() == 1) {
					balanceTeams();
				}
				for (Location loc : blueSigns) {
					Sign sign = (Sign) loc.getBlock().getState();
					sign.setLine(1, "The other");
					sign.setLine(2, "team is");
					sign.setLine(3, "picking");
					sign.update(true);
				}
				rvb.onStart();
				finished = true;
				return;
			}
			Collections.shuffle(players);
			Iterator<ArenaPlayer> iter = players.iterator();
			for (Location loc : blueSigns) {
				BlockState bs = loc.getBlock().getState();
				Sign sign = (Sign) bs;
				sign.setLine(1, "");
				sign.setLine(2, iter.next().getName());
				sign.setLine(3, "");
				sign.update(true);
			}
			for (Location loc : redSigns) {
				Sign sign = (Sign) loc.getBlock().getState();
				sign.setLine(1, "The other");
				sign.setLine(2, "team is");
				sign.setLine(3, "picking");
				sign.update(true);
			}
		}
			break;
		}
	}

	private void balanceTeams() {
		int redTeam = rvb.getByTeam(Team.RED.getTeamId()).size(), blueTeam = rvb
				.getByTeam(Team.BLUE.getTeamId()).size();
		for (ArenaPlayer ap : rvb.getByTeam(Team.NONE.getTeamId())) {
			if (redTeam <= blueTeam) {
				ap.setTeam(Team.RED.getTeamId());
				redTeam++;
			} else {
				ap.setTeam(Team.BLUE.getTeamId());
				blueTeam++;
			}
		}
	}

	@Override
	public void setId(int id) {
		this.id = id;

	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

}
