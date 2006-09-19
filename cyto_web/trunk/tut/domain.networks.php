<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <meta http-equiv="content-type" content=
   "text/html; charset=ISO-8859-1">
   <title>
	Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
  "../css/cytoscape.css">
  <META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=utf-8">
  <META NAME="AUTHOR" CONTENT="Melissa Cline">
  <STYLE>
  <!--
    @page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000; widows: 0; orphans: 0 }
    P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
  -->
  </STYLE>
</HEAD>
<BODY LANG="en-US" TEXT="#000000" DIR="LTR">
  <table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
   <tr>
     <td width="10"> </td>
     <td valign="center">
        <h1>
  	Cytoscape Online Tutorial
        </h1>
     </td>
   </tr>
  </table>
  <? include "nav.php"; ?>
  <? include "nav_tut.php"; ?>
  <div id="indent">
  <center>
     <h2>Domain Networks</h2>
  </center>
  <p> 
<P>In the interaction units we have examined so far, the smallest unit
of the network is a protein.  But in reality, proteins are assembled
from <I>domains</I>, structurally-compact units with distinct
functional interpretation.  A new development in interaction network
analysis is <I>domain interaction networks</I>.  These networks
describe protein interactions more specifically, in terms of the
domains involved in the interaction.  This has two main applications:
<OL>
	<LI>Predicted domain interactions increase your confidence in
	experimentally-observed interactions.  The technology for observing
	interactions experimentally has a high false positive rate.  But,
	where observed interactions are supported by another form of
	evidence, such as domain interactions, you can have greater
	confidence in the observed interaction.
	<LI>Where there are multiple types of protein isoforms, due
        to either
	alternative splicing or genetic variation, domain interactions can
	help determine which interactions may still occur.  With alternative
	splicing, the protein produced may be a shorter form that lacks some
	domains, and thus cannot participate in some interactions.  With
	genetic variation, one or more protein domain may fail to fold
	properly or may assume a slightly different configuration, thus
	altering the binding propensity of interactions to that domain.  In
	both cases, domain interaction networks help identify the portions
	of the network most directly affected by the protein variation, and
	help determine which interactions might and might not occur.   
	
</OL>
<P>
This module will introduce you to domain networks under Cytoscape, and
point you to a few resources for protein domain analysis.
<P>
This tutorial features the following plugins:
<UL>
	<LI>The
	DomainNetworkBuilder and DomainWebLinks plugins, available from
	<A HREF="http://www.mpi-sb.mpg.de/departments/d3/">the Computational 
        Biology and Applied Algorithmics</A> group at Max Planck Institute for
	Informatics.
</UL>
<P>and the following data files
<UL>
	<LI><A HREF="domain.networks/il6.sp.sif">il6.sp.sif</A>,
	a small dataset of interactions relating to
	<A HREF="http://www.genecards.org/cgi-bin/carddisp.pl?gene=IL6">
	IL6</A>, Interleukin 6.   IL6 is a cytokine involved in B-cell 
	and nerve cell differentiation, 
	
</UL>
<H3>Download and install the DomainNetworkBuilder and DomainWebLinks 
plugin:</H3>
<OL>
	<LI>Go to the plugin page at 
	<A HREF="http://med.bioinf.mpi-sb.mpg.de/domainnet/index.html">http://med.bioinf.mpi-sb.mpg.de/domainnet/index.html</A>
	(or via the Cytoscape plugins page
	<A HREF="http://www.cytoscape.org/plugins2.php">http://www.cytoscape.org/plugins2.php</A>
	<LI>Scroll
	down to the <B>Download</B> section, and follow the link to sign the 
	license agreement form.
	<LI>
	Follow the instructions to download the DomainNetworkBuilder and
	DomainWebLinks jar files
	<LI>	Copy the two jar files into your Cytoscape plugins directory.
	<LI>	If you are currently running Cytoscape, exit and restart.
</OL>
<H3>Basic operation of the DomainNetworkBuilder plugin </H3>
<OL>
	<LI>
	Load the network <B>il6.sp.sif</B> into Cytoscape.  After performing a
	y-files organic layout, you should see a network like the one shown
	below: 
	<P><IMG SRC="domain.networks/Fig1.png" WIDTH="60%"></P>
	<LI> Under the <B>Plugins</B> menu, select <B>Domain Network</B>, 
	and <B>Create Domain Interaction Network for Current Network</B>.  
	<LI>
	A <B>Cytoscape Message</B> window will appear, asking you to select the
	species corresponding to the network, as shown below.  Select <B>Homo
	sapiens</B>, and click on <B>Connect to Database</B>
	<P>
	<IMG SRC="domain.networks/Fig2.png" WIDTH="20%">
	<LI>
	After a brief pause, you should see a new network, such as the one
	shown below (shown following layout with <B>yFiles Organic 
	layout</B>
        <P><IMG SRC="domain.networks/Fig3.png" WIDTH="60%">
	<LI>
	To explain the graph, let us focus on the lower portion of the
	network, shown below:
	<P><IMG SRC="domain.networks/Fig4.png" WIDTH="60%">
	<UL>
	   <LI>The
	   yellow nodes represent proteins, while the red nodes represent
	   protein domains.
	   <LI>Red
	   arrows connect proteins from the same domains, forming a list. Green
	   arows connect proteins to their respective domain lists.  For
	   example, we see that the protein <B>P05231</B> contains the domain
	   <B>P05231_IL6</B>, and protein <B>P08887</B> contains domains 
	   <B>P08887_ig</B>, <B>P08887_fn3</B>, and <B>P08887_Pfam-B_34367</B>.
	   <LI>Black
	   lines connect interacting domains.  For example, the domain
	   <B>P05231_IL6</B> interacts with domains <B>P08887_ig</B> and 
	   <B>P08887_fn3</B>.
        </UL>
	<LI>You
	can reduce the complexity of this network as follows:
	<UL>
	    <LI>Under
	    the <B>Plugins</B> menu, select <B>Domain Network</B>, and 
	    <B>Set Parameters</B>.  
	   <LI>You
	   should see a menu like the one shown below.  Check the box next to
	   <B>Hide domain nodes without visible domain-domain interaction 
	   edges</B>,
	   and click <B>OK</B>.
	  <LI>Your
	network should now appear as shown below.  Notice that for protein
	<B>P29597</B> at the left, there is no longer a long string of domains 
	that are not involved in any network.
        </UL>
	<P><IMG SRC="domain.networks/Fig5.png" WIDTH="60%">
	<LI>To
	get a cleaner picture, reapply a layout algorithm or rearrange nodes
	manually.  After rearranging nodes, the resulting network is shown
	below: 
	<P><IMG SRC="domain.networks/Fig6.png"  WIDTH="60%">
	<LI>What
	does this network tell us?  For example,
	<UL>
	   <LI>protein
	   <B>Q06124</B> has three interaction-related domains: 
	   two <B>SH2</B> domains and
	   one <B>Y_phosphatase</B> domains.  Protein <B>P40189</B> has four
	   interaction-related domains: three <B>fn3</B> domains and one
	   <B>lep_receptor_Ig</B> domain.  These domains are connected, indicating
	   that both <B>fn3</B> and <B>lep_receptor_Ig</B> domains tend to 
	   interact with <B>SH2</B>
	   and <B>Y_phosphatase</B> domains.  This adds a level of confidence
	   to the observed protein-protein interaction between <B>P40189</B> 
	   and <B>Q06124</B>.
	   <LI>The
	   protein <B>P13725</B> contains one interaction-related domain: a 
	   <B>LIF_OSM</B>
	   domain. Any mutation or other aberration which affects this domain
	   is likely to affect the interactions this protein.  Mutations in
	   other parts of the protein might or might not change its interaction
	   pattern.
	</UL>
<P><BR>

<H3>Further Exploration on domain significance</H3>
<P>Domain
names are not always immediately intuitive.  For any domain, you can
get further information as follows:
<OL>
	<LI>Click
	on a domain node, such as <B>P08887__fn3</B>.  
	
	<LI>Right-click
	on this node to pull up a menu for further information.  If you
	follow the link to <B>More Web Info</B>, you should see a menu as shown
	below:
	<P><IMG SRC="domain.networks/Fig7.png" WIDTH="40%" BORDER=1>
        <LI>These are all links to additional sources of information.  
	The links
	labeled <B>domains only</B> are for the square domain nodes, while
	the links labeled <B>proteins only </B>are for the round protein
	nodes.
	<OL TYPE=i>
	    <LI><B>Pfam</B>
	     is a useful resource for learning about the biological
	     significance of a type of domain: select <B>Pfam</B>.  This should
	     bring you to a page on the Pfam entry for the <B>fn3</B> domain,
	     <B>Fibronectin type III</B>.
	     <LI>Where
	     does these domain interactions come from?  Return to the menu
	     shown above, follow the link to <B>InterDom</B>, and you should 
	     arrive at
	     the InterDom entry for domain <B>fn3</B>.  InterDom predicts 
	     domain
	     interactions that are statistically likely according to a variety
	     of factors, such as whether protein-protein interaction databases
	     frequently report interactions between proteins with two specific
	     types of domains.  See the InterDom web page for further
	     information.
	     <LI>How
	     reliable are these predictions?  Return to the menu shown above
	     and select <B>3DID</B> to arrive at the 3DID web site for 
	     domain <B>fn3</B>. 
	     3DID contains records of domains that are shown interacting in
	     structural data, when two or more proteins are co-crystallized,
	     and thus represents a very high level of evidence.   However, be
	     aware that if 3DID does not report a given domain-domain
	     interaction, that does not mean that the interaction is not real;
	     it could simply mean that there are no proteins with those domains
	     that have be co-crystallized, for a variety of technical reasons.
	     <LI>Notice
	     that the 3DID entry for fn3 reports interactions with <B>IL6</B> 
	     domains.  Thus, the domain interaction shown between <B>fn3</B> 
	     and <B>il6</B> is very reliable.
	</OL>
</OL>
<P><BR>
<H3>Final note</H3>
For the sample network used in this tutorial, the nodes are
labeled with Uniprot protein IDs.  But, the typical interaction
database labels nodes by gene symbols instead.  In organisms such as
Homo Sapiens, one gene can create multiple different proteins due to
processes such as alternative splicing.  Thus, you can map protein
IDs to gene names uniquely, but cannot always map gene names to
protein IDs uniquely.  So if you have interaction data in which the
nodes are labeled by gene names, what can you do?
<OL>
	<LI>There
	are many ID mapping services that will give you a list of protein
	IDs corresponding to a gene symbol.  For example, see the list at
	the bottom of the DomainNetworkBuilder web page at
	<A HREF="http://med.bioinf.mpi-inf.mpg.de/domainnet/index.php">http://med.bioinf.mpi-inf.mpg.de/domainnet/index.php</A>
	 In cases where there are many protein IDs listed, you will still
	need to choose one.  One reasonable option is to select the longest
	protein.
	<LI>You
	can use the DomainNetworkBuilder plugin with a network that uses
	gene symbols.  In cases where multiple protein IDs are available,
	the plugin will choose the Uniprot “consensus” sequence, which
	is generally the longest protein and the one with the fewest (or no)
	unusual mutations.  If there is more than one protein for a given
	gene, the plugin will give you a warning.
</OL>

<P><B>Congratulations!</B>
You have now performed some very hard-core bioinformatics analysis! 
<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</BODY>
</HTML>
