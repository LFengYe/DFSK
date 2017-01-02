<%-- 
    Document   : exit
    Created on : 2015-8-20, 9:51:30
    Author     : LFeng
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>退出</title>
    </head>
    <body>
        <%
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.html");
        %>
    </body>
</html>
