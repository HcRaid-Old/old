package core.bank;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import core.start.Main;
import core.syncitems.SyncInventory;

public class BankFileHandler {

	public static void saveBank(String playerName, Bank[] b) {
		for (Bank bank : b) {
			try {
				save(new SyncInventory(bank.getNormalInventory()),
						Main.dataFolder + "\\" + bank.getPlayerName()
								+ bank.getRank() + ".bank");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static SyncInventory loadInventory(String playername, int grade) {
		String s = "error";
		if (grade == 0)
			s = "ghast";
		else if (grade == 1)
			s = "ender";
		else if (grade == 2)
			s = "hero";
		try {
			return (SyncInventory) load(Main.dataFolder + "\\" + playername + s
					+ ".bank");
		} catch (Exception e) {
			return null;
		}
	}

	public static void save(final Object obj, final String path)
			throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	public static Object load(final String path) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}
}
