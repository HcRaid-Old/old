package hcmodtools.core.raidingreport;

import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3188136005567008834L;
	private final String modWhoOpened;
	private final Date dateOpened;
	private final int id;
	private final String message;
	private final String player;

	public Report(String modWhoOpened, int id, String message, Date dateOpened,
			String player) {
		this.modWhoOpened = modWhoOpened;
		this.dateOpened = dateOpened;
		this.id = id;
		this.player = player;
		this.message = message;
	}

	public boolean hasExpired() {
		return new Date(dateOpened.getTime() + (60000 * 60 * 24 * 2))
				.before(new Date());
	}

	public String getTimeLeft() {
		long difference = dateOpened.getTime() + (60000 * 60 * 24 * 2)
				- new Date().getTime();
		difference /= 1000;
		int days = (int) Math.floor(difference / 86400);
		int hours = (int) Math.floor(difference / 3600) % 24;
		int minutes = (int) Math.floor(difference) % 60;
		int seconds = (int) difference % 60;
		StringBuilder sb = new StringBuilder();
		if (days > 0)
			sb.append(days + " days ");
		if (hours > 0)
			sb.append(hours + " hours ");
		if (minutes > 0)
			sb.append(minutes + " minutes ");
		if (seconds > 0)
			sb.append(seconds + " seconds ");
		return sb.toString();

	}

	public Date getDateOpened() {
		return dateOpened;
	}

	public String getPlayer() {
		return player;
	}

	public String getModWhoOpened() {
		return modWhoOpened;
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

}
