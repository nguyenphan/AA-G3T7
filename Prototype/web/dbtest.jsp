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
            Trader trader = traderDAO.getTraderWithUsername("ptlenguyen");
            
            StockDAO stockDAO = new StockDAO();
            Stock smu = stockDAO.getStockWithName("SMU");
            Stock nus = stockDAO.getStockWithName("NUS");
            Stock ntu = stockDAO.getStockWithName("NTU");
        %>
        
        <%=trader%><br/>
        <%=smu%><br/>
        <%=nus%><br/>
        <%=ntu%><br/>
        
        <%
        Trader sarah = traderDAO.getTraderWithUsername("sarah");
            if(sarah==null){
                traderDAO.add(new Trader("sarah",1000000.00));
                sarah = traderDAO.getTraderWithUsername("sarah");
            }
        %>
        
        <%=sarah%><br/>
        
    </body>
</html>
