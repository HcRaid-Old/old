package com.addongaming.hcessentials.serverlogger;

public class PlayerTp {
	private final int userId;
	private final long date;
	private final int toLocX;
	private final int toLocY;
	private final int toLocZ;

	public PlayerTp(int userId, int toLocX, int toLocY, int toLocZ, long date) {
		this.toLocX = toLocX;
		this.toLocY = toLocY;
		this.toLocZ = toLocZ;
		this.userId = userId;
		this.date = date;
	}

	public long getDate() {
		return date;
	}

	public int getLocX() {
		return toLocX;
	}
	
	public int getLocY() {
		return toLocY;
	}
	
	public int getLocZ() {
		return toLocZ;
	}

	public int getUserId() {
		return userId;
	}
}
