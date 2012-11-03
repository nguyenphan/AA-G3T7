<%--
    Document   : endTradingDay
    Created on : Sep 3, 2012, 7:57:06 AM
    Author     : the saboteur
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="aa.*" %>
<%@ page import="java.sql.*" %>
<jsp:useBean id="exchangeBean" scope="application" class="aa.ExchangeBean" />

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>End Trading Day</title>
  </head>
  <body>

    <%
        try{
            
            exchangeBean.endTradingDay(); // clean up instance variables
            session.invalidate();
            out.print("<h1>Your Trading Day has ended!</h1>");
            
        }catch(SQLException e){
            
            out.print("<h1>Sorry, an error has occured, please try again later. :(</h1>");
            
        }
    %>

  </body>
</html>
