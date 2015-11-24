package com.addongaming.hcessentials.serverlogger;

import java.util.UUID;

public class PlayerChat {
	private final String msg;
	private final UUID userId;
	private final long date;

	public PlayerChat(String msg, UUID userId, long date) {
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
