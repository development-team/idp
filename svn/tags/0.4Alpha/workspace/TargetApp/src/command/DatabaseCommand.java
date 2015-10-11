package command;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseCommand {
	public Object executeDatabaseOperation(Connection conn) throws SQLException ;
}


