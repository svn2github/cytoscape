<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title> Cytoscape Screen Shots </title> 
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
	<link rel="shortcut icon" href="images/cyto.ico" />
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tr>
		<td width="10">
			&nbsp; 
		</td>
		<td valign="center">
			<h1> Cytoscape Screen Shots </h1> 
		</td>
	</tr>
</table>
<? include "nav.php"; ?>
<div id="indent">
	<p>
		The screenshot below shows the main window of <i>Cytoscape 2.0,</i> displaying a network of protein-protein and protein-DNA interactions among 332 yeast genes.
	</p>
	<p>
		<img src="screenshots/cytoscapeWindow2.gif" width="726" height="737" alt="main cytoscape window"> 
	</p>
	<hr width="100%" size="2">
	<p>
		You can flip through different visual styles by making a selection from the Visual Style pull down menu. "Sample2" will give gene expression values for each node will be colored along a color gradient between red and green. 
		<br>
		<br>
		<img src="screenshots/sample2.gif" width="608" height="681" alt="Sample2">
	</p>
	<hr width="100%" size="2">
	<p>
		With the Cytoscape Visual Style feature, you can easily customize the visual appearance of your graph. Cytoscape can also map values such as probabilities, confidence levels, and expression values to the visualization of networks. 
		<br>
		<br>
		<img src="screenshots/VisualStyles.gif" width="888" height="777" alt="Visual Styles"> 
 
	</p>
	<hr width="100%" size="2">
	<p>
		The attributes window displays a summary of Gene Ontology (GO) information and expression ratios for each of ten seleted genes.Hyperlinks to the GO database are also provided.
		<br>
		<br>
		<img src="screenshots/nodeInfoDialog.jpg" width="818" height="249" alt="gene info"> 
	</p>
	<hr width="100%" size="2">
	<p>
	</p>
	<p>
		Cytoscape implements an algorithm for finding "active pathways", <i> i.e.</i>, subnetworks of genes that jointly show significant differential expression over a set of experimental conditions observed by microarray experiment. The image below show the results of a run of this algorithm. Several active paths have been found; the highest scoring one has 22 genes identified as being active in three of the 20 experimental conditions. From here, one can view a graph with only these genes, display the expression data for any of the conditions, and examine Gene Ontology (GO) information available to assess the biological significance of this network.
	</p>
	<p>
		<img src="screenshots/activePathsDialog.jpg" width="513" height="529" alt="summary of active paths"> 
	</p>
</div>
</div>
<? include "footer.php"; ?>
</body>
</html>
