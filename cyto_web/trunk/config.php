<?php
# Master Configuration File for Cytoscape.org Web Site
# Author:  Ethan Cerami, MSKCC

#error_reporting (E_ERROR | E_WARNING | E_PARSE | E_NOTICE);

################################################
#  Specifies the News Option for the Home Page
#  If set to "rss", we use the RSS Feed from the Cytoscape Announce List.
#  If set to "statis", we use the Static News Feed
################################################
$news_option = "rss";

#################################################
#  Specifies the Current Cytoscape Release Version
#  Update this version number after each release.
#################################################
$version = "v2.00";


#################################################
#  Production Setting
#  Specifies whether we are running in production on cytoscape.org.
#  If set to false, we assume a local test server set-up

#################################################
$in_production = true;


#################################################
#  Base URLs for ISB and cBio
#  Note:  as long as we keep the same paths, these should not be
#  updated.
#################################################
$isb_base = "http://db.systemsbiology.net:8080/cytoscape/download/";
$cbio_base = "http://www.cbio.mskcc.org/cytoscape/release/";


#################################################
# Cytoscape 1.1 Release File Names
# Note:  these probably don't need to ever be updated.
#################################################
$cyto1_gz = "cytoscape-v1.1.1.tar.gz";
$cyto1_zip = "cytoscape-v1.1.1.zip";
$cyto1_src = "cytoscapeSource-v1.1.1.tar.gz";

# URL for Cytoscape 1.1 Zip/Tar.gz Files (East Coast)
$cyto1_gz_east= $cbio_base . $cyto1_gz;
$cyto1_zip_east= $cbio_base . $cyto1_zip;

# URL for Cytoscape 1.1 Source
$cyto1_source_east= $cbio_base . $cyto1_src;

#################################################
# Cytoscape 2.0 Release File Names
# Note:  these probably don't need to ever be updated.
#################################################
$cyto2_gz = "cytoscape-" . $version  . ".tar.gz";
$cyto2_zip = "cytoscape-" . $version  . ".zip";
$cyto2_src = "cytoscapeSource-" . $version . ".tar.gz";
$cyto2_mac = "cytoscape.dmg.zip";

# URL for Cytoscape 2.0 and Source
$cyto2_gz_east= $cbio_base . $cyto2_gz;
$cyto2_zip_east= $cbio_base . $cyto2_zip;
$cyto2_source_east= $cbio_base . $cyto2_src;
$cyto2_mac_east= $cbio_base . $cyto2_mac;

#################################################
# URL for Cytoscape Install Anywhere
#################################################
$cyto1_install_anywhere = $isb_base . "installer/installer1.1.1/install.htm";
$cyto2_install_anywhere = "ftp://baker.systemsbiology.net/pub/xmas/cytoscape2.0/cytoscape2.0_Build_Output/Web_Installers/install.htm";

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
