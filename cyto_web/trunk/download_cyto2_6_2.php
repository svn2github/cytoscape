<div id="indent">
	<blockquote>
		<p>
		You have successfully registered to download Cytoscape 2.6.2.
		</p>
		<p>
		You may now download Cytoscape for Unix, Windows and Mac OS X systems through any of the following
		options:
		</p>
		<p>
		<h4>Option 1: Platform specific installation bundles.  </h4>
		<ul>
			<li><a href='<?= $cyto2_6_2_windows ?>' >Windows</A></li>
			<li><a href='<?= $cyto2_6_2_mac ?>' >Mac OS X</A></li>
			<li><a href='<?= $cyto2_6_2_linux ?>' >Linux/Unix</A></li>
		</ul>
		</p>
		<h4>Option 2:  All Platforms:  Download a .zip or .tar.gz distribution file.
		</H4>
		</p>
		<p>
		</p>
		<ul>
			<li>
				<a href='<?= $cyto2_6_2_zip_east ?>'>
					Windows / Mac OS X 
				</a>
			</li>
			<li>
				<a href='<?= $cyto2_6_2_gz_east ?>' >
					Linux / Unix 
				</a>
			</li>
		</ul>
		</p>

		<p>
		</p>

		<p>
		<p>
		<h4>Option 3:  All Platforms:  Download the Complete Source Code for Cytoscape
		</h4>
    		<UL>
		    <li><a href='<?= $cyto2_6_2_source_east ?>'>Cytoscape source</a></li>
		    <li>Note: Saving to disk via the right-click menu is recommended.</li>
		</UL>
  </p>
  <P>
<? if ($in_production == false)  {
  echo "<P>&nbsp;<P>Debug:  [will not appear in production environment]<P>";
  echo "Windows Link: $cyto2_6_2_windows <BR>";
  echo "Mac Link: $cyto2_6_2_mac    <BR>";
  echo "Linux Link: $cyto2_6_2_linux <BR>";
  echo "Gzipped Link: $cyto2_6_2_gz_east	 <BR";
  echo "Zipped Link: $cyto2_6_2_zip_east    <BR>";
  echo "Source Link: $cyto2_6_2_source_east <BR>";
  }
?>
