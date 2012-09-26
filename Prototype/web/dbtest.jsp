<%-- 
    Document   : dbtest
    Created on : Sep 26, 2012, 5:03:47 PM
    Author     : ptlenguyen
--%>
<%@page import="Database.*" %>
<%@page import="Entity.*" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%
            TraderDAO traderDAO = new TraderDAO();
            Trader t = traderDAO.getTraderWithUsername("ptlenguyen");
            
        %>
        
        <%=t%>
        
    </body>
</html>
