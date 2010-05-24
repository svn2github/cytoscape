<?php
// 
require_once 'Zend/Search/Lucene.php';
require_once 'Zend/Search/Lucene/Document.php';

include_once 'dbUtil.inc';

// create a lucene index for all the plugins in CyPluginDB
function createLuceneIndex($connection) {
	// get all pluigin ids
	$plugin_id_array = getPluginIDs($connection);

	// Create an index, this will wipe out the existing index and create a new one
	$index = Zend_Search_Lucene::create('./luceneIndex/index');

	// index all the plugins
	foreach ($plugin_id_array as $plugin_id ) {	
		$plugin_info = getPluginInfo($connection, $plugin_id);
		$doc = new Zend_Search_Lucene_Document();
		$doc->addField(Zend_Search_Lucene_Field::Text('id', $plugin_id));
		$doc->addField(Zend_Search_Lucene_Field::Text('name', $plugin_info['name'], 'utf-8'));
		$doc->addField(Zend_Search_Lucene_Field::Text('description', $plugin_info['description'], 'utf-8'));
		$index->addDocument($doc);
	}

	$index->commit();
}

function addDoc2LuceneIndex($doc){
	$index = Zend_Search_Lucene::open('luceneIndex/index');
	$index->addDocument($doc);
	$index->commit();
}

function removeDocFromLuceneIndex($doc){
	$index = Zend_Search_Lucene::open('luceneIndex/index');
	$index->delete($doc->id);
	$index->commit();

}

?>