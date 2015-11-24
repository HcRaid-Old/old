package com.addongaming.minigames.management.scheduling;

public interface HcRepeat extends Runnable {
	// Sets the schedulers runnable ID
	public void setId(int id);

	// Gets the schedulers runnable ID
	public int getId();

	// Checks to see if the runnable is finished
	public boolean isFinished();
}
