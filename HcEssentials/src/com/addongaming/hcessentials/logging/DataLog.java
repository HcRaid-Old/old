package com.addongaming.hcessentials.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

public class DataLog {
	private String name;
	private File folder;

	public DataLog(String name, File folder) {
		this.name = name;
		this.folder = folder;
	}

	public String getName() {
		return name;
	}

	public void logPlayer(Player p, String message) {
		File toWriter = new File(folder + File.separator + p.getName() + ".txt");
		try {
			if (!toWriter.exists())
				toWriter.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(toWriter,
					true));
			pw.println(getDate() + message);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void log(String message, String file) {
		File toWriter = new File(folder + File.separator + file + ".txt");
		try {
			if (!toWriter.exists())
				toWriter.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(toWriter,
					true));
			pw.println(getDate() + message);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void log(String message) {
		log(message, "__Global__");
	}

	private String getDate() {
		return new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss")
				.format(new Date()) + " | ";
	}

	/**
	 * Gets the writer for a file as a PrintStream, useful for exceptions
	 * <strong> It is essential the PrintStream is closed to ensure data is
	 * written</strong>
	 * 
	 * @param file
	 *            File name to write to, i.e. "_Errorlog"
	 * @return PrintStream to write to
	 */
	public PrintStream getWriter(String file) {
		File toWriter = new File(folder + File.separator + file + ".txt");
		try {
			if (!toWriter.exists())
				toWriter.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(toWriter,
					true));
			return new PrintStream(new BufferedOutputStream(
					new FileOutputStream(toWriter, true)));

		} catch (Exception e) {
		}
		return null;
	}
}
