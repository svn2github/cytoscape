<%@ page import="wi.bioc.blastpathway.*" %>
<%@ page import="java.util.*" %>
<%
  /**
   * Copyright(c) 2001 Whitehead Institute for Biomedical Research.
   *           All Right Reserve
   * @author Bingbing Yuan
   * @version 1.1
   */

String uid = request.getParameter("uid");
if (uid == null) {
    out.println("cannot find uid.");
}
else {
    BlastManager bm = BlastManager.getInstance();
    PathBlast pb = bm.getBlast(uid);
    if (pb == null) {
	//we should search for the index file
	String outputfile = bm.getOutputDir(uid) + "/index.html";
	if (new java.io.File(outputfile).exists()) {
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.sendRedirect(bm.getOutputUrlBase(uid)+"/index.html");
        }
	else {
%>
Error: cannot find blast for uid: <%= uid %>.
<%
        }
    }
    else {
	if (pb.isDone()) {
	    String outputfile = bm.getOutputDir(uid) + "/index.html";
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.sendRedirect(bm.getOutputUrlBase(uid)+"/index.html");
        }
	else {
	    long now = System.currentTimeMillis();
	    long start = pb.getStartTime();
	    long refresh = 10000;
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="docs/pathblast.css"/>
</head>
<body>
<h2>Searching...</h2>
<form action="result.jsp" 
     enctype="application/x-www-form-urlencoded" method="POST">
<SCRIPT LANGUAGE="JavaScript">
<!--
setTimeout('document.forms[0].submit();', <%= refresh %>);
//-->
</SCRIPT>
<p />
<table>
<tr><td>Request ID</td><td><%= uid %></td></tr>
<tr><td>Status</td><td>Searching</td></tr>
<tr><td>Submitted at</td><td><%= new Date(start) %></td></tr>
<tr><td>Current time</td><td><%= new Date(now) %></td></tr>
</table>
<p />
<hr />
<p />
This page will be automatically updated every 10 seconds until
search is done<BR>
Time elapsed so far: <%= (now-start)/1000  %> seconds <BR>
<input type="hidden" name="uid" value="<%= uid %>" />
</form>
</body>
</html>
<%
        }
    }  
}
%>

