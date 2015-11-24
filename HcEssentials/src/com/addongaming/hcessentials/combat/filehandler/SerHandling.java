package com.addongaming.hcessentials.combat.filehandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerHandling {
	public static Object load(final String path) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(path));
		final Object result = ois.readObject();
		ois.close();
		return result;
	}

	public static void save(final Object obj, final String path)
			throws Exception {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
}
