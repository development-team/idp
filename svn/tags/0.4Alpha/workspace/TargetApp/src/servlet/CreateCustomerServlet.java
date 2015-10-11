package servlet;

import javax.servlet.RequestDispatcher;
import core.Customer;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import command.DatabaseCommand;
import command.CommandExecutor;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import command.CreateCustomer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class CreateCustomerServlet extends HttpServlet { 


	private String Patronim;
 	public String getPatronim() { return this.Patronim; }
 	public  void  setPatronim( String in) { this.Patronim = in ; }
public void doGet(HttpServletRequest request,HttpServletResponse response)
 throws java.io.IOException ,javax.servlet.ServletException {
// TODO Auto-generated method stub
		try {
            // create customer - get parameters first

                     String address_field_field= request.getParameter("address_field_prf");
                           
                     String Lastname_field= request.getParameter("Lastname_prf");
                           
                     String id_field= request.getParameter("id_prf");
                           
                     String Patronim_field= request.getParameter("Patronim_prf");
                           
                     String Firstname_field= request.getParameter("Firstname_prf");
                           

            Customer c = new Customer();


c.setAddress(address_field_field);
c.setLastname(Lastname_field);
//String the solution
int cust_id = Math.abs((int)System.currentTimeMillis());
c.setId(cust_id);
c.setPatronim(Patronim_field);
c.setFirstname(Firstname_field);
            DatabaseCommand command = new CreateCustomer(c);
            int rows = (Integer)CommandExecutor.getInstance().executeDatabaseCommand(command);
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/customer_created.jsp");
            rd.forward(request, response);

		} catch (Exception e) {
			throw new ServletException(e);
		}
}public void doPost(HttpServletRequest request,HttpServletResponse response)
 throws java.io.IOException ,javax.servlet.ServletException {
// TODO Auto-generated method stub
}public void init()
 throws javax.servlet.ServletException {
super.init();
		try {
			CommandExecutor.getInstance();
		} catch (Exception e) {
			throw new ServletException(e);
		}
}
 }