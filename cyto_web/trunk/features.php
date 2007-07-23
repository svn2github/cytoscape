<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<? include "config.php"; ?>
<! Updated by kono for 2.3 release>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape Features</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
	<link rel="shortcut icon" href="images/cyto.ico" />
</head>
<body bgcolor="#FFFFFF">
<div id="topbar">
	<div class="title">Cytoscape Features</div>
</div>

<? include "nav.php"; ?>
<div id="indent">
	<P>
	<i>Cytoscape</i> is a bioinformatics software platform for <b><i>visualizing</i></b> molecular interaction networks and <b><i>integrating </i></b>these interactions with gene expression profiles and other state data. &nbsp;Additional features are available as plugins.&nbsp; Plugins are available for network and molecular profiling analyses, new layouts, additional file format support and connection with databases. Plugins may be developed using the Cytoscape open Java software architecture by anyone and plugin <a href="community.php"> community development</a> is encouraged.
	<p>
		Cytoscape supports the following features:
		<br>
	</p>
	<h3>Input</h3>
	<ul type="square">
		<li>
			Input and construct molecular interaction networks from raw interaction files (<i>SIF</i> format) containing lists of protein-protein and/or protein-DNA interaction pairs. &nbsp;For yeast and other model organisms, large sources of pairwise interactions are available through the <a href="http://www.bind.ca"> BIND</a> and <a href="http://www.gene-regulation.com/">TRANSFAC</a> databases. User-defined interaction types are also supported.
		</li>
		<li>
			Load and save previously-constructed interaction networks in <i><a href="http://infosun.fmi.uni-passau.de/Graphlet/GML/" taget="_blank">GML</a></i> format (Graph Markup Language).
		</li>
		<li>
			<IMG src="wiki/modern/img/moin-new.png">Load and save networks and node/edge attributes in an XML 
			document format called <i><a href="http://www.cs.rpi.edu/~puninj/XGMML/" target="_blank">XGMML</a></i> (eXtensible 
			Graph Markup and Modeling Language).
		</li>
		<li>
			Input mRNA expression profiles from tab- or space-delimited text files.
		</li>
		<li>
			Load and save arbitrary attributes on nodes and edges. For example, input a set of custom annotation terms for your proteins, create a set of confidence values for your protein-protein interactions.
		</li>
		<li>
			Import gene functional annotations from the <a href="http://www.geneontology.org">Gene Ontology (GO)</a> and <a href="http://www.genome.ad.jp/kegg/">KEGG</a> databases.
		</li>
		<li>
			<IMG src="wiki/modern/img/moin-new.png">Directly import GO Terms and annotations from <a href="http://www.geneontology.org/GO.downloads.shtml#ont" target="_blank">OBO</a> and <a href="http://www.geneontology.org/GO.current.annotations.shtml">Gene Association</a> files.
		</li>
		<li>
			<IMG src="wiki/modern/img/moin-new.png">Load and save state of the Cytoscape session in a <i>Cytoscape Session (.cys) file</i>.  Cytoscape Session file includes networks, attributes (for node/edge/network), Desktop states (selected/hidden nodes and edges, window sizes), Properties, and Visual Styles.
		</li>
	</ul>
	<h3>Visualization</h3>
	<ul type="square">
		<li>
				Customize network data display using powerful visual styles.
		</li>
		<li>
				View a superposition of gene expression ratios and p-values on the network. &nbsp;Expression data can be mapped to node color, label, border thickness, or border color, etc. according to user-configurable colors and visualization schemes.
		</li>
		<li>
			Layout networks in two dimensions. &nbsp;A variety of layout algorithms are available, including cyclic and spring-embedded layouts.
		</li>
		<li>
			Zoom in/out and pan for browsing the network.
		</li>
		<li>
				<IMG src="wiki/modern/img/moin-new.png">Use the network manager to easily organize multiple networks.  And this structure can be saved in a session file.
		</li>
		<li>
				Use the bird's eye view to easily navigate large networks.
		</li>
		<li>
				<IMG src="wiki/modern/img/moin-new.png">Easily navigate large networks (100,000+ nodes and edges) by efficient rendering engine.
		</li>
	</ul>
	<h3>Analysis</h3>
	<ul type="square">
		<li>
			<strong><a href="plugins2.php">Plugins</a></strong> available for network and molecular profile analysis. For example:
		</li>
		<ul>
			<li>
				<strong>Filter the network</strong> to select subsets of nodes and/or interactions based on the current data. &nbsp;For instance, users may select nodes involved in a threshold number of interactions, nodes that share a particular GO annotation, or nodes whose gene expression levels change significantly in one or more conditions according to p-values loaded with the gene expression data.
			</li>
			<li>
				<strong>Find active subnetworks/pathway modules</strong>. The network is screened against gene expression data to identify connected sets of interactions, i.e. <i>interaction subnetworks</i>, whose genes show particularly high levels of differential expression. &nbsp;The interactions contained in each subnetwork provide hypotheses for the regulatory and signaling interactions in control of the observed expression changes.
			</li>
			<li>
				<strong>Find clusters (highly interconnected regions)</strong> in any network loaded into Cytoscape. Depending on the type of network, clusters may mean different things. For instance, clusters in a protein-protein interaction network have been shown to be protein complexes and parts of pathways. Clusters in a protein similarity network represent protein families.
			</li>
		</ul>
		<li>
			More plugins available on the <a href="plugins2.php">plugins page</a>.
		</li>
	</ul>
	<p>
		Cytoscape was initially made public in July, 2002 (v0.8); the second release (v0.9) was in November, 2002. and v1.0 was released in March 2002.&nbsp; Version 1.1.1 is the last stable release for the 1.0 series.&nbsp; Latest version of Cytoscape is <?= $latest_version ?>: <a href='<?= $latest_download_link ?>'>download</a>(<a href='<?= $latest_release_notes_link ?>'>release notes</a>).&nbsp;
	</p>
	</div>
<? include "footer.php"; ?>
</body>
</html>
