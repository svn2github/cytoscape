<?php
// This script will create Lucene Index "./luceneIndex" for all the plugins in CyPluginDB
// If there is an existing index, the existing one will be overwritten.

// Include the DBMS credentials
include 'db.inc';
include 'dbUtil.inc';
require_once 'Zend/Search/Lucene.php';
require_once 'Zend/Search/Lucene/Document.php';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

$plugin_id_array = getPluginIDs($connection);

// Create an index, this will wipe out the existing index and create a new one
$index = Zend_Search_Lucene::create('luceneIndex/index');

// index all the plugins
foreach ($plugin_id_array as $plugin_id ) {

	$doc = new Zend_Search_Lucene_Document();
	$doc->addField(Zend_Search_Lucene_Field::Text('id', $plugin_id));
	$doc->addField(Zend_Search_Lucene_Field::Text('name', $plugin_info['name'], 'utf-8'));
	$doc->addField(Zend_Search_Lucene_Field::Text('description', $plugin_info['description'], 'utf-8'));
	
	$index->addDocument($doc);
}

$index->commit();

?>