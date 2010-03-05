<?php

// update two tables 'description_word' and 'description_work_link' for a plugin deletion
function deleteWords($connection, $plugin_id) {
	// get the list of words for the plugin in table 'description_word_link'
	$dbQuery = "select word_id from description_word_link where plugin_id = $plugin_id";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
	
	$word_id_array = array(); // create an empty array
	
	if (@ mysql_num_rows($result) != 0) {
		while($_row = @ mysql_fetch_array($result))
        {
     	  $word_id_array[] = $_row["word_id"];
        } 
	}
	
	// delte the plugin in table 'description_word_link'
	$dbQuery = "delete from description_word_link where plugin_id = $plugin_id";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
	
	// update the table 'descriptiopn_words' due to the deletion of this plugin
	foreach ($word_id_array as $word_id ) {
		
		$dbQuery = "select * from description_words where word_id = $word_id";
		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();
		$_row = @ mysql_fetch_array($result);
		$plugin_count = $_row['plugin_count'];
		
		if ($plugin_count == 1){
			// remove the word in table 'description_words'
			$dbQuery = "delete from description_words where word_id = $word_id";
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();			
		}
		else if ($plugin_count > 1){
			// update the plugin_count value for this word
			$plugin_count = $plugin_count -1;
			$dbQuery = "update description_words set plugin_count = $plugin_count where word_id = $word_id";
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();						
		}
		else {
			print "If this block is reached, somethinmg worng happened!";
		}
	}
}

// Process the descriptiopn text for a plugin and populate two tables
// 'description_words' and 'description_word_link'
function addWords($connection, $plugin_id){
	
	//Get the description of this plugin
	$IndexText = getPluginDescription($connection, $plugin_id); 
	
	$IndexText = strtoupper($IndexText);
	
	// This block is For test only
	//$IndexText = "MCODE finds clusters (highly interconnected regions)"; //  in a network network. Clusters";
	//$IndexText = "in a network network. Clusters";
	//$plugin_id = 5;
	
	// Strip multiple whitespaces
	$IndexText = str_replace(".", " ", $IndexText);
	$IndexText = str_replace(",", " ", $IndexText);
	$IndexText = str_replace("'", " ", $IndexText);
	$IndexText = str_replace("!", " ", $IndexText);
	$IndexText = str_replace(":", " ", $IndexText);
	$IndexText = str_replace("=", " ", $IndexText);
	$IndexText = str_replace("\"", " ", $IndexText);
	$IndexText = str_replace("\\", " ", $IndexText);

	$IndexText = str_replace("<b>", " ", $IndexText);
	$IndexText = str_replace("</b>", " ", $IndexText);

	$IndexText = str_replace("<p>", " ", $IndexText);
	$IndexText = str_replace("</p>", " ", $IndexText);
	$IndexText = str_replace("<i>", " ", $IndexText);
	$IndexText = str_replace("</i>", " ", $IndexText);
	$IndexText = str_replace("<br>", " ", $IndexText);
	$IndexText = str_replace("<font>", " ", $IndexText);
	$IndexText = str_replace("</font>", " ", $IndexText);

	$IndexText = str_replace("<strong>", " ", $IndexText);
	$IndexText = str_replace("</strong>", " ", $IndexText);
			
	$IndexText = str_replace("-", " ", $IndexText);
	$IndexText = str_replace("(", " ", $IndexText);
	$IndexText = str_replace(")", " ", $IndexText);
	$IndexText = str_replace("<", " ", $IndexText);
	$IndexText = str_replace(">", " ", $IndexText);
	
	$IndexText = str_replace("\t", " ", $IndexText);
	$IndexText = str_replace("\n", " ", $IndexText);
	$IndexText = str_replace("\r", " ", $IndexText);

	$IndexText = str_replace("|", " ", $IndexText);
	$IndexText = str_replace("/", " ", $IndexText);

	$IndexText = preg_replace("(\s+)", " ", $IndexText);

	
	// Split text on whitespace
	$indexArray = & split(" ", $IndexText);
	
	// Count the total words in index text
	$totalWordCount = count($indexArray);
	
	// Count the number of instances of each word
	$wordCountArray = array_count_values($indexArray);
	
	// Strip double words
	$indexArray = array_unique($indexArray);
	
	// Count unique words
	$uniqueWordCount = count($indexArray);
	
	foreach ($indexArray as $indexWord) {
		
		// Ignore empty string
		if (strlen($indexWord)==0){
			continue;
		}
		
		// Store word in table 'description_words' if it does not exist.
		if (strlen($indexWord) != 0){
			$word_id = storeWord($connection, $indexWord);		
		}
	
		if ($word_id == -1){
			echo "Failed to store word $indexWord into DB\n</br>";
			continue;
		}
		
		// Calculate the relevans ranking for this word
		$frequency = ($wordCountArray[$indexWord] / $totalWordCount);
	
		//create the reference to the pluigin_id in table 'description_word_link'" .
		$dbQuery = "INSERT INTO description_word_link ( id, word_id, plugin_id, frequency )
		                     VALUES ( '0', '$word_id', '$plugin_id', '$frequency' )";

		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();
	}
}


function getPluginDescription($connection, $pluginID) {
	$description = "";
	$dbQuery = "select description from plugin_list where plugin_auto_id =".$pluginID;	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
			$the_row = @ mysql_fetch_array($result);
			$description = $the_row['description'];
	}

	return $description;
}


// store one word into table 'description_words' and return word_id
function storeWord($connection, $word) {
	$word_id = -1;

	$dbQuery = "select * from description_words where word ='$word'";	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
			// word doest not exist, insert it	
			$dbQuery = "insert into description_words (word_id, word, plugin_count) values (0,'$word', '1')";	
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();
			$word_id = mysql_insert_id($connection);
	}
	else {
		// word exists, update its count
		$the_row = @ mysql_fetch_array($result);
		$word_id = $the_row['word_id'];
		$plugin_count = $the_row['plugin_count'];
		$plugin_count = $plugin_count +1;
		$dbQuery = "update description_words set plugin_count =".$plugin_count." where word_id= $word_id";	
		
		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();
	}
	
	return $word_id;
}

?>