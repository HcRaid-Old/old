package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Inherited subclass for making a connection to a MySQL server.<br>
 * Date Created: 2011-08-26 19:08
 * 
 * @author PatPeter
 */
public class MySQL extends Database {
	public enum Statements implements StatementEnum {
		ALTER("ALTER"), CALL(
		"CALL"), COMMIT("COMMIT"), // Data definition statements
		CREATE("CREATE"), DEALLOCATE("DEALLOCATE"), DELETE("DELETE"), // Utility Statements
				DESCRIBE("DESCRIBE"), DO(
						"DO"), DROP("DROP"),

		EXECUTE("EXECUTE"), EXPLAIN("EXPLAIN"), HANDLER("HANDLER"), HELP("HELP"), INSERT("INSERT"),

		LOAD("LOAD"), LOCK("LOCK"), // Prepared Statements
		PREPARE("PREPARE"), // http://dev.mysql.com/doc/refman/5.6/en/implicit-commit.html#savepoint
		RELEASE("RELEASE"), // ROLLBACK TO SAVEPOINT
		RENAME(
				"RENAME"), // RELEASE SAVEPOINT
		REPLACE("REPLACE"), // http://dev.mysql.com/doc/refman/5.6/en/lock-tables.html
		ROLLBACK("ROLLBACK"),

		SAVEPOINT("SAVEPOINT"), // Data manipulation statements
		SELECT("SELECT"), // Database Administration
		SET("SET"),

		SHOW("SHOW"), // Transactional and Locking Statements
		START("START"),

		TRUNCATE("TRUNCATE"), UNLOCK("UNLOCK"), UPDATE("UPDATE"), USE("USE");

		private String string;

		private Statements(String string) {
			this.string = string;
		}

		public String toString() {
			return string;
		}
	}

	private HostnameDatabase delegate = DatabaseFactory.hostname();

	public MySQL(Logger log, String prefix, String database) {
		super(log, prefix, "[MySQL] ");
		setHostname("localhost");
		setPort(3306);
		setDatabase(database);
		setUsername("");
		setPassword("");
		this.driver = DBMS.MySQL;
	}

	public MySQL(Logger log, String prefix, String hostname, int port,
			String database, String username, String password) {
		super(log, prefix, "[MySQL] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.MySQL;
	}

	public MySQL(Logger log, String prefix, String database, String username) {
		super(log, prefix, "[MySQL] ");
		setHostname("localhost");
		setPort(3306);
		setDatabase(database);
		setUsername(username);
		setPassword("");
		this.driver = DBMS.MySQL;
	}

	public MySQL(Logger log, String prefix, String database, String username,
			String password) {
		super(log, prefix, "[MySQL] ");
		setHostname("localhost");
		setPort(3306);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.MySQL;
	}

	@Deprecated
	public MySQL(Logger log, String prefix, String hostname, String port,
			String database, String username, String password) {
		super(log, prefix, "[MySQL] ");
		setHostname(hostname);
		try {
			setPort(Integer.parseInt(port));
		} catch (NumberFormatException e) {
			throw new DatabaseException("Port must be a number.");
		}
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.MySQL;
	}

	@Deprecated
	public boolean createTable(String query) {
		Statement statement = null;
		if (query == null || query.equals("")) {
			this.writeError("Could not create table: query is empty or null.",
					true);
			return false;
		}

		try {
			statement = connection.createStatement();
			statement.execute(query);
			statement.close();
		} catch (SQLException e) {
			this.writeError(
					"Could not create table, SQLException: " + e.getMessage(),
					true);
			return false;
		}
		return true;
	}

	public String getDatabase() {
		return delegate.getDatabase();
	}

	public String getHostname() {
		return delegate.getHostname();
	}

	private String getPassword() {
		return delegate.getPassword();
	}

	public int getPort() {
		return delegate.getPort();
	}

	@Override
	public Statements getStatement(String query) throws SQLException {
		String[] statement = query.trim().split(" ", 2);
		try {
			Statements converted = Statements.valueOf(statement[0]
					.toUpperCase());
			return converted;
		} catch (IllegalArgumentException e) {
			throw new SQLException("Unknown statement: \"" + statement[0]
					+ "\".");
		}
	}

	public String getUsername() {
		return delegate.getUsername();
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("MySQL driver class missing: " + e.getMessage()
					+ ".", true);
			return false;
		}
	}

	@Override
	public boolean isTable(String table) {
		Statement statement;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			this.writeError(
					"Could not create a statement in checkTable(), SQLException: "
							+ e.getMessage(), true);
			return false;
		}
		try {
			statement.executeQuery("SELECT * FROM " + table);
			return true; // Result can never be null, bad logic from earlier
							// versions.
		} catch (SQLException e) {
			return false; // Query failed, table does not exist.
		}
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:mysql://" + getHostname() + ":" + getPort()
					+ "/" + getDatabase();
			try {
				this.connection = DriverManager.getConnection(url,
						getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError(
						"Could not establish a MySQL connection, SQLException: "
								+ e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {
		switch ((Statements) statement) {
		case USE:
			this.writeError(
					"Please create a new connection to use a different database.",
					false);
			throw new SQLException(
					"Please create a new connection to use a different database.");

		case PREPARE:
		case EXECUTE:
		case DEALLOCATE:
			this.writeError(
					"Please use the prepare() method to prepare a query.",
					false);
			throw new SQLException(
					"Please use the prepare() method to prepare a query.");
		case ALTER:
		case CALL:
		case COMMIT:
		case CREATE:
		case DELETE:
		case DESCRIBE:
		case DO:
		case DROP:
		case EXPLAIN:
		case HANDLER:
		case HELP:
		case INSERT:
		case LOAD:
		case LOCK:
		case RELEASE:
		case RENAME:
		case REPLACE:
		case ROLLBACK:
		case SAVEPOINT:
		case SELECT:
		case SET:
		case SHOW:
		case START:
		case TRUNCATE:
		case UNLOCK:
		case UPDATE:
		default:
			break;
		}
	}

	private void setDatabase(String database) {
		delegate.setDatabase(database);
	}

	private void setHostname(String hostname) {
		delegate.setHostname(hostname);
	}

	private void setPassword(String password) {
		delegate.setPassword(password);
	}

	private void setPort(int port) {
		delegate.setPort(port);
	}

	private void setUsername(String username) {
		delegate.setUsername(username);
	}

	@Override
	public boolean truncate(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.isTable(table)) {
				this.writeError("Table \"" + table + "\" does not exist.", true);
				return false;
			}
			statement = this.connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeUpdate(query);
			statement.close();

			return true;
		} catch (SQLException e) {
			this.writeError(
					"Could not wipe table, SQLException: " + e.getMessage(),
					true);
			return false;
		}
	}
}