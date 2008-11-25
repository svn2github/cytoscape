<?php
include_once 'db.php';
include_once 'cc_utils.php';

$pub_code = "";
if (isset ($_GET['pub_code'])) {
	$pub_code = $_GET['pub_code'];
}

//$pub_code = 'gersten2008';	

// Convert the pub_code to pub_id	
$pub_id = 0;
$is_published = true;
if ($pub_code != "") {
	$record = getPublicationRecordFromPubCode($pub_code, $connection);
	$pub_id = $record['publication_auto_id'];
	$is_published = $record['is_published'];
	if ($pub_id == "") {
		?>No such publication! <?php
		exit();
	}
}


// get the publication list -- id, cover_image, xml, html, pdf
$publications = getAllPublications($connection, $pub_id);

	//echo "Number of publications = ".count($publications)."\n";
	for ($i=0; $i<count($publications); $i++) {
		$publication = $publications[$i];
		//echo "id=".$publication['id']."\n";
		//echo "pmid=".$publication['pmid']."\n";
		//echo "pubmed_xml_record=".$publication['pubmed_xml_record']."\n";
		//echo "coverimage_id=".$publication['coverimage_id']."\n";
	}
	
$distinct_species = getSpeciesFromDB($connection, $pub_id);
//echo "Number of species =".count($all_species)."<br>\n";
//print_array($distinct_species);

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" >
<head>
  <meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
  <meta name="author"      content="Mike Daly" />
  <meta name="keywords"    content="biological networks, network models, systems biology, pathway hypotheses" />
  <meta name="description" content="Cell Circuits" />
  <meta name="robots"      content="all" />

  <script type="text/javascript" src="./javascript/advanced_search.js"></script>
  <script src="./javascript/scriptaculous.js" type="text/javascript"></script>
<script lanugage="JavaScript">
function pvaluewin(){
		if (window.screenX) { // FireFox and others
			var windowX=window.screenX + 200;
			var windowY=window.screenY + 200;
 			window.open("pvalue.html", "pValueWin", "width=400,height=350,status=no,resizable=yes,left="+windowX+",top="+windowY); 
		}
		else { //IE and Others
			window.open("pvalue.html", "pValueWin", "width=400,height=350,status=no,resizable=yes,left=200,top=200"); 	
		}
		void 0;
}
</script>
  <link type="text/css" rel="stylesheet" href="master.css">

  <title>Cell Circuits Advanced Search</title>

  <style type="text/css">
<!--
.style1 {color: #3366FF}
-->
  </style>
</head>



<body id="body-element">

  <form method="POST" name="search" action="/cgi-bin/cellcircuits/search.pl">

  <table align='center' border='0' cellspacing=0 cellpadding=0 summary='search interface'>
    <tr>
      <td align='right' rowspan=2>
	<a href='index.html'><img src='CC-logo-small.jpg' border='0' alt="Cell Circuits" title="Click to go to the Cell Circuits Home Page"/></a>
      </td>
      <td align="center" valign="bottom">
        <input type="text" size="55" name="search_query" value='' title='For information on valid queries, click "About CellCircuits" link to the right'/>
      </td>
      <td align='left' valign='center' rowspan=2>
         &nbsp;<a class='white-bg-link' href='index.html' title='Click to go to the Cell Circuits Home Page'>CellCircuits&nbsp;Home</a><br />
	 &nbsp;<a class='white-bg-link' href='about_cell_circuits.html'>About&nbsp;CellCircuits</a><br/>
	 &nbsp;<a class="white-bg-link" href="tutorial/Tutorial-advanced-search.html">Help</a>
      </td>
    </tr>
    <tr>
      <td align='center' valign='top'>
        <input type="submit" name="search_query_button" value="Search" title='Click to find models matching your query'/><input type="submit" value="Load Example Query" title="requires javaScript" onClick="LoadExampleQuery('gcn* gal4 GO:0003677','DNA binding');return false;" />
      </td>
    </tr>
  </table>

  <br />

  <table align="center" border="0">
    <tr>
      <td align="center" colspan="1">
        <b>Species to include:</b>
	<br />
        <select name="species" size=4 multiple>
		
		<?php
		for ($i=0; $i<count($distinct_species); $i++) {
			if (strcasecmp($distinct_species[$i],'Caenorhabditis elegans') ==0) {
	  			?> <option selected title="Worm">Caenorhabditis elegans</option> <?php
			}
			else if (strcasecmp($distinct_species[$i],'Drosophila melanogaster') ==0) {
	  			?> <option selected title="Fly">Drosophila melanogaster</option><?php
			}
			else if (strcasecmp($distinct_species[$i],'Homo sapiens') ==0) {
	  			?><option selected title="Human">Homo sapiens</option><?php
			}
			else if (strcasecmp($distinct_species[$i],'Plasmodium falciparum') ==0) {
	  			?><option selected title="Malaria parasite">Plasmodium falciparum</option><?php
			}
			else if (strcasecmp($distinct_species[$i],'Saccharomyces cerevisiae') ==0) {
	  			?><option selected title="Yeast">Saccharomyces cerevisiae</option><?php
			}
			else {
				?><option selected title="Unknown">Unknown species</option><?php
			}
		}
		
		?>
	</select>      </td>
      <td align="center" valign="top" colspan="1">
	<b>Sort results:</b>
	<br />
	<select name="sort_method" size=1>
      <option value="optionA_by_number_of_query_terms_matching_model" selected>By Number of Query Terms Matching Model</option>
      <option value="optionB_by_size_of_model" >By Size of Model</option>
	  <option value="optionC_by_publication" >By publication</option>
      <option value="optionD_by_most_enriched_for_a_GO_term" >By most enriched for a GO term</option>
    </select>
	<br />
	<b><a href ='javascript:pvaluewin()' class="style1">P-value cutoff:</a></b><br />
        <input type="text" size="12" name="pval_thresh" value="0.0001" title="Standard decimal or exponential notations are valid. e.g. 0.005 OR 5e-3" />      </td>
      <td align="center" valign="top" colspan="1">      </td>
    </tr>
  </table>
<?php if (!$is_published) { ?>
<p>&nbsp;</p>
<p><center><em><b>Note:</b></em> This data set is not published yet, thus is not available through the public interface of CellCircuits web site.</center> <br />
</p> &nbsp;
<p>
  <?php } ?>    
  
</p>
<table align="center" cellspacing=0 cellpadding=5 border="0">
    <!--<tr bgcolor="white"> -->
    <tr>
      <th colspan="8" align="center" valign="center">
        Select publication(s) to search:
	 <INPUT type=button value="Select All" onClick="SelectAll(true)">
	 <INPUT type=button value="Unselect All" onClick="SelectAll(false)">
	 <hr>      </th>
    </tr>    

<?php

//echo "publications count = ".count($publications)."<br>";
//echo "publication[2]['pub_url']=".$publications[2]['pub_url']."<br>";
//echo "publication[2]['pdf_file_id']=".$publications[2]['pdf_file_id']."<br>";
//echo "publication[2]['supplement_file_id']=".$publications[2]['supplement_file_id']."<br>";
//array_splice($publications, 2,1);
//echo "getCoverImage.php?coverageImageID=".$publication[0]['coverimage_id'];

//echo $publications[0]['pubmed_xml_record'];

for ($i=0; $i<count($publications)/2; $i++) {

	for ($j=$i*2; $j< ($i*2+2); $j++) {
		if ($j == count($publications)) {
			//echo "<td></td>";
			?> <td></td><?php
			break;
		}
	
		$publication = $publications[$j];
	?>
	
      <td class="advanced-search-cover" width="1">
        <input type="checkbox" name="publication" value="<?php echo $publication['publication_auto_id']; ?>" checked="checked" />&nbsp;	  
	  </td>
      
	  <td class="advanced-search-cover" width="1">
        <a href="<?php echo $publication['pub_url']; ?>">
	    <img src="<?php echo "getImage.php?image_type=cover_image&image_file_id=".$publication['coverimage_id']; ?>" border="0" />	</a>	  
	  </td>
	  
	  <?php
	  
	  if ($j%2 == 0) {
		  ?>
		  <td class="advanced-search-citation" width="40%">
		  <?php	
	  }
	  else {
		  ?>
		  <td class="advanced-search-citation-right" width="40%">
		  <?php
	  }
	  ?>
	  <?php echo stripslashes($publication['pubmed_html_advsearch']);  ?><BR><P align=right>    
		<?php
		if ($publication['pdf_file_id'] != -1) {
			?> <a class="white-bg-link" href="<?php echo "file_download.php?file_type=pdf&file_id=".$publication['pdf_file_id']; ?>">[PDF]</a> <?php
		}
		if ($publication['supplement_file_id'] != -1) {
			?> <a class="white-bg-link" href="<?php echo "file_download.php?file_type=supplement_notes&file_id=".$publication['supplement_file_id']; ?>">[Supplementary Notes]</a> <?php
		}
		if (trim($publication['supplement_url']) != "") {
			?> <a class="white-bg-link" href="<?php echo $publication['supplement_url']; ?>">[Supplemental Website]</a> <?php
		}
		?>
		<?php
			if ($publication['pmid'] >0) {
			?>
		<a class="white-bg-link" href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=<?php echo $publication['pmid']; ?>&query_hl=24"> [PubMed]</a>
		<?php
			}
			?>
		</p>
	  </td>
<?php
	}
	  if ($j%2 ==0) {
	  		?>     
				<tr><td colspan="8"><hr></td></tr>
			<?php 
	  }	  
}
?>

    <!--<tr><td colspan="8"><hr></td></tr>-->
</table>

<hr>

<center>
<a class="white-bg-link" href="index.html" title="Click to go to the CellCircuits Home Page">CellCircuits&nbsp;Home</a>&nbsp;&nbsp;|&nbsp;&nbsp;
<a class="white-bg-link" href="about_cell_circuits.html">About CellCircuits</a>&nbsp;&nbsp;|&nbsp;&nbsp;
<a class="white-bg-link" href="Tutorial-home.html">Help</a>&nbsp;&nbsp;|&nbsp;&nbsp;
<a class="white-bg-link" href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab">Ideker Lab</a>&nbsp;&nbsp;|&nbsp;&nbsp;
<a class="white-bg-link" href="http://www-bioeng.ucsd.edu/">UCSD</a><br />
<p style="font-size: 0.8em">Funding provided by the National Science Foundation (NSF 0425926).</p>
</center>

<input type="hidden" name="results_page" value="1" checked="checked" />
<input type="hidden" name="pub_id" value="<?php echo $pub_id;?>" checked="checked" />

</form>

</body>
</html>


<?php
function getAllPublications($connection, $pub_id){

	$dbQuery  = "SELECT * FROM publications ";
	if ($pub_id != 0) {
		$dbQuery .= "WHERE publication_auto_id ='$pub_id'";
	}
	else {
		$dbQuery .= "WHERE is_published =true";
	}
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}

	$publications = NULL;
	while ($_row = @ mysql_fetch_array($result)) {
		$publication = NULL;
		$publication['publication_auto_id'] = $_row["publication_auto_id"];
		$publication['pmid'] = $_row["pmid"];
		$publication['pubmed_xml_record'] = $_row["pubmed_xml_record"];
		$publication['pubmed_html_advsearch'] = $_row["pubmed_html_advsearch"];
		if ($publication['pubmed_html_advsearch'] == NULL) {
			$publication['pubmed_html_advsearch'] = "unavailable";
		}
		$publication['pub_url'] = $_row["pub_url"];
		$publication['supplement_file_id'] = $_row["supplement_file_id"];		
		$publication['supplement_url'] = $_row["supplement_url"];
		$publication['coverimage_id'] = $_row["cover_image_id"];
		$publication['pdf_file_id'] = $_row["pdf_file_id"];
		$publications[] = $publication;
	}

	return $publications;
}

function print_array($myArray) {
	if (count($myArray) == 0) {
		echo "\tEmpty Array<br>\n";
		return;
	}
	for ($i=0; $i< count($myArray); $i++) {
		echo "$i ---".$myArray[$i]."<br>\n";
	}
}

function getSpeciesFromDB($connection, $pub_id) {
	$dbQuery  = "SELECT distinct species from network_file_info ";
	
	if ($pub_id != 0) {
		$dbQuery .= "WHERE publication_id ='$pub_id'";
	}

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}

	$all_species = NULL;
	while ($_row = @ mysql_fetch_array($result)) {
		$species = $_row["species"];
		$all_species[] = $species;
	}
	
	// Extrac distinct species
	$distinct_species['placeHolder'] = 'placeHolder';
	for ($i=0; $i<count($all_species); $i++) {
		$a_species = $all_species[$i];
		$tmpArray = split(',', $a_species);
			
		for ($j=0; $j<count($tmpArray); $j++) {
			if (!array_key_exists(trim($tmpArray[$j]),$distinct_species)) {
				$distinct_species[trim($tmpArray[$j])] = 'A';
			}
		}
	}
	// Remove placeHolder
	array_splice($distinct_species,0,1);
	
	return array_keys($distinct_species);
}

function getCoverImage($cover_image_file_id, $connection) {
	$dbQuery  = "SELECT data from cover_image_files ";
	$dbQuery  .= "WHERE cover_image_file_auto_id =$cover_image_file_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}
	
	return NULL;
	//return @ mysql_result($result, 0, "data");
}

?>
