package com.addongaming.hcessentials.serverlogger;

import java.util.UUID;

public class PlayerLoginout {
	private final String msg;
	private final UUID userId;
	private final long date;

	public PlayerLoginout(UUID userId, String msg, long date) {
		this.msg = msg;
		this.userId = userId;
		this.date = date;
	}

	public long getDate() {
		return date;
	}

	public String getMsg() {
		return msg;
	}

	public UUID getUserId() {
		return userId;
	}
}
