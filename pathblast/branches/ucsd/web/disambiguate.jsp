<%@ page import="wi.bioc.blastpathway.*" %>
<%@ page import="java.util.*" %>
<%@ include file="includes/browser_caching.jsp" %>
<%
  /**
   * Copyright(c) 2001 Whitehead Institute for Biomedical Research.
   *           All Right Reserve
   * @author Bingbing Yuan
   * @version 1.1
   */

Protein[] proteins = (Protein[]) session.getAttribute(Config.PROTEINS_SESSION_KEY);
String[] _NAMES_ = Config.PROTEIN_NAMES;
String globalErrorMessage = "";
String blast2 = request.getParameter("blast2.x");
if ((blast2 != null) && (blast2.length()>0)) {

    //update proteins
    for (int i = 0; i < proteins.length; i++) {
	String proteinId = request.getParameter(Config.PROTEIN_NAMES[i]+"_id");
	proteinId = (proteinId == null? "" : proteinId.trim());
	if ((proteinId!=null)&&(proteinId.trim().length()>0)) {
		String id = (String)(proteins[i].getPotentialIds().get( Integer.parseInt(proteinId) ));
        	proteins[i].setProteinId( id );
	}
    }

    //validate if seq ids are unique
    HashMap pids = new HashMap(proteins.length);
    for (int k = 0; k < proteins.length; k++) {
            String proteinId = proteins[k].getProteinId();

            if (proteinId!=null) {
                if ( pids.containsKey(proteinId) ) {
                    Integer n = (Integer)pids.get(proteinId);
                    globalErrorMessage += "ERROR: Protein ID for "+_NAMES_[k]
                        +" is the same as "+_NAMES_[n.intValue()]+": '"+ proteinId+"'<br>";
                } else {
                    pids.put(proteinId, new Integer(k));
                }
            }
    }

    if (globalErrorMessage.length() <= 0) {
	request.getRequestDispatcher("search.jsp").forward(request, response);
    }
}
%>

<html>
<head>
<link rel="stylesheet" type="text/css" ref="docs/pathblast.css" />
</head>
<body>
<h2>Confirm Protein Selection</h2>
<p><font color="red"><%= globalErrorMessage %></font><p/>
<form name="mainForm" action="disambiguate.jsp" method="POST">
<table width="750" cellspacing="12">
<%
     for (int k = 0; k < proteins.length; k++)
     {
       String proteinId = proteins[k].getProteinId();
       String seq = proteins[k].getSeq();
       System.out.println(k + " pid " + proteinId);
       System.out.println(k + " seq " + seq);


       if ((null != proteinId) && (proteinId.length() > 0)) {
	   List<String> list = proteins[k].getPotentialIds();
           String there_are_choices;
           if (list.size() >= 2) {
               there_are_choices = "there are multiple choices";
           } else {
               there_are_choices = "there is only one choice";
           }
%>
  <tr>
    <td>
      <table cellspacing='1' cellpadding='1'>
        <tr> 
          <th>For your protein <%= _NAMES_[k] %>: <b> <%= proteinId %></b>, <%= there_are_choices %>:</th>
        </tr>
<%
	    int i = 0;
	    for ( String potId : list ) {
	    	String species = Config.getSynonymMapper().getSynonym(potId,"species");
	    	String desc = Config.getSynonymMapper().getSynonym(potId,"description");
	       String checked = "";
	       if (i == 0)
		   checked = "checked";
%>
        <tr>
          <td><input type='radio' name='<%= _NAMES_[k] %>_id'
                     value='<%= i %>' <%= checked %>>  
		     <%= potId %> (<%= species %>) - <%=desc %>
		     </input></td>
        </tr>
<%
		i++;
	   }
%>
      </table>
    </td>
  </tr>
<%
       }
     }
%>
  <tr>
    <td><input type=image src="images/blast.gif" alt="blast" value="blast" border="0" name="blast2" /></td>
  </tr>
</table>
</form>
</body>
</html>
