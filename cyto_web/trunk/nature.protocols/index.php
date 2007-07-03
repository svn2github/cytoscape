<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
		<title>
			Cytoscape Nature Protocol
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="../css/cytoscape.css" />
		<link rel="shortcut icon" href="../images/cyto.ico" />
	</head>
	<body bgcolor="#FFFFFF">
		<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
			<tr>
				<td width="10">
					&nbsp;
				</td>
				<td valign="center">
					<h1>
						Nature Protocols
					</h1>
				</td>
			</tr>
		</table>
<? include "nav.np.php"; ?>
<div id="contents">
    <div id="content">
            <P>This page contains a link to the webstart that contains all of the plugins
            necessary to properly run the protocol described in this paper: (ref here).</p>

            <p>
            Click <A HREF="runCytoscape.jnlp">here</A> for the WebStart.
            </p>
            <p>
            For more information on Java Webstart, click <A HREF="http://java.sun.com/products/javawebstart/">
here</A>.
            </p>


            <p>The following supplemental data files are provided for the convenience of any readers who have
no
            data available, or do not wish to execute the complete protocol:
            <ol>
                        <li> <A HREF="galFiltered.sif">galFiltered.sif</A> contains sample network data, which describes galactose utilization in yeast.  We have also provided a <A HREF="galFiltered.sif.pdf"> PDF </a> version of this file. 
                        <li> <A HREF="galGeneNames.csv">galGeneNames.csv</A> is a comma-delimited attribute file to augment the sample data by mapping the locus tag node identifiers to standard gene symbols.  This data is also available in a <A HREF="galGeneNames.csv.pdf"> PDF </A> version.
                        <li> <A HREF="SampleData.cys">SampleData.cys</A> is a session file containing this data, and an Agilent Literature Search network for many of the same genes.
                	<li> <A HREF="galExpData.pvals">galExpData.pvals</A> is a sample expression data file to complement the network data.  The illustrations in the manuscript reflect the <B>gal4RG</B> experiment.  This data is also available a a <A HREF="galExpData.pvals.pdf"> PDF </A> version.
            <p>
            </ol>
            All the figures shown in the paper were generated with these data files.
            <p>
    </div>
</div>

<? include "../footer.php"; ?>
	</body>
</html>
