package command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import core.Order;
/**
 * Create Order in the database
 */

public class CreateOrder implements DatabaseCommand {

	private Order order;

	public CreateOrder(Order o) {
		this.order = o;
	}
	
	public Object executeDatabaseOperation(Connection conn) throws SQLException {
		// Create order
		PreparedStatement sta = conn.prepareStatement("INSERT INTO ORDERS (ID, CUST_ID, DATE_PLACED, AMOUNT) VALUES (?, ?, ?, ?)");
		sta.setInt(1, order.getOder_id());
		sta.setInt(2, order.getUsr_id());
		sta.setDate(3, order.getDatePlace());
		sta.setInt(4, order.getAmount());
		int rows_inserted = sta.executeUpdate();
		sta.close();
		
		return rows_inserted;
	}
}
