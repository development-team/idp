package command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import core.Order;

/**
 * List Orders for a particular customer
 */

public class ListCustomerOrders implements DatabaseCommand {

	private int cust_id;
	
	public ListCustomerOrders(int i) {
		this.cust_id = i;
	}
	
	public Object executeDatabaseOperation(Connection conn) throws SQLException {
		// List customer orders
		
		ArrayList<Order> list = new ArrayList<Order>();
		PreparedStatement sta = conn.prepareStatement("SELECT ID, CUST_ID, DATE_PLACED, AMOUNT FROM ORDERS WHERE CUST_ID = ?");
		sta.setInt(1, this.cust_id);
		ResultSet rs = sta.executeQuery();
		while(rs.next()) {
			Order o = new Order();
			o.setOder_id(rs.getInt(1));
			o.setUsr_id(rs.getInt(2));
			o.setDatePlace(rs.getDate(3));
			o.setAmount(rs.getInt(4));
			list.add(o);			
		}
		
		rs.close();
		sta.close();
		
		return list;
	}
}
