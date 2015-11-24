package hcmodtools.core.monitor.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class StaffInstance {
	private PrintWriter pw = null;
	private File timefile = null;
	private final Date loginDate;
	private File logFile;
	private File privchat;
	private File pubchat;

	public StaffInstance(String name, File logFile, File timeFile,
			File pubchat, File privchat) {
		this.logFile = logFile;
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			pw = new PrintWriter(new FileWriter(logFile, true));
			if (!timeFile.exists()) {
				timeFile.createNewFile();
				PrintWriter ppw = new PrintWriter(timeFile);
				ppw.println(0);
				ppw.close();
			}
			if (!pubchat.exists())
				pubchat.createNewFile();
			if (!privchat.exists())
				privchat.createNewFile();
			this.pubchat = pubchat;
			this.privchat = privchat;
			this.timefile = timeFile;
		} catch (IOException e) {
			e.printStackTrace();

		}
		loginDate = new Date();
		pw.println(getToString("Logged in"));
		pw.close();
	}

	public void disable() {
		int i = 0;
		try {
			Scanner s = new Scanner(timefile);
			i = s.nextInt();
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			PrintWriter ppw = new PrintWriter(timefile);
			ppw.print(i + ((new Date().getTime() - loginDate.getTime())));
			ppw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			pw = new PrintWriter(new FileWriter(logFile, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.println(getToString("Logged out"));
		pw.close();
	}

	public String getToString(Object a) {

		return new SimpleDateFormat("dd/MM/YY hh-mm").format(new Date())
				+ " : " + a.toString();
	}

	public void checkCommand(String comm) {
		comm = ChatColor.stripColor(comm);
		String str = comm.split(" ")[0];
		switch (str) {
		case "/god":
		case "/godmode":
		case "/fly":
		case "/gm":
		case "/gmc":
		case "/ban":
		case "/kick":
		case "/vanish":
		case "/v":
		case "/tpo":
		case "/setwarp":
		case "/smute":
		case "/mute":
		case "/tempban":
			try {
				pw = new PrintWriter(new FileWriter(logFile, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			pw.println(getToString("commandline: " + comm));
			pw.close();
			return;
		case "/msg":
		case "/r":
			try {
				pw = new PrintWriter(new FileWriter(privchat, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			pw.println(getToString(comm));
			pw.close();
			return;
		}

	}

	public void checkSwear(String str) {
		str = ChatColor.stripColor(str);
		if (str.matches("(?i).*( nigga | nigur | niggur | nigger | coon | niga | chinky | chink | chinkie | limey |faggot|gayest|dickhead|arsehole|motherfucker|fucker|fucking|wanking|cunting|pricking|twating|bitchin|bitching|cocking|cocksuck|nigger|nigga|nige|niger|nigur|niggur|niggah|nigah|nigurh|fuck|shit|cunt|arse|gaay|fukk|fucc|dick|cock|wank|twat|yolo|swag).*")) {
			try {
				pw = new PrintWriter(new FileWriter(logFile, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			pw.println(getToString("Swear filter - "
					+ ChatColor.stripColor(str)));
			pw.close();
		}
		try {
			pw = new PrintWriter(new FileWriter(pubchat, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.println(getToString(str));
		pw.close();
		return;
	}
}
