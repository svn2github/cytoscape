<div id="indent">
	<blockquote>
		<p>
		You have successfully registered to download Cytoscape 2.7.0.
		</p>
		<p>
		You may now download Cytoscape for Unix, Windows and Mac OS X systems through any of the following
		options:
		</p>
		<p>
		<h4>Option 1: Platform specific installation bundles.  </h4>
		<ul>
			<li><a href='<?= $cyto2_7_0_windows_32 ?>' >Windows 32 bit</A></li>
			<li><a href='<?= $cyto2_7_0_windows_64 ?>' >Windows 64 bit</A></li>
			<li><a href='<?= $cyto2_7_0_mac ?>' >Mac OS X</A></li>
			<li><a href='<?= $cyto2_7_0_linux ?>' >Linux/Unix</A></li>
		</ul>
		</p>
		<h4>Option 2:  All Platforms:  Download a .zip or .tar.gz distribution file.
		</H4>
		</p>
		<p>
		</p>
		<ul>
			<li>
				<a href='<?= $cyto2_7_0_zip_east ?>'>
					Windows / Mac OS X 
				</a>
			</li>
			<li>
				<a href='<?= $cyto2_7_0_gz_east ?>' >
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
		    <li><a href='<?= $cyto2_7_0_source_east ?>'>Cytoscape source</a></li>
		    <li>Note: Saving to disk via the right-click menu is recommended.</li>
		</UL>
  </p>
  <P>
<? if ($in_production == false)  {
  echo "<P>&nbsp;<P>Debug:  [will not appear in production environment]<P>";
  echo "Windows 32 Link: $cyto2_7_0_windows_32 <BR>";
  echo "Windows 64 Link: $cyto2_7_0_windows_64 <BR>";
  echo "Mac Link: $cyto2_7_0_mac    <BR>";
  echo "Linux Link: $cyto2_7_0_linux <BR>";
  echo "Gzipped Link: $cyto2_7_0_gz_east	 <BR";
  echo "Zipped Link: $cyto2_7_0_zip_east    <BR>";
  echo "Source Link: $cyto2_7_0_source_east <BR>";
  }
?>
