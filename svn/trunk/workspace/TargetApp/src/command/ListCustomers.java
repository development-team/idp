package command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import core.Customer;

/**
 * List existing customers in the database.
 */

public class ListCustomers implements DatabaseCommand {

	public Object executeDatabaseOperation(Connection conn) throws SQLException {
		// List customers in the database
		
		ArrayList<Customer> list = new ArrayList<Customer>();
		Statement sta = conn.createStatement();
		ResultSet rs = sta.executeQuery("SELECT ID, FIRST_NAME, LAST_NAME, ADDRESS FROM CUSTOMER");
		while(rs.next()) {
			Customer cust = new Customer();
			cust.setId(rs.getInt(1));
			cust.setFirstname(rs.getString(2));
			cust.setLastname(rs.getString(3));
			cust.setAddress(rs.getString(4));
			list.add(cust);
		}
		
		rs.close();
		sta.close();
		
		return list;
	}
}
