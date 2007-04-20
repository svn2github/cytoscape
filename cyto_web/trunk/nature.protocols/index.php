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
<? include "../nav.php"; ?>
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

            <p>To get the required data and configuation files for the Agilent Literature Search software
            click <A HREF="../../download_agilent_literature_search_v2.4_data.php">here</A>

            <p>The following supplemental data files are provided for the convenience of any readers who have
no
            data available, or do not wish to execute the complete protocol:
            <ol>
                        <li> <A HREF="gene.input.list.txt">gene.input.list.txt</A> provides sample input for t
he cPath and
            Agilent Literature Search network queries.
                        <li> <A HREF="sample.session.cys">sample.session.cys</A> is a session file containing
the network
            data obtained with the sample query
                        <li> <A HREF="translate.tab">translate.tab</A> is an ID translation file built with th
e sample data
            according to the protocol.
                <li> <A HREF="galExpData.pvals">galExpData.pvals</A> is a sample expression data file
            <p>
            Note that the number of interactions in cPath increases over time.  Users who attempt to re-genera
te this
            network at a later date might not get exactly the same network, but a larger one.
            </ol>
            All the figures shown in the paper were generated with these data files.
            <p>
    </div>
</div>

<? include "../footer.php"; ?>
	</body>
</html>
