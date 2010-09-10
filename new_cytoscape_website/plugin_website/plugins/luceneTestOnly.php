<?php
// This script will create Lucene Index "./luceneIndex" for all the plugins in CyPluginDB
// If there is an existing index, the existing one will be overwritten.

// Include the DBMS credentials
include 'db.inc';
include 'dbUtil.inc';
include 'luceneUtil.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();


// Test add
if (false){
	echo "Add doc to index...\n";

	$doc = new Zend_Search_Lucene_Document();
	$doc->addField(Zend_Search_Lucene_Field::Text('id', 9999));
	$doc->addField(Zend_Search_Lucene_Field::Text('name', 'testName', 'utf-8'));
	$doc->addField(Zend_Search_Lucene_Field::Text('description', 'Peng Liang Wang ASDFG', 'utf-8'));
	
	addDoc2LuceneIndex($doc);
}

// test delete
if (false){
	echo "delete index...\n";

	$doc = new Zend_Search_Lucene_Document();
	$doc->addField(Zend_Search_Lucene_Field::Text('id', 999));
	
	removeDocFromLuceneIndex($doc);
}

// test search
if (false){

	echo "Search index...\n";
	$queryStr = "name:testName";
	$query = Zend_Search_Lucene_Search_QueryParser::parse($queryStr, 'utf-8');
	
	$index = Zend_Search_Lucene::open('luceneIndex/index');
	$hits = $index->find($query);
	foreach ($hits as $hit) {
   		/*@var $hit Zend_Search_Lucene*/
	   	$doc = $hit->getDocument();
   		//echo $doc->getField('id')->value, PHP_EOL;
   		echo "hit --- id=". $doc->getField('id')->value."\n";
	}

}


?>