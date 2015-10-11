package command;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 * Gets and caches data sources, gets connections
 * Implements Singleton pattern
 */

public class CommandExecutor {

	private static CommandExecutor myOnlyInstance = null;
	private DataSource ds = null;
	
	// accessor method for the singleton
	public static CommandExecutor getInstance() throws NamingException {
		if (myOnlyInstance == null) {
			myOnlyInstance = new CommandExecutor();
		}
		return myOnlyInstance;
	}
	
	// Constructor for the singleton
	private CommandExecutor() throws NamingException {
		getDataSource();
	}
	
	// get the data source from initial context
	public DataSource getDataSource() throws NamingException {
		
		if (ds == null) {
			
			InitialContext ctx = new InitialContext();
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/TestDB");
			
		}
		
		return ds;
	}
	
	// get the SQL connection from the database
	public Connection getConnection() throws NamingException, SQLException {
		return getDataSource().getConnection();
		
	}
	
	// execute particular database command
	public Object executeDatabaseCommand(DatabaseCommand c) throws Exception {
		
		Connection conn = null;
		try {
			conn = getConnection();
			Object o = c.executeDatabaseOperation(conn);
			return o;
		} catch (SQLException e) {
			throw e;
		} catch (NamingException ne) {
			throw ne;
		} finally {
			if (conn != null) conn.close();
		}
	}
}
