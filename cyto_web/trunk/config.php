<?php

# Master Configuration File for Cytoscape.org Web Site
# Author:  Ethan Cerami, MSKCC

#error_reporting (E_ERROR | E_WARNING | E_PARSE | E_NOTICE);

################################################
#  Specifies the News Option for the Home Page
#  If set to "atom", we use the XML/Atom Feed from the Cytoscape Announce List.
#  If set to "static", we use the Static News Feed in news.php
################################################
$news_option = "atom";

#################################################
#  Production Setting
#  Specifies whether we are running in production on cytoscape.org.
#  If set to false, we assume a local test server set-up

#################################################
$in_production = true;

#################################################
#  Base URL(s)
#  Note:  as long as we keep the same paths, these should not be
#  updated.
#################################################
$cbio_base = "http://www.cbio.mskcc.org/cytoscape/release/";

#################################################
# Cytoscape 1.1 Release File Names
# Note:  these probably don't need to ever be updated.
#################################################
$cyto1_gz_east = $cbio_base."cytoscape-v1.1.1.tar.gz";
$cyto1_zip_east = $cbio_base."cytoscape-v1.1.1.zip";
$cyto1_source_east = $cbio_base."cytoscapeSource-v1.1.1.tar.gz";

#################################################
# Cytoscape 2.0 Release File Names
# Note:  these probably don't need to ever be updated.
#################################################
$cyto2_gz_east = $cbio_base."cytoscape-v2.00.tar.gz";
$cyto2_zip_east = $cbio_base."cytoscape-v2.00.zip";
$cyto2_source_east = $cbio_base."cytoscapeSource-v2.00.tar.gz";
$cyto2_mac_east = $cbio_base."cytoscape-v2.00.dmg.zip";

#################################################
# Cytoscape 2.1 Release File Names
#################################################
$cyto2_1_gz_east = $cbio_base."cytoscape-v2.1.tar.gz";
$cyto2_1_zip_east = $cbio_base."cytoscape-v2.1.zip";
$cyto2_1_source_east = $cbio_base."cytoscapeSource-v2.1.tar.gz";
$cyto2_1_mac_east = $cbio_base."cytoscape-v2.1.dmg.zip";

#################################################
# Cytoscape 2.2 Release File Names
#################################################
$cyto2_2_gz_east = $cbio_base."cytoscape-v2.2.tar.gz";
$cyto2_2_zip_east = $cbio_base."cytoscape-v2.2.zip";
$cyto2_2_source_east = $cbio_base."cytoscapeSource-v2.2.tar.gz";
$cyto2_2_mac_east = $cbio_base."cytoscape-v2.2.dmg.zip";


#################################################
# URL for Cytoscape Install Anywhere
#################################################
$cyto1_install_anywhere = "http://db.systemsbiology.net:8080/cytoscape/download/installer/installer1.1.1/install.htm";
$cyto2_install_anywhere = "ftp://baker.systemsbiology.net/pub/xmas/cytoscape2.0/cytoscape2.0_Build_Output/Web_Installers/install.htm";
$cyto2_1_install_anywhere = "http://chianti.ucsd.edu/Cyto2.1/install.htm";
$cyto2_1_install_anywhere = "http://chianti.ucsd.edu/Cyto2.2/install.htm";

#  Production Settings
#  Do not Modify
if ($in_production == true) {
	#  The Real Cytoscape Data File
	$cyto_data = "/usr/local/www/virtual3/66/175/24/126/data/cyto_data.txt";
} else {
	#  A Test Data File (Used for local testing purposes only)
	$cyto_data = "data/cyto_data.txt";
}
?>

