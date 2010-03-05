<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Plugin download statistics</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
    <style type="text/css">
<!--
.style1a {
	font-size: 24px;
	font-weight: bold;
}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Search results</div>
</div>
<div id="container">
<?php include "../nav.php"; ?>

<?php

$SearchText = strToUpper($_POST["searchwords"]); 

?>
<br><br><b>Search words:</b> <?php echo $SearchText ?> <br><br>
<?php

// Strip multiple whitespace
$SearchText = preg_replace("(\s+)", " ", $SearchText);

// Split text on whitespace
$searchArray =& split( " ", $SearchText );

$wordSQL = "";
$i = 0;
// Build the word query string
foreach ( $searchArray as $searchWord )
{
    if ( $i == 0 )
        $wordSQL .= "word='" . $searchWord  ."' ";
    else
        $wordSQL .= " OR word='" . $searchWord ."' ";
    $i++;
}

//echo "wordSQL = $wordSQL<br>";

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

// Get the total number of plugins
include 'dbUtil.inc';
$plugin_id_array = getPluginIDs($connection);
$totalPluginCount = count($plugin_id_array);

//echo "totalPluginCount = $totalPluginCount<br>";

// Search words can at most be present in 70% of the objects
$stopWordFrequency = 0.7;

// Build the full search query using logical OR if multiple words are searched on
$searchQuery = "SELECT plugin_list.plugin_auto_id as plugin_id, plugin_list.name as name, plugin_list.description as description, description_word_link.frequency as frequency
                    FROM plugin_list, description_word_link, description_words
                    WHERE plugin_list.plugin_auto_id=description_word_link.plugin_id
                    AND description_words.word_id=description_word_link.word_id
                    AND ( $wordSQL )
                    AND ( ( description_words.plugin_count / $totalPluginCount ) < $stopWordFrequency )
                    ORDER BY description_word_link.frequency DESC";

// Run the query
if (!($result = @ mysql_query($searchQuery, $connection)))
	showerror();

if (@ mysql_num_rows($result) != 0) {

    while($_row = @ mysql_fetch_array($result))
	{	    
		$pluginID = $_row["plugin_id"];
		$pluginName = trim($_row["name"]);
		$pluginDescription = trim($_row["description"]);
		?>
		<a href="displayplugin.php?pluginid=<?php echo $pluginID ?>"><?php echo $pluginName ?></a><p>
		<?php echo $pluginDescription ?> <p>
		<?php
	}
}

?>

<?php include "../footer.php"; ?>
<br>
</body>
</html>
