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
            //test retrieve trader from database
            TraderDAO traderDAO = new TraderDAO();
            Trader trader = traderDAO.getTraderWithUsername("ptlenguyen");
            
            //test retrieve stock from database
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
            //test add trader to database
            Trader sarah = traderDAO.getTraderWithUsername("sarah");
                if(sarah==null){
                    traderDAO.add(new Trader("sarah",1000000));
                    sarah = traderDAO.getTraderWithUsername("sarah");
                }

            //test update trader in database
            sarah.deductCredit(5000);
            traderDAO.update(sarah);
            sarah = traderDAO.getTraderWithUsername("sarah");
        
        %>
        
        <%=sarah%><br/>
        
        <%
            //test add ask
            Ask ask = new Ask("sarah",1,5000);
            AskDAO askDAO = new AskDAO();
            askDAO.add(ask);
        %>
        
        <%=ask%>
        
    </body>
</html>