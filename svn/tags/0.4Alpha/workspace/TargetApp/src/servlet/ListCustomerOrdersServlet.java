package servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import command.CommandExecutor;
import command.DatabaseCommand;
import command.ListCustomerOrders;
import core.Order;


/**
 * Servlet implementation class for Servlet: ListCustomerOrdersServlet
 *
 */
 public class ListCustomerOrdersServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// List customer orders
		try {

			//
			int cust_id = 0;
			try {
				cust_id = Integer.parseInt(request.getParameter("cust_id"));
			} catch (NumberFormatException nfe) {
				throw new ServletException("Invalid Customer Number Format");
			}

			// execute database command and obtain list of orders
			DatabaseCommand command = new ListCustomerOrders(cust_id);
			ArrayList<Order> list = (ArrayList<Order>)CommandExecutor.getInstance().executeDatabaseCommand(command);

			// set request attribute and forward to a JSP
			request.setAttribute("orders", list);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/orders.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			throw new ServletException(e);
		}

	}
}