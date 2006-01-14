<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="wi.bioc.blastpathway.EValue" %>
<%@ page import="wi.bioc.blastpathway.Protein" %>
<%@ page import="wi.bioc.blastpathway.Config" %>
<%@ page import="org.biojava.bio.*" %>
<%@ page import="org.biojava.bio.seq.*" %>
<%@ page import="org.biojava.bio.seq.db.*" %>
<%@ page import="org.biojava.bio.seq.io.*" %>
<%@ page import="org.biojava.bio.symbol.*" %>
<%@ page import="nct.parsers.FastaProteinParser" %>

<%
final String[] _NAMES_ = Config.PROTEIN_NAMES; //mapping

boolean error = false;
String globalErrorMessage = "";

String e_value = request.getParameter("E_VALUE");
//check e_value to see if it is less than 1.
EValue nEvalue = (EValue) session.getAttribute(Config.EVALUE_SESSION_KEY);

if (e_value != null) {
    try {
        nEvalue = new EValue(e_value);
    } catch (Exception ignore) {
        error = true;
        globalErrorMessage = "<br/>ERROR: e-value should be within 0 and 1.<br/>";
    }
}

if (nEvalue == null)
        nEvalue = new EValue(EValue.DEFAULT_EVALUE);
session.setAttribute(Config.EVALUE_SESSION_KEY, nEvalue);

String t_org = request.getParameter("T_ORG");
if (t_org != null) 
	session.setAttribute(Config.TORG_SESSION_KEY, t_org);

Protein[] proteins = (Protein[]) session.getAttribute(Config.PROTEINS_SESSION_KEY);

/*
String reset0 = request.getParameter("homer.x");
String reset1 = request.getParameter("marge.x");
String reset2 = request.getParameter("reset.x");
if ( reset0 != null || reset1 != null || reset2 != null ) {
	proteins = null;
        globalErrorMessage += "<br/>reseting.";
} else {
        globalErrorMessage += "<br/>NOT.";
}
*/

if (proteins == null ) {
%> <%@ include file="includes/browser_caching.jsp" %> <%
    proteins = new Protein[3];
    proteins[0] = new Protein();
    proteins[1] = new Protein();
    proteins[2] = new Protein();
} else {
    if (request.getParameter("SamplePathway1.x") != null) {
        //make sample proteins
        proteins = new Protein[3];
        proteins[0] = Protein.createProtein("MYS1_YEAST"); // Myo1
        proteins[1] = Protein.createProtein("ACT_YEAST"); // Act1
        proteins[2] = Protein.createProtein("ST20_YEAST"); // Ste20
        t_org = Config.T_ORG_VALUES[0];
        nEvalue = new EValue(EValue.DEFAULT_EVALUE);
    } else if (request.getParameter("SamplePathway2.x") != null) {
        //make sample proteins
        proteins = new Protein[3];
        proteins[0] = Protein.createProtein("ST11_YEAST");// Ste11
        proteins[1] = Protein.createProtein("STE7_YEAST");//Ste7
        proteins[2] = Protein.createProtein("KSS1_YEAST");//Kss1
        nEvalue = new EValue(EValue.DEFAULT_EVALUE);
        t_org = Config.T_ORG_VALUES[0];
    } else if (request.getParameter("SamplePathway3.x") != null) {
        //make sample proteins
        proteins = new Protein[3];
        proteins[0] = Protein.createProtein("ST11_YEAST");// Ste11
        proteins[1] = Protein.createProtein("STE7_YEAST");//Ste7
        proteins[2] = Protein.createProtein("KSS1_YEAST");//Kss1
        t_org = Config.T_ORG_VALUES[3];
        nEvalue = new EValue(EValue.DEFAULT_EVALUE);
    } else if (request.getParameter("SamplePathway4.x") != null) {
        //make sample proteins
        proteins = new Protein[4];
        proteins[0] = Protein.createProtein("UBIQ_YEAST"); //UB14??? ubiquitin
        proteins[1] = Protein.createProtein("UBC3_YEAST");//CDC34
        proteins[2] = Protein.createProtein("CDC4_YEAST");//CDC4
        proteins[3] = Protein.createProtein("CC53_YEAST");//CDC53
        t_org = Config.T_ORG_VALUES[0];
        nEvalue = new EValue(1.0E-2);
    } else if (request.getParameter("SamplePathway5.x") != null) {
        //make sample proteins
        proteins = new Protein[3];
        proteins[0] = Protein.createProtein("UBA2_YEAST"); // UBA2
        proteins[1] = Protein.createProtein("RH31_YEAST"); // AOS1
        proteins[2] = Protein.createProtein("UBC9_YEAST");//UBC9
        t_org = Config.T_ORG_VALUES[0];
        nEvalue = new EValue(1.0E-2);
    } else if (request.getParameter("MoreProteins.x") != null) {
        if (proteins.length < 5) {
            //make one more protein
            Protein[] newProteins = new Protein[proteins.length+1];
            for (int i = 0; i < proteins.length; i++) {
                newProteins[i] = proteins[i];
            }
            newProteins[proteins.length] = new Protein();
            proteins = newProteins;
        }
    } else if (request.getParameter("LessProteins.x") != null) {
        if (proteins.length > 2) {
            //destroy the last protein
            Protein[] newProteins = new Protein[proteins.length-1];
            for (int i = 0; i < newProteins.length; i++) {
                newProteins[i] = proteins[i];
            }
            proteins = newProteins;
        }
    } else {
    	//
	// Now actually process the proteins and move forward if things are ok.
	// 

    	boolean disambiguateRequired = false;
    	// update proteins
	for (int i = 0; i < proteins.length; i++) {
            String seq = request.getParameter(_NAMES_[i]+"_seq");
            seq = (seq == null? "" : seq);
	    // A regular expression for a FASTA file.
	    if ( seq.matches(">.+(\n??|\r??){1,}\\w+((\n??|\r??){1,}\\w*)*") ) {
                        try {
                        SequenceIterator si = FastaProteinParser.parseString(seq);
                        if ( si.hasNext() ) 
                                seq = si.nextSequence().seqString();
                        } catch ( Exception e ) { e.printStackTrace(); }
            }
            proteins[i].setSeq(seq);
	    System.out.println("got seq '" + seq + "'");
		
       	    String proteinId = request.getParameter(_NAMES_[i]+"_id");
            proteinId = (proteinId == null? "" : proteinId.trim());
	    if ( proteinId.length() == 0 && seq.length() > 0 )
	    	proteinId = "protein_" + _NAMES_[i];
	    else
	    	System.out.println("pid len " + proteinId.length() + " seq len: " + seq.length() );
	    System.out.println("final protein id '" + proteinId + "'");
	    proteinId = proteinId.toUpperCase();
            proteins[i].setProteinId(proteinId);


	    // make sure the proteins are valid
	    int numValid = proteins[i].validate();
	    if ( numValid <= 0 )  {
	    	error = true;
                globalErrorMessage += "<br/>One or more invalid proteins found.";
	    } else if ( numValid > 1 ) 
	    	disambiguateRequired = true;
        }

        //validate if seq ids are unique
        HashMap pids = new HashMap();
        for (int k = 0; k < proteins.length; k++) {
                String proteinId = proteins[k].getProteinId();
		System.out.println("checking " + proteinId);

                if (proteinId!=null) {
		    if ( pids.containsKey(proteinId) ) {
                    	Integer n = (Integer)pids.get(proteinId);
                        globalErrorMessage += "<br/>ERROR: Protein ID for "+_NAMES_[k] +" is the same as "+_NAMES_[n.intValue()]+": " + proteinId;
                    } else {
                        pids.put(proteinId, new Integer(k));
                    }
                }
        }

        if ( !error && globalErrorMessage.length() == 0 ) {
		if ( disambiguateRequired )
            		request.getRequestDispatcher("disambiguate.jsp").forward(request, response);
		else
            		request.getRequestDispatcher("search.jsp").forward(request, response);
        }
    }
}

session.setAttribute(Config.PROTEINS_SESSION_KEY, proteins);

%>
<html>
  <head>
    <title>PathBLAST</title>
     <link rel="stylesheet" type="text/css" href="docs/pathblast.css"/>
     <script language="javascript">
     <!--
       function alertError() {
         alert("There are errors in your form, the IDs that cannot match to the database are marked red, please correct it before submit it again.");
       }
     //-->
     </script>
   </head>

  <body> 
  <div id="title">
  <div id="titlebackground">
  <h1>PathBLAST</h1>
  </div>
  </div>
  <div id="inst">
      <ul class="institutions">
        <li class="institutions"><i>Collaborating Labs:</i></li>

        <li class="institutions">UC San Diego
	<ul class="submenu">
	  <li class="submenu"><a class="submenu" href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab">Ideker Lab</a></li>
	  <li class="submenu"><a class="submenu" href="http://www.ucsd.edu/">ucsd.edu</a></li>
	</ul>
        <li class="institutions">UC Berkeley
	<ul class="submenu">
          <li class="submenu"><a class="submenu" href="http://www.cs.berkeley.edu/People/Faculty/Homepages/karp.html">Karp Lab</a></li>
          <li class="submenu"><a class="submenu" href="http://www.berkeley.edu/">UC Berkeley</a></li>
	</ul>
	</li>
        <li class="institutions">Tel Aviv University
	<ul class="submenu">
          <li class="submenu"><a class="submenu" href="http://www.cs.tau.ac.il/~roded">Sharan Lab</a>
          <li class="submenu"><a class="submenu" href="http://www.tau.ac.il">Tel Aviv University</a>
	</ul>
	</li>
        <li class="institutions">Whitehead Institute
	<ul class="submenu">
          <li class="submenu"><a class="submenu" href="http://wi.mit.edu/">Whitehead Institute</a>
          <li class="submenu"><a class="submenu" href="http://jura.wi.mit.edu/bio">Bioinformatics and Research Computing</a>
	</ul>
	</li>
      </ul>
     <br/>
   </div>
   <h2>About PathBLAST</h2>
  
    <p>
      PathBLAST searches the protein-protein interaction network of the target organism
      to extract all protein interaction pathways that align with a pathway query.
    </p>
    <p>
      <ul class="inline">
         <li class="inline">To learn more about PathBLAST read:</li>
        <li class="inline"><a href="docs/faq.html">FAQ</a></li>
         <li class="inline"><a href="docs/publications.html">Selected Publications</a> </li>
      </ul>
    </p>
  
   <a name="new"></a>
   <h2>PathBLAST Search</h2>
     <form name="mainForm" action='blastpathway.jsp#new' method='POST'>
    
    <p>Example Input Pathways:
      <ul class="inline">
        <li class="inline"> <input type="image" src="images/input_sample_pathway1.gif" alt="sample pathway1" border="0" name="SamplePathway1" value="Sample pathway1" /></li>
        <li class="inline"><input type="image" src="images/input_sample_pathway2.gif" alt="sample pathway2" border="0" name="SamplePathway2" value="Sample pathway2" /></li>
        <li class="inline"><input type="image" src="images/input_sample_pathway3.gif" alt="sample pathway3" border="0" name="SamplePathway3" value="Sample pathway3" /></li>
        <li class="inline"><input type="image" src="images/input_sample_pathway4.gif" alt="sample pathway4" border="0" name="SamplePathway4" value="Sample pathway4" /></li>
        <li class="inline"><input type="image" src="images/input_sample_pathway5.gif" alt="sample pathway5" border="0" name="SamplePathway5" value="Sample pathway5" /></li>
       </ul>
     </p>
     <p>
      Please enter the proteins in your pathway query: 
     </p>
     <p class="error">
	<% /*= globalErrorMessage */ %> 
     </p>
     <p>
      <table> 
        <tr>
          <th></th>
          <th></th>
          <th><a href="docs/seq_output_name.html">Protein ID</a></th>
          <th></th>
          <th><a href="docs/sequence_format.html">Protein Sequence</a></th>
        </tr>
<%
for (int i = 0; i < proteins.length; i++) {
   //String aId = _NAMES_[proteins[i].getIndex()-1];
   String aId = _NAMES_[i];
   String aIdHtml = " ";
   String errMsg = proteins[i].getProteinError();
   if ( errMsg != null )
     aIdHtml = "<a href=\"javascript:alert('"+errMsg+"')\"><img src='images/Stop.gif' border='0'></img></a>";
   
%>
        <tr class="node">
          <td><%= aIdHtml %></td>
          <td><%= aId %></td>
          <td><input type="text" name="<%= aId %>_id" value="<%= proteins[i].getProteinId() %>" size="15" maxlength=20></td>
          <td>and/or</td>
          <td><textarea name="<%= aId %>_seq" rows=5 cols=40><%= proteins[i].getSeq() %></textarea></td>
        </tr>

<% if ( i < proteins.length -1 ) { %>
        <tr>
          <td colspan=5 align="center"><img src="images/Arrow.gif"></img></td>
        </tr>
<% 
   } 
}
%>
      </table>
    <p/>


    <p>
      <ul class="inline">
<% if ( proteins.length < 5 ) { %>
        <li class="inline"><input type="image" src="images/add_protein.gif" alt="more proteins" border="0" name="MoreProteins" value="More Proteins" /></li>
<% } %>
<% if ( proteins.length > 2 ) { %>
        <li class="inline"><input type="image" src="images/remove_protein.gif" alt="less proteins" border="0" name="LessProteins" value="Less Proteins" /></li>
<% } %>
      </ul>
    </p>




    <p>Please enter the BLAST <a href="docs/e_value.html">E-value Threshold</a> for protein alignment <input type="text" name="E_VALUE" size="8" value="<%= nEvalue.getString() %>" maxlength=20></p>
    <p>Please select the <a href="docs/target_organism.html">Target Organism Network</a>: 
      <select name="T_ORG">
<%
        for (int j=0; j<Config.T_ORG_NAMES.length;j++) {
          if ((t_org != null) && t_org.equals(Config.T_ORG_VALUES[j])) {
%>
            <option value="<%=Config.T_ORG_VALUES[j]%>" selected> <%=Config.T_ORG_NAMES[j]%> </option>
<%
          } else {
%>
            <option value="<%=Config.T_ORG_VALUES[j]%>"> <%=Config.T_ORG_NAMES[j]%> </option>
<%
          }
        }
%>

      </select>
    </p>
  
    <p>
      <ul class="inline">
        <li class="inline"><input type=image src="images/NEXT.gif" alt="blast" value="blast" border="0" valign="bottom" name="blast" /></li>
	<!--
        <li class="inline">or </li>
        <li class="inline"><a href="javascript:document.forms[0].reset();"><img src="images/RESET.gif" alt="reset all" border="0" /></a></li>
        <li class="inline"><input type=submit alt="blast" value="blast" name="blast" /></li>
        <li class="inline">or </li>
        <li class="inline"><input type=reset name="homer" value="marge"/></li>
	-->
      </ul>
    </p>
    </form>
    <div class="footer">
    <p>Please send questions and comments to: <a href="mailto:msmoot@ucsd.edu">msmoot@ucsd.edu</a>
    </p>
    <p>
Funding for PathBLAST is provided by a federal grant from the U.S. <a href="http://www.nigms.nih.gov">National Institute of General Medical Sciences (NIGMS)</a> of the <a href="http://www.nih.gov">National Institutes of Health (NIH)</a> under award number GM070743-01 and the U.S. <a href="http://www.nsf.gov">National Science Foundation (NSF)</a>. 
    </p>
    </div>
  </body>
</html>
