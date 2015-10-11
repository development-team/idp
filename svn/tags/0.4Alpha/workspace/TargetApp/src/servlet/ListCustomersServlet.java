package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.util.ArrayList;
import command.*;

import core.Customer;

/**
 * Servlet implementation class for Servlet: ListCustomers
 * Lists Customers from the MySQL database
 *
 */
 public class ListCustomersServlet extends javax.servlet.http.HttpServlet {
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			// perform list customer operations
			ArrayList<Customer> list = (ArrayList<Customer>)CommandExecutor.getInstance().executeDatabaseCommand(new command.ListCustomers());
			request.setAttribute("customers", list);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/customers.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		try {
			CommandExecutor.getInstance();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}