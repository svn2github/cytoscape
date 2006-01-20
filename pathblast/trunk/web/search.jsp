<%@ page import="wi.bioc.blastpathway.*" %>
<%@ page import="java.util.*" %>
<%
  /**
   * Copyright(c) 2001 Whitehead Institute for Biomedical Research.
   *           All Right Reserve
   * @author Bingbing Yuan
   * @version 1.1
   */
%>

<%@ include file="includes/browser_caching.jsp" %>
<html>
<head>
<link rel="stylesheet" type="text/css" href="docs/pathblast.css"/>
</head>
<body> 
<h2>PathBLAST Search</h2>


<%
//String xml_file_name = null;
try {
String uid = System.currentTimeMillis()+"W"+session.getId();
Protein[] proteins
    = (Protein[]) session.getAttribute(Config.PROTEINS_SESSION_KEY);
EValue e_value
    = (EValue) session.getAttribute(Config.EVALUE_SESSION_KEY);
String t_org
    = (String) session.getAttribute(Config.TORG_SESSION_KEY);
boolean useZero = Boolean.parseBoolean((String)session.getAttribute(Config.USE_ZERO_SESSION_KEY));

//xml_file_name = XmlFileGenerator.save(uid, proteins, e_value.getDouble(), t_org);
BlastManager bm = BlastManager.getInstance();
//bm.runBlast(xml_file_name, uid);
bm.runBlast(uid, proteins, e_value.getDouble(), t_org, useZero);
%>

<form action="result.jsp"
      enctype="application/x-www-form-urlencoded"
      method="POST"
      NAME="FormatForm"
      TARGET="Blast_Results_for_<%= uid %>">
<br />
Your request has been successfully submitted and put into the Blast Queue.
<p />
<b>Query</b>
<%
     for (int i =  0; i < proteins.length; i++) {
	 //if (!proteins[i].isEmpty()) {
	     if (i != 0) {
		 out.print("<font color='red'> --&gt; </font>");
	     }
	     out.print(proteins[i].getProteinId());
//	 }
     }
%>
<p />
<br>
<input name="FORMAT_PAGE_TARGET" type="hidden" value="Format_page">
<SCRIPT LANGUAGE="JavaScript">
 <!--
 window.name = "Format_page";
// -->
</SCRIPT>
<input name="RESULTS_PAGE_TARGET" type="hidden" value="Blast_Results"><p />
The request ID is <input name="uid" size="<%= uid.length()+15 %>" type="text" value="<%= uid %>" />
<p />
<a href="javascript:document.forms[0].submit();">
<img align="middle" alt="Format button" border="0" src="images/results.gif"></a>
<!--
&nbsp; or &nbsp;
<a href="javascript:document.forms[0].reset();">
<img align="middle" alt="Reset" border="0" src="images/RESET.gif"></a>
-->
<p />
<font size="-1">The results are estimated to be ready in 100 seconds but may be done sooner.</font>
<p />
<font size="-1">Please press &quot;RESULTS &quot; when you wish to check your results. You may also request results of a different search by entering any other valid request ID to see other recent jobs.</font>

<%
}
catch (Exception e) {
e.printStackTrace(System.out);
}
%>
</body>
</html>
