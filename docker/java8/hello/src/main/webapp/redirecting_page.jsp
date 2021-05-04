<%@ page import = "java.io.*,java.util.*" %>

<html>
   <head>
      <title>Page Redirection</title>
   </head>
   
   <body>
      <center>
         <h1>Page Redirection</h1>
      </center>
      <%
         // New location to be redirected
         String site = new String("/redirection_target.jsp");
         response.setStatus(response.SC_MOVED_TEMPORARILY);
         response.setHeader("Location", site); 
      %>
   </body>
</html>