<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Orders</title>

<jsp:useBean id="orders" type="java.util.ArrayList<core.Order>" scope="request"/>

</head>
<body>

Orders for the customer with id: <%= request.getParameter("cust_id") %><br>
<table border="1">
<tr>
	<th>Order id</th>
	<th>Date Placed</th>
	<th>Amount</th>
</tr>
<% for(core.Order o : orders) { %>
<tr>
	<td><%= o.getOder_id() %></td>
	<td><%= o.getDatePlace() %></td>
	<td><%= o.getAmount() %></td>
</tr>
<% } %>
</table>
</body>
</html>

