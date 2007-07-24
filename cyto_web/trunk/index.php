<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<? include "config.php"; ?>
<! Updated by kono for 2.5 7_23_2007 >
<! Updated by apico with "latest release" variables for future updates (see config.php). 12_21_2006 >
<html>
<head>
<title>Cytoscape: Analyzing and Visualizing Network Data</title>
<link href="css/cytoscape.css" media="screen" type="text/css" rel="stylesheet">
<link href="images/cyto.ico" rel="shortcut icon">
<meta content="Cytoscape, visualization, interaction network,
		software, genetic, gene, expression, protein interaction, graph,
		bioinformatics, computational biology,
		Whitehead Institute, Institute for Systems Biology, microarray analysis,
		clustering, pathways, integration, algorithm, simulated annealing,
		gene regulation" name="keywords">
</head>
<body>
<!>
<! ========== Title and menu bar ============ >
<!>
<div id="feature">
	<div class="title">
		Cytoscape
	</div>
   <img src="images/logo.png"/>

   <div class="article">
		<h2><a href='<?= $latest_download_link ?>'>Download Cytoscape
            <?= $latest_version ?>
            ! </a>
		</h2>
		<a href='<?= $latest_release_notes_link ?>'> <?= $latest_version ?> Release Notes &raquo; </a>


       <h2><a href="retreat2007/index.php">Announcing Cytoscape Retreat 2007</a></h2>
          <a href="retreat2007/index.php">Now first time in Europe!!! <b>Amsterdam - Netherlands &raquo;</b></a><br>
          <b>November 6<sup>th</sup>-9<sup>th</sup> 2007</b>
          <br>
          </p>
	</div>
</div>
<? include "nav.php"; ?>
<? include "detailed_nav.php"; ?>
<!>
<! ========== Main Contents ============ >
<!>
<div id="content">
  <div class="item">
    <h2><i>NEW!</i> &nbsp;&nbsp;Cytoscape 2.5.0</h2>
        <A HREF="screenshots/2_5_ss1.png"><img src="screenshots/2_5_ss1_thumb.png" border="0" align="left" alt="Cytoscape 2.5 Screenshot" /> </A>
    <div class="paragraph"> (Updated 7/23/2007) <br>
      New features include: <br>
      <ul>
        <li> New VizMapper User Interface
          <ul>
            <li> More intuitive.</li>
            <li> Continuous mapping editors.</li>
            <li> Visual editor for default view.</li>
            <li> Visual mapping browser. </li>
            <li> Improved visual legend generator. </li>
            <li> Utilities to generate discrete values. </li>
          </ul>
        </li>
        <li>New Features for Visual Style
          <ul>
            <li> Transparency (opactiy) support.</li>
            <li> Continuous edge width.</li>
            <li> Color visual property is separated from Arrow and Edge.</li>
          </ul>
        </li>
        <li>New Filter User Interface
          <ul>
            <li> Intuitive widgets for basic filters.</li>
            <li> Suggested search values with indexing.</li>
            <li> Options to save in session or globally.</li>
          </ul>
        </li>
        <li>Plugin Manager and New Plugin Website
          <ul>
            <li> Install/Update/Delete plugins from within Cytoscape.</li>
            <li> Search for version compatible plugins from any host site.</li>
            <li> Display list of installed plugins.</li>
          </ul>
        </li>
        <li>Layout customization.</li>
        <li>Undo and Redo.</li>
        <li>Group API for plugin developers.</li>
        <li>Node stacking.</li>
        <li>Tested on both Java SE 5 and 6</li>
        <li> Many, many bug fixes! </li>
      </ul>
    </div>
  </div>
  <div class="item">
    <h2><i>NEW!</i> &nbsp;&nbsp;<a href="retreat2007/index.php">Cytoscape Retreat 2007!</a></h2>
    <a href="retreat2007/venue.php"><img src="retreat2007/images/magere-brug-small.jpg" border="0" alt="Amsterdam by night" /> </a>
    <div class="paragraph">Now in Europe! November 6<sup>th</sup> - 9<sup>th</sup><br>
      Including a public symposium on November 8<sup>th</sup>, with a formidable list of confirmed speakers among them
      <ul>
        <li>Leroy Hood </li>
        <li>Peter Sorger </li>
        <li>Ewan Birney </li>
      </ul>
      Hosted by the <a href="http://www.humangenetics-amc.nl" target="_blank">Human Genetics Department of the Academic Medical Center</a> in the vibrant historic city of <a href="/retreat2007/venue.php">Amsterdam</a>. </div>
  </div>
  <div class="item">
    <h2>Cytoscape 2.4.1</h2>
    <div class="paragraph"> No new features, but several bugs have been fixed. </div>
  </div>
  <div class="item">
    <h2>Cytoscape 2.4.0 </h2>
    <A HREF="screenshots/2_4_ss1.png"> <img src="screenshots/2_4_ss1_thumb.png" border="0" align="left" alt="Cytoscape 2.4.0 Screenshot" /> </A>
    <div class="paragraph"> (Updated 1/16/2007) <br>
      New features include: <br>
      <ul>
        <li> Publication quality image generation.
          <ul>
            <li> Node label position adjustment.</li>
            <li> Automatic Visual Legend generator.</li>
            <li> Node position fine-tuning by arrow keys.</li>
            <li> The ability to override selected VizMap settings. </li>
          </ul>
        </li>
        <li>Quick Find plugin.</li>
        <li>New icons for a cleaner user interface.</li>
        <li>Consolidated network import capabilities.</li>
        <ul>
          <li> Import network from remote data sources (through http or ftp).</li>
          <li> Default support for the following file formats: SBML, BioPAX, PSI-MI, Delimited text, Excel.</li>
        </ul>
        </li>
        <li>New Ontology Server.
          <ul>
            <li> Native support for OBO format ontology files. </li>
            <li> Ability to visualize the ontology tree as a network (DAG).</li>
            <li> Full support for Gene Association files. </li>
          </ul>
        </li>
        <li>Support for Java SE 5</li>
        <li> Many, many bug fixes! </li>
      </ul>
      </ul>
      See the <a HREF="cyto_2_4_features.php">Release Notes</a> for more detail. </div>
  </div>
  <div class="item">
    <h2>Genome Research article on Cytoscape </h2>
    <div class="paragraph"> Genome Research has published a journal article on Cytoscape <a href="http://www.genome.org/cgi/content/full/13/11/2498"> [Abstract] </a> <a href="http://www.genome.org/cgi/reprint/13/11/2498"> [PDF] </a> <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=14597658&amp;dopt=Abstract"> [PubMed entry]</a>. </div>
  </div>
  <div class="item">
    <h2><i>Updated!&nbsp;</i>(Mar. 9 2006)</i>&nbsp;&nbsp;Research using Cytoscape</h2>
    <div class="paragraph"> [<a href="pubs.php">View All Publications</a>]
      <p> <b>Nature Aug. 2005</b> <br>
        Kristin C. Gunsalus, Hui Ge, Aaron J. Schetter, Debra S. Goldberg, 
        Jing-Dong J. Han, Tong Hao, Gabriel F. Berriz, Nicolas Bertin, Jerry Huang, 
        Ling-Shiang Chuang, Ning Li, Ramamurthy Mani, Anthony A. Hyman, Birte Sönnichsen, 
        Christophe J. Echeverri, Frederick P. Roth, Marc Vidal and Fabio Piano <br>
        Predictive models of molecular machines involved in Caenorhabditis elegans early embryogenesis <br>
        [<A HREF="http://www.nature.com/nature/journal/v436/n7052/abs/nature03876.html"/>Abstract</A>]
        [<A HREF="http://www.nature.com/nature/journal/v436/n7052/full/nature03876.html">Full Text</A>]
        [<A HREF="http://www.nature.com/nature/journal/v436/n7052/pdf/nature03876.pdf">PDF</A>] <br>
      <p>
      <p> <b>Nature Nov. 2005</b> <br>
        Suthram S, Sittler T, Ideker T. <br>
        Plasmodium protein network diverges from those of other eukaryotes. <br>
        [<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16267557&query_hl=5"/>Abstract</A>]
        [<A HREF="">Full Text</A>]
        [<A HREF="">PDF</A>] <br>
        <br>
      <p>
      <p> <b>J Comput Biol. Jul-Aug 2005</b> <br>
        Sharan R, Ideker T, Kelley B, Shamir R, Karp RM. <br>
        Identification of protein complexes by comparative analysis of yeast and bacterial protein interaction data. <br>
        [<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16108720&query_hl=5"/>Abstract</A>]
        [<A HREF="http://www.liebertonline.com/doi/pdf/10.1089/cmb.2005.12.835">Full Text</A>]
        [<A HREF="http://www.liebertonline.com/doi/pdf/10.1089/cmb.2005.12.835">PDF</A>] <br>
        <br>
      <p>
      <p> <b>Genome Biology. July 2005</b> <br>
        Yeang CH, Mak HC, McCuine S, Workman C, Jaakkola T, Ideker T. <br>
        Validation and refinement of gene-regulatory pathways on a network of physical interactions. <br>
        [<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15998451&query_hl=5"/>Abstract</A>]
        [<A HREF="http://www.pubmedcentral.gov/articlerender.fcgi?tool=pubmed&pubmedid=15998451">Full Text</A>]
        [<A HREF="http://www.pubmedcentral.gov/picrender.fcgi?artid=1175993&blobtype=pdf">PDF</A>] <br>
        <br>
      <p>
      <p> <b>Nature Biotechnology. May 2005</b> <br>
        Kelley R, Ideker T. <br>
        Systematic interpretation of genetic interactions using protein networks. <br>
        [<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15877074&query_hl=5"/>Abstract</A>]
        [<A HREF="http://www.nature.com/nbt/journal/v23/n5/full/nbt1096.html">Full Text</A>]
        [<A HREF="http://www.nature.com/nbt/journal/v23/n5/pdf/nbt1096.pdf">PDF</A>] <br>
        <br>
      <p> <b>BMC Bioinformatics, July 2005</b> <br>
        David J Reiss , Iliana Avila-Campillo , Vesteinn Thorsson , Benno Schwikowski  and Timothy Galitski <br>
        Tools enabling the elucidation of molecular pathways active in human disease: Application to Hepatitis C virus infection <br>
        BMC Bioinformatics 2005, 6:154 <br>
        [<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15967031">Abstract</A>]
        [<A HREF="http://www.biomedcentral.com/1471-2105/6/154">Full Text</A>]
        [<A HREF="http://www.biomedcentral.com/content/pdf/1471-2105-6-154.pdf">PDF</A>] <br>
      <P> <br>
        <b> Note: </b> If you have a publication which makes use of Cytoscape, please let us know by sending an email to the <a href="http://groups-beta.google.com/group/cytoscape-discuss"> cytoscape-discuss </a> mailing list. 
    </div>
  </div>
  <div class="item">
    <div class="paragraph">
      Past news articles are available <a href="past_news.php">here.</a>
    </div>
  </div>
</div>
<div id="rightbox">
  <?
include "help.php";

if ($news_option == "atom") {
	include "feed.php";
} else {
	include "news.php";
	
}
?>
  <?
include "community_box.php";
include "collab.php";
?>
</div>
<p>
  <? include "footer.php"; ?>
</p>
</body>
</html>
