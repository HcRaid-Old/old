package com.addongaming.hcessentials.limits;

import org.bukkit.Bukkit;

public class NCPCleaner implements Runnable {

	@Override
	public void run() {
		try {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					"ncp removeplayer *");
			System.out.println("Removed debug");
		} catch (Exception e) {
			System.out
					.println("An error has occured removeing players from no cheat.");
			System.out.println("Printing the stack trace.");
			System.out.println("");
			System.out.println("============================================");
			System.out.println("");
			e.printStackTrace(System.err);
			System.out.println("");
			System.out.println("============================================");
			System.out.println("");
		}
	}

}
