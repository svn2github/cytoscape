<div id="indent">
	<blockquote>
		<p>
		You have successfully registered to download Cytoscape 2.1. 
		</p>
		<p>
		You may now download Cytoscape for Unix, Windows and Mac OS X systems through any of the following 
		four options:
		</p>
<!--		
		<h4>
		Option 1:  Windows / Linux:  One Step Installation
		</h4>
		<p>
		</p>
		<UL>
			<LI><a href='<?= $cyto2_1_install_anywhere ?>'>
			Install Cytoscape 2.1 (One Click Install)</a> 
		</UL>
		</h2>
-->
		<p>
		<h4>Option 1:  All Platforms:  Download a .tar.gz or .zip distribution file
		</H4>
		</p>
		<p>
		</p>
		<ul>
			<li>
				<a href='<?= $cyto2_1_gz_east ?>' >
					Unix / Linux / Mac OS X
				</a>
				- 
				<a href='<?= $cyto2_1_zip_east ?>'>
					Windows 
				</a>
				- USA, East Coast (New York, NY). 
			</li>		
		</ul>
		</p>

		<p>
		</p>
		<h4>
		Option 2:  Mac OS X Release
		</h4>
		<p>
		<ul>
			<li><a href='<?= $cyto2_1_mac_east ?>' >Mac OS X Release</A></li>
		</ul>
		</p>

		<p>
		<p>
		<h4>Option 3:  All Platforms:  Download the Complete Source Code for Cytoscape
		</h4>
    		<UL>
		    <li><a href='<?= $cyto2_1_source_east ?>'>Cytoscape source</a> - USA, East Coast (New York, NY).</li>  
		</UL>
  </p>
  <P>
<? if ($in_production == false)  {
  echo "<P>&nbsp;<P>Debug:  [will not appear in production environment]<P>";
  echo "Install Anywhere Link: $cyto2_1_install_anywhere <BR>";
  echo "Mac Link: $cyto2_1_mac_east    <BR>";
  echo "Gzipped Link: $cyto2_1_gz_east	 <BR";
  echo "Zipped Link: $cyto2_1_zip_east    <BR>";
  echo "Source Link: $cyto2_1_source_east <BR>";
  }
?>