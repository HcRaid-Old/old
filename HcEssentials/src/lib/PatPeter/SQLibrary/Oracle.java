package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Child class for the Oracle database.<br>
 * Date Created: 2011-08-27 17:03.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Oracle extends Database {
	/**
	 * http://docs.oracle.com/html/A95915_01/sqcmd.htm
	 */
	protected enum Statements implements StatementEnum {
		ALTER("ALTER"), COMMIT(
		"COMMIT"), CONSTRAINT(
		"CONSTRAINT"), CREATE("CREATE"), CURRVAL("CURRVAL"), DELETE("DELETE"), DROP("DROP"), EXPLAIN(
				"EXPLAIN"), GRANT("GRANT"), INSERT("INSERT"), LEVEL("LEVEL"), NEXTVAL("NEXTVAL"), OL_ROW_STATUS("OL_ROW_STATUS"), REVOKE(
						"REVOKE"), ROLLBACK("ROLLBACK"), ROWID(
				"ROWID"), ROWNUM(
				"ROWNUM"), SELECT("SELECT"), SET("SET"), TRUNCATE("TRUNCATE"), UPDATE("UPDATE");

		private String string;

		private Statements(String string) {
			this.string = string;
		}

		public String toString() {
			return string;
		}
	}

	private HostnameDatabase delegate = DatabaseFactory.hostname();

	public Oracle(Logger log, String prefix, String database)
			throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname("localhost");
		setPort(1521);
		setDatabase(database);
		setUsername("");
		setPassword("");
		this.driver = DBMS.Oracle;
	}

	public Oracle(Logger log, String prefix, String hostname, int port,
			String database, String username, String password)
			throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Oracle;
	}

	public Oracle(Logger log, String prefix, String database, String username)
			throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname("localhost");
		setPort(1521);
		setDatabase(database);
		setUsername(username);
		setPassword("");
		this.driver = DBMS.Oracle;
	}

	public Oracle(Logger log, String prefix, String database, String username,
			String password) throws SQLException {
		super(log, prefix, "[Oracle] ");
		setHostname("localhost");
		setPort(1521);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.Oracle;
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
	public boolean initialize() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); // com.jdbc.OracleDriver
																// ?
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("Oracle driver class missing: " + e.getMessage()
					+ ".", true);
			return false;
		}
	}

	@Override
	public boolean isTable(String table) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:oracle:thin:@" + getHostname() + ":" + getPort() + ":"
					+ getDatabase();
			try {
				this.connection = DriverManager.getConnection(url,
						getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError(
						"Could not establish an Oracle connection, SQLException: "
								+ e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}

	protected void queryValidation(StatementEnum statement) throws SQLException {
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
		throw new UnsupportedOperationException();
	}

}
