package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Delegates.HostnameDatabase;
import lib.PatPeter.SQLibrary.Factory.DatabaseFactory;

/**
 * Child class for the Microsoft SQL database.<br>
 * Date Created: 2011-09-03 17:18.
 * 
 * @author PatPeter
 */
public class MicrosoftSQL extends Database {
	/**
	 * http://msdn.microsoft.com/en-us/library/ms131699.aspx
	 */
	protected enum Statements implements StatementEnum {
		__CONNECTIONS("@@CONNECTIONS"), __CPU_BUSY(
				"@@CPU_BUSY"), __CURSOR_ROWS("@@CURSOR_ROWS"), __DATEFIRST(
				"@@DATEFIRST"), __DBTS("@@DBTS"), __ERROR("@@ERROR"), __FETCH_STATUS(
				"@@FETCH_STATUS"), __IDENTITY("@@IDENTITY"), __IDLE("@@IDLE"), __IO_BUSY(
				"@@IO_BUSY"), __LANGID("@@LANGID"), __LANGUAGE("@@LANGUAGE"), __LOCK_TIMEOUT(
				"@@LOCK_TIMEOUT"), __MAX_CONNECTIONS("@@MAX_CONNECTIONS"), __MAX_PRECISION(
				"@@MAX_PRECISION"), __NESTLEVEL("@@NESTLEVEL"), __OPTIONS(
				"@@OPTIONS"), __PACK_RECEIVED("@@PACK_RECEIVED"), __PACK_SENT(
				"@@PACK_SENT"), __PACKET_ERRORS("@@PACKET_ERRORS"), __PROCID(
				"@@PROCID"), __REMSERVER("@@REMSERVER"), __ROWCOUNT(
				"@@ROWCOUNT"), __SERVERNAME("@@SERVERNAME"), __SERVICENAME(
				"@@SERVICENAME"), __SPID("@@SPID"), __TEXTSIZE("@@TEXTSIZE"), __TIMETICKS(
				"@@TIMETICKS"), __TOTAL_ERRORS("@@TOTAL_ERRORS"), __TOTAL_READ(
				"@@TOTAL_READ"), __TOTAL_WRITE("@@TOTAL_WRITE"), __TRANCOUNT(
				"@@TRANCOUNT"), __VERSION("@@VERSION"), _PARTITION("$PARTITION"), ABS("ABS"), ACOS("ACOS"), ADD(
				"ADD"), ALL("ALL"), ALTER(""), AND("AND"), ANY("ANY"), APP_NAME(
				"APP_NAME"), APPLOCK_MODE(
				"APPLOCK_MODE"), APPLOCK_TEST("APPLOCK_TEST"), ASCII("ASCII"), ASIN("ASIN"), ASSEMBLYPROPERTY(
				"ASSEMBLYPROPERTY"), ASYMKEY_ID("AsymKey_ID"), ATAN("ATAN"), ATN2(
				"ATN2"), AVG("AVG"), BACKUP("BACKUP"), BEGIN("BEGIN"), BETWEEN(
				"BETWEEN"), BINARY("BINARY"), BINARY_CHECKSUM("BINARY_CHECKSUM"), BIT(
				"bit"), BREAK("BREAK"), BULK("BULK"), CASE("CASE"), CATCH("CATCH"), CEILING("CEILING"), CERT_ID("Cert_ID"), CERTPROPERTY(
				"CertProperty"), CHAR("char"), CHARINDEX(
				"CHARINDEX"), CHECKPOINT("CHECKPOINT"), CHECKSUM("CHECKSUM"), CHECKSUM_AGG(
				"CHECKSUM_AGG"), CLOSE("CLOSE"), COALESCE("COALESCE"), COL_LENGTH(
				"COL_LENGTH"), COL_NAME("COL_NAME"), COLLATE(
				"COLLATE"), COLLATIONPROPERTY("COLLATIONPROPERTY"), COLUMNPROPERTY(
				"COLUMNPROPERTY"), COLUMNS_UPDATED("COLUMNS_UPDATED"), COMMIT(
				"COMMIT"), COMPUTE("COMPUTE"), CONSTANTS("Constants"), CONTAINS(
				"CONTAINS"), CONTAINSTABLE("CONTAINSTABLE"), CONTEXT_INFO(
				"CONTEXT_INFO"), CONTINUE("CONTINUE"), CONVERT(
				"CONVERT"), COS("COS"), COT("COT"), COUNT(
				"COUNT"), COUNT_BIG("COUNT_BIG"), CREATE("CREATE"), CURRENT_REQUEST_ID(
				"CURRENT_REQUEST_ID"), CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"), CURRENT_USER(
				"CURRENT_USER"), CURSOR("cursor"), CURSOR_STATUS(
				"CURSOR_STATUS"), CURSORS("Cursors"), DATA("Data"), DATABASE_PRINCIPAL_ID(
				"DATABASE_PRINCIPAL_ID"), DATABASEPROPERTY(
				"DATABASEPROPERTY"), DATABASEPROPERTYEX("DATABASEPROPERTYEX"), DATALENGTH("DATALENGTH"), DATE("Date"), DATEADD(
				"DATEADD"), DATEDIFF("DATEDIFF"), DATENAME("DATENAME"), DATEPART(
				"DATEPART"), DAY("DAY"), DB_ID("DB_ID"), DB_NAME(
				"DB_NAME"), DBCC("DBCC"), DEALLOCATE("DEALLOCATE"), DECIMAL(
				"decimal"), DECLARE("DECLARE"), DECRYPTBYASYMKEY(
				"DecryptByAsymKey"), DECRYPTBYCERT("DecryptByCert"), DECRYPTBYKEYAUTOASYMKEY(
				"DecryptByKeyAutoAsymKey"), DECRYPYBYKEY(
				"DecryptByKey"), DECRYPYBYKEYAUTOCERT(
				"DecryptByKeyAutoCert"), DECRYPYBYPASSPHRASE(
				"DecryptByPassPhrase"), DEGREES("DEGREES"), DELETE("DELETE"), DENSE_RANK(
				"DENSE_RANK"), DENY("DENY"), DIFFERENCE("DIFFERENCE"), DISABLE(
				"DISABLE"), DROP("DROP"), DUMP("DUMP"), ELSE("ELSE"), ENABLE(
				"ENABLE"), ENCRYPTBYASYMKEY("EncryptByAsymKey"), ENCRYPTBYKEY("EncryptByKey"), ENCRYPTBYPASSPHRASE(
				"EncryptByPassPhrase"), ENCRYPYBYCERT(
				"EncryptByCert"), END("END"), ERROR_LINE("ERROR_LINE"), ERROR_MESSAGE(
				"ERROR_MESSAGE"), ERROR_NUMBER("ERROR_NUMBER"), ERROR_PROCEDURE(
				"ERROR_PROCEDURE"), ERROR_SEVERITY("ERROR_SEVERITY"), ERROR_STATE(
				"ERROR_STATE"), EVENTDATA("EVENTDATA"), EXCEPT("EXCEPT"), EXECUTE(
				"EXECUTE"), EXISTS("EXISTS"), EXP("EXP"), EXPRESSIONS(
				"Expressions"), FETCH("FETCH"), FILE_ID("FILE_ID"), FILE_IDEX(
				"FILE_IDEX"), FILE_NAME("FILE_NAME"), FILEGROUP_ID(
				"FILEGROUP_ID"), FILEGROUP_NAME("FILEGROUP_NAME"), FILEGROUPPROPERTY(
				"FILEGROUPPROPERTY"), FILEPROPERTY("FILEPROPERTY"), FLOAT(
				"float"), FLOOR("FLOOR"), FN_GET_SQL("fn_get_sql"), FN_HELPCOLLATIONS(
				"fn_helpcollations"), FN_LISTEXTENDEDPROPERTY(
				"fn_listextendedproperty"), FN_MY_PERMISSIONS(
				"fn_my_permissions"), FN_SERVERSHAREDRIVES(
				"fn_servershareddrives"), FN_TRACE_GETEVENTINFO(
				"fn_trace_geteventinfo"), FN_TRACE_GETFILTERINFO(
				"fn_trace_getfilterinfo"), FN_TRACE_GETINFO("fn_trace_getinfo"), FN_TRACE_GETTABLE(
				"fn_trace_gettable"), FN_VIRTUALFILESTATS("fn_virtualfilestats"), FN_VIRTUALSERVERNODES(
				"fn_virtualservernodes"), FOR("FOR"), FORMATMESSAGE(
				"FORMATMESSAGE"), FREETEXT("FREETEXT"), FREETEXTTABLE(
				"FREETEXTTABLE"), FROM("FROM"), FULLTEXTCATALOGPROPERTY(
				"FULLTEXTCATALOGPROPERTY"), FULLTEXTSERVICEPROPERTY(
				"FULLTEXTSERVICEPROPERTY"), FUNCTIONS("Functions"), GET("GET"), GET_TRANSMISSION_STATUS(
				"GET_TRANSMISSION_STATUS"), GETANSINULL("GETANSINULL"), GETDATE(
				"GETDATE"), GETUTCDATE("GETUTCDATE"), GO("GO"), GOTO("GOTO"), GRANT(
				"GRANT"), GROUP("GROUP"), GROUPING("GROUPING"), HAS_DBACCESS(
				"HAS_DBACCESS"), HAS_PERMS_BY_NAME("Has_Perms_By_Name"), HASHBYTES(
				"HashBytes"), HAVING("HAVING"), HINTS("Hints"), HOST_ID(
				"HOST_ID"), HOST_NAME("HOST_NAME"), IDENT_CURRENT(
				"IDENT_CURRENT"), IDENT_INCR("IDENT_INCR"), IDENT_SEED(
				"IDENT_SEED"), IDENTITY("IDENTITY"), IF("IF"), IMAGE("image"), IN(
				"IN"), INDEX_COL("INDEX_COL"), INDEXKEY_PROPERTY("INDEXKEY_PROPERTY"), INDEXPROPERTY(
				"INDEXPROPERTY"), INSERT("INSERT"), INT(
				"int"), INTO("INTO"), IS("IS"), IS_MEMBER("IS_MEMBER"), IS_SRVROLEMEMBER(
				"IS_SRVROLEMEMBER"), ISDATE("ISDATE"), ISNULL(
				"ISNULL"), ISNUMERIC("ISNUMERIC"), KEY_GUID("Key_GUID"), KEY_ID(
				"Key_ID"), KILL("KILL"), LEFT("LEFT"), LEN("LEN"), LIKE("LIKE"), LOAD(
				"LOAD"), LOG("LOG"), LOG10("LOG10"), LOGINPROPERTY(
				"LOGINPROPERTY"), LOWER("LOWER"), LTRIM("LTRIM"), MAX("MAX"), MIN(
				"MIN"), MIN_ACTIVE_ROWVERSION("MIN_ACTIVE_ROWVERSION"), MONEY(
				"money"), MONTH("MONTH"), MOVE("MOVE"), NCHAR("NCHAR"), NEWID(
				"NEWID"), NEWSEQUENTIALID("NEWSEQUENTIALID"), NOT("NOT"), NTEXT(
				"ntext"), NTILE("NTILE"), NULLIF("NULLIF"), NUMERIC("numeric"), OBJECT_DEFINITION(
				"OBJECT_DEFINITION"), OBJECT_ID("OBJECT_ID"), OBJECT_NAME(
				"OBJECT_NAME"), OBJECT_SCHEMA_NAME("OBJECT_SCHEMA_NAME"), OBJECTPROPERTY(
				"OBJECTPROPERTY"), OBJECTPROPERTYEX("OBJECTPROPERTYEX"), OPEN(
				"OPEN"), OPENDATASOURCE("OPENDATASOURCE"), OPENQUERY(
				"OPENQUERY"), OPENROWSET("OPENROWSET"), OPENXML("OPENXML"), OPERATORS(
				"Operators"), OPTION("OPTION"), OR("OR"), ORDER("ORDER"), ORIGINAL_LOGIN(
				"ORIGINAL_LOGIN"), OUTPUT("OUTPUT"), OVER("OVER"), PARSENAME(
				"PARSENAME"), PATINDEX("PATINDEX"), PERMISSIONS("PERMISSIONS"), PI(
				"PI"), POWER("POWER"), PREDICATE("Predicate"), PRINT("PRINT"), PUBLISHINGSERVERNAME(
				"PUBLISHINGSERVERNAME"), QUOTENAME("QUOTENAME"), RADIANS(
				"RADIANS"), RAISERROR("RAISERROR"), RAND("RAND"), RANK("RANK"), READTEXT(
				"READTEXT"), REAL("real"), RECEIVE("RECEIVE"), RECONFIGURE(
				"RECONFIGURE"), REPLACE("REPLACE"), REPLICATE("REPLICATE"), RESERVED(
				"Reserved"), RESTORE("RESTORE"), RETURN("RETURN"), REVERSE(
				"REVERSE"), REVERT("REVERT"), REVOKE("REVOKE"), RIGHT("RIGHT"), ROLLBACK(
				"ROLLBACK"), ROUND("ROUND"), ROW_NUMBER(
				"ROW_NUMBER"), ROWCOUNT_BIG("ROWCOUNT_BIG"), RTRIM("RTRIM"), SAVE("SAVE"), SCHEMA_ID(
				"SCHEMA_ID"), SCHEMA_NAME("SCHEMA_NAME"), SCOPE_IDENTITY(
				"SCOPE_IDENTITY"), SEARCH("Search"), SELECT("SELECT"), SEND(
				"SEND"), SERVERPROPERTY("SERVERPROPERTY"), SESSION_USER(
				"SESSION_USER"), SESSIONPROPERTY("SESSIONPROPERTY"), SET("SET"), SETUSER(
				"SETUSER"), SHUTDOWN("SHUTDOWN"), SIGN("SIGN"), SIGNBYASYMKEY(
				"SignByAsymKey"), SIGNBYCERT("SignByCert"), SIN("SIN"), SMALLDATETIME(
				"smalldatetime"), SMALLINT("smallint"), SMALLMONEY("smallmoney"), SOME(
				"SOME"), SOUNDEX("SOUNDEX"), SPACE("SPACE"), SQL_VARIANT(
				"sql_variant"), SQL_VARIANT_PROPERTY("SQL_VARIANT_PROPERTY"), SQRT(
				"SQRT"), SQUARE("SQUARE"), STATS_DATE("STATS_DATE"), STDEV(
				"STDEV"), STDEVP("STDEVP"), STR("STR"), STUFF("STUFF"), SUBSTRING(
				"SUBSTRING"), SUM("SUM"), SUSER_ID("SUSER_ID"), SUSER_NAME(
				"SUSER_NAME"), SUSER_SID("SUSER_SID"), SUSER_SNAME(
				"SUSER_SNAME"), SYS("sys"), SYSTEM_USER("SYSTEM_USER"), TABLE(
				"table"), TAN("TAN"), TERTIARY_WEIGHTS("TERTIARY_WEIGHTS"), TEXT(
				"text"), TEXTPTR("TEXTPTR"), TEXTVALID("TEXTVALID"), TIMESTAMP(
				"timestamp"), TINYINT("tinyint"), TOP("TOP"), TRACE("Trace"), TRANSACTIONS(
				"Transactions"), TRIGGER_NESTLEVEL(
				"TRIGGER_NESTLEVEL"), TRUNCATE("TRUNCATE"), TRY("TRY"), TYPE_ID("TYPE_ID"), TYPE_NAME(
				"TYPE_NAME"), TYPEPROPERTY("TYPEPROPERTY"), UNICODE("UNICODE"), UNION(
				"UNION"), UNIQUEIDENTIFIER("uniqueidentifier"), UPDATE("UPDATE"), UPDATETEXT(
				"UPDATETEXT"), UPPER("UPPER"), USE("USE"), USER("USER"), USER_ID(
				"USER_ID"), USER_NAME("USER_NAME"), VAR("VAR"), VARBINARY(
				"varbinary"), VARCHAR("varchar"), VARP("VARP"), VERIFYSIGNEDBYASYMKEY(
				"VerifySignedByAsmKey"), VERIFYSIGNEDBYCERT(
				"VerifySignedByCert"), WAITFOR("WAITFOR"), WHERE("WHERE"), WHILE(
				"WHILE"), WITH("WITH"), WRITETEXT("WRITETEXT"), XACT_STATE(
				"XACT_STATE"), XML("xml"), XML_SCHEMA_NAMESPACE(
				"xml_schema_namespace"), YEAR("YEAR");

		private String value;

		private Statements(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	private HostnameDatabase delegate = DatabaseFactory.hostname();

	public MicrosoftSQL(Logger log, String prefix, String database)
			throws SQLException {
		super(log, prefix, "[MicrosoftSQL] ");
		setHostname("localhost");
		setPort(1433);
		setDatabase(database);
		setUsername("");
		setPassword("");
		this.driver = DBMS.MicrosoftSQL;
	}

	public MicrosoftSQL(Logger log, String prefix, String hostname, int port,
			String database, String username, String password)
			throws SQLException {
		super(log, prefix, "[MicrosoftSQL] ");
		setHostname(hostname);
		setPort(port);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.MicrosoftSQL;
	}

	public MicrosoftSQL(Logger log, String prefix, String database,
			String username) throws SQLException {
		super(log, prefix, "[MicrosoftSQL] ");
		setHostname("localhost");
		setPort(1433);
		setDatabase(database);
		setUsername(username);
		setPassword("");
		this.driver = DBMS.MicrosoftSQL;
	}

	public MicrosoftSQL(Logger log, String prefix, String database,
			String username, String password) throws SQLException {
		super(log, prefix, "[MicrosoftSQL] ");
		setHostname("localhost");
		setPort(1433);
		setDatabase(database);
		setUsername(username);
		setPassword(password);
		this.driver = DBMS.MicrosoftSQL;
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
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError(
					"MicrosoftSQL driver class missing: " + e.getMessage()
							+ ".", true);
			return false;
		}
	}

	@Override
	public boolean isTable(String table) {
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT TOP 10 * FROM "
					+ table);

			if (result != null)
				return true;
			else
				return false;
		} catch (SQLException e) {
			this.writeError("Could not check if table \"" + table
					+ "\" exists, SQLException: " + e.getMessage(), true);
			return false;
		}
	}

	@Override
	public boolean open() {
		if (initialize()) {
			String url = "jdbc:sqlserver://" + getHostname() + ":" + getPort()
					+ ";databaseName=" + getDatabase() + ";user="
					+ getUsername() + ";password=" + getPassword();
			try {
				this.connection = DriverManager.getConnection(url,
						getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				this.writeError(
						"Could not establish a Microsoft SQL connection, SQLException: "
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
