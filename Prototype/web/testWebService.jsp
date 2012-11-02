<%-- 
    Document   : testWebService
    Created on : Oct 30, 2012, 12:40:09 PM
    Author     : Lionel
--%>

<%@ page import="aa.*" %>
<jsp:useBean id="exchangeBean" scope="application" class="aa.ExchangeBean" />
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Test Web Service</title>
    </head>
    <body>
<%
        boolean status = exchangeBean.sendToBackOffice("my test string");
%>
    Status of sending to Back Office: <%=status%>
    </body>
</html>
