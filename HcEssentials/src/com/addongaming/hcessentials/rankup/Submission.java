package com.addongaming.hcessentials.rankup;

import java.util.Date;

public class Submission {
	private final String name;
	private final String key;
	private final String rank;
	private final long timeUsed;
	private final boolean real;
	private final String server;

	/**
	 * This is for the /rank command executed by players
	 * 
	 * @param name
	 *            Name of the player
	 * @param key
	 *            Key the player gave
	 * @param rank
	 *            The rank the key qualifies them for
	 * @param server
	 *            The server the rank is for
	 * @param real
	 *            Whether or not the rank/key is real and works.
	 */
	public Submission(String name, String key, String rank, String server,
			boolean real) {
		this.name = name;
		this.key = key;
		this.server = server;
		this.rank = rank;
		this.timeUsed = new Date().getTime();
		this.real = real;
	}

	/**
	 * This is for commandsenders version so that it can be added to a SQL
	 * database
	 * 
	 * @param name
	 *            Players name
	 * @param rank
	 *            The rank they received
	 * @param timeUsed
	 *            Time in ms they recieved their rank
	 * @param server
	 *            Server it was obtained on
	 */
	public Submission(String name, String rank, long timeUsed, String server) {
		this.name = name;
		this.rank = rank;
		this.timeUsed = timeUsed;
		this.server = server;
		real = true;
		key = null;
	}

	public long getTimeUsed() {
		return timeUsed;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getRank() {
		return rank;
	}

	public boolean isReal() {
		return real;
	}

	public boolean canReset() {
		return new Date(timeUsed + ((60000 * 5))).before(new Date());
	}

	public String getServer() {
		return this.server;
	}

}
