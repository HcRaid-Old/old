package lib.PatPeter.SQLibrary.Delegates;

import lib.PatPeter.SQLibrary.DatabaseException;

/**
 * Implementation of databases using hostnames, usernames, etc.<br>
 * Date Created: 2012-12-18 04:45.
 * 
 * @author PatPeter
 */
public class HostnameDatabaseImpl implements HostnameDatabase {
	private String database = "minecraft";
	private String hostname = "localhost";
	private String password = "";
	private int port = 0;
	private String username = "minecraft";

	public HostnameDatabaseImpl() {
	}

	@Override
	public String getDatabase() {
		return this.database;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setDatabase(String database) {
		if (database == null || database.length() == 0)
			throw new DatabaseException("Database cannot be null or empty.");
		this.database = database;
	}

	@Override
	public void setHostname(String hostname) {
		if (hostname == null || hostname.length() == 0)
			throw new DatabaseException("Hostname cannot be null or empty.");
		this.hostname = hostname;
	}

	@Override
	public void setPassword(String password) {
		if (password == null)
			throw new DatabaseException("Password cannot be null.");
		this.password = password;
	}

	@Override
	public void setPort(int port) {
		if (port < 0 || 65535 < port)
			throw new DatabaseException(
					"Port number cannot be below 0 or greater than 65535.");
		this.port = port;
	}

	@Override
	public void setUsername(String username) {
		if (username == null)
			throw new DatabaseException("Username cannot be null.");
		this.username = username;
	}
}
