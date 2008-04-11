<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
		<title>
			Cytoscape Community
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
		<link rel="shortcut icon" href="images/cyto.ico" />
	</head>
	<body>
	<div id="container">
	
	<div id="topbar">
		<div class="title">Cytoscape Community</div>
	</div>
	
	<? include "nav.php"; ?>

	<div id="paragraph">
		<p>
		<br>Cytoscape currently supports three mailing lists, all of which are hosted at 
		<A HREF="http://groups-beta.google.com/">Google Groups</A>.
		</p>
	</div>	
	
	<form action="http://groups-beta.google.com/group/cytoscape-announce/boxsubscribe">
	<div class="item">
		<H2>cytoscape-announce</H2>
		<div id="paragraph">
		Description:  A low-volume mailing list used to announce new developments in Cytoscape.
		<br/>
		Subscribe to cytoscape-announce:
		<p><br/>
		Email: <input type=text name=email>
       		<input type=submit name="sub" value="Subscribe">
       		&nbsp;&nbsp;   <a href="http://groups-beta.google.com/group/cytoscape-announce">Browse Archives</a> 
		</div>
	</div>
	</form>

	<form action="http://groups-beta.google.com/group/cytoscape-helpdesk/boxsubscribe">
	<div class="item">
		<H2>cytoscape-helpdesk</H2>
		<div id="paragraph">
		Description:  Open forum for getting Cytoscape help. Geared towards new users.
		<br/>
		Subscribe to cytoscape-helpdesk:
		<P><br/>
		Email: <input type=text name=email>
       		<input type=submit name="sub" value="Subscribe">
       		&nbsp;&nbsp;   <a href="http://groups-beta.google.com/group/cytoscape-helpdesk">Browse Archives</a> 
		</div>
		</div>
	</form>

	
	<form action="http://groups-beta.google.com/group/cytoscape-discuss/boxsubscribe">
		<div class="item">
		<H2>cytoscape-discuss</H2>
		<div id="paragraph">
		Description:  Open forum for discussing Cytoscape, asking questions, suggesting new features, and developing plugins.
		<br/>
		Subscribe to cytoscape-discuss:
		<P><br/>
		Email: <input type=text name=email>
       		<input type=submit name="sub" value="Subscribe">
       		&nbsp;&nbsp;   <a href="http://groups-beta.google.com/group/cytoscape-discuss">Browse Archives</a> 
		</div>
		</div>
	</form>
	
	
	<? include "footer.php"; ?>
	
	</div>
	</body>
</html>
