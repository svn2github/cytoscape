<? include "config.php"; ?>
<div id="links2">
	<div class="naventry1">
		<h1><a href="features2.php">About Cytoscape</a></h1>
		<p>
		Cytoscape is an open source bioinformatics software platform for 
		<b><i>visualizing</i></b> molecular interaction networks and 
		<b><i>integrating</i></b> these interactions with gene expression 
		profiles and other state data. 
		<a href="features2.php"><br><strong>Read more &raquo; </strong></a>
		</p>
	</div>
	<div class="naventry2">
		<h1><a href="<?= $latest_download_link?>">Download Cytoscape</a></h1>
		<p>
			<a href="<?= $latest_download_link?>">Download Version <?= $latest_version?></a>
		 (Requires <a href="http://java.sun.com/j2se/1.5.0/index.jsp">Java SE 5</a> or 
			<a href="http://java.sun.com/javase/downloads/index.jsp">Java SE 6</a>)
		</p>
		<p>
		<a href='<?= $latest_release_notes_link ?>'><?= $latest_version ?> Release Notes &raquo; </a>
		</p>
	</div>
	<div class="naventry2">
		<h1><a href="http://cytoscape.wodaklab.org/wiki/Presentations">
		Online Tutorials</a></h1>
		<p>
			Get Started with the expanded Cytoscape 
			<a href="http://cytoscape.wodaklab.org/wiki/Presentations">online tutorials</a>.  
			Eight tutorials describe Cytoscape from basic operation to detailed plugin operation.
		</p>
	</div>
	<div class="naventry2">
		<h1><a href="<?= $latest_manual_html?>">Manual</a></h1>
		<p>
			<a href="<?= $latest_manual_html?>">HTML format</a> or
			<a href="<?= $latest_manual_pdf?>">PDF format</a>, 
			explains all basic features of Cytoscape.
			<a href="http://www.adobe.com/products/acrobat/readstep2.html">Get Acrobat reader</a>
		</p>
	</div>
	<div class="naventry3">
		<h1><a href="http://cytoscape.wodaklab.org/wiki">Developers</a></h1>
		<item><a href="http://cytoscape.wodaklab.org/wiki/Future_Cytoscape_Features">Roadmap</a></item>
    	<item><a href="<?= $latest_javadoc?>">Javadoc API</a></item>
    	<item><a href="http://cytoscape.wodaklab.org/wiki">Wiki</a></item>
		<item><a href="http://csbi.sourceforge.net/">Graph INterface librarY (GINY)</a></item>
		<item><a href="download_list.php#cvs">Download Source from SVN</a></item>
	
	</div>
</div>
