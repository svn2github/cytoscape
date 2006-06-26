<div id="indent">
	<blockquote>
		<p>
		You have successfully registered to download Cytoscape 2.3.
		</p>
		<p>
		You may now download Cytoscape for Unix, Windows and Mac OS X systems through any of the following
		options:
		</p>
		<p>
		<h4>Option 1:  One Click Install </h4>
		<ul>
			<li><a href='<?= $cyto2_3_install_anywhere ?>' >Windows / Linux / Unix</A></li>
		</ul>
		</p>
		<h4>
		Option 2:  Mac OS X Release
		</h4>
		<p>
		<ul>
			<li><a href='<?= $cyto2_3_mac_east ?>' >Mac OS X Release</A></li>
		</ul>
		</p>
		<h4>Option 3:  All Platforms:  Download a .tar.gz or .zip distribution file
		</H4>
		</p>
		<p>
		</p>
		<ul>
			<li>
				<a href='<?= $cyto2_3_zip_east ?>'>
					Windows / Mac OS X 
				</a>
				-
				<a href='<?= $cyto2_3_gz_east ?>' >
					Linux / Unix 
				</a>
			</li>
		</ul>
		</p>

		<p>
		</p>

		<p>
		<p>
		<h4>Option 4:  All Platforms:  Download the Complete Source Code for Cytoscape
		</h4>
    		<UL>
		    <li><a href='<?= $cyto2_3_source_east ?>'>Cytoscape source</a></li>
		    <li>Note: Saving to disk via the right-click menu is recommended.</li>
		</UL>
  </p>
  <P>
<? if ($in_production == false)  {
  echo "<P>&nbsp;<P>Debug:  [will not appear in production environment]<P>";
  echo "Install Anywhere Link: $cyto2_3_install_anywhere <BR>";
  echo "Mac Link: $cyto2_3_mac_east    <BR>";
  echo "Gzipped Link: $cyto2_3_gz_east	 <BR";
  echo "Zipped Link: $cyto2_3_zip_east    <BR>";
  echo "Source Link: $cyto2_3_source_east <BR>";
  }
?>
