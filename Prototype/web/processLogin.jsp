<%-- 
    Document   : processLogin
    Created on : Aug 30, 2012, 11:07:00 AM
    Author     : the saboteur
--%>
<%@page import="Database.TraderDAO" %>
<%@page import="Entity.Trader" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Process Login</title>
    </head>
    <body>
        <%
            String username = request.getParameter("id").trim();
            
            //get trader from database
            TraderDAO traderDAO = new TraderDAO();
            Trader trader = traderDAO.getTraderWithUsername(username);
            
            //if trader does not exist, create a new one.
            if(trader==null){
                trader = new Trader(username);
                traderDAO.add(trader);
            }
            
            //save trader in session
            session.setAttribute("userId", request.getParameter("id").trim());
            session.setAttribute("authenticatedUser", true);
        %> 
        
        <jsp:forward page = "loginSuccess.jsp" />
    </body>
</html>
