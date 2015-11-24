package com.addongaming.minigames.management.scheduling;

import com.addongaming.minigames.minigames.TheShip;

public class ShipTimer implements HcRepeat {

	private int id;
	private boolean finished = false;
	private int timeLeft = 31;
	private TheShip shipGame;

	public ShipTimer(TheShip shipGame) {
		this.shipGame = shipGame;
	}

	@Override
	public void run() {
		if (finished)
			return;
		if (timeLeft > 0) {
			timeLeft--;
			if (timeLeft == 0) {
				shipGame.swapSide();
				timeLeft = 60 * 5;
				return;
			}
			if (timeLeft % 60 == 0) {
				shipGame.messageAll(timeLeft / 60 + " minute"
						+ (timeLeft / 60 == 1 ? "" : "s")
						+ " left till the next round.");
			} else if (timeLeft == 30 || 10 >= timeLeft)
				shipGame.messageAll(timeLeft + " seconds until the next round.");
		}
	}

	@Override
	public void setId(int id) {
		this.id = id;

	}

	public boolean hasTimeLeft() {
		return timeLeft > 0;
	}

	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
		shipGame.messageAll(timeLeft + " seconds until the next round.");

	}

	@Override
	public int getId() {
		return id;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	public int getTimeLeft() {
		return this.timeLeft;
	}

}
