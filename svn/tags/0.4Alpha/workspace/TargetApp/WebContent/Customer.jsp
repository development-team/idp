<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Customers list</title>
</head>
<body>

<jsp:useBean id="customers" type="java.util.ArrayList<core.Customer>" scope="request"/>

<b>Registered Customers:</b><br>
<table border="1">
<tr>
	<th>ID</th>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Address</th>
	<th>Orders</th>
</tr>
<% for(core.Customer c : customers) { %>
<tr>
	<td><%= c.getId() %></td>
	<td><%= c.getName() %></td>
	<td><%= c.getSurname() %></td>
	<td><%= c.getAddress() %></td>
	<td><a href="/Orders/ListCustomerOrders?cust_id=<%= c.getId() %>">Orders</a></td>
	</tr>
<% } %>

</table>
</body>
</html>