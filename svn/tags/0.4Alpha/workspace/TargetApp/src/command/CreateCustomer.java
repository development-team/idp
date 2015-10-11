package command;

import core.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class CreateCustomer implements DatabaseCommand { 
public  CreateCustomer(Customer c)
{

}

	private Customer cust;
 	public Customer getCreateCustomer_CustField() { return this.cust; }
 	public  void  setCreateCustomer_CustField( Customer in) { this.cust = in ; }
	private String Patronim;
 	public String getPatronim() { return this.Patronim; }
 	public  void  setPatronim( String in) { this.Patronim = in ; }
public Object executeDatabaseOperation(Connection conn)
 throws java.sql.SQLException {
int i = -1;
PreparedStatement sta = conn.prepareStatement("INSERT INTO CUSTOMER (customer_address, customer_id, customer_name, Patronim) VALUES ( ? ,  ? ,  ? ,  ? )");

	                       i=i+1; 
	                       sta.setString(i,cust.getAddress().toString());
	                           
	                       i=i+1; 
	                       sta.setString(i,cust.getLastname().toString());
	                           
	                       i=i+1; 
	                       sta.setString(i,cust.getId().toString());
	                           
	                       i=i+1; 
	                       sta.setString(i,cust.getPatronim().toString());
	                           
	                       i=i+1; 
	                       sta.setString(i,cust.getFirstname().toString());
	                           
	int rows_updated = sta.executeUpdate();
	sta.close();
		
	return new Integer(rows_updated);
}
 }