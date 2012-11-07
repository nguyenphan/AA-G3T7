<%@page import="java.sql.Connection" %>
<%@page import="java.sql.SQLException" %>
<%@page import="Database.ConnectionFactory" %>
<%@page import="Database.TraderDAO" %>
<%@page import="Entity.Trader" %>

<%-- 
    Document   : processLogin
    Created on : Aug 30, 2012, 11:07:00 AM
    Author     : the saboteur
--%>

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

            Connection conn = null;

            try {

                conn = ConnectionFactory.getInstance().getConnection();
                conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);  //enable range locks
                conn.setAutoCommit(false);

                //get trader from database
                Trader trader = TraderDAO.getTraderWithUsername(conn, username);    //note that trader, whether existant or not, will be locked
                conn.commit();  //release lock, otherwise existant trader cannot be updated later, like for updating credits

                //if trader does not exist, create a new one.
                if (trader == null) {

                    trader = new Trader(username);
                    TraderDAO.getTraderWithUsername(conn, username);    //select again to get range lock
                    TraderDAO.add(conn, trader); //add
                    conn.commit();  //release lock

                }

                //save trader in session
                session.setAttribute("userId", request.getParameter("id").trim());
                session.setAttribute("authenticatedUser", true);


            } catch (SQLException e) {

                //rollback and display error
                e.printStackTrace();
                if (conn != null) {
                    conn.rollback();
                }
                System.err.print("Transaction is rolled back.");

            } finally {

                //release connection
                if (conn != null) {
                    conn.close();
                }
            }

            RequestDispatcher rd = request.getRequestDispatcher("loginSuccess.jsp");
            rd.forward(request, response);

        %> 
        
    </body>
</html>
