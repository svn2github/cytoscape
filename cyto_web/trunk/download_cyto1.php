<div id="indent">
	<blockquote>
		<p>
		You have successfully registered to download Cytoscape 1.1. 
		</p>
		<p>
		You may now download Cytoscape for Unix, Windows and Mac OS X systems through any of the following 
		three options:
		</p>
		<h4>
		Option 1 (One Click Install) : Run the InstallAnywhere Cytoscape Installer. 
		</h4>
		<p>
		</p>
			<a href='<?= $cyto1_install_anywhere ?>'>
			Install Cytoscape 2.1 (One Click Install)
			</a>
		<p>
		</p>
		<p>
		<h4>Option 2:  Download a .tar.gz or .zip distribution file: 
		</H4>
		</p>
		<p>
		</p>
		<ul>
			<li>
				<a href='<?= $cyto1_gz_east ?>' >
					Unix 
				</a>
				- 
				<a href='<?= $cyto1_zip_east ?>'>
					Windows 
				</a>
				- USA, East Coast (New York, NY). 
			</li>		
		</ul>
		</p>
		<p>
		<p>
		<h4>Option 3:  Download the Complete Source Code for Cytoscape:
		</h4>
    		<UL>
		    <li><a href='<?= $cyto1_source_east ?>'>Cytoscape source</a> - USA, East Coast (New York, NY).</li>  
		</UL>
	<p>&nbsp;&nbsp;<b>Note:</b>
In order to compile Cytoscape 1.1 from the source files, you will need a copy of yFiles 2.01, available from <a
 href="http://www.yworks.com/en/products_yfiles_about.htm">yWorks</a>.
  </p>
<? if ($in_production == false)  {
  echo "<P>&nbsp;<P>Debug:  [will not appear in production environment]<P>";
  echo "Install Anywhere: $cyto1_install_anywhere <BR>";
  echo "Gzipped Link: $cyto1_gz_east	 <BR";
  echo "Zipped Link: $cyto1_zip_east    <BR>";
  echo "Source Link: $cyto1_source_east <BR>";
  }
?>    