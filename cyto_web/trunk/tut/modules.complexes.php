!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content=
"text/html; charset=ISO-8859-1">
<title>Cytoscape Online Tutorial</title>
<link rel="stylesheet" type="text/css" media="screen" href=
"../css/cytoscape.css">
<meta http-equiv="CONTENT-TYPE" content="text/html; charset=utf-8">
<meta name="AUTHOR" content="Melissa Cline">
<style type="text/css">
  <!--
    @page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000; widows: 0; orphans: 0 }
    P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
  -->
  
</style>
</head>
<body lang="en-US" text="#000000">
<table id="feature" border="0" cellpadding="0" cellspacing="0"
summary="">
<tr>
<td width="10"></td>
<td valign="center">
<h1>Cytoscape Online Tutorial</h1>
</td>
</tr>
</table>

<? include "nav.php"; ?>
<? include "nav_tut.php"; ?>
<center>
<h2>Modules and complexes</h2>
</center>

<P>Biological
networks have a modular architecture.  A <I>network module</I> is a
group of nodes in the network that work together to execute some
common function.  Once you have identified the nodes in a module, you
can intuitively reduce the complexity of your network by replacing
the individual nodes with one large parent
node, as illustrated in the conceptual diagram below.  
This will allow you to focus on the interactions with the module,
and not worry about its internal operation. 

<TABLE BORDER=1 ALIGN=center>
  <TR>
    <TD><IMG SRC="modules.complexes/Fig1.jpg" WIDTH=245 HEIGHT=197>
    <TD><IMG SRC="modules.complexes/Fig2.jpg" WIDTH=261 HEIGHT=197></P>
  </TR>
</TABLE>
<P>This
tutorial will cover methods for finding modules and <I>complexes</I>,
a special type of module in which several individual proteins are
assembled into one larger macromolecular machine.  In
this tutorial, you will learn</P>
<UL>
	<LI>How
	to identify putative complexes in two ways: through network
	connectivity, and through connectivity and coexpression.
	<LI>
	How to use expression data to identify the putative modules or
	pathways with significant response to the experimental conditions.
</UL>
<P>This
tutorial features the following plugins, all available via the
<A HREF="http://www.cytoscape.org/plugins2.php">Cytoscape plugins page</A>.
<UL>
	<LI>The
	MCODE Plugin, developed by Gary Bader at <A HREF="http://www.cbio.mskcc.org/">the Computational Biology Center </A> at Memorial Sloan-Kettering Cancer
	Center.  This plugin is available at
	<A HREF="http://cbio.mskcc.org/~bader/software/mcode/index.html">http://cbio.mskcc.org/~bader/software/mcode/index.html</A>
	and published in
	<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=pubmed&amp;dopt=Abstract&amp;list_uids=12525261&amp;query_hl=4&amp;itool=pubmed_DocSum">BMC Bioinformatics.</A>.
	<LI>The
	Dynamic Expression Plugin, developed by Iliana Avila-Campillo
	at the Institute for Systems Biology.  This plugin is available at
	the  
	<A HREF="http://www.cytoscape.org/plugins2.php">
	Cytoscape plugins page.</A>
	<LI>The
	jActiveModules plugin, developed by
	<A HREF="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">
	the Ideker Lab</A> at the Department of Bioengineering at UCSD, and
	published in
	<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=12169552&amp;dopt=Abstract">Bioinformatics, 2002.</A>
	<LI>The
	BiNGO plugin, developed by the <A HREF="http://www.psb.ugent.be/cbd/">Computational Biology Division</A>, Dept. of Plant Systems Biology,
	Flanders Interuniversitary Institute for Biotechnology (VIB),
    published in <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=pubmed&amp;dopt=Abstract&amp;list_uids=15972284&amp;query_hl=1&amp;itool=pubmed_docsum">Bioinformatics</A>
	in 2005.
</UL>
and the following data files:
<UL>
  <LI><A HREF="modules.complexes/galFiltered.sif">galFiltered.sif</A>, a model of the galactose
  utilization pathway in yeast.
  <LI><A HREF="modules.complexes/gal.5936x20.mrna">gal.5936.mrna</A>, a companion expression
  dataset with the results of several genetic pertubations.
  <LI><A HREF="modules.complexes/galExpData.pvals">galExpData.pvals</A>, another companion 
  expression dataset.  This dataset contains P values to describe the significance of
  each observed change in expression.
</UL>
Please download these files to your local computer before starting.
<P>
This tutorial and accompanying lectures were delivered at <a href=
"http://www.csc.fi/suomi/info/index.phtml.en">CSC</a>, the Finnish
IT center for science. The lecture slides of background material
are available <a href="modules.complexes/modules.complexes.ppt">here</a> and an accompanying
video presentation is available <a href=
"http://rm.tv.funet.fi:8080/ramgen/fi/csc/kurssit/2005/cytoscape/_cytos07.rm">
here</a> courtesy of CSC. 

<P>
<hr>
<P>

Complexes
are a special type of module: they are a group of proteins that
interact to form one single piece of cellular machinery, such as the
ribosome or the spliceosome.  This section illustrates a couple
methods for determining complexes.  The first is the MCODE method,
which follows the principle that highly-connected regions of 
interaction network are often complexes.</P>
<OL>
	
		<LI>Start
		Cytoscape version 2.2.  Later in this section, we shall work with
		Cytoscape version 2.1, but for now we shall work with version 2.2.
		<LI>Load
		the network <B>galFiltered.sif</B> and apply your favorite layout
		algorithm.  With spring-embedded layout, you should start with a
		figure such as the one shown here:                       
		<P><IMG SRC="modules.complexes/Fig3.jpg" WIDTH="50%">
		<LI>Under
		the <B>Plugins</B> menu, select <B>MCODE</B>, and <B>Run MCODE on
		current network</B>.  Shortly, the <B>MCODE Results Summary</B>
		window should appear, as shown: 
		<P><IMG SRC="modules.complexes/Fig4.jpg" WIDTH="40%">
		<LI>The MCODE results lists thirteen putative complexes, giving the score
		and the number of nodes and edges in each.  For instance, in the
		first row, the entry 5,7 in the column Size indicates the putative
		complex has five nodes and seven edges..  Not all of these results
		will be significant.  A significant result is one with a high score
		(greater than one) and a decent number of nodes and edges.  In this
		case, the first three results may be significant, while the others
		are more dubious.
		<LI>Click on the results for the first complex.  Notice that on the Cytoscape
		canvas, the corresponding nodes are selected, as shown: 
		<P><IMG SRC="modules.complexes/Fig5.png" WIDTH="30%" BORDER=1>
		<LI>If these nodes are a portion of a complex, then there should be some
		process in which they all operate.  Thus, if we explore enrichment
		under GO, we should see some biological process with significant
		enrichment for these nodes.  Explore this as follows:
		<OL TYPE=i>
			<LI>Under
			the <B>Plugins</B> menu, select<B> BiNGO.</B>
			<LI>In
			the <B>BiNGO Settings </B>dialog box, fill in the following:
			<OL>
				<LI>A network name of your choice (in this example, I used the
				highly-creative name of "b").  Leave the box <B>Get cluster
				from network </B>checked.
				<LI>Select the Hypergeometric statistics test with FDR correction.
				<LI>Select a high cutoff p-value of 0.05.  Why?  A higher cutoff value will
				give us more data that we can review in detail below.
				<LI>Select the GO categories overrepresented after correction.
				<LI>Under <B>Select Reference Set</B>, select <B>Test cluster versus
				network</B>.  Why choose that and not <B>Test cluster versus
				complete annotation</B>?  Because this network is a portion of
				the yeast galactose utilization pathway, and thus any random
				collection of genes in the network are probably involved in
				galactose utilization.  If we want to know what specific role is
				played by a portion of the network, we need to look for
				enrichment relative to the rest of the network.
				<LI>Select an ontology of <B>GO Biological Process</B>, the species
				<B>Saccharomyces cerevisiae</B>, and gene identifiers of Locus
				Tags.  
				<LI>The <B>BiNGO Settings </B>dialog box should appear as follows: 
				<P><IMG SRC="modules.complexes/Fig6.jpg" WIDTH="60%">
				<LI> Click <B>Start BiNGO</B>.  You should see a graph that appears like the
				one below: 
				<P><IMG SRC="modules.complexes/Fig7.jpg" WIDTH="60%" BORDER=1>
				<LI>Notice the dark color of the nodes "peroxisome organization and
				biogenesis" and "protein-peroxisome targeting".  Recall
				that dark colors imply significant enrichment.  What is the
				p-value?  Check this with the Node Attribute browser.  Hint: if I give
				my BiNGO network the highly-original name <EM>b</EM, the
				attributes I need are <EM>adjustedPValue_b</EM> and <EM>description_b</EM>.
				 You should end up with your Node Attribute browser appearing as
				follows: 
				<P><IMG SRC="modules.complexes/Fig8.jpg" WIDTH="60%">
				<LI> Note that according to the P values, the enrichment is most
				significant for "peroxisome organization and biogenesis".  With further
				investigation, you would see that this MCODE complex prediction contains 
				all the genes in <I>S. cerevisiae</I> with this GO term.  Thus, this was
				probably a significant hit.
				<LI>For contrast, return to your MCODE results, select putative cluster
				#10, and run BiNGO on this cluster.  You should see a graph like
				the one shown below, and no P value of comparable significance
				(verify this). 
				<P><IMG SRC="modules.complexes/Fig9.jpg" WIDTH=476 HEIGHT=495><BR CLEAR=LEFT>
			</OL>
		
	</OL>
	Another indicator of a putative complex is that the genes in a complex are
	frequently co-expressed.  Here, we shall use the Dynamic Expression
	plugin (a.k.a. "the Movie") to identify connected regions that
	are also co-expressed.
	<OL>
		<LI>Return to your original network, <B>galFiltered.sif</B>, and load the
		expression matrix file <B>gal.5936x20.mrna</B> from your sample
		data directory.  This file contains experimental results on twenty
		different experiments.  In each experiment, some gene in the
		galactose utilization pathway was "perturbed", artificially
		changing its level of activity and expression.</P>
		<LI><P>Under the <B>Plugins</B> menu, select<B> Dynamic Expression</B>.  You
		should see a window as shown: 
		</P>
        <P><IMG SRC="modules.complexes/Fig10.jpg" WIDTH="40%">
		<LI>Click on the <B>Play</B> button.  You should see a "movie" in which
		the nodes of your network are colored according to the expression
		values in each experiment in turn, as shown: 
		<P><IMG SRC="modules.complexes/Fig11.jpg"  WIDTH="60%" BORDER=1>
		<LI><P>In this display, nodes with expression fold changes of 1 or greater
		are colored red, nodes with expression fold changes of -1 or less
		are colored blue,  unchanged nodes are colored white, and nodes
		with small fold changes are colored pink or light blue according to
		whether they are greater or less than zero.
		<LI>To see a static image colored by any experiment, go to the <B>Dynamic
		Expression</B> window, and move the <B>Conditions </B>slider to
		that experiment.  To change the playing speed, adjust the <B>Speed
		</B>slider.  During playing of the "movie", you may stop or
		pause at any time using the <B>Stop</B> and <B>Pause</B> buttons.
		<LI>Press <B>Play</B>, and watch for any connected regions of the network
		that change expression levels together.  If you watch your MCODE
		complex, you should see that while those nodes don"t show any
		dramatic changes of expression, they show similar patterns.  Can
		you spot any others?
		<LI>Select by name the node YDR395W, and select its first neighbors.  This
		should select the region of the network shown below: 
		<P><IMG SRC="modules.complexes/Fig12.png" WIDTH="30%" BORDER=1>
		<LI>Select
		these nodes to a new network, and clean up the layout.  This should
		yield a child network such as the one shown below: 
		<P><IMG SRC="modules.complexes/Fig13.gif" WIDTH="40%" BORDER=1>
		<LI>Return to the <B>Dynamic Expression</B> window and click on <B>Play</B> to
		watch how these nodes change together.  You should see that with
		the exception or YER056CA, they show patterns of expression that
		are quite similar.
		<LI>Here, we shall untangle the mystery of YER056CA.  
		<OL TYPE=i>
			<LI>Under the <B>Visualization </B>menu, select <B>Set Visual Style</B>.
			<LI>When the <B>Visual Styles</B> popup menu appears, click on <B>Define</B>
			to define the default visual style.
			<LI>You should see the <B>Set Visual Style</B> window, as shown: 
			<P><IMG SRC="modules.complexes/Fig14.jpg" WIDTH="50%">
			<LI>Note that the default node color is pink, so it will be hard to discern
			nodes with a slight positive fold change from nodes with no
			expression values defined.  Change the default color to grey,
			click on <B>Apply to Network</B>, and close the <B>Set Visual
			Style</B> popup and the <B>Visual Style</B> popup.</P>
			<LI>Replay the movie.  Notice that our mystery node is grey, indicating that
			it has no expression value defined, but that all the nodes with
			defined expression values change in concert with each other.</P>
		</OL>
		<LI>What are these nodes?  Return to the Cytoscape canvas for the parent
		network, where YDR395W and its immediate neighbors should still be
		selected.  Run <B>BiNGO</B>, making sure that you test for
		enrichment relative to the current network.  You should see a
		network such as the one shown: 
		<P><IMG SRC="modules.complexes/Fig15.gif" WIDTH="40%" BORDER=1></P>
		<LI>By exploring this network, you will see small P values for the nodes
		"Protein biosynthesis" and "Ribosomal large subunit assembly
		and maintenance".  If you were to explore these nodes further
		under Entrez Gene at NCBI, you would see that they are all
		components of the large subunit of the ribosome.  Since these genes
		produce proteins that work together in the ribosome, it makes sense
		that they would be co-expressed.
		<LI>Can you enlarge the set by identifying any other nodes connected to
		YDR395W and its neighbors, and showing similar expression profiles?
		 If you enlarge this set, do your <B>BiNGO</B> results become more
		significant?  Hint: the answer is yes.
	</OL>
This section will illustrate application of the Cytoscape jActiveModules plugin to 
find subnetworks of nodes for where all or most nodes show substantial responses to 
the same experimental conditions.  
	<OL>
		<LI>Return to the network <B>galFiltered.sif</B>.
		<LI>Load the expression data matrix <B>galExpData.pvals</B>.
		This file contains expression results for three
		sets of expression analysis, involving perturbation of three
		transcription factors involved in the yeast Galactose utilization
		pathway.  This file also contains a necessary ingredient for
		jActiveModules: p-values indicating the significance of each
		expression value.
		<LI>Under the Plugins menu, select <B>jActiveModules</B>, and then <B>Active</B>
		<B>Modules: Find Modules. </B> This will run jActiveModules with
		the default parameters.
		<LI>Shortly,you should see the <B>Conditions vs. Pathways </B>window similar to
		the one shown below.  You might not get exactly the same results,
		because jActiveModules involves  random sampling, as we shall
		discuss below. 
		<P><IMG SRC="modules.complexes/Fig16.jpg" WIDTH=463 HEIGHT=480>
		<P>What do these results mean?
		<OL TYPE=i>
			<LI>The plugin found five putative modules.
			<LI>Module #1 contains 14 nodes, has a respectable score of roughly 3.7, and
			appears to be significant in all three experimental conditions
			<B>gal1RG</B>, <B>gal4RG</B>, and <B>gal80R</B>.  This looks like
			a significant hit.
			<LI>Module #5 contains four nodes, with a moderate score of about 2.6,
			significant in all three experimental conditions.  This might not
			be an interesting hit, but we shall explore it further later.</P>
			<LI>So why did we get five  hits? Under the <B>Plugins</B> menu, select
			<B>jActiveModules</B>, and <B>Set Parameters</B>.  The <B>Find
			Active Modules Parameters</B> window appears as shown below: 
			<P><IMG SRC="modules.complexes/Fig17.jpg"  WIDTH="50%">
			<LI>Under <B>General Parameters</B>, notice the input field labeled <B>Number
			of Paths</B>.  This is where one controls the number of modules returned by 
			jActiveModules.  The default value is 5.  So, jActiveModules will return five 
			putative hits, even if it finds only one good one.
		</OL>
		<LI>Let's focus on Module 1.
		<OL TYPE=i>
			<LI>Under the Module 1 column, go down to the experimental conditions
			column, and click on one of the red bars indicating which
			experimental conditions yielded significant results.  On the
			Cytoscape canvas, the nodes belonging to this module should be
			selected and highlighted in yellow, as shown:
		    <P><IMG SRC="modules.complexes/Fig18.jpg"  WIDTH="50%">
			<LI>Click on the bars corresponding to another modules, and the nodes of
			that module should be highlighted instead.  Try this.
			<LI>What does these subnetworks represent?  This is a set of connected
			nodes that altogether showed significant expression changes in the
			same experiment.  So, this is a subnetwork with an overall
			significant response to the experimental conditions.  As described
			in Ideker et al., <I>Bioinformatics</I> 2005 18:S233-S240, such
			subnetworks tend to correspond to known pathways in the
			literature.  
	</OL>
</OL>
<P><B>Congratulations!</B>
 By now, you are almost ready to go out into the Systems Biology
world and do great things!  First, have a cup of coffee to celebrate.</P>

<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</p>

</body>
</html>
