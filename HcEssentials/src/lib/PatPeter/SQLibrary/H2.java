package lib.PatPeter.SQLibrary;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.FilenameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Child class for the H2 database.<br>
 * Date Created: 2011-09-03 17:16.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class H2 extends Database {
	// http://www.h2database.com/html/grammar.html
	private enum Statements implements StatementEnum {
		// Data Definition
		ALTER("ALTER"), ANALYZE("ANALYZE"), BACKUP(
				"BACKUP"), CALL("CALL"), // Other
		CHECKPOINT("CHECKPOINT"), COMMENT(
						"COMMENT"), COMMIT("COMMIT"), CONSTRAINT("CONSTRAINT"), CREATE("CREATE"), DELETE("DELETE"), DROP("DROP"),

		EXPLAIN("EXPLAIN"), GRANT("GRANT"), HELP("HELP"), INSERT("INSERT"), MERGE("MERGE"), PREPARE(
						"PREPARE"), REVOKE("REVOKE"),

		ROLLBACK("ROLLBACK"), RUNSCRIPT(
		"RUNSCRIPT"), SAVEPOINT(
		"SAVEPOINT"), SCRIPT("SCRIPT"), // Data Manipulation
		SELECT("SELECT"), SET("SET"), SHOW("SHOW"), SHUTDOWN("SHUTDOWN"), TRUNCATE("TRUNCATE"), UPDATE("UPDATE");

		private String string;

		private Statements(String string) {
			this.string = string;
		}

		public String toString() {
			return string;
		}
	}

	private FilenameDatabase delegate = DatabaseFactory.filename();

	public H2(Logger log, String prefix, String directory, String filename) {
		super(log, prefix, "[H2] ");
		setFile(directory, filename);
		this.driver = DBMS.H2;
	}

	public H2(Logger log, String prefix, String directory, String filename,
			String extension) {
		super(log, prefix, "[H2] ");
		setFile(directory, filename, extension);
		this.driver = DBMS.H2;
	}

	private File getFile() {
		return delegate.getFile();
	}

	@Override
	public StatementEnum getStatement(String query) throws SQLException {
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

	@Override
	protected boolean initialize() {
		try {
			Class.forName("org.h2.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("H2 driver class missing: " + e.getMessage() + ".",
					true);
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
			try {
				this.connection = DriverManager.getConnection("jdbc:h2:file:"
						+ getFile().getAbsolutePath());
				return true;
			} catch (SQLException e) {
				this.writeError(
						"Could not establish an H2 connection, SQLException: "
								+ e.getMessage(), true);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {
	}

	private void setFile(String directory, String filename) {
		delegate.setFile(directory, filename);
	}

	private void setFile(String directory, String filename, String extension) {
		delegate.setFile(directory, filename, extension);
	}

	@Override
	public boolean truncate(String table) {
		throw new UnsupportedOperationException();
	}
}
