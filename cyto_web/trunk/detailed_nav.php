<? include "config.php"; ?>
<table cellspacing="0" cellpadding="2" border="0" summary="" id="links2">

		<tr>
			<td width="120" id="first">
				<h1>
					<a href="features.php">
						About Cytoscape
					</a>
				</h1>
				Cytoscape is an open source bioinformatics software platform for <b><i>visualizing</i></b> molecular interaction networks and <b><i>integrating</i></b> these interactions with gene expression profiles and other state data. <a href="features.php"><br><strong>Read more &raquo; </strong></a>
			</td>
			<td width="60">
				<h1>
					<a href="<?= $latest_download_link?>">
						Download Cytoscape
					</a>
				</h1>
				<p>
					<a href="<?= $latest_download_link?>">
						Download Version <?= $latest_version?>
					</a>
				</p>
				<p>
					Requires
					<a href="http://java.sun.com/j2se/1.5.0/index.jsp">
						Java 1.5.0
					</a>
				</p>
				<a href='<?= $latest_release_notes_link ?>'>
            <?= $latest_version ?>
            Release Notes &raquo; </a>
			</td>
			<td width="120">
				<h1>
					<a href="tut/tutorial.php">
						Online Tutorials
					</a>
				</h1>
				<p>
					Get Started with the expanded Cytoscape <a href="tut/tutorial.php">online tutorials</a>.  Eight tutorials describe Cytoscape from basic operation to detailed plugin operation.
				</p>
			</td>
			<td width="120">
				<h1>
					<a href="<?= $latest_manual_html?>">
						Manual
					</a>
				</h1>
				<p>
					<a href="<?= $latest_manual_html?>">
						HTML format
					</a> or
					<a href="<?= $latest_manual_pdf?>">
						PDF format</a>, explains all basic features of Cytoscape.
					<a href="http://www.adobe.com/products/acrobat/readstep2.html">
						Get Acrobat reader
					</a>
				</p>
			</td>
			<td width="120">
				<h1>
				    <a href="cgi-bin/moin.cgi/">
						Developers
					</a>
				</h1>
				<p>
					<a href="http://www.cytoscape.org/cgi-bin/moin.cgi/Future_Cytoscape_Features">
						Roadmap
					</a> |
    				<a href="<?= $latest_javadoc?>">
						Javadoc API
					</a> |
    			    <a href="cgi-bin/moin.cgi/">
				        Wiki
				    </a>
				</p>
                <p>
					<a href="http://csbi.sourceforge.net/">
						Graph INterface librarY (GINY)
					</a>
				</p>
				<p>
					<a href="download_list.php#cvs">
					Download Source from SVN</A>
				</p>
			</td>
		</tr>
	</tbody>
</table>
