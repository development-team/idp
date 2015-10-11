package servlet;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;


import command.CommandExecutor;
import command.CreateOrder;
import command.DatabaseCommand;
import core.Order;

/**
 * Servlet implementation class for Servlet: CreateOrderServlet
 *
 */
 public class CreateOrderServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			// create new order
			int cust_id = (int)Math.random();
			try {
				cust_id = Integer.parseInt(request.getParameter("cust_id"));
			} catch (NumberFormatException nfe) {};
			String date_placed_str = request.getParameter("date_placed");
			Date date_placed = new Date(System.currentTimeMillis());
			try {
				long d = Date.parse(date_placed_str);
				date_placed = new Date(d);
			} catch (Exception e) {}
			int order_amount = 0;
			try {
				order_amount = Integer.parseInt(request.getParameter("order_amount"));
			} catch (NumberFormatException nfe) {}

			// create an order object
			Order o = new Order();
			int oid = Math.abs((int)System.currentTimeMillis());
			o.setOder_id (oid);
			o.setDatePlace(date_placed);
			o.setUsr_id(cust_id);
			o.setAmount(order_amount);

			// create and execute database command
			DatabaseCommand command = new CreateOrder(o);
			Integer rows = (Integer)CommandExecutor.getInstance().executeDatabaseCommand(command);

			// forward to a JSP
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/order_created.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}