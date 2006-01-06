<%@ page language="java" isErrorPage="true" %>
<%
  /**
   * Copyright(c) 2001 Whitehead Institute for Biomedical Research.
   *           All Right Reserve
   * @author Bingbing Yuan
   * @version 1.1
   */
%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="docs/pathblast.css"/>
</head>
<body>
<h2>ERROR!!!</h2>
An error occurred, please contact <a href="mailto:msmoot@ucsd.edu">msmoot@ucsd.edu</a> with
the error message and if possible the input parameters that caused the problem. <p>
<!--  error message: exception stacktrace -->
Detailed Message: <p>
<%
    //print stack trace
    out.println("<pre>");
    exception.printStackTrace(new java.io.PrintWriter(out));
    out.println("</pre>");
%>
</body>
</html>
